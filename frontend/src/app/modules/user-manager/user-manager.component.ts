import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {MatSort, MatSortModule, Sort} from '@angular/material/sort';
import {UserSearchCriteria} from '../../models/user-search-criteria.model';
import {Page} from '../../models/page.model';
import {UserListItem} from '../../models/user-list-item.model';
import {UserService} from '../../services/user.service';
import {MatIconModule} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuPanel, MatMenuTrigger} from '@angular/material/menu';
import {MatOption, MatSelect, MatSelectTrigger} from '@angular/material/select';
import {MatPseudoCheckbox} from '@angular/material/core';
import {RoleService} from '../../services/role.service';
import {RoleDTO} from '../../models/role-dto.model';
import {PermissionDTO} from '../../models/permission-dto.model';
import {PermissionService} from '../../services/permission.service';

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
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    MatSelect,
    MatOption,
    MatSelectTrigger
  ],
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})
export class UserManagerComponent implements OnInit {
  filterForm: FormGroup;
  allRoles: RoleDTO[] = [];
  allPermissions: PermissionDTO[] = [];
  displayedColumns: string[] = [
    'id',
    'username',
    'email',
    'phoneNumber',
    'roles',
    'permissions'
  ];
  dataSource: UserListItem[] = [];
  totalElements = 0;
  pageSizeOptions = [10, 50, 100];
  pageIndex = 0;
  pageSize = 10;
  sortBy = 'id';
  sortDirection: 'ASC' | 'DESC' = 'ASC';
  currentRoles: string[] = [];
  currentPermissions: string[] = [];


  @ViewChild('tableContainer') tableContainer!: ElementRef<HTMLDivElement>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private roleService: RoleService,
    private permissionService: PermissionService,
  ) {
    this.filterForm = this.fb.group({
      idFilter: [''],
      usernameFilter: [''],
      emailFilter: [''],
      phoneNumberFilter: [''],

      rolesFilter: [[]],
      permissionsFilter: [[]],

      sortBy: ['id'],
      sortDirection: ['ASC']
    });
  }


  ngOnInit(): void {
    this.loadAllRolesAndPermissions();
    this.searchUsers();
    this.fixTableHeight();
  }

  searchUsers(): void {
    const formVal = this.filterForm.value;
    const criteria: UserSearchCriteria = {
      idFilter: formVal.idFilter ? Number(formVal.idFilter) : undefined,
      usernameFilter: formVal.usernameFilter,
      emailFilter: formVal.emailFilter,
      phoneNumberFilter: formVal.phoneNumberFilter,
      rolesFilter: formVal.rolesFilter || [],
      permissionsFilter: formVal.permissionsFilter || [],
      page: this.pageIndex,
      size: this.pageSize,
      sortBy: formVal.sortBy,
      sortDirection: formVal.sortDirection
    };

    this.userService.searchUsers(criteria).subscribe((page: Page<UserListItem>) => {
      this.dataSource = page.content;
      this.totalElements = page.totalElements;
    });
  }

  loadAllRolesAndPermissions(): void {
    this.roleService.getAll().subscribe((roles: RoleDTO[]) => {
      this.allRoles = roles;
    });

    this.permissionService.getAll().subscribe((perms: PermissionDTO[]) => {
      this.allPermissions = perms;
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

  openRoles(roles: string[]): void {
    this.currentRoles = roles;
  }

  openPermissions(perms: string[]): void {
    this.currentPermissions = perms;
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
