import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TransactionListComponent } from './components/transaction-list/transaction-list.component';
import { IssueBookComponent } from './components/issue-book/issue-book.component';

const routes: Routes = [
  { path: '',      component: TransactionListComponent },
  { path: 'issue', component: IssueBookComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TransactionsRoutingModule {}