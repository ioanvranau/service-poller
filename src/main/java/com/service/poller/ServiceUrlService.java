package com.service.poller;

import java.util.logging.Logger;
import com.service.poller.model.ServiceUrl;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import static utls.ServicePollerUtils.print;
import static utls.ServicePollerUtils.validUrl;

public class ServiceUrlService {
    private final static Logger LOGGER = Logger.getLogger(ServiceUrlService.class.getName());
    private final ServiceUrlRepository serviceUrlRepository;

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

    public void createRoutesForAlreadyAddedUrls(Router router) {
        this.serviceUrlRepository.findAll()
                .onSuccess(
                        data -> {
                            for (ServiceUrl serviceUrl : data) {
                                Route messageRoute = router.get("/api/" + serviceUrl.getPath());
                                messageRoute.handler(newRc -> {
                                    newRc.response().end(serviceUrl.displayStatus());
                                });
                            }
                            LOGGER.info("Routes for existing urls has been created");
                        });
    }

    public void createAndPersistNewService(Router router, RoutingContext routingContext, String name, String path) {
        if (!validUrl(path)) {
            routingContext.response().end("Invalid url path for" + name + " and path ");
            return;
        }
        this.serviceUrlRepository.findByPath(path)
                .onSuccess(
                        data -> {
                            if (data != null) {
                                routingContext.response().end("Already exists");
                                return;
                            }
                            Route dynamicRoute = router.get("/api/" + path);
                            ServiceUrl serviceUrl = new ServiceUrl();
                            serviceUrl.setName(name);
                            serviceUrl.setPath(path);
                            serviceUrlRepository.save(serviceUrl).onSuccess(
                                    id -> {
                                        dynamicRoute.handler(rc -> rc.response().end(serviceUrl.displayStatus()));
                                        routingContext.response().end("Service added with name" + name + " and path " + path);
                                    }).onFailure(throwable -> {
                                routingContext.response().end("Cannot save service");
                                print(throwable.getMessage());
                            });
                        }).onFailure(throwable -> {
                            print(throwable.getMessage());
                            routingContext.response().end("Cannot save service");
                        }
                );
    }
}
