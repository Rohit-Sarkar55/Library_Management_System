import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MemberService } from '../../services/member.service';

@Component({
  selector: 'app-member-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './member-form.component.html',
  styleUrl: './member-form.component.scss'
})
export class MemberFormComponent implements OnInit {

  memberForm: FormGroup;
  isEditMode = false;
  memberId: number | null = null;
  loading = false;
  submitting = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private memberService: MemberService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.memberForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.memberId = +id;
      this.loadMember(this.memberId);
    }
  }

  loadMember(id: number): void {
    this.loading = true;
    this.memberService.getMemberById(id).subscribe({
      next: (member) => {
        this.memberForm.patchValue({
          name: member.name,
          email: member.email
        });
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load member details';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.memberForm.invalid) {
      this.memberForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.error = '';

    if (this.isEditMode && this.memberId) {
      this.memberService.updateMember(this.memberId, this.memberForm.value).subscribe({
        next: () => {
          this.router.navigate(['/members']);
        },
        error: (err) => {
          this.error = err.error?.error || 'Failed to update member';
          this.submitting = false;
        }
      });
    } else {
      this.memberService.createMember(this.memberForm.value).subscribe({
        next: () => {
          this.router.navigate(['/members']);
        },
        error: (err) => {
          this.error = err.error?.error || 'Failed to create member';
          this.submitting = false;
        }
      });
    }
  }

  isInvalid(field: string): boolean {
    const control = this.memberForm.get(field);
    return !!(control && control.invalid && control.touched);
  }
}