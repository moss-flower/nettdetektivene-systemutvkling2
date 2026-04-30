import { useFactorySessionCache } from "~/composables/useFactorySessionCache";

export default defineNuxtRouteMiddleware((to, from) => {
	if (from.path.startsWith("/teacher/factory") && !to.path.startsWith("/teacher/factory")) {
		useFactorySessionCache().clearFactorySessionCache();
	}
});
