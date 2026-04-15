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
    studentHelp = '/student/help',
    notYetImplemented = '/notYetImplemented',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;