package com.tenpo.mscalculator;

import org.springframework.boot.SpringApplication;

public class TestTenpoChallengeApplication {

  public static void main(String[] args) {
    SpringApplication.from(TenpoChallengeApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
