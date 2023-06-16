package com.demoApp.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class MemberDto {

    private Long id;
    private String userName;
    private String age;

    public MemberDto(Long id, String userName, String age) {
        this.id = id;
        this.userName = userName;
        this.age = age;
    }
}
