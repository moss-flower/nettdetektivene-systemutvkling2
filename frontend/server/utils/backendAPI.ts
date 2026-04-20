import type { H3Event } from "h3";

export function getBackendAPI(event: H3Event) {
  return $fetch.create({
    baseURL: "http://localhost:8080/api",

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

