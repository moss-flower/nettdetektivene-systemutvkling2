export enum routePaths {
    home = '/',
    levels = '/student/levels',
    login = '/login/login',
    join = '/login/join',
    register = '/login/register',
    teacherOverview = '/teacher/home',
    classroomOverview = '/classroom/overview',
    classroomMystery = '/classroom/mystery',
    studentHome = '/student/home',
    studentProfile = '/student/profile',
    studentAchievements = '/student/achievements',
    studentHelp = '/student/help',
    notYetImplemented = '/shared/notYetImplemented',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;