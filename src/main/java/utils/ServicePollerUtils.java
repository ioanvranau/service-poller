package utils;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class ServicePollerUtils {

    public static SqlConnectOptions getSqlConnectOptions() {
        String host = "localhost";
        String user = "root";
        String password = "admin";
        String database = "servicepoller";
        final int port = 3306;
        return new MySQLConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password);
    }

    public static void print(String message) {
        System.out.println(message);
    }

    public static boolean validUrl(String urlPath) {
        if (urlPath == null || urlPath.startsWith("/") || urlPath.startsWith("\\")) {
            return false;
        }
        return true;
    }
}
