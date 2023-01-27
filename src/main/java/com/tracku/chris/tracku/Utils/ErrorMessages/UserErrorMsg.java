package com.tracku.chris.tracku.Utils.ErrorMessages;

public enum UserErrorMsg {
    UNAUTHORIZED("User is not authorized."),
    NOT_FOUND("User does not exists"),
    USER_EXISTS("User already exists"),
    INVALID_CREDENTIALS("Username or password is not correct"),
    INVALID_CURRENT_PASSWORD("Current password is incorrect");

    public final String label;
    private UserErrorMsg(String label) {
        this.label = label;
    }

}
