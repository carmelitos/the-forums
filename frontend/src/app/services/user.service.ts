import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../enviroment/enviroment.prod';
import {UserDTO} from '../models/user-dto.model';
import {Page} from '../models/page.model';
import {UserSearchCriteria} from '../models/user-search-criteria.model';
import {RoleDTO} from '../models/role-dto.model';
import {UserListItem} from '../models/user-list-item.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = `${environment.apiUrl}/api/users`;

  constructor(private http: HttpClient) {
  }

  public getUserRoles(userId: number): Observable<RoleDTO[]> {
    return this.http.get<RoleDTO[]>(`${this.baseUrl}/users/${userId}/roles`);
  }

  userHasAnyRoles(userId: number, roleNames: string[]): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/${userId}/has-any-roles`, roleNames);
  }

  searchUsers(criteria: UserSearchCriteria): Observable<Page<UserListItem>> {
    return this.http.post<Page<UserListItem>>(`${this.baseUrl}/search`, criteria);
  }


}
