import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {environment} from '../enviroment/enviroment.prod';
import {OperationResult} from '../models/operation-result.model';

@Injectable({
  providedIn: 'root'
})
export class PasswordResetService {
  private readonly baseUrl = `${environment.apiUrl}/api/password`;

  constructor(private http: HttpClient) {}

  requestPasswordReset(email: string): Observable<OperationResult<string>> {
    return this.http.post<OperationResult<string>>(`${this.baseUrl}/request-reset`, email).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  resetPassword(token: string, newPassword: string): Observable<OperationResult<string>> {
    return this.http.post<OperationResult<string>>(`${this.baseUrl}/reset`, { token, newPassword }).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }
}
