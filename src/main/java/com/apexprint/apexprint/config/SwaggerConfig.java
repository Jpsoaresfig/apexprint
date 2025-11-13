package com.apexprint.apexprint.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apexprintOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Apex Print Report API")
                        .description("API de geração de relatórios em PDF, HTML e XLSX a partir de templates JRXML e XML de dados. Compatível com Oracle APEX Print Server.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe APEXPRINT")
                                .email("contato@apexprint.com.br")));
    }
}
