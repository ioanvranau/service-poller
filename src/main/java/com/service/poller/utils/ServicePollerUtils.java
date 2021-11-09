package com.service.poller.utils;

import org.apache.commons.validator.routines.UrlValidator;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class ServicePollerUtils {

    public static SqlConnectOptions getSqlConnectOptions() {
        String host = "db4free.net";
        String user = "servicepollerus";
        String password = "servicepollerpw";
        String database = "servicepoller";
        final int port = 3306;
        return new MySQLConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password);
    }

    public static Pool getSqlClient(Vertx vertx, SqlConnectOptions sqlConnectOptions) {
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        Pool client;
        client = MySQLPool.pool(vertx, (MySQLConnectOptions) sqlConnectOptions, poolOptions);
        return client;
    }

    public static boolean validName(String name) {
        return name != null && name.length() != 0 && name.length() < 100;
    }

    public static boolean validUrl(String urlPath) {

        if (urlPath == null || urlPath.length() == 0 || urlPath.startsWith("/") || urlPath.startsWith("/api")
                || urlPath.startsWith("\\") || urlPath.length() >= 100) {
            return false;
        }
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid("https://a.com/" + urlPath);
    }
}
