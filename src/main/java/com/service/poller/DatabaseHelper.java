package com.service.poller;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import static utls.ServicePollerUtils.print;

public class DatabaseHelper {
    public static Pool getSqlClient(Vertx vertx, SqlConnectOptions sqlConnectOptions) {
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        Pool client;
        print("Creating sql pool" + sqlConnectOptions);
        client = MySQLPool.pool(vertx, (MySQLConnectOptions) sqlConnectOptions, poolOptions);
        return client;
    }
}
