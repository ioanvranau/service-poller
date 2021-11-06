package com.service.poller;

import java.util.logging.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnectOptions;
import static com.service.poller.DatabaseHelper.getSqlClient;
import static utls.ServicePollerUtils.getSqlConnectOptions;

public class MainVerticle extends AbstractVerticle {

    public static final String URL_NAME_PARAM = "urlName";
    public static final String URL_PATH_PARAM = "urlPath";
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
        final Router router = Router.router(vertx);
        Route messageRoute = router.get("/api/message");
        messageRoute.handler(rc -> {
            rc.response().end(helloMessage + ">>>");
        });
        final ServiceUrlRepository serviceUrlRepository = ServiceUrlRepository.create(sqlClient);
        final ServiceUrlService serviceUrlService = ServiceUrlService.create(serviceUrlRepository);
        serviceUrlService.createRoutesForAlreadyAddedUrls(router);

        router.get().handler(StaticHandler.create());

        Route getAllUrlsRoute = router.get("/api/url");
        getAllUrlsRoute.handler(serviceUrlService::all);

        Route addUrlsRoute = router.route(HttpMethod.POST, "/api/url");

        addUrlsRoute.handler(routingContext -> {
            final HttpServerRequest request = routingContext.request();
            final String urlName = request.params().get(URL_NAME_PARAM);
            final String urlPath = request.params().get(URL_PATH_PARAM);

            serviceUrlService.createAndPersistNewService(router, routingContext, urlName, urlPath);
        });

        vertx.createHttpServer().requestHandler(router).listen(port).onSuccess(server -> {
                    LOGGER.info("HTTP server started on port " + server.actualPort());
                })
                .onFailure(event -> {
                    LOGGER.severe("Failed to start HTTP server:" + event.getMessage());
                });
    }
}