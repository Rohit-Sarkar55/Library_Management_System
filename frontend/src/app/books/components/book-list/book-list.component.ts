import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Book } from '../../models/book';
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

  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading = true;
    this.error = '';
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data;
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
    this.bookService.searchBooks(
      this.searchTitle,
      this.searchAuthor,
      this.searchIsbn,
      this.searchCategory
    ).subscribe({
      next: (data) => {
        this.books = data;
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
}