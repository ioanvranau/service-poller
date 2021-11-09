package com.service.poller;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.service.poller.model.ServiceUrl;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnectOptions;
import static com.service.poller.utils.ServicePollerUtils.getSqlClient;
import static com.service.poller.utils.ServicePollerUtils.getSqlConnectOptionsFromArgsIfApplicable;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class MainVerticleTest {
    public static final String API_URL = "/api/url";
    HttpClient client;

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext testContext) {
        SqlConnectOptions sqlConnectOptions = getSqlConnectOptionsFromArgsIfApplicable("99999", "localhost", "3306");
        Pool sqlClient = getSqlClient(vertx, sqlConnectOptions);
        vertx.deployVerticle(new MainVerticle(5555, sqlClient), testContext.succeeding(id -> testContext.completeNow()));
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultPort(5555);
        this.client = vertx.createHttpClient(options);
    }

    @AfterEach
    @DisplayName("Check that the verticle is still there")
    void lastChecks(Vertx vertx) {
        assertThat(vertx.deploymentIDs())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void testCRUD(Vertx vertx, VertxTestContext testContext) {
        final String path = "my/path";
        final String name = "name12";
        final String updatedName = "name12444";
        client.request(HttpMethod.DELETE, API_URL)
                // just cleanup
                .flatMap(req -> req.putHeader("Content-Type", "application/json")
                        .send(Json.encode(ServiceUrl.of("s", path, new Date().toString())))
                        .onSuccess(
                                deleteResponse -> assertThat(deleteResponse.statusCode() == 200 || deleteResponse.statusCode() == 404).isEqualTo(true)
                        )
                ).flatMap(response -> client.request(HttpMethod.POST, API_URL)
                        .flatMap(req -> req.putHeader("Content-Type", "application/json")
                                .send(Json.encode(ServiceUrl.of(name, path, new Date().toString())))
                                .onSuccess(
                                        postResponse -> assertThat(postResponse.statusCode()).isEqualTo(200)
                                )
                        )
                ).flatMap(response -> client.request(HttpMethod.GET, API_URL)
                        .flatMap(HttpClientRequest::send)
                        .flatMap(HttpClientResponse::body)
                        .onSuccess(
                                getAllResponse -> {
                                    final String responseJson = getAllResponse.toJson().toString();
                                    assertThat(responseJson).contains(path);
                                    assertThat(responseJson).contains(name);
                                }
                        )
                ).flatMap(response -> client.request(HttpMethod.PUT, API_URL)
                        .flatMap(req -> req.putHeader("Content-Type", "application/json")
                                .send(Json.encode(ServiceUrl.of(updatedName, path, new Date().toString())))
                                .onSuccess(
                                        putResponse -> assertThat(putResponse.statusCode()).isEqualTo(200)
                                )
                        )
                ).flatMap(response -> client.request(HttpMethod.GET, API_URL)
                        .flatMap(HttpClientRequest::send)
                        .flatMap(HttpClientResponse::body)
                        .onSuccess(
                                getAllResponse -> {
                                    final String responseJson = getAllResponse.toJson().toString();
                                    assertThat(responseJson).contains(path);
                                    assertThat(responseJson).contains(updatedName);
                                }
                        )
                ).flatMap(response -> client.request(HttpMethod.DELETE, API_URL)
                        .flatMap(req -> req.putHeader("Content-Type", "application/json")
                                .send(Json.encode(ServiceUrl.of(updatedName, path, new Date().toString())))
                                .onSuccess(
                                        deleteResponse -> assertThat(deleteResponse.statusCode()).isEqualTo(200)
                                )
                        )
                ).onComplete(
                        testContext.succeeding(id -> testContext.completeNow())
                );

    }

    @Test
    void testUpdateByNoneExistingPath(Vertx vertx, VertxTestContext testContext) {
        client.request(HttpMethod.PUT, API_URL)
                .flatMap(req -> req.putHeader("Content-Type", "application/json")
                        .send(Json.encode(ServiceUrl.of("test title", "path/here" + UUID.randomUUID(), new Date().toString())))
                )
                .onComplete(
                        testContext.succeeding(
                                response -> testContext.verify(
                                        () -> {
                                            assertThat(response.statusCode()).isEqualTo(404);
                                            testContext.completeNow();
                                        }
                                )
                        )
                );
    }

    @Test
    void testDeleteByNoneExistingPath(Vertx vertx, VertxTestContext testContext) {
        client.request(HttpMethod.DELETE, API_URL)
                .flatMap(req -> req.putHeader("Content-Type", "application/json")
                        .send(Json.encode(ServiceUrl.of("s", "bad/path/here" + UUID.randomUUID(), new Date().toString())))
                )
                .onComplete(
                        testContext.succeeding(
                                response -> testContext.verify(
                                        () -> {
                                            assertThat(response.statusCode()).isEqualTo(404);
                                            testContext.completeNow();
                                        }
                                )
                        )
                );
    }
}