import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { BookService } from '../../../books/services/book.service';
import { MemberService } from '../../../members/services/member.service';
import { TransactionService } from '../../services/transaction.service';
import { Book } from '../../../books/models/book';
import { Member } from '../../../members/models/member';

@Component({
  selector: 'app-issue-book',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './issue-book.component.html',
  styleUrl: './issue-book.component.scss'
})
export class IssueBookComponent implements OnInit {

  issueForm: FormGroup;
  books: Book[] = [];
  members: Member[] = [];
  loading = false;
  submitting = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private bookService: BookService,
    private memberService: MemberService,
    private router: Router
  ) {
    this.issueForm = this.fb.group({
      memberId: ['', Validators.required],
      bookId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadBooks();
    this.loadMembers();
  }

  loadBooks(): void {
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        // Only show books with available copies
        this.books = data.filter(b => b.availableCopies > 0);
      },
      error: () => {
        this.error = 'Failed to load books';
      }
    });
  }

  loadMembers(): void {
    this.memberService.getAllMembers().subscribe({
      next: (data) => {
        // Only show active members
        this.members = data.filter(m => m.status === 'ACTIVE');
      },
      error: () => {
        this.error = 'Failed to load members';
      }
    });
  }

  onSubmit(): void {
    if (this.issueForm.invalid) {
      this.issueForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.error = '';

    this.transactionService.issueBook(this.issueForm.value).subscribe({
      next: () => {
        this.router.navigate(['/transactions']);
      },
      error: (err) => {
        this.error = err.error?.error || 'Failed to issue book';
        this.submitting = false;
      }
    });
  }

  isInvalid(field: string): boolean {
    const control = this.issueForm.get(field);
    return !!(control && control.invalid && control.touched);
  }
}