import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { TransactionsRoutingModule } from './transactions-routing.module';
import { TransactionListComponent } from './components/transaction-list/transaction-list.component';
import { IssueBookComponent } from './components/issue-book/issue-book.component';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TransactionsRoutingModule,
    TransactionListComponent,
    IssueBookComponent
  ]
})
export class TransactionsModule {}