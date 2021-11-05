package com.service.poller;

import java.util.Random;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class DatabaseHelper {
    private static final Random RANDOM = new Random();
    private final static String[] STATUS_LIST = new String[]{"OK", "FAIL"};
    public static void createRoutesForAlreadyAddedUrls(Router router, Pool sqlClient) {

        sqlClient.getConnection().compose(conn -> conn.query("SELECT * from url")
                .execute()).onComplete(ar -> {
            if (ar.succeeded()) {
                RowSet<Row> result = ar.result();
                for (Row row : result) {
                    final String urlName = row.getValue("name").toString();
                    final String urlPath = row.getValue("path").toString();
                    Route messageRoute = router.get("/api/" + urlPath);
                    messageRoute.handler(rc -> {
                        rc.response().end("My name is: " + urlName + " and I am: " + getRandomValue());
                    });
                }

            } else {
                System.out.println("Failure: " + ar.cause().getMessage());
            }
        });

    }

    public static String getRandomValue() {
        final int index = RANDOM.nextInt(STATUS_LIST.length);
        return STATUS_LIST[index];
    }

    public static void createAndPersistNewService(Router router, RoutingContext routingContext, String urlName, String urlPath, Pool sqlClient) {
        Route dynamicRoute = router.get("/api/" + urlPath);
        dynamicRoute.handler(rc -> {
            rc.response().end("My name is: " + urlName + " and I am: " + getRandomValue());
        });
        sqlClient.getConnection().compose(conn -> conn.preparedQuery("SELECT * from url where path=?")
                .execute(Tuple.of(urlPath))).onComplete(ar -> {
            if (ar.succeeded()) {
                RowSet<Row> rows = ar.result();
                final int size = rows.size();
                if (size > 0) {
                    routingContext.response().end("Cannot add service with name" + urlName + " and path " + urlPath + " Already exists!");
                } else {
                    insertNewService(routingContext, urlName, urlPath, sqlClient);
                }
            } else {
                System.out.println("Failure: " + ar.cause().getMessage());
                routingContext.response().end("Service failed to add");
            }
        });

    }

    public static void insertNewService(RoutingContext routingContext, String urlName, String urlPath, Pool sqlClient) {
        String sql = "INSERT INTO url (name, path) VALUES (?, ?)";
        sqlClient.getConnection().compose(conn -> conn.preparedQuery(sql)
                .execute(Tuple.of(urlName, urlPath))).onComplete(arInsert -> {
            if (arInsert.succeeded()) {
                routingContext.response().end("Service added with name" + urlName + " and path " + urlPath);
            } else {
                System.out.println("Failure: " + arInsert.cause().getMessage());
                routingContext.response().end("Service failed to add");
            }
        });
    }
}
