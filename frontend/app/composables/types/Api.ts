//requests
export interface ClassroomRequest {
  name: string;
  description?: string;
}

//responses
export interface OwnerSummary {
  publicUuid: string;
  username: string;
}

export interface MemberSummary {
  publicUuid: string;
  username: string;
}

export interface ClassroomResponse {
  id: number;
  name: string;
  description: string;
  code: string;
  owners: OwnerSummary[];
  members: MemberSummary[];
}