package com.service.poller;

import java.util.Map;
import java.util.logging.Logger;
import com.service.poller.model.ServiceUrl;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import utils.ServiceException;
import static utils.ServicePollerUtils.print;
import static utils.ServicePollerUtils.validUrl;

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
                                createNewRoute(router, serviceUrl);
                            }
                            LOGGER.info("Routes for existing urls has been created");
                        });
    }

    private void createNewRoute(Router router, ServiceUrl serviceUrl) {
        Route messageRoute = router.get("/api/" + serviceUrl.getPath());
        messageRoute.handler(newRc -> {
            newRc.response().end(serviceUrl.getStatus());
        });
    }

    public void createAndPersistNewService(Router router, RoutingContext routingContext) {
        final ServiceUrl newServiceUrl = routingContext.getBodyAsJson().mapTo(ServiceUrl.class);
        final String name = newServiceUrl.getName();
        final String path = newServiceUrl.getPath();
        if (!validUrl(path)) {
            routingContext.response().end("Invalid url path for" + name + " and path ");
            return;
        }
        this.serviceUrlRepository.findByPath(path)
                .onSuccess(
                        data -> {
                            if (data != null) {
                                routingContext.response().end(Json.encode(new ServiceException("Service already exists with this path: " + path)));
                                return;
                            }
                            ServiceUrl serviceUrl = new ServiceUrl();
                            serviceUrl.setName(name);
                            serviceUrl.setPath(path);
                            serviceUrlRepository.save(serviceUrl).onSuccess(
                                    id -> {
                                        createNewRoute(router, serviceUrl);
                                        routingContext.response().end(Json.encode(newServiceUrl));
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

    public void delete(RoutingContext rc) {
        Map<String, String> params = rc.pathParams();
        String path = params.get("path");

        this.serviceUrlRepository.findByPath(path)
                .compose(
                        post -> this.serviceUrlRepository.deleteByPath(path)
                )
                .onSuccess(
                        data -> rc.response().setStatusCode(204).end()
                )
                .onFailure(
                        throwable -> rc.fail(404, throwable)
                );

    }
}
