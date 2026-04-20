export type ClassroomUser = {
  publicUuid: string;
  username: string;
};

export type Classroom = {
  id: number;
  name: string;
  description: string;
  code: string;
  owners: ClassroomUser[];
  members: ClassroomUser[];
};

export type ClassroomRequest = {
  name: string;
  description: string;
};