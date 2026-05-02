import type { H3Event } from "h3";

export function getBackendAPI(event: H3Event) {
  const config = useRuntimeConfig(event);
  const baseURL = config.apiBase;

  const forwardSetCookies = (headers: Headers) => {
    const setCookies =
      typeof headers.getSetCookie === "function" ? headers.getSetCookie() : [];

    for (const cookie of setCookies) {
      appendResponseHeader(event, "set-cookie", cookie);
    }
  };

  return $fetch.create({
    baseURL,

    onRequest({ options }) {
      const cookieHeader = getHeader(event, "cookie");
      const authHeader = getHeader(event, "authorization");
      const xsrfHeader = getHeader(event, "x-xsrf-token");
      const headers = new Headers(options.headers || {});

      if (cookieHeader) {
        headers.set("cookie", cookieHeader);
      }

      if (authHeader) {
        headers.set("Authorization", authHeader);
      }

      if (xsrfHeader) {
        headers.set("X-XSRF-TOKEN", xsrfHeader);
      }

      options.headers = headers;
    },

    onResponse({ response }) {
      forwardSetCookies(response.headers);
    },

    onResponseError({ response }) {
      throw createError({
        statusCode: response.status,
        statusMessage: response._data?.detail ?? response.statusText,
        data: response._data,
      });
    },
  });
}
