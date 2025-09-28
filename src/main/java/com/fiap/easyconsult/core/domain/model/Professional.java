package com.fiap.easyconsult.core.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Professional implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private String email;
    private String name;

    private Professional(Builder builder) {
        this.id = builder.id;
        this.email = validateEmail(builder.email);
        this.name = validateName(builder.name);
    }

    public static Builder builder() {
        return new Builder();
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Professional name cannot be empty");
        }
        return name.trim();
    }

    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Professional email cannot be empty");
        }
        return email.trim();
    }

    public void updateEmail(String newEmail) {
        this.email = validateEmail(newEmail);
    }

    public void updateName(String newName) {
        this.name = validateName(newName);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private Long id;
        private String email;
        private String name;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Professional build() {
            return new Professional(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Professional that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}