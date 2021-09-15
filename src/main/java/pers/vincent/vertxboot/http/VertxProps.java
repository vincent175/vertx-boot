package pers.vincent.vertxboot.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Data
@Component
@ConfigurationProperties(prefix = "vertx")
public class VertxProps {
    private int corePoolSize = Runtime.getRuntime().availableProcessors();

    private int workPoolSize = Runtime.getRuntime().availableProcessors() * 2;

    private int verticleCount = 1;

    private Server server;

    private List<RouteInfo> routeInfos;

    @Data
    public static class Server {
        /**
         * The port that the http service listens to
         */
        private int port;
        /**
         * The root path of the project
         */
        private String rootPath;
    }
}
