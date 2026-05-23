import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Member } from '../../models/member';
import { MemberService } from '../../services/member.service';

@Component({
  selector: 'app-member-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './member-list.component.html',
  styleUrl: './member-list.component.scss'
})
export class MemberListComponent implements OnInit {

  members: Member[] = [];
  loading = false;
  error = '';
  successMessage = '';

  constructor(private memberService: MemberService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  loadMembers(): void {
    this.loading = true;
    this.error = '';
    this.memberService.getAllMembers().subscribe({
      next: (data) => {
        this.members = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load members';
        this.loading = false;
      }
    });
  }

  deactivateMember(id: number): void {
    if (confirm('Are you sure you want to deactivate this member?')) {
      this.memberService.deactivateMember(id).subscribe({
        next: () => {
          this.successMessage = 'Member deactivated successfully';
          this.loadMembers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.error || 'Failed to deactivate member';
        }
      });
    }
  }
}