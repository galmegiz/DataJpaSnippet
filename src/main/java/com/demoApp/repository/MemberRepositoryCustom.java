package com.demoApp.repository;

import com.demoApp.dto.MemberSearchCondition;
import com.demoApp.dto.MemberTeamDto;
import com.demoApp.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    public List<Member> findMemberCustom();

    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
