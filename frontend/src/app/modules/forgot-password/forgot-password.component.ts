import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {PasswordResetService} from '../../services/password-reset.service';
import {OperationStatus} from '../../models/enums/operation-status.model';
import {OperationResult} from '../../models/operation-result.model';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule
  ]
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  errorMessage: string | null = null;
  isRequesting: boolean = false;

  constructor(
    private fb: FormBuilder,
    private passwordResetService: PasswordResetService,
    private router: Router
  ) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.forgotForm.valid) {
      this.isRequesting = true;
      this.errorMessage = null;

      const emailValue = this.forgotForm.get('email')?.value;
      this.passwordResetService.requestPasswordReset(emailValue).subscribe({
        next: (result: OperationResult<string>) => {
          this.isRequesting = false;
          if (result.status === OperationStatus.SUCCESS) {
            this.router.navigate(['/email-sent'], { queryParams: { mode: 'reset' } });
          } else {
            this.errorMessage = result.message;
          }
        },
        error: (err) => {
          this.isRequesting = false;
          this.errorMessage = err.error?.message || 'Failed to request password reset.';
        }
      });
    }
  }
}
