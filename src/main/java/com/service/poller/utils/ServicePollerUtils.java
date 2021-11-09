package com.service.poller.utils;

import org.apache.commons.validator.routines.UrlValidator;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class ServicePollerUtils {

    public static SqlConnectOptions getSqlConnectOptionsFromArgsIfApplicable(String... args) {
        int dbPort = 3306;
        String host = "db4free.net";
        if (args != null && args.length > 1) {
            host = args[1];
            if (args.length > 2) {
                try {
                    dbPort = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    // use default
                }
            }
        }
        String user = "servicepollerus";
        String password = "servicepollerpw";
        String database = "servicepoller";
        return new MySQLConnectOptions()
                .setPort(dbPort)
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
