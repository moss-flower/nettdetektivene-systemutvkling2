export enum routePaths {
    home = '/',
    levels = '/levels',
    login = '/login',
    join = '/join',
    register = '/register',
    teacherOverview = '/teacherOverview',
    classroomOverview = '/classroomOverview',
    studentHome = '/student/home',
    studentProfile = '/student/profile',
    studentAchievements = '/student/achievements',
}

export const routePathsWithString = (path: routePaths, string: string) => `${path}${string}`;