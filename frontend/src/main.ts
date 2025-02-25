
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import {
  HTTP_INTERCEPTORS, HttpClientModule,
  provideHttpClient, withInterceptors, withInterceptorsFromDi
} from '@angular/common/http';
import {AuthInterceptor} from './app/interceptors/auth.interceptor';
import {routes} from './app/app.routes';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {BrowserAnimationsModule, provideAnimations} from '@angular/platform-browser/animations';
import {importProvidersFrom} from '@angular/core';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide:HTTP_INTERCEPTORS,
      useClass:AuthInterceptor,
      multi:true
    },
    provideAnimationsAsync(),
    { provide: BrowserAnimationsModule, useValue: BrowserAnimationsModule }
  ]
}).catch((err) => console.error(err));
