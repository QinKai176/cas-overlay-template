package com.demo.config;

import com.demo.exception.MyException;
import com.demo.handler.MyAuthenticationExceptionHandlerAction;
import com.demo.handler.MyAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.webflow.execution.Action;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration("myAuthenticationConfiguration")
@Import(MyAuthenticationHandler.class)
public class AuthenticationConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private MyAuthenticationHandler myAuthenticationHandler;

    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
        // 注册自定义验证器注册
        plan.registerAuthenticationHandler(myAuthenticationHandler);
    }

    @Bean
    @ConditionalOnMissingBean(name = "authenticationExceptionHandler")
    public Action authenticationExceptionHandler() {
        Set<Class<? extends Throwable>> classSet = handledAuthenticationExceptions();
        return new MyAuthenticationExceptionHandlerAction(classSet);
    }

    private Set<Class<? extends Throwable>> handledAuthenticationExceptions() {
        final Set<Class<? extends Throwable>> errors = new LinkedHashSet<>();
        errors.add(MyException.class);
        return errors;
    }
}