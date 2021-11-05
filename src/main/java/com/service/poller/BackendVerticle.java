package com.service.poller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnectOptions;

public class BackendVerticle extends AbstractVerticle {

    public static final String URL_NAME_PARAM = "urlName";
    public static final String URL_PATH_PARAM = "urlPath";
    final SqlConnectOptions sqlConnectOptions;
    private final int port;
    private String helloMessage = "Hello React from Vert.x!";

    public BackendVerticle(int port, SqlConnectOptions sqlConnectOptions) {
        this.port = port;
        this.sqlConnectOptions = sqlConnectOptions;
    }

    public static void main(String[] args) {
        int port = 5000;
        SqlConnectOptions sqlConnectOptions = getSqlConnectOptions("localhost", "root", "admin", "servicepoller", false);
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // use default
            }
            sqlConnectOptions = getConnectionOptionsFromCommandLineArgumentsIfApplicable(args, sqlConnectOptions);
        }
        Vertx vertx = Vertx.vertx(); // (1)
        vertx.deployVerticle(new BackendVerticle(port, sqlConnectOptions)); // (2)
    }

    private static SqlConnectOptions getConnectionOptionsFromCommandLineArgumentsIfApplicable(String[] args, SqlConnectOptions sqlConnectOptions) {
        if (args.length > 3 && args[1] != null && !args[1].equals("")) {
            String host = args[1];
            if (args[2] != null && !args[2].equals("")) {
                String username = args[2];
                if (args[3] != null && !args[3].equals("")) {
                    String password = args[3];
                    sqlConnectOptions = getSqlConnectOptions(host, username, password, "dc1h7vucrk45rm", true);
                }
            }
        }
        return sqlConnectOptions;
    }

    private static SqlConnectOptions getSqlConnectOptions(String host, String user, String password, String database, boolean isRemotePostgress) {
        SqlConnectOptions connectOptions;
        if (isRemotePostgress) {
            connectOptions = new PgConnectOptions()
                    .setHost(host)
                    .setUser(user)
                    .setPort(5432)
                    .setDatabase(database)
                    .setSsl(true)
                    .setTrustAll(true)
                    .setPassword(password);
        } else {
            //assuming mysql
            connectOptions = new MySQLConnectOptions()
                    .setPort(3306)
                    .setHost(host)
                    .setDatabase(database)
                    .setUser(user)
                    .setPassword(password);
        }
        return connectOptions;
    }

    @Override
    public void start() throws Exception {
        final Router router = Router.router(vertx);
        Route messageRoute = router.get("/api/message"); // (1)
        messageRoute.handler(rc -> {
            final String s = insertIntoDbTest(rc);

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

    private String insertIntoDbTest(RoutingContext rc) {

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        SqlClient client;
        if (sqlConnectOptions instanceof PgConnectOptions) {
            client = PgPool.client(vertx, (PgConnectOptions) sqlConnectOptions, poolOptions);
        } else {
            client = MySQLPool.pool(vertx, (MySQLConnectOptions) sqlConnectOptions, poolOptions);
        }

        client
                .query("SELECT * FROM url")
                .execute(ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> result = ar.result();
                        final String resultString = "Got " + result.size() + " rows ";
                        System.out.println(resultString);
                        rc.response().end(helloMessage + ">>>" + resultString); // (2)
                    } else {
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }

                    // Now close the pool
                    client.close();
                });

        return "";
    }

}
