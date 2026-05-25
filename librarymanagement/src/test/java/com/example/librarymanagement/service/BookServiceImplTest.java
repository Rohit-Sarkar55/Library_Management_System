package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.exceptions.DuplicateResourceException;
import com.example.librarymanagement.exceptions.ResourceNotFoundException;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .category("Programming")
                .totalCopies(5)
                .availableCopies(3)
                .shelfLocation("A1")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookRequest = new BookRequest("Clean Code", "Robert C. Martin",
                "9780132350884", "Programming", 5, 3, "A1");
    }

    @Test
    void getAllBooks_returnsListOfBookDtos() {
        when(bookRepository.findAllActiveBooks(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(book)));

        Page<BookDto> result = bookService.getAllBooks(PageRequest.of(1, 10));

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getBookById_returnsBookDto() {
        when(bookRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(book));

        BookDto result = bookService.getBookById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getBookById_throwsResourceNotFoundException_whenNotFound() {
        when(bookRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 99");
    }

    @Test
    void searchBooks_returnsMatchingBooks() {
        when(bookRepository.searchBooks("Clean Code", null, null, null)).thenReturn(List.of(book));

        List<BookDto> result = bookService.searchBooks("Clean Code", null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void createBook_savesAndReturnsBookDto() {
        when(bookRepository.existsByIsbnAndIsDeletedFalse(bookRequest.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = bookService.createBook(bookRequest);

        assertThat(result.getTitle()).isEqualTo("Clean Code");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_throwsDuplicateResourceException_whenIsbnExists() {
        when(bookRepository.existsByIsbnAndIsDeletedFalse(bookRequest.getIsbn())).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(bookRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("9780132350884");
    }

    @Test
    void createBook_throwsIllegalArgumentException_whenAvailableExceedsTotal() {
        BookRequest invalid = new BookRequest("Clean Code", "Robert C. Martin",
                "9780132350884", "Programming", 2, 5, "A1");

        when(bookRepository.existsByIsbnAndIsDeletedFalse(invalid.getIsbn())).thenReturn(false);

        assertThatThrownBy(() -> bookService.createBook(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Available copies cannot be greater than total copies");
    }

    @Test
    void updateBook_updatesAndReturnsBookDto() {
        when(bookRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbnAndIsDeletedFalseAndIdNot(bookRequest.getIsbn(), 1L)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = bookService.updateBook(1L, bookRequest);

        assertThat(result.getId()).isEqualTo(1L);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_throwsDuplicateResourceException_whenIsbnTakenByOtherBook() {
        when(bookRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbnAndIsDeletedFalseAndIdNot(bookRequest.getIsbn(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> bookService.updateBook(1L, bookRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateBook_throwsIllegalArgumentException_whenAvailableExceedsTotal() {
        BookRequest invalid = new BookRequest("Clean Code", "Robert C. Martin",
                "9780132350884", "Programming", 2, 5, "A1");

        when(bookRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbnAndIsDeletedFalseAndIdNot(invalid.getIsbn(), 1L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.updateBook(1L, invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Available copies cannot be greater than total copies");
    }

    @Test
    void deleteBook_setsIsDeletedTrue() {
        when(bookRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.deleteBook(1L);

        assertThat(book.getIsDeleted()).isTrue();
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_throwsResourceNotFoundException_whenNotFound() {
        when(bookRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 99");
    }
}
