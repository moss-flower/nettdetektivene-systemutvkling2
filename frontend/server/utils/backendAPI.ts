import type { H3Event } from "h3";

export function getBackendAPI(event: H3Event) {
  const config = useRuntimeConfig(event);
  const baseURL = (config.public.apiBase as string);

  return $fetch.create({
    baseURL,

    onRequest({ options }) {
      const cookieHeader = getHeader(event, "cookie");
      const authHeader = getHeader(event, "authorization");
      const headers = new Headers(options.headers || {});

      if (cookieHeader) {
        headers.set("cookie", cookieHeader);
      }

      if (authHeader) {
        headers.set("Authorization", authHeader);
      }

      options.headers = headers;
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
