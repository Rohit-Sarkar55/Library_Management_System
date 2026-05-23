import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-book-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './book-form.component.html',
  styleUrl: './book-form.component.scss'
})
export class BookFormComponent implements OnInit {

  bookForm: FormGroup;
  isEditMode = false;
  bookId: number | null = null;
  loading = false;
  submitting = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.bookForm = this.fb.group({
      title: ['', [Validators.required]],
      author: ['', [Validators.required]],
      isbn: ['', [Validators.required, Validators.pattern('^[0-9]{10}$|^[0-9]{13}$')]],
      category: [''],
      totalCopies: [1, [Validators.required, Validators.min(0)]],
      availableCopies: [1, [Validators.required, Validators.min(0)]],
      shelfLocation: ['']
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.bookId = +id;
      this.loadBook(this.bookId);
    }
  }

  loadBook(id: number): void {
    this.loading = true;
    this.bookService.getBookById(id).subscribe({
      next: (book) => {
        this.bookForm.patchValue({
          title: book.title,
          author: book.author,
          isbn: book.isbn,
          category: book.category,
          totalCopies: book.totalCopies,
          availableCopies: book.availableCopies,
          shelfLocation: book.shelfLocation
        });
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load book details';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.bookForm.invalid) {
      this.bookForm.markAllAsTouched();
      return;
    }

    // Validate available <= total
    const total = this.bookForm.value.totalCopies;
    const available = this.bookForm.value.availableCopies;
    if (available > total) {
      this.error = 'Available copies cannot be greater than total copies';
      return;
    }

    this.submitting = true;
    this.error = '';

    if (this.isEditMode && this.bookId) {
      this.bookService.updateBook(this.bookId, this.bookForm.value).subscribe({
        next: () => {
          this.router.navigate(['/books']);
        },
        error: (err) => {
          this.error = err.error?.error || 'Failed to update book';
          this.submitting = false;
        }
      });
    } else {
      this.bookService.createBook(this.bookForm.value).subscribe({
        next: () => {
          this.router.navigate(['/books']);
        },
        error: (err) => {
          this.error = err.error?.error || 'Failed to create book';
          this.submitting = false;
        }
      });
    }
  }

  // Helper for template validation
  isInvalid(field: string): boolean {
    const control = this.bookForm.get(field);
    return !!(control && control.invalid && control.touched);
  }
}