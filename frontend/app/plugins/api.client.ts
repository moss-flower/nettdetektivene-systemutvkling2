const XSRF_COOKIE = "XSRF-TOKEN";
const XSRF_HEADER = "X-XSRF-TOKEN";

const readXsrfToken = (): string | null => {
  if (typeof document === "undefined") return null;
  const match = document.cookie.match(
    new RegExp("(?:^|;\\s*)" + XSRF_COOKIE + "=([^;]+)"),
  );
  return match ? decodeURIComponent(match[1]!) : null;
};

const attachXsrfHeader = (headersInit: HeadersInit | undefined): Headers => {
  const headers = new Headers(headersInit || {});
  const token = readXsrfToken();
  if (token) {
    headers.set(XSRF_HEADER, token);
  }
  return headers;
};

export default defineNuxtPlugin(() => {
  let refreshInFlight: Promise<void> | null = null;

  const api = $fetch.create({
    baseURL: "/api/v1",
    credentials: "include",
    onRequest({ options }) {
      options.headers = attachXsrfHeader(options.headers);
    },
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
        headers: attachXsrfHeader(undefined),
      })
        .then(() => undefined)
        .finally(() => {
          refreshInFlight = null;
        });

      await refreshInFlight;

      (options as any)._refreshRetried = true;
      options.retry = 1;
      options.retryDelay = 0;
      options.retryStatusCodes = [401];
    },
  });

  return {
    provide: {
      api,
    },
  };
});
