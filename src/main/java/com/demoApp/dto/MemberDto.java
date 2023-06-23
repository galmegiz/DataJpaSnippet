package com.demoApp.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class MemberDto {

    private Long id;
    private String userName;
    private String age;

    //@QueryProjection compile 시 QDto 생성됨
    public MemberDto(Long id, String userName, String age) {
        this.id = id;
        this.userName = userName;
        this.age = age;
    }
}
