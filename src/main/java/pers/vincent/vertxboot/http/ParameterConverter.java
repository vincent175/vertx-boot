package pers.vincent.vertxboot.http;

import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pers.vincent.vertxboot.http.annotation.Param;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Slf4j
public class ParameterConverter {

    @SneakyThrows
    public static Object[] convertParams(RoutingContext routingContext, Method method) {

        if (method.getParameterCount() == 0) {
            return new Object[0];
        }
        Object[] paramsValues = new Object[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        List<String> paramNames = getParamNames(method);

        Map<String, Map<String, String>> params;

        MultiMap formAttributes = routingContext.request().formAttributes().addAll(routingContext.request().params());
        log.debug("request params:{}", JSON.toJSONString(formAttributes.entries()));
        params = group(formAttributes);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);
            String name = param == null || StringUtils.isBlank(param.name()) ? paramNames.get(i) : param.name();
            boolean required = param != null ? param.required() : false;

            if (HttpServerRequest.class.isAssignableFrom(parameter.getType())) {
                paramsValues[i] = routingContext.request();
            } else if (HttpServerResponse.class.isAssignableFrom(parameter.getType())) {
                paramsValues[i] = routingContext.response();
            } else if (RoutingContext.class.isAssignableFrom(parameter.getType())) {
                paramsValues[i] = routingContext;
            } else {
                if (parameter.getType().isPrimitive()
                        || String.class.isAssignableFrom(parameter.getType())
                        || Number.class.isAssignableFrom(parameter.getType())
                        || Boolean.class.isAssignableFrom(parameter.getType())
                        || BigDecimal.class.isAssignableFrom(parameter.getType())) {
                    paramsValues[i] = params.get("") == null ? null
                            : DefaultTypeConverter.convertValue(params.get("").get(name), parameter.getType());
                } else {
                    paramsValues[i] = setValues4Object(parameter.getType(), name, params);
                }
            }
            if (required && paramsValues[i] == null) {
                throw new NullPointerException(" Parameter " + name + " must not be null.");
            }
        }
        return paramsValues;
    }

    private static List<String> getParamNames(Method method) {
        List<String> paramNames = new ArrayList<>();
        Class<?> clazz = method.getDeclaringClass();
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get(clazz.getName());
            CtMethod cm = cc.getDeclaredMethod(method.getName());
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            if (attr != null) {
                boolean isStatic = Modifier.isStatic(cm.getModifiers());
                boolean begin = false;
                int j = cm.getParameterTypes().length;
                for (int i = 0; i < 100; i++) {
                    if (j == 0)
                        break;
                    String paramName = attr.variableName(i);
                    if (paramName == null)
                        break;
                    if (paramName.equals("this")) {
                        begin = true;
                        continue;
                    }
                    if (begin || isStatic) {
                        paramNames.add(paramName);
                        j--;
                    }
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return paramNames;
    }

    public static Map<String, Map<String, String>> group(MultiMap params) {
        Map<String, Map<String, String>> paramsGroup = new HashMap<>();
        params.entries().forEach(entry -> {
            String key = entry.getKey();
            String prefix = key.substring(0, Math.max(0, key.lastIndexOf(".")));
            List<String> prefixs = getPrefixs(key);
            String attrName = key.substring(key.lastIndexOf(".") + 1);

            prefixs.forEach(px -> {
                if (!px.equals(prefix)) {
                    String refName = getRefName(px, key);
                    Map<String, String> map = paramsGroup.get(px);
                    if (map == null) {
                        map = new HashMap<>();
                        paramsGroup.put(px, map);
                    }
                    if (!map.containsKey(refName)) {
                        map.put(refName, "$ref");
                    }
                }
            });

            Map<String, String> map = paramsGroup.get(prefix);
            if (map == null) {
                map = new HashMap<>();
                paramsGroup.put(prefix, map);
            }
            map.put(attrName, entry.getValue());
        });
        return paramsGroup;
    }

    private static List<String> getPrefixs(String key) {
        List<String> prefixs = new ArrayList<>();
        String[] keys = key.split("\\.");
        for (int i = 1; i < keys.length; i++) {
            String prefix = "";
            for (int j = 0; j < i; j++) {
                prefix = prefix + keys[j] + ".";
            }
            if (prefix.length() > 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            prefixs.add(prefix);
        }
        return prefixs;
    }

    private static String getRefName(String px, String key) {
        String remainKey = key.substring(px.length() + 1);
        if (remainKey.contains(".")) {
            return remainKey.substring(0, remainKey.indexOf("."));
        }
        return remainKey;
    }

    private static <T> T setValues4Object(Class<T> clazz, String prefix, Map<String, Map<String, String>> params)
            throws InstantiationException, IllegalAccessException {
        Map<String, String> fieldAndValues = params.get(prefix);
        if (fieldAndValues != null && !fieldAndValues.isEmpty()) {
            T obj = clazz.newInstance();
            Iterator<String> iterator = fieldAndValues.keySet().iterator();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                try {
                    Field field = getField(clazz, fieldName);
                    field.setAccessible(true);
                    if (field.getType().isPrimitive() || String.class.isAssignableFrom(field.getType())
                            || Number.class.isAssignableFrom(field.getType())
                            || Date.class.isAssignableFrom(field.getType())
                            || Boolean.class.isAssignableFrom(field.getType())) {
                        field.set(obj,
                                DefaultTypeConverter.convertValue(fieldAndValues.get(fieldName), field.getType()));
                    } else {
                        if (params.get(prefix + "." + field.getName()) != null) {
                            Object instance = setValues4Object(field.getType(), prefix + "." + field.getName(), params);
                            field.set(obj, instance);
                        }
                    }
                } catch (Exception e) {
                    log.debug("set value for {} field {} failed!", obj.getClass(), fieldName);
                }
            }
            return obj;
        } else {
            return null;
        }
    }

    private static <T> Field getField(Class<T> clazz, String fieldName) throws Exception {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getSuperclass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException | SecurityException e1) {
                throw e1;
            }
        } catch (SecurityException e) {
            throw e;
        }
        return field;
    }
}
