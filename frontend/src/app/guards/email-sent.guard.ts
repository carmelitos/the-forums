import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class EmailSentGuard implements CanActivate {
  private readonly isBrowser: boolean;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  canActivate(): boolean {
    if (!this.isBrowser) {
      return true;
    }

    const emailSentFlag = localStorage.getItem('emailSent');
    if (emailSentFlag === 'true') {
      return true;
    }

    this.router.navigate(['/register']);
    return false;
  }
}
