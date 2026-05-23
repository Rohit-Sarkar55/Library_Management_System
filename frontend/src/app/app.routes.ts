import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'books',
    loadChildren: () =>
      import('./books/books.module').then(m => m.BooksModule)
  },
  {
    path: 'members',
    loadChildren: () =>
      import('./members/members.module').then(m => m.MembersModule)
  },
  {
    path: 'transactions',
    loadChildren: () =>
      import('./transactions/transactions.module').then(m => m.TransactionsModule)
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];