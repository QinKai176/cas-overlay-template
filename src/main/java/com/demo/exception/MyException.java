package com.demo.exception;

import javax.naming.AuthenticationException;

public class MyException extends AuthenticationException {

    private String msg;

    public MyException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
