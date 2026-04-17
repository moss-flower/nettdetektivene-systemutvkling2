export enum routePaths {
    home = '/',
    studentLevels = '/student/home/directions/stations',
    login = '/login/login',
    join = '/login/join',
    register = '/login/register',
    teacherOverview = '/teacher/home',
    teacherClassroomOverview = '/teacher/classroom/overview',
    teacherClassroomMystery = '/teacher/classroom/mystery',
    teacherClassroomNotifications = '/teacher/classroom/notifications',
    teacherClassroomStudentProgress = '/teacher/classroom/student-progress',
    teacherClassroomStudents = '/teacher/classroom/students',
    teacherClassroomExercises = '/teacher/classroom/tasks',
    teacherFactoryExercise = '/teacher/factory/exerciseFactory',
    teacherFactoryStation = '/teacher/factory/stationFactory',
    studentHome = '/student/home/home',
    studentProfile = '/student/home/directions/profile',
    studentAchievements = '/student/home/directions/achievements',
    studentExerciseDifficultySelect = '/student/task/exerciseDifficultySelect',
    
    // to implement next
    studentLeaderboard = '/student/home/directions/leaderboard',
    studentMystery = '/student/home/directions/mystery',
    studentNotes = '/student/home/directions/notes',
    studentSettings = '/student/home/directions/settings',

    notYetImplemented = '/shared/notYetImplemented',
    studentHelp = '/student/home/directions/help',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;