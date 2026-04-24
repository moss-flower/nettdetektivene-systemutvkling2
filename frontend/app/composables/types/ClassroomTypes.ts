export type ClassroomUser = {
  publicUuid: string;
  username: string;
};

export type Classroom = {
  uid: string;
  name: string;
  description: string;
  worldUid: string;
  code: string;
  owners: ClassroomUser[];
  members: ClassroomUser[];
};

export type ClassroomRequest = {
  name: string;
  description: string;
  worldUid: string;
};
