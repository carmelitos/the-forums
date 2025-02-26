export interface UserListItem {
  id: number;
  username: string;
  email: string;
  phoneNumber: string;

  roles: string[];
  permissions: string[];

}
