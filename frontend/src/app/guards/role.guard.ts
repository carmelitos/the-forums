import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router
} from '@angular/router';
import { Observable, map } from 'rxjs';
import { UserService } from '../services/user.service';
import {TokenStorageService} from '../services/token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private tokenStorage: TokenStorageService,
    private userService: UserService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> {
    const requiredRoles = route.data['roles'] as string[];
    const userId = this.tokenStorage.getUserIdFromToken();
    if (!userId) {
      this.router.navigate(['/login']);
      return new Observable<boolean>(observer => {
        observer.next(false);
        observer.complete();
      });
    }

    return this.userService.userHasAnyRoles(userId, requiredRoles).pipe(
      map(hasRole => {
        if (!hasRole) {
          this.router.navigate(['/unauthorized']);
          return false;
        }
        return true;
      })
    );
  }
}
