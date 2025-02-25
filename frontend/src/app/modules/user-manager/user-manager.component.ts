import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { UserSearchCriteria } from '../../models/user-search-criteria.model';
import { Page } from '../../models/page.model';
import { UserListItem } from '../../models/user-list-item.model';
import { UserService } from '../../services/user.service';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-user-manager',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
  ],
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})
export class UserManagerComponent implements OnInit {
  filterForm: FormGroup;
  displayedColumns: string[] = ['id', 'username', 'email', 'phoneNumber'];
  dataSource: UserListItem[] = [];
  totalElements = 0;
  pageSizeOptions = [10, 50, 100];
  pageIndex = 0;
  pageSize = 10;
  sortBy = 'id';
  sortDirection: 'ASC' | 'DESC' = 'ASC';

  // Reference the table container for dynamic height
  @ViewChild('tableContainer') tableContainer!: ElementRef<HTMLDivElement>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.filterForm = this.fb.group({
      idFilter: [''],
      usernameFilter: [''],
      emailFilter: [''],
      phoneNumberFilter: ['']
    });
  }

  ngOnInit(): void {
    this.searchUsers();
    this.fixTableHeight();
  }

  searchUsers(): void {
    const criteria: UserSearchCriteria = {
      idFilter: this.filterForm.value.idFilter ? Number(this.filterForm.value.idFilter) : null,
      usernameFilter: this.filterForm.value.usernameFilter,
      emailFilter: this.filterForm.value.emailFilter,
      phoneNumberFilter: this.filterForm.value.phoneNumberFilter,
      page: this.pageIndex,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection
    };

    this.userService.searchUsers(criteria).subscribe((page: Page<UserListItem>) => {
      this.dataSource = page.content;
      this.totalElements = page.totalElements;
      this.fixTableHeight();
    });
  }

  applyFilters(): void {
    this.pageIndex = 0;
    this.searchUsers();
  }

  onPageEvent(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.searchUsers();
  }

  onSortData(sort: Sort): void {
    if (!sort.active || sort.direction === '') {
      this.sortBy = 'id';
      this.sortDirection = 'ASC';
    } else {
      this.sortBy = sort.active;
      this.sortDirection = sort.direction.toUpperCase() as 'ASC' | 'DESC';
    }
    this.searchUsers();
  }

  private fixTableHeight(): void {
    const rowHeight = 48;   // px per row
    const headerHeight = 48; // px for the table header row
    const container = this.tableContainer.nativeElement;

    const totalHeight = (this.pageSize * rowHeight) + headerHeight;
    container.style.height = totalHeight + 'px';
    container.style.overflowY = 'auto';
  }
}
