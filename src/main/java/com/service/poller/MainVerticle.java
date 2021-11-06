package com.service.poller;

import java.util.logging.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnectOptions;
import static com.service.poller.DatabaseHelper.getSqlClient;
import static utils.ServicePollerUtils.getSqlConnectOptions;

public class MainVerticle extends AbstractVerticle {

    private final static Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

    final Pool sqlClient;
    private final int port;

    private final String helloMessage = "Hello React from Vert.x!";

    public MainVerticle(int port, Pool sqlClient) {
        this.port = port;
        this.sqlClient = sqlClient;
    }

    public static void main(String[] args) {
        int webAppPort = 5000;
        SqlConnectOptions sqlConnectOptions = getSqlConnectOptions();
        if (args != null && args.length > 0) {
            try {
                webAppPort = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // use default
            }
        }
        Vertx vertx = Vertx.vertx();
        Pool sqlClient = getSqlClient(vertx, sqlConnectOptions);
        vertx.deployVerticle(new MainVerticle(webAppPort, sqlClient));
    }


    @Override
    public void start() throws Exception {
        LOGGER.info("Starting HTTP server...");
        final ServiceUrlRepository serviceUrlRepository = ServiceUrlRepository.create(sqlClient);
        final ServiceUrlService serviceUrlService = ServiceUrlService.create(serviceUrlRepository);

        final Router router = createRoutes(serviceUrlService);

        vertx.createHttpServer().requestHandler(router).listen(port).onSuccess(server -> {
                    LOGGER.info("HTTP server started on port " + server.actualPort());
                })
                .onFailure(event -> {
                    LOGGER.severe("Failed to start HTTP server:" + event.getMessage());
                });
    }

    private Router createRoutes(ServiceUrlService serviceUrlService) {
        final Router router = Router.router(vertx);
        Route testRoute = router.get("/api/message");
        testRoute.handler(rc -> {
            rc.response().end(helloMessage + ">>>");
        });

        router.get().handler(StaticHandler.create());

        Route getAllUrlsRoute = router.get("/api/url");
        getAllUrlsRoute.produces("application/json").handler(serviceUrlService::all);
        router.delete("/api/url/:path").handler(serviceUrlService::delete);

        serviceUrlService.createRoutesForAlreadyAddedUrls(router);

        router.post("/api/url").consumes("application/json").handler(BodyHandler.create()).handler(routingContext -> {
            serviceUrlService.createAndPersistNewService(router, routingContext);
        });
        return router;
    }
}
