export type StudentLeaderboardEntry = {
  id: string;
  name: string;
  classroomId: string;
  completedLevels: number;
  totalLevels: number;
  points: number;
};

export type ClassroomSummary = {
  id: string;
  name: string;
  totalScore: number;
  studentCount: number;
  averageProgressPercent: number;
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

const classroomNames: Record<string, string> = {
  'ronvik-6a': 'Rønvik skole 6A',
  'ronvik-6b': 'Rønvik skole 6B',
  'ronvik-6c': 'Rønvik skole 6C'
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

export function useTeacherDashboard() {
  return {
    getTeacherClassroomSummaries,
    getTeacherClassroomById,
    getClassroomLeaderboard,
  };
}
