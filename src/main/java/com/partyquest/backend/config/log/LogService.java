package com.partyquest.backend.config.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class LogService {
    public String getUrl(Method method, Class<? extends Annotation> annotationClass, String baseUrl) {
        Annotation annotation = method.getAnnotation(annotationClass);
        String[] value;
        String httpMethod;
        try {
            value = (String[]) annotationClass.getMethod("value").invoke(annotation);
            httpMethod = (annotationClass.getSimpleName().replace("Mapping", "")).toUpperCase();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            return null;
        }
        return String.format("%s %s%s", httpMethod, baseUrl, value.length > 0 ? value[0] : "");
    }

    public String getRequestUrl(JoinPoint joinPoint, Class<?> cls) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return "TEST CODE";
        }
        String baseUrl = requestMapping.value()[0];


        return Stream.of(GetMapping.class, PutMapping.class, PostMapping.class
                        , PatchMapping.class,DeleteMapping.class,RequestMapping.class)
                .filter(method::isAnnotationPresent)
                .map(mappingClass -> getUrl(method,mappingClass,baseUrl))
                .findFirst().orElse(null);
    }
    public Map<?,?> params(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            params.put(parameterNames[i], args[i]);
        }
        return params;
    }
}
