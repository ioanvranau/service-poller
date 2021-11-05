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
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import static com.service.poller.DatabaseHelper.createAndPersistNewService;
import static com.service.poller.DatabaseHelper.createRoutesForAlreadyAddedUrls;
import static utls.ServicePollerUtils.getConnectionOptionsFromCommandLineArgumentsIfApplicable;
import static utls.ServicePollerUtils.getSqlConnectOptions;

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
        int port = 5000;
        SqlConnectOptions sqlConnectOptions = getSqlConnectOptions("localhost", "root", "admin", "servicepoller", false);
        String url = "";
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // use default
            }
            url = args[1];
//            sqlConnectOptions = getConnectionOptionsFromCommandLineArgumentsIfApplicable(args, sqlConnectOptions);
        }
        Vertx vertx = Vertx.vertx();
        Pool sqlClient = getSqlClient(vertx, sqlConnectOptions, url);
        System.out.println("Starting app");
        vertx.deployVerticle(new BackendVerticle(port, sqlClient));
    }

    private static Pool getSqlClient(Vertx vertx, SqlConnectOptions sqlConnectOptions, String url) {
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        Pool client;
        System.out.println("Creating sql pool" + sqlConnectOptions);
        if (true) {
            client = PgPool.pool(vertx, url, poolOptions);
        } else {
            client = MySQLPool.pool(vertx, (MySQLConnectOptions) sqlConnectOptions, poolOptions);
        }
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
//
//        router.get().handler(StaticHandler.create());
//
//        Route route = router.route(HttpMethod.POST, "/api/url");
//
//        route.handler(routingContext -> {
//            final HttpServerRequest request = routingContext.request();
//            final String urlName = request.params().get(URL_NAME_PARAM);
//            final String urlPath = request.params().get(URL_PATH_PARAM);
//
//            createAndPersistNewService(router, routingContext, urlName, urlPath, sqlClient);
//        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port);

    }
}
