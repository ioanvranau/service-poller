package com.service.poller;

import com.service.poller.model.ServiceUrl;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

public class ServiceStatsRepository {

    private final Pool client;

    private ServiceStatsRepository(Pool client) {
        this.client = client;
    }

    public static ServiceStatsRepository create(Pool client) {
        return new ServiceStatsRepository(client);
    }

    public Future<Integer> save(ServiceUrl serviceUrl) {
        String sql = "INSERT INTO url_stats (name, path, status) VALUES (?, ?, ?)";
        return client.preparedQuery(sql).execute(Tuple.of(serviceUrl.getName(), serviceUrl.getPath(), serviceUrl.getStatus()))
                .map(SqlResult::rowCount);
    }
}
