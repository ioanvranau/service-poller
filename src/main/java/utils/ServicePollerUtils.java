package utils;

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
//        String host = "localhost";
//        String user = "root";
//        String password = "admin";
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

    public static boolean validUrl(String urlPath) {
        if (urlPath == null || urlPath.length() == 0 || urlPath.startsWith("/") || urlPath.startsWith("\\")) {
            return false;
        }
        return true;
    }
}
