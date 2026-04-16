export enum routePaths {
    home = '/',
    levels = '/student/levels',
    login = '/login/login',
    join = '/login/join',
    register = '/login/register',
    teacherOverview = '/teacher/home',
    teacherClassroomOverview = '/teacher/classroom/overview',
    teacherClassroomMystery = '/teacher/classroom/mystery',
    teacherClassroomNotifications = '/teacher/classroom/notifications',
    teacherClassroomStudentProgress = '/teacher/classroom/student-progress',
    teacherClassroomStudents = '/teacher/classroom/students',
    teacherClassroomTasks = '/teacher/classroom/tasks',
    studentHome = '/student/home',
    studentProfile = '/student/profile',
    studentAchievements = '/student/achievements',
    notYetImplemented = '/shared/notYetImplemented',
    studentHelp = '/student/help',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;