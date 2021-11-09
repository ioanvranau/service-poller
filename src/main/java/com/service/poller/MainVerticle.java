package com.service.poller;

import java.util.logging.Logger;
import com.service.poller.repository.ServiceStatsRepository;
import com.service.poller.repository.ServiceUrlRepository;
import com.service.poller.service.UrlService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnectOptions;
import static com.service.poller.utils.ServicePollerUtils.getSqlClient;
import static com.service.poller.utils.ServicePollerUtils.getSqlConnectOptionsFromArgsIfApplicable;

public class MainVerticle extends AbstractVerticle {

    private final static Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

    final Pool sqlClient;
    private final int port;

    public MainVerticle(int port, Pool sqlClient) {
        this.port = port;
        this.sqlClient = sqlClient;
    }

    public static void main(String[] args) {
        int webAppPort = 5000;
        if (args != null && args.length > 0) {
            try {
                webAppPort = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // use default
            }
        }
        SqlConnectOptions sqlConnectOptions = getSqlConnectOptionsFromArgsIfApplicable(args);
        Vertx vertx = Vertx.vertx();
        Pool sqlClient = getSqlClient(vertx, sqlConnectOptions);
        vertx.deployVerticle(new MainVerticle(webAppPort, sqlClient));
    }


    @Override
    public void start() throws Exception {
        LOGGER.info("Starting HTTP server...");
        final ServiceUrlRepository serviceUrlRepository = ServiceUrlRepository.create(sqlClient);
        final ServiceStatsRepository serviceStatsRepository = ServiceStatsRepository.create(sqlClient);
        final UrlService urlService = UrlService.create(serviceUrlRepository, serviceStatsRepository);

        final Router router = createRoutes(urlService);

        vertx.createHttpServer().requestHandler(router).listen(port)
                .onSuccess(server -> LOGGER.info("HTTP server started on port " + server.actualPort()))
                .onFailure(event -> LOGGER.severe("Failed to start HTTP server:" + event.getMessage()));
    }

    private Router createRoutes(UrlService urlService) {
        final Router router = Router.router(vertx);
        router.get().handler(StaticHandler.create());

        router.get("/api/url").produces("application/json").handler(urlService::all);
        router.delete("/api/url").consumes("application/json")
                .handler(BodyHandler.create())
                .handler(urlService::delete);

        urlService.createRoutesForAlreadyAddedUrls(router);

        router.post("/api/url").consumes("application/json")
                .handler(BodyHandler.create())
                .handler(routingContext -> urlService.createAndPersistNewService(router, routingContext));

        router.put("/api/url").consumes("application/json")
                .handler(BodyHandler.create())
                .handler(urlService::update);

        router.post("/api/urlstats").consumes("application/json")
                .handler(BodyHandler.create())
                .handler(urlService::saveStats);
        return router;
    }
}
