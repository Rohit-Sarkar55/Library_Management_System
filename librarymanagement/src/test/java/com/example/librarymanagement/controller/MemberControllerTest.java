package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.MemberDto;
import com.example.librarymanagement.dto.MemberRequest;
import com.example.librarymanagement.entities.MemberStatus;
import com.example.librarymanagement.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MemberDto memberDto;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        memberDto = MemberDto.builder()
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
    void getAllMembers_returnsListOfMembers() throws Exception {
        when(memberService.getAllMembers()).thenReturn(List.of(memberDto));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void getMemberById_returnsMember() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(memberDto);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void createMember_returnsCreatedMember() throws Exception {
        when(memberService.createMember(any(MemberRequest.class))).thenReturn(memberDto);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void updateMember_returnsUpdatedMember() throws Exception {
        when(memberService.updateMember(eq(1L), any(MemberRequest.class))).thenReturn(memberDto);

        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deactivateMember_returnsDeactivatedMember() throws Exception {
        MemberDto deactivated = MemberDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .status(MemberStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(memberService.deactivateMember(1L)).thenReturn(deactivated);

        mockMvc.perform(patch("/api/members/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void createMember_withInvalidRequest_returnsBadRequest() throws Exception {
        MemberRequest invalid = new MemberRequest("", "not-an-email");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
