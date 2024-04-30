package com.lcwd.electroic.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket docket(){
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(getApiInfo());
        //security
        docket.securityContexts(Arrays.asList(getSecurityContext()));
        docket.securitySchemes(Arrays.asList(getSchemes()));
        ApiSelectorBuilder select = docket.select();
        select.apis(RequestHandlerSelectors.any());
        select.paths(PathSelectors.any());
        Docket build = select.build();
        return build;
    }

    private ApiKey getSchemes() {
        return new ApiKey("JWT","Authorization","header");
    }

    private SecurityContext getSecurityContext() {
        SecurityContext context = SecurityContext
                .builder()
                .securityReferences(getSecurityReferences())
                .build();
        return context;
    }

    private List<SecurityReference> getSecurityReferences() {
        AuthorizationScope[] scopes ={
                new AuthorizationScope("Global","Access Everything")
        };
        return  Arrays.asList(new SecurityReference("JWT",scopes));
    }

    private ApiInfo getApiInfo() {

        ApiInfo apiInfo = new ApiInfo("Electronic Store Backend: APIS","This is backend project created by LCWD","1.0.0V","" +
                "https://www.electronicstorebackend.com",
                new Contact("Aditee","https://www.linkedin.com/in/aditee-adhikari-43a35a194/","aditeeadhikari98@gmail.com"),
                "License Of APIs",
                "https://www.electronicstorebackend.com/about",
                new ArrayDeque<>()
                );
        return apiInfo;

    }
}
