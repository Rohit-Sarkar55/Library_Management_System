package com.example.librarymanagement.dto;


import com.example.librarymanagement.entities.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long id;

    // Book details
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;

    // Member details
    private Long memberId;
    private String memberName;
    private String memberEmail;

    // Transaction details
    private TransactionStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime dueDate;
    private LocalDateTime returnedAt;
    private LocalDateTime createdAt;
}
