export type StudentLeaderboardEntry = {
  id: string;
  name: string;
  classroomId: string;
  completedLevels: number;
  totalLevels: number;
  points: number;
};

export type StudentOverviewEntry = StudentLeaderboardEntry & {
  badges: number;
  lastActive: string;
};

export type PendingStudentEntry = {
  id: string;
  name: string;
  requestedAt: string;
};

export type ClassroomSummary = {
  id: string;
  name: string;
  worldUid: string;
  totalScore: number;
  studentCount: number;
  averageProgressPercent: number;
};

export type ClassroomNotification = {
  id: string;
  classroomId: string;
  title: string;
  description: string;
  timeLabel: string;
};

export type ClassroomExercise = {
  uid: string;
  classroomId: string;
  title: string;
  status: string;
  level: string;
};

export type teacherClassroomMystery = {
  classroomId: string;
  title: string;
  description: string;
  status: string;
};

const TOTAL_LEVELS = 7;

const mockStudents: StudentLeaderboardEntry[] = [
  { id: 'ada-berg', name: 'Ada Berg', classroomId: 'ronvik-6a', completedLevels: 6, totalLevels: TOTAL_LEVELS, points: 1280 },
  { id: 'liam-dahl', name: 'Liam Dahl', classroomId: 'ronvik-6a', completedLevels: 4, totalLevels: TOTAL_LEVELS, points: 910 },
  { id: 'mina-hagen', name: 'Mina Hagen', classroomId: 'ronvik-6a', completedLevels: 5, totalLevels: TOTAL_LEVELS, points: 1140 },
  { id: 'tobias-solheim', name: 'Tobias Solheim', classroomId: 'ronvik-6a', completedLevels: 2, totalLevels: TOTAL_LEVELS, points: 520 },
  { id: 'nora-eide', name: 'Nora Eide', classroomId: 'ronvik-6b', completedLevels: 7, totalLevels: TOTAL_LEVELS, points: 1430 },
  { id: 'isak-foss', name: 'Isak Foss', classroomId: 'ronvik-6b', completedLevels: 3, totalLevels: TOTAL_LEVELS, points: 740 },
  { id: 'oskar-vik', name: 'Oskar Vik', classroomId: 'ronvik-6b', completedLevels: 4, totalLevels: TOTAL_LEVELS, points: 930 },
  { id: 'sara-olesen', name: 'Sara Olesen', classroomId: 'ronvik-6b', completedLevels: 5, totalLevels: TOTAL_LEVELS, points: 1090 },
  { id: 'emilie-nilsen', name: 'Emilie Nilsen', classroomId: 'ronvik-6c', completedLevels: 6, totalLevels: TOTAL_LEVELS, points: 1310 },
  { id: 'leah-aas', name: 'Leah Aas', classroomId: 'ronvik-6c', completedLevels: 5, totalLevels: TOTAL_LEVELS, points: 1170 },
  { id: 'sander-lunde', name: 'Sander Lunde', classroomId: 'ronvik-6c', completedLevels: 3, totalLevels: TOTAL_LEVELS, points: 760 },
  { id: 'jonas-moen', name: 'Jonas Moen', classroomId: 'ronvik-6c', completedLevels: 1, totalLevels: TOTAL_LEVELS, points: 310 }
];

const studentExtras: Record<string, { badges: number; lastActive: string }> = {
  'ada-berg': { badges: 8, lastActive: 'I dag' },
  'liam-dahl': { badges: 5, lastActive: 'I går' },
  'mina-hagen': { badges: 7, lastActive: 'I dag' },
  'tobias-solheim': { badges: 2, lastActive: 'For 2 dager siden' },
  'nora-eide': { badges: 10, lastActive: 'I dag' },
  'isak-foss': { badges: 3, lastActive: 'I går' },
  'oskar-vik': { badges: 4, lastActive: 'I går' },
  'sara-olesen': { badges: 6, lastActive: 'I dag' },
  'emilie-nilsen': { badges: 9, lastActive: 'I dag' },
  'leah-aas': { badges: 7, lastActive: 'I går' },
  'sander-lunde': { badges: 3, lastActive: 'For 3 dager siden' },
  'jonas-moen': { badges: 1, lastActive: 'For 4 dager siden' }
};

const pendingStudentsByClassroom: Record<string, PendingStudentEntry[]> = {
  'ronvik-6a': [
    { id: 'elin-holte', name: 'Elin Holte', requestedAt: 'I dag' },
    { id: 'markus-bakke', name: 'Markus Bakke', requestedAt: 'For 1 time siden' }
  ],
  'ronvik-6b': [],
  'ronvik-6c': []
};

const teacherClassroomNotifications: ClassroomNotification[] = [
  { id: 'n1', classroomId: 'ronvik-6a', title: 'Ny innlevering', description: 'Ada Berg har fullført en ny oppgave.', timeLabel: 'For 10 minutter siden' },
  { id: 'n2', classroomId: 'ronvik-6a', title: 'Elev trenger hjelp', description: 'Tobias Solheim har prøvd samme nivå flere ganger.', timeLabel: 'I dag' },
  { id: 'n3', classroomId: 'ronvik-6b', title: 'Ukens mysterium', description: 'Tre elever har sendt inn forslag til ukens mysterium.', timeLabel: 'I dag' },
  { id: 'n4', classroomId: 'ronvik-6c', title: 'Høy aktivitet', description: 'Klassen har hatt ekstra høy aktivitet denne uken.', timeLabel: 'I går' }
];

