package com.eventify.ms.controller;

import com.eventify.ms.dto.member.MemberResponse;
import com.eventify.ms.dto.member.UpdateMemberRequest;
import com.eventify.ms.service.MemberService;
import com.eventify.ms.service.auth.JwtService;
import com.eventify.ms.exception.InvalidTokenException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    public MemberController(MemberService memberService, JwtService jwtService) {
        this.memberService = memberService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(memberService.getAllMembers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @GetMapping("/by-email")
    public ResponseEntity<MemberResponse> getMemberByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(memberService.getMemberByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        return ResponseEntity.ok(memberService.updateMember(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        memberService.deleteMember(id, userId);
        return ResponseEntity.noContent().build();
    }
}