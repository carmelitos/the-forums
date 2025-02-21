import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {PasswordResetService} from '../../services/password-reset.service';
import {OperationResult} from '../../models/operation-result.model';
import {OperationStatus} from '../../models/enums/operation-status.model';

@Component({
  selector: 'app-forgot-password-reset',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule
  ],
  templateUrl: './forgot-password-reset.component.html',
  styleUrls: ['./forgot-password-reset.component.scss']
})
export class ForgotPasswordResetComponent implements OnInit {
  resetForm: FormGroup;
  token: string | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  isSubmitting: boolean = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private passwordResetService: PasswordResetService
  ) {
    this.resetForm = this.fb.group(
      {
        newPassword: ['', Validators.required],
        confirmNewPassword: ['', Validators.required]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  ngOnInit(): void {
    this.token = this.route.snapshot.paramMap.get('token');
  }

  onSubmit(): void {
    if (!this.token) {
      this.errorMessage = 'No reset token found in the URL.';
      return;
    }
    if (this.resetForm.valid) {
      this.isSubmitting = true;
      const newPassword = this.resetForm.get('newPassword')?.value;
      this.passwordResetService.resetPassword(this.token, newPassword).subscribe({
        next: (res: OperationResult<string>) => {
          this.isSubmitting = false;
          if (res.status === OperationStatus.SUCCESS) {
            this.successMessage = res.message || 'Password has been reset successfully!';
            // Optionally redirect to login after a short delay
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 2000);
          } else {
            this.errorMessage = res.message;
          }
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.error?.message || 'Failed to reset password.';
        }
      });
    }
  }

  private passwordMatchValidator(form: FormGroup): null | object {
    const newPassword = form.get('newPassword')?.value;
    const confirmNewPassword = form.get('confirmNewPassword')?.value;
    return newPassword === confirmNewPassword ? null : { mismatch: true };
  }
}
