package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.service.BookService;
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

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    // Don't autowire — create directly
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private BookDto bookDto;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        bookDto = BookDto.builder()
                .id(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .category("Programming")
                .totalCopies(5)
                .availableCopies(3)
                .shelfLocation("A1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookRequest = new BookRequest("Clean Code", "Robert C. Martin",
                "9780132350884", "Programming", 5, 3, "A1");
    }

    @Test
    void getAllBooks_returnsListOfBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(bookDto));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void getBookById_returnsBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(bookDto);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void searchBooks_returnsMatchingBooks() throws Exception {
        when(bookService.searchBooks("Clean Code", null, null, null))
                .thenReturn(List.of(bookDto));

        mockMvc.perform(get("/api/books/search")
                        .param("title", "Clean Code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void createBook_returnsCreatedBook() throws Exception {
        when(bookService.createBook(any(BookRequest.class))).thenReturn(bookDto);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void updateBook_returnsUpdatedBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookRequest.class))).thenReturn(bookDto);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteBook_returnsNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createBook_withInvalidRequest_returnsBadRequest() throws Exception {
        BookRequest invalid = new BookRequest("", "", "bad-isbn", null, -1, -1, null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}