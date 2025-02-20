import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-email-verify',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './email-verify.component.html',
  styleUrls: ['./email-verify.component.scss']
})
export class EmailVerifyComponent implements OnInit {
  token: string | null | undefined;
  errorMessage: string | "" | undefined;
  successMessage: string | "" | undefined;
  verifyButtonClicked: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit() {
    this.token = this.route.snapshot.paramMap.get('token');
  }

  verify(): void {
    if (this.token == null) {
      return;
    }
    if (this.verifyButtonClicked) {
      return;
    }

    this.verifyButtonClicked = true;

    this.authService.sendEmailVerify(this.token).subscribe({
      next: result => {
        this.successMessage = "Verification completed! You are being redirected to login page!"
        setTimeout(() => {
          this.router.navigate(['/login']).then();
        }, 3000);
      },
      error: error => {
        this.verifyButtonClicked = false;
        this.errorMessage = error.error?.message;
      }
    });
  }


}
