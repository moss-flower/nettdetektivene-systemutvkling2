export enum routePaths {
    home = '/',
    levels = '/levels',
    login = '/login',
    join = '/join',
    register = '/register',
    teacherOverview = '/teacherOverview',
    classroomOverview = '/classroom/overview',
    classroomMystery = '/classroom/mystery',
    studentHome = '/student/home',
    studentProfile = '/student/profile',
    studentAchievements = '/student/achievements',
    notYetImplemented = '/notYetImplemented',
    studentHelp = '/student/help',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;