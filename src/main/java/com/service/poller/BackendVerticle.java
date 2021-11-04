package com.service.poller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class BackendVerticle extends AbstractVerticle {

    public static final String URL_NAME_PARAM = "urlName";
    public static final String URL_PATH_PARAM = "urlPath";
    private final int port;
    private String helloMessage = "Hello React from Vert.x!";

    public BackendVerticle(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        int port = 5000;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // use default
            }
        }
        Vertx vertx = Vertx.vertx(); // (1)
        vertx.deployVerticle(new BackendVerticle(port)); // (2)
    }

    @Override
    public void start() throws Exception {
        final Router router = Router.router(vertx);
        Route messageRoute = router.get("/api/message"); // (1)
        messageRoute.handler(rc -> {
            rc.response().end(helloMessage); // (2)
        });

        router.get().handler(StaticHandler.create()); // (3)

        Route route = router.route(HttpMethod.POST, "/api/url");

        route.handler(routingContext -> {
            final HttpServerRequest request = routingContext.request();
            final String urlPath = request.params().get(URL_PATH_PARAM);
            final String urlName = request.params().get(URL_NAME_PARAM);

            Route dynamicRoute = router.get("/api/" + urlPath);
            dynamicRoute.handler(rc -> {
                rc.response().end(urlName); // (2)
            });

            routingContext.response().end("Message posted!" + urlName + " " + urlPath);
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port);

    }
}
