package com.demoApp.entity;

import com.demoApp.dto.MemberDto;
import com.demoApp.repository.MemberJpaRepository;
import com.demoApp.repository.MemberRepository;
import com.demoApp.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity(){
        Team teamA = new Team("team a");
        Team teamB = new Team("team b");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        System.out.println("members = " + members);
        for (Member member : members) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member1", 10);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member1.getId()).get();

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        em.remove(findMember1);
        em.remove(findMember2);

        assertThat(memberJpaRepository.count()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberRepository.findNamedByUserName("member1");
        assertThat(result.size()).isEqualTo(0);

    }

    @Test
    public void findUserTest(){
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberRepository.findUser("member1", 10);
    }

    @Test
    public void findMemberDtoTest(){
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member member1 = new Member("member1", 10);
        member1.setTeam(team);
        memberJpaRepository.save(member1);



        List<MemberDto> result = memberRepository.findMemberDto();
        System.out.println("result = " + result);
    }

    @Test
    public void findByNames(){
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member member1 = new Member("member1", 10);
        member1.setTeam(team);
        memberJpaRepository.save(member1);

        List<Member> members = memberRepository.findByNames(Arrays.asList("member1", "member2"));
    }

}
