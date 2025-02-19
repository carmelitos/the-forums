import { Component, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-email-sent',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './email-sent.component.html',
  styleUrls: ['./email-sent.component.scss']
})
export class EmailSentComponent implements OnDestroy {
  private readonly isBrowser: boolean;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    if (this.isBrowser) {
      localStorage.removeItem('emailSent');
    }
  }
}
