import {OperationStatus} from './enums/operation-status.model';

export interface OperationResult<T> {
  status: OperationStatus;
  message: string;
  data?: T;
}
