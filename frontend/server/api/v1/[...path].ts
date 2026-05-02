import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const path = getRouterParam(event, "path") ?? "";
  const method = event.node.req.method ?? "GET";
  const url = getRequestURL(event);

  const body = method === "GET" || method === "HEAD" ? undefined : await readBody(event);

  return await api(`/${path}${url.search}`, {
    method,
    body,
  });
});
