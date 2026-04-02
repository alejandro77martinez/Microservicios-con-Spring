package com.auth_service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

  @Value("${SERVICE_REGISTRY_HOST:NO_ENCONTRADA}")
  private String uri;

  @Value("${jwt.secret}")
  private String secret;

	public static void main(String[] args) {
  
		SpringApplication.run(AuthServiceApplication.class, args);
	}

  @PostConstruct
  void printEnv() {
    System.out.println(">>> SECRET = " + secret);
    System.out.println(">>> URI = " + uri);
  }
}
