///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
package com.digis01.DRosasAguilarDamianNCapasProject.Configuration;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI drosasOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DRosasAguilar — APIs")
                .version("1.0")
                .description("""
                    Documentación de APIs de Usuario, Dirección, Roles y Catálogos.
                
                """)
                .contact(new Contact()
                    .name("Equipo DRosasAguilar")
                    .email("soporte@demo.local")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local")
            ))
            .externalDocs(new ExternalDocumentation()
                .description("Swagger UI")
                .url("http://localhost:8080/docs"));
    }

    @Bean
    public GroupedOpenApi usuarioGroup() {
        return GroupedOpenApi.builder()
            .group("usuarioapi")
            .pathsToMatch("/usuarioapi/**")
            .build();
    }

    @Bean
    public GroupedOpenApi direccionGroup() {
        return GroupedOpenApi.builder()
            .group("direccionapi")
            .pathsToMatch("/direccionapi/**")
            .build();
    }

    @Bean
    public GroupedOpenApi rolGroup() {
        return GroupedOpenApi.builder()
            .group("rolapi")
            .pathsToMatch("/rolapi/**")
            .build();
    }

    @Bean
    public GroupedOpenApi catalogoGroup() {
        return GroupedOpenApi.builder()
            .group("catalogoapi")
            .pathsToMatch("/catalogoapi/**")
            .build();
    }
}