package com.demoApp.repository;

import com.demoApp.dto.MemberDto;
import com.demoApp.entity.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
//구현체 : SimpleJpaRepository
//Repository <- CrudRepository <- PagingAndSortingRepository <- JpaRepository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);
    Long countByUserName(String userName);
    Boolean existsByUserName(String userName);

    void deleteByUserName(String userName);
    Long removeByUserName(String userName);

    //List<Member> findDistinct();
    List<Member> findTop3By();
    @Query(name = "Member.findByUserName") // 주석처리해도 동작함, 관례상 메소드와 동일한 named query를 먼저 찾는다. named query가 없을 경우 method명으로 쿼리 생성
    List<Member> findNamedByUserName(@Param("userName") String userName);

    @Query("select m from Member m where m.userName = :userName and m.age = :age") // named query처럼 compile 시점에 오류 발생
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    @Query("select m.userName from Member m")
    List<String> findUsernameList();

    @Query("select new com.demoApp.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //in 절 사용
    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);


    //반환타입 종류 List<Member>, Member, Optional<Member>, Page<Member> ...

    //count쿼리 분리
    //sorting 조건도 복잡해지면 기본적인 pageable sort로도 사용불가능하다. 이런 경우에도 querey에서 직접 sort 짜면된다.
    @Query(value = "select m from Member m", countQuery = "select count(m.userName) from Member m ")
    Page<Member> findByAge(int age, Pageable pageable);

    //bulk성 수정 쿼리에는 @Modifying 붙여줘야 함, 없으면 예외 발생
    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //LazyLoading 상태인 team객체를 같이 가져오고 싶은데 JPQL쓰고 싶지 않을 때
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //이미 작성된 jpql에도 적용 가능
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //쿼리 메소드에도 적용 가능
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUserName(@Param("userName") String userName);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String usrName);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Member> findLockByUserName(String userName);
}
