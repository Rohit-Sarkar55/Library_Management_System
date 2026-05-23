package com.example.librarymanagement.repository;



import com.example.librarymanagement.entities.BookTransaction;
import com.example.librarymanagement.entities.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<BookTransaction, Long> {

    // Get all transactions by member
    List<BookTransaction> findAllByMemberId(Long memberId);

    // Get all transactions by book
    List<BookTransaction> findAllByBookId(Long bookId);

    // Check if member already has an active issue for the same book
    boolean existsByMemberIdAndBookIdAndStatus(
            Long memberId, Long bookId, TransactionStatus status);

    // Find active transaction for return
    Optional<BookTransaction> findByIdAndStatus(Long id, TransactionStatus status);
}
