import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Member, MemberRequest } from '../models/member';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MemberService {

  private apiUrl = `${environment.apiUrl}/members`;

  constructor(private http: HttpClient) {}

  getAllMembers(): Observable<Member[]> {
    return this.http.get<Member[]>(this.apiUrl);
  }

  getMemberById(id: number): Observable<Member> {
    return this.http.get<Member>(`${this.apiUrl}/${id}`);
  }

  createMember(member: MemberRequest): Observable<Member> {
    return this.http.post<Member>(this.apiUrl, member);
  }

  updateMember(id: number, member: MemberRequest): Observable<Member> {
    return this.http.put<Member>(`${this.apiUrl}/${id}`, member);
  }

  deactivateMember(id: number): Observable<Member> {
    return this.http.patch<Member>(`${this.apiUrl}/${id}/deactivate`, {});
  }
}