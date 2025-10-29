package com.caerus.userservice.configure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addParameters("X-Correlation-Id",
                        new HeaderParameter()
                                .name("X-Correlation-Id")
                                .description("Correlation ID for tracking requests")
                                .required(true)
                                .schema(new StringSchema()._default("a8a299f5-59c1-4cde-b167-8908968618fc"))))
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("API documentation for User Service"));
    }

    @Bean
    public OpenApiCustomizer  correlationIdHeaderCustomiser() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths != null) {
                paths.forEach((path, pathItem) -> {
                    for (PathItem.HttpMethod method : pathItem.readOperationsMap().keySet()) {
                        Operation operation = pathItem.readOperationsMap().get(method);
                        operation.addParametersItem(new HeaderParameter()
                                .name("X-Correlation-Id")
                                .description("Correlation ID for tracking requests")
                                .required(true)
                                .schema(new StringSchema()._default("a8a299f5-59c1-4cde-b167-8908968618fc")));
                    }
                });
            }
        };
    }
}