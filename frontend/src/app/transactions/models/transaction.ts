export interface Transaction {
  id: number;
  bookId: number;
  bookTitle: string;
  bookIsbn: string;
  memberId: number;
  memberName: string;
  memberEmail: string;
  status: 'ISSUED' | 'RETURNED' | 'OVERDUE';
  issuedAt: string;
  dueDate: string;
  returnedAt: string;
  createdAt: string;
}

export interface IssueRequest {
  memberId: number;
  bookId: number;
}