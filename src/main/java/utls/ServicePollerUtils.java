package utls;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class ServicePollerUtils {
    public static SqlConnectOptions getConnectionOptionsFromCommandLineArgumentsIfApplicable(String[] args, SqlConnectOptions sqlConnectOptions) {
        if (args.length > 3 && args[1] != null && !args[1].equals("")) {
            String host = args[1];
            if (args[2] != null && !args[2].equals("")) {
                String username = args[2];
                if (args[3] != null && !args[3].equals("")) {
                    String password = args[3];
                    sqlConnectOptions = getSqlConnectOptions(host, username, password, "dc1h7vucrk45rm", true);
                    System.out.println("Connection options successfully received " + host +  username + password);
                }
            }
        }
        return sqlConnectOptions;
    }

    public static SqlConnectOptions getSqlConnectOptions(String host, String user, String password, String database, boolean isRemotePostgress) {
        SqlConnectOptions connectOptions;
        if (isRemotePostgress) {
            connectOptions = new PgConnectOptions()
                    .setHost(host)
                    .setUser(user)
                    .setPort(5432)
                    .setDatabase(database)
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
}
