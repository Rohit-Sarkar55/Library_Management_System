package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.IssueRequest;
import com.example.librarymanagement.dto.TransactionDto;
import com.example.librarymanagement.entities.*;
import com.example.librarymanagement.exceptions.ResourceNotFoundException;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.repository.TransactionRepository;
import com.example.librarymanagement.service.impl.TransactionServiceImpl;
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
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Member member;
    private Book book;
    private BookTransaction transaction;
    private IssueRequest issueRequest;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(5L)
                .name("John Doe")
                .email("john.doe@example.com")
                .status(MemberStatus.ACTIVE)
                .build();

        book = Book.builder()
                .id(10L)
                .title("Clean Code")
                .isbn("9780132350884")
                .availableCopies(3)
                .totalCopies(5)
                .isDeleted(false)
                .build();

        transaction = BookTransaction.builder()
                .id(1L)
                .book(book)
                .member(member)
                .status(TransactionStatus.ISSUED)
                .issuedAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .createdAt(LocalDateTime.now())
                .build();

        issueRequest = new IssueRequest(5L, 10L);
    }

    @Test
    void issueBook_createsTransactionAndDecreasesAvailableCopies() {
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member));
        when(bookRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.existsByMemberIdAndBookIdAndStatus(5L, 10L, TransactionStatus.ISSUED)).thenReturn(false);
        when(transactionRepository.save(any(BookTransaction.class))).thenReturn(transaction);

        TransactionDto result = transactionService.issueBook(issueRequest);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.ISSUED);
        assertThat(book.getAvailableCopies()).isEqualTo(2);
        verify(bookRepository).save(book);
    }

    @Test
    void issueBook_throwsResourceNotFoundException_whenMemberNotFound() {
        when(memberRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.issueBook(issueRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 5");
    }

    @Test
    void issueBook_throwsIllegalArgumentException_whenMemberInactive() {
        member.setStatus(MemberStatus.INACTIVE);
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> transactionService.issueBook(issueRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member is inactive");
    }

    @Test
    void issueBook_throwsResourceNotFoundException_whenBookNotFound() {
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member));
        when(bookRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.issueBook(issueRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 10");
    }

    @Test
    void issueBook_throwsIllegalArgumentException_whenNoCopiesAvailable() {
        book.setAvailableCopies(0);
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member));
        when(bookRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> transactionService.issueBook(issueRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No available copies");
    }

    @Test
    void issueBook_throwsIllegalArgumentException_whenMemberAlreadyHasBookIssued() {
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member));
        when(bookRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.existsByMemberIdAndBookIdAndStatus(5L, 10L, TransactionStatus.ISSUED)).thenReturn(true);

        assertThatThrownBy(() -> transactionService.issueBook(issueRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member already has this book issued");
    }

    @Test
    void returnBook_updatesTransactionAndIncreasesAvailableCopies() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(BookTransaction.class))).thenReturn(transaction);

        TransactionDto result = transactionService.returnBook(1L);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.RETURNED);
        assertThat(book.getAvailableCopies()).isEqualTo(4);
        verify(bookRepository).save(book);
    }

    @Test
    void returnBook_setsStatusOverdue_whenReturnedAfterDueDate() {
        transaction.setDueDate(LocalDateTime.now().minusDays(1));
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(BookTransaction.class))).thenReturn(transaction);

        transactionService.returnBook(1L);

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.OVERDUE);
    }

    @Test
    void returnBook_throwsResourceNotFoundException_whenTransactionNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.returnBook(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found with id: 99");
    }

    @Test
    void returnBook_throwsIllegalArgumentException_whenAlreadyReturned() {
        transaction.setStatus(TransactionStatus.RETURNED);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.returnBook(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already been returned");
    }

    @Test
    void getAllTransactions_returnsListOfTransactionDtos() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionDto> result = transactionService.getAllTransactions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getTransactionsByMember_returnsTransactions() {
        when(memberRepository.existsById(5L)).thenReturn(true);
        when(transactionRepository.findAllByMemberId(5L)).thenReturn(List.of(transaction));

        List<TransactionDto> result = transactionService.getTransactionsByMember(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMemberId()).isEqualTo(5L);
    }

    @Test
    void getTransactionsByMember_throwsResourceNotFoundException_whenMemberNotFound() {
        when(memberRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.getTransactionsByMember(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 99");
    }

    @Test
    void getTransactionsByBook_returnsTransactions() {
        when(bookRepository.existsById(10L)).thenReturn(true);
        when(transactionRepository.findAllByBookId(10L)).thenReturn(List.of(transaction));

        List<TransactionDto> result = transactionService.getTransactionsByBook(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookId()).isEqualTo(10L);
    }

    @Test
    void getTransactionsByBook_throwsResourceNotFoundException_whenBookNotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.getTransactionsByBook(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 99");
    }
}
