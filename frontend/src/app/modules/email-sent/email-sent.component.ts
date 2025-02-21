import {Component, OnInit, Inject, PLATFORM_ID, OnDestroy} from '@angular/core';
import {isPlatformBrowser} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-email-sent',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './email-sent.component.html',
  styleUrls: ['./email-sent.component.scss']
})
export class EmailSentComponent implements OnInit, OnDestroy {
  private readonly isBrowser: boolean;
  mode: 'verify' | 'reset' | null = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.mode = (params.get('mode') as 'verify' | 'reset') || null;
    });
  }

  ngOnDestroy(): void {
    if (this.isBrowser) {
      localStorage.removeItem('emailSent');
    }
  }
}
