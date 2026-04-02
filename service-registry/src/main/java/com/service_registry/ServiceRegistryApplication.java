package com.service_registry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {

  @Value("${SERVICE_REGISTRY_PORT:NO_ENCONTRADA}")
  private String registryPort;

  @Value("${SERVICE_REGISTRY_HOST:NO_ENCONTRADA}")
  private String registryHost;

  public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryApplication.class, args);
	}

  @PostConstruct
  void printEnv() {
    System.out.println(">>> SERVICE_REGISTRY_PORT = " + registryPort);
    System.out.println(">>> System.getenv()       = " + System.getenv("SERVICE_REGISTRY_PORT"));
    System.out.println(">>> SERVICE_REGISTRY_HOST = " + registryHost);
    System.out.println(">>> System.getenv()       = " + System.getenv("SERVICE_REGISTRY_HOST"));
  }
}
