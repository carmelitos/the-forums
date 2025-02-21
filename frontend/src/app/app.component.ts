import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { Observable, Subscription } from 'rxjs';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { TokenStorageService } from './services/token-storage.service';
import {MatDivider} from '@angular/material/divider';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [CommonModule, RouterModule, MatToolbarModule, MatButtonModule, MatMenuModule, MatIconModule, MatDivider]
})
export class AppComponent implements OnInit, OnDestroy {
  isAuthenticated$: Observable<boolean>;
  username: string | null = null;
  private authSubscription!: Subscription;

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router
  ) {
    this.isAuthenticated$ = this.authService.isAuthenticatedSubject.asObservable();
  }

  ngOnInit(): void {
    this.authSubscription = this.isAuthenticated$.subscribe((isAuth) => {
      if (isAuth) {
        this.username = this.tokenStorage.getUsername();
      } else {
        this.username = null;
      }
    });
  }

  goToProfile(): void {
  }

  //todo handle logout errors
  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']).then();
      },
      error: () => {
        this.router.navigate(['/login']).then();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
