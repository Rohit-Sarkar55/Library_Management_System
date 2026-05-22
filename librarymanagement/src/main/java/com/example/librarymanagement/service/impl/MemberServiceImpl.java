package com.example.librarymanagement.service.impl;



import com.example.librarymanagement.dto.MemberDto;
import com.example.librarymanagement.dto.MemberRequest;
import com.example.librarymanagement.entities.Member;
import com.example.librarymanagement.entities.MemberStatus;
import com.example.librarymanagement.exceptions.DuplicateResourceException;
import com.example.librarymanagement.exceptions.ResourceNotFoundException;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDto getMemberById(Long id) {
        return mapToDto(findMemberById(id));
    }

    @Override
    @Transactional
    public MemberDto createMember(MemberRequest request) {
        // Check duplicate email
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Member with email " + request.getEmail() + " already exists");
        }

        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .status(MemberStatus.ACTIVE)
                .build();

        return mapToDto(memberRepository.save(member));
    }

    @Override
    @Transactional
    public MemberDto updateMember(Long id, MemberRequest request) {
        Member member = findMemberById(id);

        // Check duplicate email excluding current member
        if (memberRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException(
                    "Member with email " + request.getEmail() + " already exists");
        }

        member.setName(request.getName());
        member.setEmail(request.getEmail());

        return mapToDto(memberRepository.save(member));
    }

    @Override
    @Transactional
    public MemberDto deactivateMember(Long id) {
        Member member = findMemberById(id);

        // Check if already inactive
        if (member.getStatus() == MemberStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "Member with id " + id + " is already inactive");
        }

        member.setStatus(MemberStatus.INACTIVE);
        return mapToDto(memberRepository.save(member));
    }

    // ─── Private helpers ───────────────────────────────────────

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with id: " + id));
    }

    private MemberDto mapToDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
