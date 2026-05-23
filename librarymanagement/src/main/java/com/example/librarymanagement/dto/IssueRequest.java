package com.example.librarymanagement.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueRequest {

    @NotNull(message = "Member id is required")
    private Long memberId;

    @NotNull(message = "Book id is required")
    private Long bookId;
}
