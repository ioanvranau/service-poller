package com.service.poller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class BackendVerticle extends AbstractVerticle {

    private final int port;

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
        Router router = Router.router(vertx);
        Route messageRoute = router.get("/api/message"); // (1)
        messageRoute.handler(rc -> {
            rc.response().end("Hello React from Vert.x!"); // (2)
        });

        router.get().handler(StaticHandler.create()); // (3)

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port);

    }
}
