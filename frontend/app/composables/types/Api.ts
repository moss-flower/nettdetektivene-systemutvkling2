//requests
export interface ClassroomRequest {
  name: string;
  description?: string;
  worldUid: string;
}

//responses
export interface OwnerSummary {
  publicUuid: string;
  username: string;
}

export interface MemberSummary {
  publicUuid: string;
  username: string;
  score: number;
}

export interface ClassroomResponse {
  uid: string;
  name: string;
  description: string;
  worldUid: string;
  code: string;
  owners: OwnerSummary[];
  members: MemberSummary[];
}
