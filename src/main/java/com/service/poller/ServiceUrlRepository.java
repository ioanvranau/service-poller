package com.service.poller;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.service.poller.model.ServiceUrl;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

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

    public Future<ServiceUrl> findByPath(String path) {

        return client.preparedQuery("SELECT * FROM url WHERE path=?").execute(Tuple.of(path))
                .map(RowSet::iterator)
                .map(iterator -> {
                            if (iterator.hasNext()) {
                                return MAPPER.apply(iterator.next());
                            }
                            return null;
                        }
                );
    }

    public Future<Integer> save(ServiceUrl serviceUrl) {
        String sql = "INSERT INTO url (name, path) VALUES (?, ?)";
        return client.preparedQuery(sql).execute(Tuple.of(serviceUrl.getName(), serviceUrl.getPath()))
                .map(SqlResult::rowCount);
    }
}
