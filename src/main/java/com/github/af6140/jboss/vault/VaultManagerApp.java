package com.github.af6140.jboss.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VaultManagerApp {

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(VaultManagerApp.class, args)));
  }
}
