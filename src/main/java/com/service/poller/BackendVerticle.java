package com.service.poller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import static com.service.poller.DatabaseHelper.createAndPersistNewService;
import static com.service.poller.DatabaseHelper.createRoutesForAlreadyAddedUrls;
import static utls.ServicePollerUtils.getSqlConnectOptions;
import static utls.ServicePollerUtils.print;

public class BackendVerticle extends AbstractVerticle {

    public static final String URL_NAME_PARAM = "urlName";
    public static final String URL_PATH_PARAM = "urlPath";

    final Pool sqlClient;
    private final int port;

    private String helloMessage = "Hello React from Vert.x!";

    public BackendVerticle(int port, Pool sqlClient) {
        this.port = port;
        this.sqlClient = sqlClient;
    }

    public static void main(String[] args) {
        int webAppPort = 5000;
        SqlConnectOptions sqlConnectOptions = getSqlConnectOptions();
        String url = "";
        if (args != null && args.length > 0) {
            try {
                webAppPort = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // use default
            }
        }
        Vertx vertx = Vertx.vertx();
        Pool sqlClient = getSqlClient(vertx, sqlConnectOptions);
        print("Starting app");
        vertx.deployVerticle(new BackendVerticle(webAppPort, sqlClient));
    }

    private static Pool getSqlClient(Vertx vertx, SqlConnectOptions sqlConnectOptions) {
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        Pool client;
        print("Creating sql pool" + sqlConnectOptions);
        client = MySQLPool.pool(vertx, (MySQLConnectOptions) sqlConnectOptions, poolOptions);
        return client;
    }

    @Override
    public void start() throws Exception {
        final Router router = Router.router(vertx);
        Route messageRoute = router.get("/api/message");
        messageRoute.handler(rc -> {
            rc.response().end(helloMessage + ">>>");
        });
        createRoutesForAlreadyAddedUrls(router, sqlClient);

        router.get().handler(StaticHandler.create());

        final ServiceUrlRepository serviceUrlRepository = ServiceUrlRepository.create(sqlClient);
        final ServiceUrlService serviceUrlService = ServiceUrlService.create(serviceUrlRepository);
        Route getAllUrlsRoute = router.get("/api/url");
        getAllUrlsRoute.handler(serviceUrlService::all);

        Route addUrlsRoute = router.route(HttpMethod.POST, "/api/url");

        addUrlsRoute.handler(routingContext -> {
            final HttpServerRequest request = routingContext.request();
            final String urlName = request.params().get(URL_NAME_PARAM);
            final String urlPath = request.params().get(URL_PATH_PARAM);

            createAndPersistNewService(router, routingContext, urlName, urlPath, sqlClient);
        });

        vertx.createHttpServer().requestHandler(router).listen(port);
    }
}
