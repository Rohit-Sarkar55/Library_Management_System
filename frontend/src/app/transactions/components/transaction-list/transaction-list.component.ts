import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Transaction } from '../../models/transaction';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.scss'
})
export class TransactionListComponent implements OnInit {

  transactions: Transaction[] = [];
  loading = false;
  error = '';
  successMessage = '';

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.loading = true;
    this.error = '';
    this.transactionService.getAllTransactions().subscribe({
      next: (data) => {
        this.transactions = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load transactions';
        this.loading = false;
      }
    });
  }

  returnBook(transactionId: number): void {
    if (confirm('Confirm book return?')) {
      this.transactionService.returnBook(transactionId).subscribe({
        next: () => {
          this.successMessage = 'Book returned successfully';
          this.loadTransactions();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.error || 'Failed to return book';
        }
      });
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ISSUED':   return 'badge bg-primary';
      case 'RETURNED': return 'badge bg-success';
      case 'OVERDUE':  return 'badge bg-danger';
      default:         return 'badge bg-secondary';
    }
  }
}