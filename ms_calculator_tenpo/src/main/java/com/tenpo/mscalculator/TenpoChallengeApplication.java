package com.tenpo.mscalculator;

import com.tenpo.mscalculator.config.properties.PercentageCacheProperties;
import com.tenpo.mscalculator.config.properties.PercentageServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableConfigurationProperties({PercentageServiceProperties.class, PercentageCacheProperties.class})
public class TenpoChallengeApplication {

  public static void main(String[] args) {
    SpringApplication.run(TenpoChallengeApplication.class, args);
  }
}
