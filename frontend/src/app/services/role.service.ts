import {Injectable} from '@angular/core';
import {environment} from '../enviroment/enviroment.prod';
import {HttpClient} from '@angular/common/http';
import {RoleDTO} from '../models/role-dto.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private baseUrl = `${environment.apiUrl}/api/roles`;

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<RoleDTO[]> {
    return this.http.get<RoleDTO[]>(this.baseUrl);
  }
}
