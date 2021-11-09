package com.service.poller;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import com.service.poller.model.ServiceUrl;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import utils.ServiceException;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static utils.ServicePollerUtils.validName;
import static utils.ServicePollerUtils.validUrl;

public class UrlService {
    private final static Logger LOGGER = Logger.getLogger(UrlService.class.getName());
    private final ServiceUrlRepository serviceUrlRepository;
    private final ServiceStatsRepository serviceStatsRepository;
    private static final Random RANDOM = new Random();
    private final static String[] STATUS_LIST = new String[]{"OK", "FAIL"};

    public static String getRandomValue() {
        final int index = RANDOM.nextInt(STATUS_LIST.length);
        return STATUS_LIST[index];
    }
    private UrlService(ServiceUrlRepository serviceUrlRepository, ServiceStatsRepository serviceStatsRepository) {
        this.serviceUrlRepository = serviceUrlRepository;
        this.serviceStatsRepository = serviceStatsRepository;
    }

    public static UrlService create(ServiceUrlRepository serviceUrlRepository, ServiceStatsRepository serviceStatsRepository) {
        return new UrlService(serviceUrlRepository, serviceStatsRepository);
    }

    public void all(RoutingContext rc) {
        LOGGER.info("Getting all urls");
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
        Route messageRoute = router.get("/" + serviceUrl.getPath());
        messageRoute.handler(newRc -> {
            LOGGER.info("get status for service " + serviceUrl.getPath());
            newRc.response().end(getRandomValue());
        });
    }



    public void createAndPersistNewService(Router router, RoutingContext routingContext) {
        final ServiceUrl newServiceUrl = routingContext.getBodyAsJson().mapTo(ServiceUrl.class);
        final String name = newServiceUrl.getName();
        final String path = newServiceUrl.getPath();
        if (!validUrl(path) || !validName(name)) {
            routingContext.fail(HTTP_NOT_ACCEPTABLE);
            return;
        }
        this.serviceUrlRepository.findByPath(path)
                .onSuccess(
                        data -> {
                            routingContext.fail(HttpURLConnection.HTTP_CONFLICT, new ServiceException("Service url already exists"));
                        })
                .onFailure(throwable -> {
                            saveServiceUrl(router, routingContext, newServiceUrl.getName(), newServiceUrl.getPath());
                        }
                );
    }

    private Future<Integer> saveServiceUrl(Router router, RoutingContext routingContext, String name, String path) {
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setName(name);
        serviceUrl.setPath(path);
        return serviceUrlRepository.save(serviceUrl).onSuccess(
                id -> {
                    createNewRoute(router, serviceUrl);
                    routingContext.response().end(Json.encode(serviceUrl));
                }).onFailure(throwable -> {
            routingContext.response().end("Cannot save service");
            LOGGER.severe(throwable.getMessage());
        });
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


    public void update(Router router, RoutingContext rc) {
        final ServiceUrl newOrExistingServiceUrl = rc.getBodyAsJson().mapTo(ServiceUrl.class);
        this.serviceUrlRepository.findByPath(newOrExistingServiceUrl.getPath())

                .compose(
                        serviceUrl -> {
                            if (serviceUrl != null) {
                                serviceUrl.setName(newOrExistingServiceUrl.getName());
                                return this.serviceUrlRepository.update(serviceUrl);
                            } else {
                                return saveServiceUrl(router, rc, newOrExistingServiceUrl.getName(), newOrExistingServiceUrl.getPath());
                            }
                        }
                )
                .onSuccess(
                        data -> rc.response().setStatusCode(204).end()
                )
                .onFailure(
                        throwable -> rc.fail(404, throwable)
                );

    }

    public void saveStats(RoutingContext routingContext) {
        final ServiceUrl serviceUrl = routingContext.getBodyAsJson().mapTo(ServiceUrl.class);
        serviceStatsRepository.save(serviceUrl).onSuccess(
                id -> {
                    routingContext.response().end();
                }).onFailure(throwable -> {
            routingContext.response().end("Cannot save stats");
            LOGGER.severe(throwable.getMessage());
        });
    }

}
