import {PermissionDTO} from './permission-dto.model';

export interface RoleDTO {
  name: string;
  default: boolean;
  permissions: PermissionDTO[];
}
