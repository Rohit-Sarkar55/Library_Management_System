import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Book, PageResponse } from '../../models/book';
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit {

  books: Book[] = [];
  loading = false;
  error = '';
  successMessage = '';

  // Search fields
  searchTitle = '';
  searchAuthor = '';
  searchIsbn = '';
  searchCategory = '';
  isSearchMode = false;

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading = true;
    this.error = '';
    this.isSearchMode = false;
    this.bookService.getAllBooks(this.currentPage, this.pageSize).subscribe({
      next: (data) => {
        this.books = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load books';
        this.loading = false;
      }
    });
  }

  searchBooks(): void {
    this.loading = true;
    this.error = '';
    this.isSearchMode = true;
    this.currentPage = 0;
    this.bookService.searchBooks(
      this.searchTitle,
      this.searchAuthor,
      this.searchIsbn,
      this.searchCategory
    ).subscribe({
      next: (data : Book[]) => {
        this.books = data;
        this.totalPages = 0;
        this.totalElements = data.length;
        this.loading = false;
      },
      error: () => {
        this.error = 'Search failed';
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchTitle = '';
    this.searchAuthor = '';
    this.searchIsbn = '';
    this.searchCategory = '';
    this.currentPage = 0;
    this.isSearchMode = false;
    this.loadBooks();
  }

  deleteBook(id: number): void {
    if (confirm('Are you sure you want to delete this book?')) {
      this.bookService.deleteBook(id).subscribe({
        next: () => {
          this.successMessage = 'Book deleted successfully';
          this.loadBooks();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: () => {
          this.error = 'Failed to delete book';
        }
      });
    }
  }

  // Pagination methods
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadBooks();
    }
  }

  previousPage(): void {
    this.goToPage(this.currentPage - 1);
  }

  nextPage(): void {
    this.goToPage(this.currentPage + 1);
  }

  getPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}