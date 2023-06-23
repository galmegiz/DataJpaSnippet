package com.demoApp.dto;

import org.springframework.beans.factory.annotation.Value;

//인터페이스 기반 프로젝션 : 인터페이스로 프록시 생성함
public interface UserNameOnly {

    //close projection
    String getUserName(); // 인터페이스에 프로퍼티명 getter 생성

    //open projection : member entity 전체를 가져온 다음 spl을 분석해서 결과를 만들어냄
    @Value("#{target.userName + ' ' + target.age}")
    String getUserNameAndAge();

}
