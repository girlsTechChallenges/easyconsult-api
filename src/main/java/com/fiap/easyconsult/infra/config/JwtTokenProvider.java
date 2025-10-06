package com.fiap.easyconsult.infra.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.public.key.location}")
    private Resource publicKeyResource;

    private RSAPublicKey publicKey;

    @PostConstruct
    public void init() {
        try (InputStream is = publicKeyResource.getInputStream()) {
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            this.publicKey = (RSAPublicKey) factory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao carregar chave pública RSA", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        String subject = claims.getSubject();

        Object scopeClaim = claims.get("scope");
        List<String> scopes = switch (scopeClaim) {
            case String string -> Arrays.stream(string.split("[ ,]+")).filter(s -> !s.isBlank()).toList();
            case List<?> list -> list.stream().map(Object::toString).toList();
            case null, default -> List.of();
        };

        Collection<SimpleGrantedAuthority> authorities = scopes.stream()
                .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
                .toList();

        return new UsernamePasswordAuthenticationToken(subject, token, authorities);
    }
}