package com.demoApp.repository;

import com.demoApp.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
//Spring Data Jpa interface명 + Impl, 인터페이스명은 자유롭게 지을 수 있다.
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return null;
    }
}
