export enum routePath {
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

export const routePathWithString = (path: routePath, string: string) => `${path}${string}`;