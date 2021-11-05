package com.service.poller;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class ServiceUrlService {
    private ServiceUrlRepository serviceUrlRepository;

    private ServiceUrlService(ServiceUrlRepository serviceUrlRepository) {
        this.serviceUrlRepository = serviceUrlRepository;
    }

    public static ServiceUrlService create(ServiceUrlRepository serviceUrlRepository) {
        return new ServiceUrlService(serviceUrlRepository);
    }

    public void all(RoutingContext rc) {
        this.serviceUrlRepository.findAll()
                .onSuccess(
                        data -> rc.response().end(Json.encode(data))
                );
    }
}
