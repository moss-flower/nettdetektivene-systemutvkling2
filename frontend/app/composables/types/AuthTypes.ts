export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  publicUuid: string;
  username: string;
  email: string;
  roles: string[];
  message: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}