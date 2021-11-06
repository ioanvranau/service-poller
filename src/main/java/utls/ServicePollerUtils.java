package utls;

import io.vertx.mysqlclient.MySQLConnectOptions;
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

    public static void print(String message) {
        System.out.println(message);
    }

    public static boolean validUrl(String urlPath) {
        if (urlPath.startsWith("/") || urlPath.startsWith("\\")) {
            return false;
        }
        return true;
    }
}
