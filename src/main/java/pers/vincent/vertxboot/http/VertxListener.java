package pers.vincent.vertxboot.http;

import com.alibaba.fastjson.JSON;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pers.vincent.vertxboot.http.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Slf4j
@Component
public class VertxListener implements ApplicationListener<ApplicationStartedEvent> {

    Throwable bootError;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        VertxProps vertxProps = context.getBean(VertxProps.class);
        log.info("vertx config:{}", JSON.toJSONString(vertxProps));
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RequestMapping.class);
        Set<Class> routeClasses = new HashSet<>();
        beansWithAnnotation.forEach((name,bean) -> routeClasses.add(bean.getClass()));
        List<RouteInfo> routeInfos = RouteInfo.findRouteInfos(routeClasses);
        routeInfos.forEach(routeInfo -> routeInfo.setTarget(context.getBean(routeInfo.getMethod().getDeclaringClass())));
        vertxProps.setRouteInfos(routeInfos);
        deploy(context, vertxProps);
    }

    @SneakyThrows
    private void deploy(ApplicationContext context, VertxProps vertxProps) {
        // create vertx
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(vertxProps.getCorePoolSize())
                .setWorkerPoolSize(vertxProps.getWorkPoolSize())
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
                .setMaxEventLoopExecuteTime(vertxProps.getMaxEventLoopExecuteTime())
                .setMaxWorkerExecuteTimeUnit(TimeUnit.MILLISECONDS)
                .setMaxWorkerExecuteTime(vertxProps.getMaxWorkerExecuteTime())
                .setWarningExceptionTimeUnit(TimeUnit.SECONDS)
                .setWarningExceptionTime(30L);
        Vertx vertx = Vertx.vertx(vertxOptions);

        DeploymentOptions depOptions = new DeploymentOptions();
        depOptions.setInstances(vertxProps.getVerticleCount());

        HttpServerVerticle.springContext = context;

        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(HttpServerVerticle.class, depOptions, ar -> {
            if (!ar.succeeded()) {
                log.error("err:", ar.cause());
                this.bootError = ar.cause();
                latch.countDown();
                return;
            }
            log.info("http verticle deployed successfully at {}", vertxProps.getServer().getPort());
            latch.countDown();
        });

        latch.await();
        if (null != bootError) {
            // close vertx
            vertx.close();
            // throw exception to cause the spring service to fail to start
            throw bootError;
        }
    }
}
