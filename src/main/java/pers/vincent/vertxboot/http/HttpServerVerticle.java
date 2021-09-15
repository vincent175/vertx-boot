package pers.vincent.vertxboot.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Slf4j
public class HttpServerVerticle extends AbstractVerticle {

    public static ApplicationContext springContext;

    @Override
    public void start(Promise<Void> startPromise) {
        VertxProps config = springContext.getBean(VertxProps.class);
        List<RouteInfo> routeInfos = config.getRouteInfos();

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        addHandlers(router, routeInfos);

        server.requestHandler(router)
                .listen(config.getServer().getPort(), result -> {
                    if (result.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(result.cause());
                    }
                });
    }


    private void addHandlers(Router router, List<RouteInfo> routeInfos) {
        if (routeInfos != null) {
            routeInfos.forEach(routeInfo -> {
                Route route = router.route(routeInfo.getPath());

                route.produces(routeInfo.getProduce());
                Arrays.stream(routeInfo.getHttpMethods())
                        .map(httpMethod -> new io.vertx.core.http.HttpMethod(httpMethod.name()))
                        .forEach(httpMethod -> route.method(httpMethod));

                Handler<RoutingContext> handler = context -> {
                    long beginTime = System.currentTimeMillis();
                    try {
                        Object result = routeInfo.getMethod().invoke(routeInfo.getTarget(), ParameterConverter.convertParams(context, routeInfo.getMethod()));
                        HttpServerVerticle.this.response(context, routeInfo.getProduce(), result);
                        log.info("execute{" + route.getPath() + "} takes " + (System.currentTimeMillis() - beginTime) + "ms");
                    } catch (Exception e) {
                        log.error("path:" + routeInfo.getPath() + ",method:" + routeInfo.getMethod().getName() + ",execute error", e);
                    }
                };

                if (routeInfo.isBlocked()) {
                    route.blockingHandler(handler).failureHandler(context -> {
                        // TODO
                    });
                } else {
                    route.handler(handler).failureHandler(context -> {
                        // TODO
                    });
                }
            });
        }
    }

    private void response(RoutingContext routingContext, String produce, Object result) {
        if (result == null) {
            result = "";
        }
        HttpServerResponse response = routingContext.response();
        switch (produce) {
            case "application/json":
                String jsonResult;
                try {
                    jsonResult = JSON.toJSONString(result);
                } catch (Exception e) {
                    jsonResult = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
                }
                response.setStatusCode(200)
                        .putHeader("Content-Length", String.valueOf(jsonResult.getBytes(StandardCharsets.UTF_8).length))
                        .putHeader("Content-Type", produce)
                        .end(jsonResult);
                break;
            case "file":
                break;
            default:

        }
    }
}