const teacherClassroomExercises: ClassroomExercise[] = [
  { uid: 't1', classroomId: 'ronvik-6a', title: 'Skille ekte og falske nyheter', status: 'Aktiv', level: 'Nyhetskvartalet' },
  { uid: 't2', classroomId: 'ronvik-6a', title: 'Oppdage mistenkelige e-poster', status: 'Kladd', level: 'Postkontoret' },
  { uid: 't3', classroomId: 'ronvik-6b', title: 'Vurdere nettbutikker', status: 'Aktiv', level: 'Markedsplassen' },
  { uid: 't4', classroomId: 'ronvik-6c', title: 'Sterke passord', status: 'Planlagt', level: 'Passordbanken' }
];

const classroomMysteries: teacherClassroomMystery[] = [
  { classroomId: 'ronvik-6a', title: 'Mysteriet om den rare overskriften', description: 'Elevene skal finne ut hvorfor en dramatisk overskrift ikke forteller hele sannheten.', status: 'Publisert' },
  { classroomId: 'ronvik-6b', title: 'Det forsvunne passordet', description: 'Klassen jobber med spor som handler om deling av passord og trygg innlogging.', status: 'Klargjøres' },
  { classroomId: 'ronvik-6c', title: 'Bildet som lurte alle', description: 'Et bilde sprer seg raskt, og elevene må finne ut om det er ekte, KI-laget eller manipulert.', status: 'Publisert' }
];

const classroomNames: Record<string, string> = {
  'ronvik-6a': 'Rønvik skole 6A',
  'ronvik-6b': 'Rønvik skole 6B',
  'ronvik-6c': 'Rønvik skole 6C'
};

const classroomWorldUids: Record<string, string> = {
  'ronvik-6a': 'schoolyard',
  'ronvik-6b': 'quickstart',
  'ronvik-6c': 'creative-lab'
};

function calculateProgressPercent(completedLevels: number, totalLevels: number): number {
  if (totalLevels === 0) {
    return 0;
  }

  return Math.round((completedLevels / totalLevels) * 100);
}

function buildClassroomSummary(classroomId: string): ClassroomSummary {
  const students = mockStudents.filter((student) => student.classroomId === classroomId);
  const totalScore = students.reduce((sum, student) => sum + student.points, 0);
  const totalCompletedLevels = students.reduce((sum, student) => sum + student.completedLevels, 0);
  const averageProgressPercent = students.length === 0
    ? 0
    : Math.round((totalCompletedLevels / (students.length * TOTAL_LEVELS)) * 100);

  return {
    id: classroomId,
    name: classroomNames[classroomId] ?? 'Ukjent klasserom',
    worldUid: classroomWorldUids[classroomId] ?? 'schoolyard',
    totalScore,
    studentCount: students.length,
    averageProgressPercent,
  };
}

function getTeacherClassroomSummaries(): ClassroomSummary[] {
  return Object.keys(classroomNames)
    .map((classroomId) => buildClassroomSummary(classroomId))
    .sort((left, right) => right.totalScore - left.totalScore);
}

function getTeacherClassroomById(classroomId: string): ClassroomSummary | null {
  if (!classroomNames[classroomId]) {
    return null;
  }

  return buildClassroomSummary(classroomId);
}

function getClassroomLeaderboard(classroomId: string) {
  return mockStudents
    .filter((student) => student.classroomId === classroomId)
    .map((student) => ({
      ...student,
      progressPercent: calculateProgressPercent(student.completedLevels, student.totalLevels)
    }))
    .sort((left, right) => {
      if (right.progressPercent !== left.progressPercent) {
        return right.progressPercent - left.progressPercent;
      }

      return right.points - left.points;
    });
}

function getteacherClassroomStudents(classroomId: string): StudentOverviewEntry[] {
  return mockStudents
    .filter((student) => student.classroomId === classroomId)
    .map((student) => ({
      ...student,
      badges: studentExtras[student.id]?.badges ?? 0,
      lastActive: studentExtras[student.id]?.lastActive ?? 'Ukjent'
    }))
    .sort((left, right) => left.name.localeCompare(right.name));
}

function getTeacherClassroomPendingStudents(classroomId: string): PendingStudentEntry[] {
  return pendingStudentsByClassroom[classroomId] ?? [];
}

function getStudentById(classroomId: string, studentId: string): StudentOverviewEntry | null {
  return getteacherClassroomStudents(classroomId).find((student) => student.id === studentId) ?? null;
}

function getteacherClassroomNotifications(classroomId: string): ClassroomNotification[] {
  return teacherClassroomNotifications.filter((notification) => notification.classroomId === classroomId);
}

function getTeacherClassroomExercises(classroomId: string): ClassroomExercise[] {
  return teacherClassroomExercises.filter((exercise) => exercise.classroomId === classroomId);
}

function getteacherClassroomMystery(classroomId: string): teacherClassroomMystery | null {
  return classroomMysteries.find((mystery) => mystery.classroomId === classroomId) ?? null;
}

export function useTeacherDashboard() {
  return {
    getTeacherClassroomSummaries,
    getTeacherClassroomById,
    getClassroomLeaderboard,
    getteacherClassroomStudents,
    getTeacherClassroomPendingStudents,
    getStudentById,
    getteacherClassroomNotifications,
    getTeacherClassroomExercises,
    getteacherClassroomMystery,
  };
}
