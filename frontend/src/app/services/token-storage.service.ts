import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private readonly isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  public getAccessToken(): string | null {
    return this.isBrowser ? localStorage.getItem('accessToken') : null;
  }

  public setAccessToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem('accessToken', token);
    }
  }

  public getRefreshToken(): string | null {
    return this.isBrowser ? localStorage.getItem('refreshToken') : null;
  }

  public setRefreshToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem('refreshToken', token);
    }
  }

  public clear(): void {
    if (this.isBrowser) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  }

  public getUsername(): string | null {
    const token = this.getAccessToken();
    if (!token) {
      return null;
    }
    try {
      const payload = token.split('.')[1];
      // Replace URL-safe characters to standard Base64 characters
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const decodedPayload = JSON.parse(atob(base64));
      return decodedPayload.sub || null;
    } catch (error) {
      console.error('Error decoding JWT token:', error);
      return null;
    }
  }
}
