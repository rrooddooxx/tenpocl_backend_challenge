package com.tenpo.mscalculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${docs-info.app.title}")
  private String title;

  @Value("${docs-info.app.description}")
  private String description;

  @Value("${docs-info.app.version}")
  private String version;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(
                    new Contact()
                        .name("Sebastián Kravetz")
                        .email("root@sebastiankravetz.dev")
                        .url("https://sebastiankravetz.dev")));
  }
}
