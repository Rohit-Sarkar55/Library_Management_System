package com.example.librarymanagement.service.impl;


import com.example.librarymanagement.dto.IssueRequest;
import com.example.librarymanagement.dto.TransactionDto;
import com.example.librarymanagement.entities.*;
import com.example.librarymanagement.exceptions.ResourceNotFoundException;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.repository.TransactionRepository;
import com.example.librarymanagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public TransactionDto issueBook(IssueRequest request) {
        // Fetch member
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with id: " + request.getMemberId()));

        // Check member is active
        if (member.getStatus() == MemberStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "Member is inactive and cannot issue books");
        }

        // Fetch book
        Book book = bookRepository.findByIdAndIsDeletedFalse(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id: " + request.getBookId()));

        // Check available copies
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException(
                    "No available copies for book: " + book.getTitle());
        }

        // Check if member already has this book issued
        if (transactionRepository.existsByMemberIdAndBookIdAndStatus(
                request.getMemberId(), request.getBookId(), TransactionStatus.ISSUED)) {
            throw new IllegalArgumentException(
                    "Member already has this book issued");
        }

        // Decrease available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Create transaction
        BookTransaction transaction = BookTransaction.builder()
                .book(book)
                .member(member)
                .status(TransactionStatus.ISSUED)
                .issuedAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .build();

        return mapToDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public TransactionDto returnBook(Long transactionId) {
        // Fetch transaction
        BookTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + transactionId));

        // Check if already returned
        if (transaction.getStatus() == TransactionStatus.RETURNED) {
            throw new IllegalArgumentException(
                    "Book has already been returned for transaction: " + transactionId);
        }

        // Fetch book and increase available copies
        Book book = transaction.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Update transaction
        transaction.setReturnedAt(LocalDateTime.now());
        transaction.setStatus(
                LocalDateTime.now().isAfter(transaction.getDueDate())
                        ? TransactionStatus.OVERDUE
                        : TransactionStatus.RETURNED);

        return mapToDto(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsByMember(Long memberId) {
        // Verify member exists
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException(
                    "Member not found with id: " + memberId);
        }
        return transactionRepository.findAllByMemberId(memberId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsByBook(Long bookId) {
        // Verify book exists
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException(
                    "Book not found with id: " + bookId);
        }
        return transactionRepository.findAllByBookId(bookId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ─── Private helper ────────────────────────────────────────

    private TransactionDto mapToDto(BookTransaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .bookId(transaction.getBook().getId())
                .bookTitle(transaction.getBook().getTitle())
                .bookIsbn(transaction.getBook().getIsbn())
                .memberId(transaction.getMember().getId())
                .memberName(transaction.getMember().getName())
                .memberEmail(transaction.getMember().getEmail())
                .status(transaction.getStatus())
                .issuedAt(transaction.getIssuedAt())
                .dueDate(transaction.getDueDate())
                .returnedAt(transaction.getReturnedAt())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
