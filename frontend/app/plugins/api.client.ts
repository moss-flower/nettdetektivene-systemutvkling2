export default defineNuxtPlugin(() => { 
  let refreshInFlight: Promise<void> | null = null;

  const api = $fetch.create({
    baseURL: "/api/v1",
    credentials: 'include',
    async onResponseError({ request, response, options }) {
      const requestUrl = String(request);
      const isAuthEndpoint =
        requestUrl.includes("/auth/login") ||
        requestUrl.includes("/auth/logout") ||
        requestUrl.includes("/auth/refresh");
      const alreadyRetried = Boolean((options as any)._refreshRetried);

      if (response.status !== 401 || isAuthEndpoint || alreadyRetried) {
        return;
      }

      refreshInFlight ??= $fetch("/api/v1/auth/refresh", {
        method: "POST",
        credentials: "include",
      }).then(() => undefined).finally(() => {
        refreshInFlight = null;
      });

      await refreshInFlight;

      (options as any)._refreshRetried = true;
      options.retry = 1;
      options.retryDelay = 0;
      options.retryStatusCodes = [401];
    },
  })

  return {
    provide: {
      api,
    },
  }
})