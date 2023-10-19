package com.partyquest.backend.config.log;

import com.partyquest.backend.service.logic.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@Aspect
public class AuthorizationAspect {
    private final LogService logService;
    private final AuthService authService;

    @Autowired
    public AuthorizationAspect(LogService logService, AuthService authService) {
        this.authService = authService;
        this.logService = logService;
    }

    @Pointcut("execution(* com.partyquest.backend.controller.*.*(..,long))")
    public void noAuthUserRequest(){}

    @Pointcut("execution(* com.partyquest.backend.controller.*.*(long,..))")
    public void authUserRequest(){}

    @Around("noAuthUserRequest()")
    public Object beforeNoAuthUserLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> cls = joinPoint.getTarget().getClass();
        Object result;
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
            return result;
        } finally {
            log.info("ENTER "+logService.getRequestUrl(joinPoint,cls)+"\tParams: "+logService.params(joinPoint));
        }
    }

    @Around("authUserRequest()")
    public Object beforeAuthUserLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> cls = joinPoint.getTarget().getClass();
        Object result;
        Map<?,?> targetParameter = logService.params(joinPoint);
        String email = authService.getEmailById((long)targetParameter.get("id"));
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
            return result;
        } finally {
            log.info("ENTER MEMBER:" + email + "\t"+logService.getRequestUrl(joinPoint,cls));
        }
    }
}
