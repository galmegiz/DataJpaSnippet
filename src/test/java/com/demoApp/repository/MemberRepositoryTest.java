package com.demoApp.repository;

import com.demoApp.dto.UserNameOnly;
import com.demoApp.entity.Member;
import com.demoApp.entity.Team;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest
//@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired
    EntityManager em;
    @Test
    public void QueryExampleTest(){
        Team teamA = new Team("TeamA");
        em.persist(teamA);

        Member m1 = new Member("sun", 0, teamA);
        Member m2 = new Member("sun2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        Member member = new Member("sun", 0);
        Team team = new Team("TeamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        //mather를 주지 않으면 age(default 값)까지 포함한 select query가 나간다.
        //outer join에는 적용 불가능하다.
        //중첩 제약 조건이 안 된다. ex) fristname= ?0 or (firstname = ?1 and lastname = ?2)
        //매칭 조건이 매우 단순함, starts/contains/ends/regex
        Example<Member> example = Example.of(member, matcher);

        //select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.user_name from member m1_0 join team t1_0 on t1_0.team_id=m1_0.team_id where t1_0.name=? and m1_0.user_name=?
        //select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.user_name from member m1_0 join team t1_0 on t1_0.team_id=m1_0.team_id where t1_0.name='TeamA' and m1_0.user_name='sun';
        List<Member> all = memberRepository.findAll(example);
        assertThat(all.get(0).getUserName()).isEqualTo("sun");
    }

    @Test
    public void projectionTest(){
        Team teamA = new Team("TeamA");
        em.persist(teamA);

        Member m1 = new Member("sun", 0, teamA);
        Member m2 = new Member("sun2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        List<UserNameOnly> result = memberRepository.findProjectionsByUserName("sun");

        //userNameOnly = org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap@33822750

        for(UserNameOnly userNameOnly : result){
            System.out.println("userNameOnly = " + userNameOnly);
        }
    }

    @Test
    public void nativeQueryTest(){
        Team teamA = new Team("TeamA");
        em.persist(teamA);

        Member m1 = new Member("sun", 0, teamA);
        Member m2 = new Member("sun2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Member result = memberRepository.findNativeQuery("sun");




    }

}