package com.demoApp.querydsl.basic;

import com.demoApp.dto.MemberDto;
import com.demoApp.entity.Member;
import com.demoApp.entity.QMember;
import com.demoApp.entity.QTeam;
import com.demoApp.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.demoApp.entity.QMember.*;
import static com.demoApp.entity.QTeam.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;


    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

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
    }

    @Test
    public void startJPQL(){
        Member findMEmber = em.createQuery("select m from Member m where m.userName = :username", Member.class)
                .setParameter("username", "member1").getSingleResult();

        assertThat(findMEmber.getUserName()).isEqualTo("member1");
    }


    @Test
    public void startQuerydsl() {
        QMember m = new QMember("m");
        QMember m1 = member; //static import

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.userName.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUserName()).isEqualTo("member1");
    }


    @Test
    public void resultFetch() {
        List<Member> fetch = queryFactory.selectFrom(member).fetch();
        Member fetchOne = queryFactory.selectFrom(member).fetchOne();
        Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();

        //deprecated될 예정
        //쿼리 두번 실행됨, totalcount를 가져와야되기 때문에 count()쿼리가 발생함
        QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults();
        results.getTotal();
        List<Member> content = results.getResults();

        //deprecated될 예정
        Long count = queryFactory.selectFrom(member).fetchCount();
    }

    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));


        //null은 sort 순서 마지막에
        queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.userName.asc().nullsLast())
                .fetch();
    }

    @Test
    public void paging1() {
        queryFactory.selectFrom(member)
                .orderBy(member.userName.desc())
                .offset(1)
                .limit(2)
                .fetch();

        //count쿼리 발생함
        queryFactory.selectFrom(member)
                .orderBy(member.userName.desc())
                .offset(1)
                .limit(2)
                .fetchResults();


    }

    @Test
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                ).from(member)
                .fetch();
        //실무에서는 tuple직접 쓰지 않음

        Tuple tuple = result.get(0);
        //assertThat(tuple.get(member.age.sum()))).isEqaulTo(100));
    }

    @Test
    public void group() {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                //.having(team.)
                .fetch();
    }

    @Test
    public void join() {

        //JPQL과 같음(fetch join 아님)
        queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
    }


    public void theta_join() {

        queryFactory
                .select(member)
                .from(member, team)
                .where(member.userName.eq(team.name))
                .fetch();

    }

    //필터링 또는 연관관계가 없는 두 테이블을 join할 때 사용
    public void join_on_filtering() {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();


        //on절로 team을 필터링하고 member와 left join
        queryFactory
                .select(member, team)
                .from(member) // join에 값을 한 개만 넣어준다.
                .leftJoin(team).on(member.userName.eq(team.name))
                .fetch();
    }

    @PersistenceUnit
    EntityManagerFactory emf;
    @Test
    public void fetchJoin() {
        //fetch join x
        Member findMember = queryFactory.selectFrom(member)
                .join(member.team, team)
                .where(member.userName.eq("member1"))
                .fetchOne();
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        queryFactory.selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.userName.eq("member1"))
                .fetch();


    }

    //from절 subquery는 1. join으로 변경, 2. 쿼리를 2번 분리해서 실행 3. nativeSql 실행
    @Test
    public void subQuery(){
        QMember membersub = new QMember("memberSub");

        queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions.select(membersub.age.max())
                                .from(membersub)
                )).fetch();

        queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions.select(membersub.age)
                                .from(membersub)
                                .where(membersub.age.gt(10))
                )).fetch();


        queryFactory
                .select(member.userName,
                        JPAExpressions
                                .select(membersub.age.avg())
                                .from(membersub)
                ).from(member)
                .fetch();
    }

    @Test
    public void basicCase(){
        queryFactory.select(
                member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")

        ).from(member)
                .fetch();

        queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .otherwise("기타")
                ).from(member)
                .fetch();

    }

    public void constfunc(){
        queryFactory.select(member.userName.concat("-").concat(member.age.stringValue()))
                .from(member)
                .fetch();
    }

    public void projection(){
        List<String> result = queryFactory.select(member.userName)
                .from(member)
                .fetch();

        List<Tuple> result2= queryFactory
                .select(member.userName, member.age)
                .from(member)
                .fetch();

        //기본 생성자 필요, getter, setter이용
        queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.userName,
                        member.age))
                .from(member)
                .fetch();

        //field에 바로 주입
        queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.userName.as("name"), //dto의 field명과 entity필드명이 다를 때
                        member.age))
                .from(member)
                .fetch();

        QMember memberSub = new QMember("memberSub");
        queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.userName.as("name"), //dto의 field명과 entity필드명이 다를 때
                        ExpressionUtils.as(JPAExpressions
                                .select(member.age.max())
                                .from(memberSub), "age")
                ))
                .from(member)
                .fetch();

        //생성자 방식
        queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.userName,
                        member.age))
                .from(member)
                .fetch();

/*
        @QueryProjection사용시-> 런타임으로 오류 발생
        queryFactory
                .select(new QMemberDot(member.userName, member.age))
                .from(member)
                .fetch();
*/


    }

    public void dynamicQuery_BooleanBuilder() {
        String username = "member1";
        Integer age = 10;
        List<Member> results = searchMember1(username, age);
    }

    private List<Member> searchMember1(String username, Integer age) {
        BooleanBuilder builder = new BooleanBuilder();

        if (username != null) {
            builder.and(member.userName.eq((username)));
        }

        if (age != null) {
            builder.and(member.age.eq(age));
        }

        return queryFactory.selectFrom(member)
                .where(builder)
                .fetch();
    }

    public void dynamicQuery_Where(){
        String username = "member1";
        Integer age = 10;
        List<Member> results = searchMember2(username, age);
    }
    private List<Member> searchMember2(String username, Integer age) {


        return queryFactory.selectFrom(member)
                .where(usernameEq(username), ageEq(age))
                .fetch();
    }

    private Predicate ageEq(Integer age) {
        return age != null ? member.age.eq(age) : null;
    }

    private Predicate usernameEq(String username) {
        return username != null ? member.userName.eq(username) : null;
    }


    private BooleanExpression allEq(String username, Integer age) {
        return usernameEq1(username).and(ageEq(age));
    }
    private BooleanExpression ageEq1(Integer age) {
        return age != null ? member.age.eq(age) : null;
    }

    private BooleanExpression usernameEq1(String username) {
        return username != null ? member.userName.eq(username) : null;
    }


    public void bulkUpdate(){
        long count = queryFactory
                .update(member)
                .set(member.userName, "비회원")
                .where(member.age.lt(28))
                .execute();
    }

    public void sqlFunction(){
        queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace', {0}, {1}, {2})", member.userName, "member", "M")
                ).from(member)
                .fetch();
    }

}

