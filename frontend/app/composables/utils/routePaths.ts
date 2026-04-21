export enum routePaths {
    home = '/',
    studentLevels = '/student/game/stations',
    login = '/login',
    join = '/login/join',
    register = '/login/register',
    teacherOverview = '/teacher',
    teacherClassroomOverview = '/teacher/classroom/overview',
    teacherClassroomMystery = '/teacher/classroom/mystery',
    teacherClassroomNotifications = '/teacher/classroom/notifications',
    teacherClassroomStudentProgress = '/teacher/classroom/student-progress',
    teacherClassroomStudents = '/teacher/classroom/students',
    teacherClassroomExercises = '/teacher/classroom/tasks',
    teacherFactoryExercise = '/teacher/factory/exerciseFactory',
    teacherFactoryStation = '/teacher/factory/stationFactory',
    teacherFactoryWorld = '/teacher/factory/worldFactory',
    studentHome = '/student',
    studentProfile = '/student/profile',
    studentAchievements = '/student/achievements',
    studentExerciseDifficultySelect = '/student/game/selectDifficulty',
    
    // to implement next
    studentLeaderboard = '/student/home/directions/leaderboard',
    studentMystery = '/student/home/directions/mystery',
    studentNotes = '/student/home/directions/notes',
    studentSettings = '/student/home/directions/settings',

    notYetImplemented = '/shared/notYetImplemented',
    studentHelp = '/student/home/directions/help',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;