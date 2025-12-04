package com.inditex.zara.similarproducts.integration.application.service;

import com.inditex.zara.similarproducts.infrastructure.bootstrap.SimilarProductsApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(
        classes = SimilarProductsApplication.class,
        webEnvironment = RANDOM_PORT
)
class SimilarProductsIntegrationTest {

    @Container
    static WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock:3.9.1");

    @DynamicPropertySource
    static void configureProps(DynamicPropertyRegistry registry) {
        // This will be picked up by ExternalApiClient (if you use @Value or @ConfigurationProperties)
        registry.add("external.product-api.base-url", wiremock::getBaseUrl);
    }

    @Autowired
    WebTestClient webTestClient;

    @BeforeAll
    static void setupStubs() throws Exception {
        wiremock.start();
        registerStub("/product/1/similarids", "[2,3,4]");
        registerStub("/product/2", "{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}");
        registerStub("/product/3", "{\"id\":\"3\",\"name\":\"Blazer\",\"price\":29.99,\"availability\":false}");
        registerStub("/product/4", "{\"id\":\"4\",\"name\":\"Boots\",\"price\":39.99,\"availability\":true}");

    }

    private static void registerStub(String path, String jsonBody) throws Exception {
        String adminUrl = wiremock.getBaseUrl() + "/__admin/mappings";

        String mapping = """
        {
          "request": {
            "method": "GET",
            "url": "%s"
          },
          "response": {
            "status": 200,
            "jsonBody": %s,
            "headers": {
              "Content-Type": "application/json"
            }
          }
        }
        """.formatted(path, jsonBody);

        HttpURLConnection connection = (HttpURLConnection) new URL(adminUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        try(OutputStream os = connection.getOutputStream()) {
            os.write(mapping.getBytes());
        }

        connection.getResponseCode(); // force HTTP call
    }


    @Test
    void getSimilarProducts_shouldReturnAggregatedDetails() {
        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[1].id").isEqualTo("3")
                .jsonPath("$[2].id").isEqualTo("4");
    }
}