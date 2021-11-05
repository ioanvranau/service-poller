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
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

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
        String password = "";
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // use default
            }
            if(args[1] != null && !args[1].equals("")) {

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
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("sql11.freesqldatabase.com")
                .setDatabase("sql11449016")
                .setUser("sql11449016")
                .setPassword("4RqctZVYkH");

// Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

// Create the client pool
        final String username = "ckwvpptcaufjgn";
        final String passworkd = "21cd747b69a92f3d39871c8b62cd9ff22dd8af3773d15948247dfe1ed520d763";
        final String host = "ec2-54-73-110-26.eu-west-1.compute.amazonaws.com:5432";
        final String databse = "dc1h7vucrk45rm";
        SqlClient client = PgPool.client(vertx, "postgres://" + username + ":" + passworkd + "@" + host + "/" + databse + "?sslmode=require");
        //SqlClient client = MySQLPool.pool(vertx, connectionUri);

// A simple query
        client
                .query("SELECT * FROM urls")
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
