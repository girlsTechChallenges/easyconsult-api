package com.fiap.easyconsult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EasyconsultMain {

	public static void main(String[] args) {
		SpringApplication.run(EasyconsultMain.class, args);
	}

}
