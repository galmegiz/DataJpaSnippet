package com.demoApp.dto;

//실행 시 m.uerName, t.*으로 가져옴
//프로젝션 대상이 root 엔티티가 아니면 left outer join하고 모든 필드를 select해서 엔티티로 조회한 다음 계산
//프로젝션 대사이 root 엔티티를 넘어가면 select 최적화 불가
//실무에서는 root entity를 단순히 조회할 때만 사용
public interface NestedClosedProjections {
    String getUserName();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }
}
