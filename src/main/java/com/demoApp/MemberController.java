package com.demoApp;

import com.demoApp.entity.Member;
import com.demoApp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members1/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUserName();
    }

    //도메인 클래스 컨버터 repository를 호출하지 않음에도 결과가 나온다.
    //트랜잭션이 없는 범위에서 엔티티를 조회했기 때문에 엔티티를 변경해도 DB에 반영되지 않는다.
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUserName();
    }

    @GetMapping("/members3/{id}")
    public Page<Member> list(@PageableDefault(size = 5) Pageable pageable){
        return memberRepository.findAll(pageable);
    }

    // /members?member_page=0&order_page=1
    @GetMapping("/members4/{id}")
    public Page<Member> list2(@Qualifier("member") Pageable memberPage,
                              @Qualifier("order") Pageable orderPage){
        return memberRepository.findAll(memberPage);
    }

}
