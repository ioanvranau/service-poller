package com.service.poller;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.service.poller.model.ServiceUrl;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;

public class ServiceUrlRepository {
    private static Function<Row, ServiceUrl> MAPPER = (row) ->
            ServiceUrl.of(
                    row.getString("name"),
                    row.getString("path")
            );

    private final Pool client;

    private ServiceUrlRepository(Pool client) {
        this.client = client;
    }

    public static ServiceUrlRepository create(Pool client) {
        return new ServiceUrlRepository(client);
    }

    public Future<List<ServiceUrl>> findAll() {
        return client.query("SELECT * FROM url")
                .execute()
                .map(rs -> StreamSupport.stream(rs.spliterator(), false)
                        .map(MAPPER)
                        .collect(Collectors.toList())
                );
    }

}
