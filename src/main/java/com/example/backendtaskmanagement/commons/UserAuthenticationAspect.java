package com.example.backendtaskmanagement.commons;

import com.example.backendtaskmanagement.exceptions.UnauthorizedException;
import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.services.UserContextService;
import com.example.backendtaskmanagement.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Component
public class UserAuthenticationAspect {

    private final UserContextService userContextService;
    private final UserService userService;

    public UserAuthenticationAspect(UserContextService userContextService, UserService userService) {
        this.userContextService = userContextService;
        this.userService = userService;
    }

    @Around("@annotation(com.example.backendtaskmanagement.commons.RequiresAuthentication)")  // Custom annotation to mark methods requiring user context
    public Object authenticateAndProceed(ProceedingJoinPoint joinPoint) throws Throwable {
        String token = getTokenFromRequest();

        if (token == null) {
            throw new UnauthorizedException("Auth token not found in request");
        }

        // Retrieve the authenticated user
        User authenticatedUser = userContextService.getAuthenticatedUser(token);

        // Proceed with the method call and inject the user information
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof User) {
                args[i] = authenticatedUser;
            }
        }

        return joinPoint.proceed(args);
    }

    private String getTokenFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return userService.extractTokenFromRequest(request);
    }
}
