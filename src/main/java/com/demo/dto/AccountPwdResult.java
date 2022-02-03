package com.demo.dto;

public class AccountPwdResult {

    private static AccountPwdResult successResult = new AccountPwdResult(true, null);

    private boolean success;

    private String errorMsg;

    private AccountPwdResult(boolean success, String errorMsg) {
        this.success = success;
        this.errorMsg = errorMsg;
    }

    public AccountPwdResult() {
    }

    public static AccountPwdResult success() {
        return successResult;
    }

    public static AccountPwdResult fail(String errorMsg) {
        return new AccountPwdResult(false, errorMsg);
    }


    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
