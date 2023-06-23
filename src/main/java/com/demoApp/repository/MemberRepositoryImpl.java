package com.demoApp.repository;

import com.demoApp.dto.MemberDto;
import com.demoApp.dto.MemberSearchCondition;
import com.demoApp.dto.MemberTeamDto;
import com.demoApp.dto.QMemberTeamDto;
import com.demoApp.entity.Member;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.demoApp.entity.QMember.member;
import static com.demoApp.entity.QTeam.team;


//Spring Data Jpa interface명 + Impl, 인터페이스명은 자유롭게 지을 수 있다.
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;
    private final JPQLQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Member> findMemberCustom() {
        return null;
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> result =  queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberid"),
                        member.userName,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName"))
                )
                .from(member)
                .leftJoin(member.team, team)
                .where()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = result.getResults();
        long total = result.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> result =  queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberid"),
                        member.userName,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName"))
                )
                .from(member)
                .leftJoin(member.team, team)
                .where()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /*
        long total = queryFactory.select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where()
                .fetchCount();
*/
        JPAQuery<Member> countQuery = (JPAQuery<Member>) queryFactory.select(member).from(member).leftJoin(member.team, team).where();

        //pageable이 페이징 쿼리 최적화해줌
        //pageable sort은 사용하지 말자.
        return PageableExecutionUtils.getPage(result, pageable, () -> countQuery.fetchCount());
        //return new PageImpl<>(result, pageable, total);
    }
}
