import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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
import { UserListItem } from '../../models/user-list-item.model';  // <-- new model
import { UserService } from '../../services/user.service';

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
    MatSortModule
  ],
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})
export class UserManagerComponent implements OnInit {
  filterForm: FormGroup;
  displayedColumns: string[] = ['id', 'username', 'email', 'phoneNumber'];
  dataSource: UserListItem[] = [];  // <-- replaced UserDTO[] with UserListItem[]
  totalElements = 0;
  pageSizeOptions = [10, 50, 100];
  pageIndex = 0;
  pageSize = 10;
  sortBy = 'id';
  sortDirection: 'ASC' | 'DESC' = 'ASC';

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

    // subscribe to Page<UserListItem>
    this.userService.searchUsers(criteria).subscribe((page: Page<UserListItem>) => {
      this.dataSource = page.content;
      this.totalElements = page.totalElements;
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
}
