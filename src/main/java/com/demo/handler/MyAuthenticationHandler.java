package com.demo.handler;

import com.demo.dto.AccountPwdResult;
import com.demo.exception.MyException;
import lombok.SneakyThrows;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;


@Component
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    @Autowired
    private RestTemplate restTemplate;

    public MyAuthenticationHandler(@Qualifier("servicesManager") ServicesManager servicesManager) {
        super(MyAuthenticationHandler.class.getSimpleName(), servicesManager, new DefaultPrincipalFactory(), 1);
    }

    @SneakyThrows
    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws GeneralSecurityException, PreventedException {
        String username = credential.getUsername();
        String password = credential.getPassword();

        String checkUserPwdUrl = "http://localhost:18083/cas/check/" + username + "/" + password;
        AccountPwdResult result = restTemplate.getForEntity(checkUserPwdUrl, AccountPwdResult.class, Collections.emptyMap()).getBody();
        if (result.isSuccess()) {
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username), new ArrayList<>(0));
        } else {
            String errorMsg = result.getErrorMsg();
            throw new MyException(errorMsg);
        }

    }
}

