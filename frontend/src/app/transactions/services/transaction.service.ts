import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transaction, IssueRequest } from '../models/transaction';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.apiUrl);
  }

  getTransactionsByMember(memberId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/member/${memberId}`);
  }

  getTransactionsByBook(bookId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/book/${bookId}`);
  }

  issueBook(request: IssueRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/issue`, request);
  }

  returnBook(transactionId: number): Observable<Transaction> {
    return this.http.post<Transaction>(
      `${this.apiUrl}/${transactionId}/return`, {});
  }
}