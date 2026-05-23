package com.example.librarymanagement.service;


import com.example.librarymanagement.dto.IssueRequest;
import com.example.librarymanagement.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    TransactionDto issueBook(IssueRequest request);

    TransactionDto returnBook(Long transactionId);

    List<TransactionDto> getAllTransactions();

    List<TransactionDto> getTransactionsByMember(Long memberId);

    List<TransactionDto> getTransactionsByBook(Long bookId);
}
