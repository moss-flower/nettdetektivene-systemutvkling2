export enum routePaths {
    home = '/',
    studentLevels = '/student/game/stations',
    login = '/login',
    join = '/login/join',
    pending = '/login/pending',
    register = '/login/register',
    teacherOverview = '/teacher',
    teacherSettings = '/teacher/settings',
    teacherNotifications = '/teacher/notifications',
    teacherClassroomCreate = '/teacher/classroom/create',
    teacherClassroomOverview = '/teacher/classroom/overview',
    teacherClassroomMystery = '/teacher/classroom/mystery',
    teacherClassroomNotifications = '/teacher/classroom/notifications',
    teacherClassroomStudentProgress = '/teacher/classroom/student-progress',
    teacherClassroomStudents = '/teacher/classroom/students',
    teacherClassroomStudentNotes = '/teacher/classroom/student-notes',
    teacherClassroomExercises = '/teacher/classroom/tasks',
    teacherFactoryExercise = '/teacher/factory/exerciseFactory',
    teacherFactoryStation = '/teacher/factory/stationFactory',
    teacherFactoryWorld = '/teacher/factory/worldFactory',
    studentHome = '/student',
    studentEditAvatar = '/student/editAvatar',
    studentProfile = '/student/profile',
    studentAchievements = '/student/achievements',
    studentExerciseDifficultySelect = '/student/game/selectDifficulty',
    
    // to implement next
    studentLeaderboard = '/student/leaderboard',
    studentMystery = '/student/mystery',
    studentMysteryPlay = '/student/mystery/play',
    studentNotes = '/student/notepad',
    studentNewNote = '/student/newNote',
    studentSettings = '/student/settings',

    notYetImplemented = '/shared/notYetImplemented',
    studentHelp = '/student/help',


    editAvatar = "/student/editavatar"
}

export const routePathsWithString = (path: routePaths, query: Record<string, string>) => ({ path, query });
export const routePathsWithUid = (path: routePaths, uid: string) => ({ path, query: { uid } });
