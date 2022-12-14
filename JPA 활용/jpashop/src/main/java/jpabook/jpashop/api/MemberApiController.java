package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.member.Member;
import jpabook.jpashop.dto.member.*;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody 를 합친 어노테이션
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
     * - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를
     위한 모든 요청 요구사항을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * 결론
     * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     */
    @PostMapping("/api/v1/members")
    public PostMemberRes joinMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new PostMemberRes(id);
    }
    // @RequestBody : JSON 타입으로 온 데이터를 매핑하기 위한 어노테이션
    // Member 객체를 이용하면 유연성이 떨어지기 때문에 DTO 객체를 추가 생성하여 해결하자

    /**
     * 등록 V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
     */
    @PostMapping("/api/v2/members")
    public PostMemberRes joinMemberV2(@RequestBody @Valid PostMemberReq request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new PostMemberRes(id);

    }
    // 엔티티의 스펙은 노출하면 안됨!!
    // 엔티티와 API 스펙을 분리 가능

    /**
     * 수정 V2
     */
    @PutMapping("/api/v2/members/{id}")
    public PutMemberRes updateMemberV2(
            @PathVariable("id") Long memberId,
            @RequestBody @Valid PutMemberReq request) {

        memberService.update(memberId, request.getName());
        Member findMember =  memberService.findMember(memberId);

        return new PutMemberRes(findMember.getId(), findMember.getName());
    }

    /**
     * 조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 기본적으로 엔티티의 모든 값이 노출된다.
     * - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
     * - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * - 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으로 해결)
     * 결론
     * - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.
     */
    @GetMapping("/api/v1/members")
    public List<Member> getMembersV1() {
        return memberService.findMembers();
    }
    //조회 V1: 안 좋은 버전, 모든 엔티티가 노출, @JsonIgnore -> 이건 정말 최악, api가 이거 하나인가! 화면에 종속적이지 마라!

    /**
     * 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환한다.
     */
    @GetMapping("/api/v2/members")
    public GetMemberRes getMembersV2() {
        List<Member> members = memberService.findMembers();
        List<GetMemberDto> getMemberInfos = members
                .stream()
                .map(m -> new GetMemberDto(m.getName()))
                .collect(Collectors.toList());

        return new GetMemberRes(getMemberInfos);
    }
    // DTO를 활용하여 필요한 정보만 노출시키자
    // GetMemberRes 클래스로 을 getMemberInfos를 감싸서 향후 필요한 필드를 추가할 수 있다.

}
