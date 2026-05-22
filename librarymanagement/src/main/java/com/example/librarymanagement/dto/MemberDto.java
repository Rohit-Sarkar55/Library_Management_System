package com.example.librarymanagement.dto;

import com.example.librarymanagement.entities.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String name;
    private String email;
    private MemberStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}