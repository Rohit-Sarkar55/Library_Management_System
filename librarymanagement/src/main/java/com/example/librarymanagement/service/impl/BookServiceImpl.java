package com.example.librarymanagement.service.impl;



import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.exceptions.DuplicateResourceException;
import com.example.librarymanagement.exceptions.ResourceNotFoundException;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAllActiveBooks(pageable)
                .map(this::mapToDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = findBookById(id);
        return mapToDto(book);
    }

    @Override
    public List<BookDto> searchBooks(String title, String author, String isbn, String category) {
        return bookRepository.searchBooks(title, author, isbn, category)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookDto createBook(BookRequest request) {
        // Check duplicate ISBN
        if (bookRepository.existsByIsbnAndIsDeletedFalse(request.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        // Validate available copies <= total copies
        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot be greater than total copies");
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .category(request.getCategory())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getAvailableCopies())
                .shelfLocation(request.getShelfLocation())
                .isDeleted(false)
                .build();

        return mapToDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookDto updateBook(Long id, BookRequest request) {
        Book book = findBookById(id);

        // Check duplicate ISBN — ignore current book's own ISBN
        if (bookRepository.existsByIsbnAndIsDeletedFalseAndIdNot(request.getIsbn(), id)) {
            throw new DuplicateResourceException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        // Validate available copies <= total copies
        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot be greater than total copies");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getAvailableCopies());
        book.setShelfLocation(request.getShelfLocation());

        return mapToDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookById(id);
        book.setIsDeleted(true);
        bookRepository.save(book);
    }

    // ─── Private helpers ───────────────────────────────────────

    private Book findBookById(Long id) {
        return bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    private BookDto mapToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .shelfLocation(book.getShelfLocation())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
