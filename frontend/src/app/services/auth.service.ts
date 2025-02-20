import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { TokenStorageService } from './token-storage.service';
import { environment } from '../enviroment/enviroment.prod';

export interface AuthenticationRequest {
  username: string;
  password: string;
}

export interface AuthenticationResponse {
  accessToken: string;
  refreshToken: string;
}

export interface UserDTO {
  username: string;
  email: string;
  password: string;
}

export enum OperationStatus {
  SUCCESS = 'SUCCESS',
  FAILURE = 'FAILURE'
}

export interface OperationResult<T> {
  status: OperationStatus;
  message: string;
  data?: T;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/api/auth`;

  public isAuthenticatedSubject: BehaviorSubject<boolean>;

  constructor(
    private http: HttpClient,
    private tokenStorage: TokenStorageService
  ) {
    this.isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidAccessToken());
  }

  private hasValidAccessToken(): boolean {
    return !!this.tokenStorage.getAccessToken();
  }

  login(request: AuthenticationRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.baseUrl}/login`, request).pipe(
      tap((response) => {
        this.tokenStorage.setAccessToken(response.accessToken);
        this.tokenStorage.setRefreshToken(response.refreshToken);
        this.isAuthenticatedSubject.next(true);
      }),
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  register(user: UserDTO): Observable<OperationResult<number>> {
    return this.http.post<OperationResult<number>>(`${this.baseUrl}/register`, user).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  sendVerificationEmail(email: string): Observable<OperationResult<string>> {
    return this.http.post<OperationResult<string>>(`${this.baseUrl}/send-verification-email`, email).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  sendEmailVerify(token: string): Observable<OperationResult<string>> {
    return this.http.post<OperationResult<string>>(`${this.baseUrl}/verify-email`, token).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  refreshToken(): Observable<AuthenticationResponse> {
    const refreshToken = this.tokenStorage.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    return this.http.post<AuthenticationResponse>(`${this.baseUrl}/refresh-token`, { refreshToken }).pipe(
      tap((response) => {
        this.tokenStorage.setAccessToken(response.accessToken);
        this.tokenStorage.setRefreshToken(response.refreshToken);
        this.isAuthenticatedSubject.next(true);
      }),
      catchError((error) => {
        this.logout();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    this.tokenStorage.clear();
    this.isAuthenticatedSubject.next(false);
  }
}
