package com.demoApp.dto;

public class UserNameOnlyDto {

    private final String userName;

    //parameter 명으로 projection을 수행함
    public UserNameOnlyDto(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
