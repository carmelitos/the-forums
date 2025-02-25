import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {UserDTO} from '../../models/user-dto.model';
import {OperationResult} from '../../models/operation-result.model';
import {AuthService} from '../../services/auth.service';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  isRegistering: boolean;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.isRegistering = false;
    this.registerForm = this.fb.group(
      {
        username: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required]
      },
      {validators: this.passwordMatchValidator}
    );
  }

  private passwordMatchValidator(form: FormGroup): null | object {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return password === confirmPassword ? null : {mismatch: true};
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const user: UserDTO = {
        username: this.registerForm.get('username')?.value,
        email: this.registerForm.get('email')?.value,
        password: this.registerForm.get('password')?.value
      };

      if (this.isRegistering) return;

      this.isRegistering = true;
      this.authService.register(user).subscribe({
        next: (result: OperationResult<number>) => {
          if (result.status === 'SUCCESS') {
            this.authService.sendVerificationEmail(user.email).subscribe();
            localStorage.setItem('emailSent', 'true');
            this.router.navigate(['/email-sent'], { queryParams: { mode: 'verify' } }).then();
          } else {
            this.isRegistering = false;
            this.errorMessage = result.message;
          }
        },
        error: (err) => {
          console.error('Registration failed', err);
          this.isRegistering = false;
          this.errorMessage = err.error?.message || 'Registration failed. Please try again.';
        }
      });
    }
  }
}
