package com.example.librarymanagement.service;


import com.example.librarymanagement.dto.MemberDto;
import com.example.librarymanagement.dto.MemberRequest;

import java.util.List;

public interface MemberService {

    List<MemberDto> getAllMembers();

    MemberDto getMemberById(Long id);

    MemberDto createMember(MemberRequest request);

    MemberDto updateMember(Long id, MemberRequest request);

    MemberDto deactivateMember(Long id);
}
