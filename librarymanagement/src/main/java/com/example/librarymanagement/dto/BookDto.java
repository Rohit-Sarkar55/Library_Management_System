package com.example.librarymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    private String title;

    private String author;

    private String isbn;

    private String category;

    private Integer totalCopies;

    private Integer availableCopies;

    private String shelfLocation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
