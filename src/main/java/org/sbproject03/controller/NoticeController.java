package org.sbproject03.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.Member;
import org.sbproject03.domain.Notice;
import org.sbproject03.service.MemberService;
import org.sbproject03.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private MemberService memberService;

    // 페이징 블럭수
    private final int blockCount = 10;

    // notice 목록
    @GetMapping("/list")
    public String viewNoticeListPage(Model model) {
        return viewNoticeViewPage(1, "id", "desc", model);
    }


    // 페이징 처리 후 QnA목록 화면으로 이동
    @GetMapping("/page")
    public String viewNoticeViewPage(@RequestParam("pageNum") int pageNum, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {
        Page<Notice> page = noticeService.getNoticeList(pageNum, sortField, sortDir);
        List<Notice> noticeList = page.getContent();

        // 블럭의 시작 번호 설졍
        int startPage = pageNum - (pageNum - 1) % blockCount;

        // 페이징 정보 저장
        model.addAttribute("currentPage", pageNum);                  // 페이지 번호
        model.addAttribute("totalPages", page.getTotalPages());      // 전체 페이지 수
        model.addAttribute("totalItems", page.getTotalElements());   // 전체 항목 수
        model.addAttribute("sortField", sortField);                  // 정렬할 필드
        model.addAttribute("sortDir", sortDir);                      // 정렬 방법
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("startPage", startPage);   // 블럭의 시작번호
        model.addAttribute("blockCount", blockCount);   // 블럭의 개수
        model.addAttribute("noticeList", noticeList);   // QnA 목록

        // 로그인 여부 체크
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            // 로그인된 사용자라면 사용자 정보를 가져오기
            String userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Member member = memberService.getMemberById(userId);
            model.addAttribute("member", member);  // 로그인된 회원 정보 추가
        } else {
            // 로그인되지 않은 사용자라면 빈 객체 추가
            model.addAttribute("newMember", new Member());  // 빈 객체
        }

        return "/notice/noticeList";
    }

    // 글 등록 화면
    @GetMapping("/add")
    public String newNoticeForm(Model model) {
        model.addAttribute("notice", new Notice());
        return "/notice/newNotice";
    }

    // 글 등록 처리
    @PostMapping("/add")
    public String newNoticePro(@ModelAttribute Notice notice, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "notice/newNotice";
        }

        noticeService.save(notice);
        return "redirect:/notice/list";
    }

    // 글 상세 보기 (1건)
    @GetMapping("/view/{id}")
    public String requestDetailView(@PathVariable Long id, HttpServletRequest request, Model model) {
        Notice notice = noticeService.getNoticeById(id);
        model.addAttribute("notice", notice);

        HttpSession session = request.getSession(true);
        Member member = (Member) session.getAttribute("userLoginInfo");
        // model => request와 같은 개념
        model.addAttribute("modifiedOK", false);

        // 로그인한 자신의 글인지를 판별 -> 자신의 글일 때만 수정, 삭제
        if (member != null && notice.getWriterId().equals(member.getMemberId())) {
            model.addAttribute("modifiedOK", true);
        }

        // 로그인 여부 체크
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            // 로그인된 사용자라면 사용자 정보를 가져오기
            String userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Member member1 = memberService.getMemberById(userId);
            model.addAttribute("member", member1);  // 로그인된 회원 정보 추가
        } else {
            // 로그인되지 않은 사용자라면 빈 객체 추가
            model.addAttribute("newMember", new Member());  // 빈 객체
        }

        return "notice/viewNotice";
    }

    // notice 수정
    @PostMapping("/update")
    public String updateNotice(@ModelAttribute Notice notice, BindingResult bindingResult, HttpServletRequest request, Model model) {
        // 유효성 검사 테스트
        if (bindingResult.hasErrors()) {
            HttpSession session = request.getSession(true);
            Member member = (Member) session.getAttribute("userLoginInfo");
            // model => request와 같은 개념
            model.addAttribute("userLoginInfo", true);

            // 로그인한 자신의 글인지를 판별 -> 자신의 글일 때만 수정, 삭제
            if(member != null && notice.getWriterId().equals(member.getMemberId())) {
                model.addAttribute("userLoginInfo", true);
            }

            return "notice/viewNotice";
        }

        noticeService.save(notice);
        return "redirect:/notice/view/" + notice.getId();
    }

    // 게시글 삭제
    @GetMapping("/delete/{id}")
    public String deleterNotice(@PathVariable Long id) {
        noticeService.deleteNoticeById(id);
        return "redirect:/notice/list";

    }





}