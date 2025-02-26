import {Injectable} from '@angular/core';
import {environment} from '../enviroment/enviroment.prod';
import {HttpClient} from '@angular/common/http';
import {RoleDTO} from '../models/role-dto.model';
import {Observable} from 'rxjs';
import {PermissionDTO} from '../models/permission-dto.model';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private baseUrl = `${environment.apiUrl}/api/permissions`;

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<PermissionDTO[]> {
    return this.http.get<PermissionDTO[]>(this.baseUrl);
  }
}
