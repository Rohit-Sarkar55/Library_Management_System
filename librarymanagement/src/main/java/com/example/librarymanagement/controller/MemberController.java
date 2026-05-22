package com.example.librarymanagement.controller;


import com.example.librarymanagement.dto.MemberDto;
import com.example.librarymanagement.dto.MemberRequest;
import com.example.librarymanagement.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PostMapping
    public ResponseEntity<MemberDto> createMember(
            @RequestBody @Valid MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberService.createMember(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(
            @PathVariable Long id,
            @RequestBody @Valid MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<MemberDto> deactivateMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.deactivateMember(id));
    }
}
