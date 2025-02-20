import { Routes } from '@angular/router';
import { EmailSentGuard } from './guards/email-sent.guard';
import {LoginComponent} from './modules/login/login.component';
import {RegisterComponent} from './modules/register/register.component';
import {EmailSentComponent} from './modules/email-sent/email-sent.component';
import {EmailVerifyComponent} from './modules/email-verify/email-verify.component';
import {PendingEmailVerificationGuard} from './guards/pending-email-verification.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'email-sent',
    component: EmailSentComponent,
    canActivate: [EmailSentGuard]
  },
  {
    path: 'email-verify/:token',
    component: EmailVerifyComponent,
    canActivate: [PendingEmailVerificationGuard]
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
