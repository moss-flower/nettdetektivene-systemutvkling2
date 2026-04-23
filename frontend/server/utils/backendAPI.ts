import type { H3Event } from "h3";

export function getBackendAPI(event: H3Event) {
  const config = useRuntimeConfig(event);
  const baseURL = (config.apiBase as string);

  return $fetch.create({
    baseURL,

    onRequest({ options }) {
      const authHeader = getHeader(event, "authorization");

      if (authHeader) {
        const headers = new Headers(options.headers || {});
        headers.set("Authorization", authHeader);
        options.headers = headers;
      }
    },
  });
}

