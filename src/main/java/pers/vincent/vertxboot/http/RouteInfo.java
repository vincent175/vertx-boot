package pers.vincent.vertxboot.http;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.vincent.vertxboot.http.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Data
public class RouteInfo {
    /**
     * Http request path
     */
    private String path;
    /**
     * Request allowed method type
     */
    private HttpMethod[] httpMethods;
    /**
     * Method of requesting path mapping
     */
    private Method method;
    /**
     * The Object used to execute the method
     */
    private Object target;
    /**
     * Identify whether the method called is blocked
     */
    private boolean blocked = true;
    /**
     * Response content type
     */
    private String produce;

    public static List<RouteInfo> findRouteInfos(Set<Class> classes) {
        List<RouteInfo> routeInfos = new ArrayList<>();
        if (classes == null) {
            return routeInfos;
        }
        classes.forEach(aClass -> routeInfos.addAll(findRouteInfos(aClass)));
        return routeInfos;
    }

    private static List<RouteInfo> findRouteInfos(Class<?> aClass) {
        List<RouteInfo> routeInfos = new ArrayList<>();
        if (aClass == null) {
            return routeInfos;
        }
        RequestMapping classMapping = aClass.getAnnotation(RequestMapping.class);
        RouteInfo classRouteInfo = new RouteInfo();
        if (classMapping != null) {
            String path = null;
            if (StringUtils.isNotBlank(classMapping.value())) {
                path = classMapping.value();
            } else if (StringUtils.isNotBlank(classMapping.path())) {
                path = classMapping.path();
            } else if (StringUtils.isNotBlank(classMapping.name())) {
                path = classMapping.name();
            }
            classRouteInfo.path = path;
            classRouteInfo.httpMethods = classMapping.method();
        }
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            if (method.getName().contains("lambda$")) {
                continue;
            }
            RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
            if (methodMapping != null) {
                RouteInfo routeInfo = new RouteInfo();
                String path = null;
                if (StringUtils.isNotBlank(methodMapping.value())) {
                    path = methodMapping.value();
                } else if (StringUtils.isNotBlank(methodMapping.path())) {
                    path = methodMapping.path();
                } else if (StringUtils.isNotBlank(methodMapping.name())) {
                    path = methodMapping.name();
                }
                routeInfo.path = classRouteInfo.path + path;
                routeInfo.httpMethods = methodMapping.method() == null || methodMapping.method().length == 0 ?
                        classRouteInfo.httpMethods : methodMapping.method();
                String[] produces = methodMapping.produces();
                routeInfo.produce = StringUtils.join(produces, ";");
                routeInfo.blocked = methodMapping.async();
                routeInfo.method = method;
                routeInfos.add(routeInfo);
            }
        }
        return routeInfos;
    }
}
