package com.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.domain.AdminCommentEntity;
import com.web.domain.Member;
import com.web.domain.OneToOneInquiryEntity;
import com.web.service.OneToOneLnqueryService;
import com.web.service.TokenService;

@RestController
@RequestMapping("/board")
public class OneToOneInquiryController {

	@Autowired
	private OneToOneLnqueryService oneToOneLnqueryService;
	@Autowired
	private TokenService tockenService;

	@PostMapping("/InquiryArea")
	public Map<String, Object> InquiryArea(
			@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token,
			@RequestBody OneToOneInquiryEntity inquiryEntity) {
		System.out.println(inquiryEntity);
		Member currentMember = tockenService.getMemberByMemberNum(token);
		String res = oneToOneLnqueryService.InquiryArea(inquiryEntity, currentMember);
		Map<String, Object> map = new HashMap<>();

		map.put("result", res);
		return map;
	}

 
	@PostMapping("/InquiryList")
	public Map<String ,Object> InquiryList(
			@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token,
			@PageableDefault(size = 10, page = 0) Pageable pageable) {
		Member memberNum = tockenService.getMemberByMemberNum(token);
//		System.out.println(memberNum.getMemberNum());
		Page<OneToOneInquiryEntity> paging = oneToOneLnqueryService.InquiryPaging(pageable, memberNum.getMemberNum());
		oneToOneLnqueryService.InquiryPaging(pageable, memberNum.getMemberNum());
		Map<String ,Object> map = new HashMap<>();
		map.put("paging", paging);
//		System.out.println(paging); 
		return map;
  
	}   
	
	
//	@PostMapping("/InquiryList")
//	public void InquiryList(
//	        @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token) {
//	    Member memberNum = tockenService.getMemberByMemberNum(token);
//	    System.out.println("ㅎㅇ");
//	    System.out.println(memberNum);
//	    List<OneToOneInquiryEntity> list = oneToOneInquiryService.getList(memberNum.getMemberNum());
//	}
//	-----------------------------------
	// 1대1 상세정보
	@PostMapping("/InquiryDetail/{inquiryId}")
	public OneToOneInquiryEntity LnqueryDetail(@PathVariable Long inquiryId) {
		System.out.println("/////////"+inquiryId);
		return oneToOneLnqueryService.InquiryDetail(inquiryId);
	}
	// admin 답변
	@PostMapping("/AdminComment/{inquiryId}")
	public AdminCommentEntity AdminComment(@PathVariable Long inquiryId,@RequestBody AdminCommentEntity commentEntity) {
		System.out.println("bhjvhvhvhhj"+commentEntity);
		return oneToOneLnqueryService.AdminCommnet(inquiryId,commentEntity);
	}
	// 답변 뽑아내기
	@PostMapping("AdminCommentList/{inquiryId}")
	public Map<String,Object > AdminCommentList(@PathVariable Long inquiryId){
		System.out.println(inquiryId);
		Map<String,Object > map = new HashMap<>();
		List<AdminCommentEntity> list = oneToOneLnqueryService.AdminCommnetList(inquiryId);
		map.put("list", list);
		System.out.println(list);
		return map;
	}
}
