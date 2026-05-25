package com.example.librarymanagement.repository;


import com.example.librarymanagement.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Fetch all non-deleted books
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false")
    Page<Book> findAllActiveBooks(Pageable pageable);

    // Fetch single non-deleted book
    Optional<Book> findByIdAndIsDeletedFalse(Long id);

    // Check duplicate ISBN excluding deleted books
    boolean existsByIsbnAndIsDeletedFalse(String isbn);

    // Check duplicate ISBN excluding current book on update
    boolean existsByIsbnAndIsDeletedFalseAndIdNot(String isbn, Long id);

    // Search by title, author, isbn, category
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false AND " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%'))) AND " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', CAST(:author AS string), '%'))) AND " +
            "(:isbn IS NULL OR b.isbn = CAST(:isbn AS string)) AND " +
            "(:category IS NULL OR LOWER(b.category) LIKE LOWER(CONCAT('%', CAST(:category AS string), '%')))")
    List<Book> searchBooks(
            @Param("title") String title,
            @Param("author") String author,
            @Param("isbn") String isbn,
            @Param("category") String category);
}