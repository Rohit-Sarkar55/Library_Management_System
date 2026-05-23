package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.IssueRequest;
import com.example.librarymanagement.dto.TransactionDto;
import com.example.librarymanagement.entities.TransactionStatus;
import com.example.librarymanagement.service.TransactionService;
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

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private TransactionDto transactionDto;
    private IssueRequest issueRequest;

    @BeforeEach
    void setUp() {
        transactionDto = TransactionDto.builder()
                .id(1L)
                .bookId(10L)
                .bookTitle("Clean Code")
                .bookIsbn("9780132350884")
                .memberId(5L)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .status(TransactionStatus.ISSUED)
                .issuedAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .createdAt(LocalDateTime.now())
                .build();

        issueRequest = new IssueRequest(5L, 10L);
    }

    @Test
    void issueBook_returnsCreatedTransaction() throws Exception {
        when(transactionService.issueBook(any(IssueRequest.class))).thenReturn(transactionDto);

        mockMvc.perform(post("/api/transactions/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ISSUED"));
    }

    @Test
    void returnBook_returnsUpdatedTransaction() throws Exception {
        TransactionDto returned = TransactionDto.builder()
                .id(1L)
                .bookId(10L)
                .bookTitle("Clean Code")
                .bookIsbn("9780132350884")
                .memberId(5L)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .status(TransactionStatus.RETURNED)
                .issuedAt(LocalDateTime.now().minusDays(7))
                .dueDate(LocalDateTime.now().plusDays(7))
                .returnedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusDays(7))
                .build();

        when(transactionService.returnBook(1L)).thenReturn(returned);

        mockMvc.perform(post("/api/transactions/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    void getAllTransactions_returnsListOfTransactions() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(List.of(transactionDto));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Code"));
    }

    @Test
    void getTransactionsByMember_returnsTransactions() throws Exception {
        when(transactionService.getTransactionsByMember(5L)).thenReturn(List.of(transactionDto));

        mockMvc.perform(get("/api/transactions/member/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(5L))
                .andExpect(jsonPath("$[0].memberName").value("John Doe"));
    }

    @Test
    void getTransactionsByBook_returnsTransactions() throws Exception {
        when(transactionService.getTransactionsByBook(10L)).thenReturn(List.of(transactionDto));

        mockMvc.perform(get("/api/transactions/book/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(10L))
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Code"));
    }

    @Test
    void issueBook_withInvalidRequest_returnsBadRequest() throws Exception {
        IssueRequest invalid = new IssueRequest(null, null);

        mockMvc.perform(post("/api/transactions/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
