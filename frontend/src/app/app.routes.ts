import { Routes } from '@angular/router';
import { EmailSentGuard } from './guards/email-sent.guard';
import { LoginComponent } from './modules/login/login.component';
import { RegisterComponent } from './modules/register/register.component';
import { EmailSentComponent } from './modules/email-sent/email-sent.component';
import { EmailVerifyComponent } from './modules/email-verify/email-verify.component';
import {AlreadyAuthenticatedGuard} from './guards/already-auth.guard';
import {MaintenanceComponent} from './modules/maintenance/maintenance.component';
import {AuthGuard} from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [AlreadyAuthenticatedGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [AlreadyAuthenticatedGuard] },
  {
    path: 'email-sent',
    component: EmailSentComponent,
    canActivate: [EmailSentGuard]
  },
  { path: 'email-verify/:token', component: EmailVerifyComponent },
  { path: 'maintenance', component: MaintenanceComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
