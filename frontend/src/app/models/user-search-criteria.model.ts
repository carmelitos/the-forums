export interface UserSearchCriteria {
  idFilter?: number | null;
  usernameFilter?: string | null;
  emailFilter?: string | null;
  phoneNumberFilter?: string | null;

  rolesFilter?: string[];
  permissionsFilter?: string[];

  page: number;
  size: number;
  sortBy: string;
  sortDirection: 'ASC' | 'DESC';
}
