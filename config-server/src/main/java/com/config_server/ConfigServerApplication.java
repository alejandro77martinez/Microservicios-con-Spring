package com.config_server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import jakarta.annotation.PostConstruct;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServerApplication {

  @Value("${SERVICE_REGISTRY_HOST:NO_ENCONTRADA}")
  private String uri;

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

  @PostConstruct
  void printEnv() {
    System.out.println(">>> SERVICE_REGISTRY_HOST = " + uri);
    System.out.println(">>> System.getenv()       = " + System.getenv("SERVICE_REGISTRY_HOST"));
    
  }

}
