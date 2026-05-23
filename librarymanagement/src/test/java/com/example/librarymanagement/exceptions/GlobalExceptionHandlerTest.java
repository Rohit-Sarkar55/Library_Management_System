package com.example.librarymanagement.exceptions;

import com.example.librarymanagement.controller.BookController;
import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void handleResourceNotFoundException_returns404() throws Exception {
        when(bookService.getBookById(99L))
                .thenThrow(new ResourceNotFoundException("Book not found with id: 99"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Book not found with id: 99"));
    }

    @Test
    void handleDuplicateResourceException_returns409() throws Exception {
        when(bookService.createBook(any()))
                .thenThrow(new DuplicateResourceException("Book with ISBN 9780132350884 already exists"));

        BookRequest request = new BookRequest("Clean Code", "Robert C. Martin",
                "9780132350884", "Programming", 5, 3, "A1");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Book with ISBN 9780132350884 already exists"));
    }

    @Test
    void handleValidationException_returns400WithFieldErrors() throws Exception {
        BookRequest invalid = new BookRequest("", "", "bad", null, -1, -1, null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.messages").isMap());
    }

    @Test
    void handleIllegalArgumentException_returns400() throws Exception {
        when(bookService.createBook(any()))
                .thenThrow(new IllegalArgumentException("Available copies cannot be greater than total copies"));

        BookRequest request = new BookRequest("Clean Code", "Robert C. Martin",
                "9780132350884", "Programming", 2, 5, "A1");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Available copies cannot be greater than total copies"));
    }

    @Test
    void handleGlobalException_returns500() throws Exception {
        when(bookService.getAllBooks())
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));
    }
}
