export enum routePaths {
    home = '/',
    levels = '/student/levels',
    login = '/login/login',
    join = '/login/join',
    register = '/login/register',
    teacherOverview = '/teacher/home',
    classroomOverview = '/teacher/classroom/overview',
    classroomMystery = '/teacher/classroom/mystery',
    classroomNotifications = '/teacher/classroom/notifications',
    classroomStudentProgress = '/teacher/classroom/student-progress',
    classroomStudents = '/teacher/classroom/students',
    classroomTasks = '/teacher/classroom/tasks',
    studentHome = '/student/home',
    studentProfile = '/student/profile',
    studentAchievements = '/student/achievements',
    notYetImplemented = '/shared/notYetImplemented',
    studentHelp = '/student/help',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;