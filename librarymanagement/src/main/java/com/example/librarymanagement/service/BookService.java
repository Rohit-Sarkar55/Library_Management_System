package com.example.librarymanagement.service;


import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.dto.BookRequest;

import java.util.List;

public interface BookService {

    List<BookDto> getAllBooks();

    BookDto getBookById(Long id);

    List<BookDto> searchBooks(String title, String author, String isbn, String category);

    BookDto createBook(BookRequest request);

    BookDto updateBook(Long id, BookRequest request);

    void deleteBook(Long id);
}