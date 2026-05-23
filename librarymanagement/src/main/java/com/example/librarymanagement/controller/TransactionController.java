package com.example.librarymanagement.controller;


import com.example.librarymanagement.dto.IssueRequest;
import com.example.librarymanagement.dto.TransactionDto;
import com.example.librarymanagement.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/issue")
    public ResponseEntity<TransactionDto> issueBook(
            @RequestBody @Valid IssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.issueBook(request));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<TransactionDto> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.returnBook(id));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByMember(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(transactionService.getTransactionsByMember(memberId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByBook(
            @PathVariable Long bookId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBook(bookId));
    }
}
