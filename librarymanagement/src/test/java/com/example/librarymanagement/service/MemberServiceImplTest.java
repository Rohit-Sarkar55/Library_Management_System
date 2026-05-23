package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.MemberDto;
import com.example.librarymanagement.dto.MemberRequest;
import com.example.librarymanagement.entities.Member;
import com.example.librarymanagement.entities.MemberStatus;
import com.example.librarymanagement.exceptions.DuplicateResourceException;
import com.example.librarymanagement.exceptions.ResourceNotFoundException;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .status(MemberStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        memberRequest = new MemberRequest("John Doe", "john.doe@example.com");
    }

    @Test
    void getAllMembers_returnsListOfMemberDtos() {
        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<MemberDto> result = memberService.getAllMembers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void getMemberById_returnsMemberDto() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberDto result = memberService.getMemberById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void getMemberById_throwsResourceNotFoundException_whenNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 99");
    }

    @Test
    void createMember_savesAndReturnsMemberDto() {
        when(memberRepository.existsByEmail(memberRequest.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberDto result = memberService.createMember(memberRequest);

        assertThat(result.getName()).isEqualTo("John Doe");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void createMember_throwsDuplicateResourceException_whenEmailExists() {
        when(memberRepository.existsByEmail(memberRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> memberService.createMember(memberRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john.doe@example.com");
    }

    @Test
    void updateMember_updatesAndReturnsMemberDto() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.existsByEmailAndIdNot(memberRequest.getEmail(), 1L)).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberDto result = memberService.updateMember(1L, memberRequest);

        assertThat(result.getId()).isEqualTo(1L);
        verify(memberRepository).save(member);
    }

    @Test
    void updateMember_throwsDuplicateResourceException_whenEmailTakenByOtherMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.existsByEmailAndIdNot(memberRequest.getEmail(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> memberService.updateMember(1L, memberRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateMember_throwsResourceNotFoundException_whenMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.updateMember(99L, memberRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 99");
    }

    @Test
    void deactivateMember_setsStatusInactive() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        memberService.deactivateMember(1L);

        assertThat(member.getStatus()).isEqualTo(MemberStatus.INACTIVE);
        verify(memberRepository).save(member);
    }

    @Test
    void deactivateMember_throwsIllegalArgumentException_whenAlreadyInactive() {
        member.setStatus(MemberStatus.INACTIVE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.deactivateMember(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already inactive");
    }

    @Test
    void deactivateMember_throwsResourceNotFoundException_whenMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.deactivateMember(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 99");
    }
}
