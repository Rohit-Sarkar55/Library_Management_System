import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BookService } from '../books/services/book.service';
import { MemberService } from '../members/services/member.service';
import { TransactionService } from '../transactions/services/transaction.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {

  totalBooks = 0;
  totalMembers = 0;
  activeIssues = 0;
  overdueBooks = 0;
  loading = false;
  error = '';

  constructor(
    private bookService: BookService,
    private memberService: MemberService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.loading = true;

    // Load books count
    this.bookService.getAllBooks().subscribe({
      next: (books) => {
        this.totalBooks = books.totalElements;
      }
    });

    // Load members count
    this.memberService.getAllMembers().subscribe({
      next: (members) => {
        this.totalMembers = members.length;
      }
    });

    // Load transactions stats
    this.transactionService.getAllTransactions().subscribe({
      next: (transactions) => {
        this.activeIssues = transactions.filter(
          t => t.status === 'ISSUED').length;
        this.overdueBooks = transactions.filter(
          t => t.status === 'OVERDUE').length;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load dashboard stats';
        this.loading = false;
      }
    });
  }
}