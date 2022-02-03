package com.demo.controller;

import com.demo.dto.AccountPwdResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class DemoController {

    @GetMapping("/check/{accountName}/{password}")
    public AccountPwdResult checkAccountAndPwd(@PathVariable("accountName") String accountName, @PathVariable("password") String password) {
        if (Objects.equals(accountName, "bob") && Objects.equals(password, "bob")) {
            return AccountPwdResult.success();
        }
        return AccountPwdResult.fail("用户密码错误！！！");
    }
}
