export interface StudentRegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface StudentRegisterResponse {
  publicUuid: string;
  username: string;
  email: string;
  roles: string[];
  message: string;
}