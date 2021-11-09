package com.service.poller.service;

import java.net.HttpURLConnection;
import java.util.Random;
import java.util.logging.Logger;
import com.service.poller.model.ServiceUrl;
import com.service.poller.repository.ServiceStatsRepository;
import com.service.poller.repository.ServiceUrlRepository;
import com.service.poller.utils.ServiceException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import static com.service.poller.utils.ServicePollerUtils.validName;
import static com.service.poller.utils.ServicePollerUtils.validUrl;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;

public class UrlService {
    private final static Logger LOGGER = Logger.getLogger(UrlService.class.getName());
    private static final Random RANDOM = new Random();
    private final static String[] STATUS_LIST = new String[]{"OK", "FAIL"};
    private final ServiceUrlRepository serviceUrlRepository;
    private final ServiceStatsRepository serviceStatsRepository;

    private UrlService(ServiceUrlRepository serviceUrlRepository, ServiceStatsRepository serviceStatsRepository) {
        this.serviceUrlRepository = serviceUrlRepository;
        this.serviceStatsRepository = serviceStatsRepository;
    }

    public static String getRandomValue() {
        final int index = RANDOM.nextInt(STATUS_LIST.length);
        return STATUS_LIST[index];
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
                .onSuccess(data -> routingContext.fail(HttpURLConnection.HTTP_CONFLICT, new ServiceException("Service url already exists")))
                .onFailure(throwable -> saveServiceUrl(router, routingContext, newServiceUrl.getName(), newServiceUrl.getPath())
                );
    }

    private void saveServiceUrl(Router router, RoutingContext routingContext, String name, String path) {
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setName(name);
        serviceUrl.setPath(path);
        serviceUrlRepository.save(serviceUrl).onSuccess(
                id -> {
                    createNewRoute(router, serviceUrl);
                    routingContext.response().end(Json.encode(serviceUrl));
                }).onFailure(throwable -> {
            routingContext.response().end("Cannot save service");
            LOGGER.severe(throwable.getMessage());
        });
    }

    public void delete(RoutingContext rc) {
        final ServiceUrl newServiceUrl = rc.getBodyAsJson().mapTo(ServiceUrl.class);
        final String path = newServiceUrl.getPath();

        this.serviceUrlRepository.findByPath(path)
                .compose(
                        serviceUrl -> this.serviceUrlRepository.deleteByPath(path)
                )
                .onSuccess(
                        data -> rc.response().setStatusCode(200).end()
                )
                .onFailure(
                        throwable -> rc.fail(404, throwable)
                );

    }


    public void update(RoutingContext rc) {
        final ServiceUrl newOrExistingServiceUrl = rc.getBodyAsJson().mapTo(ServiceUrl.class);
        this.serviceUrlRepository.findByPath(newOrExistingServiceUrl.getPath())
                .compose(
                        serviceUrl -> {
                            serviceUrl.setName(newOrExistingServiceUrl.getName());
                            return this.serviceUrlRepository.update(serviceUrl);
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
                id -> routingContext.response().end()).onFailure(throwable -> {
            routingContext.response().end("Cannot save stats");
            LOGGER.severe(throwable.getMessage());
        });
    }

}
