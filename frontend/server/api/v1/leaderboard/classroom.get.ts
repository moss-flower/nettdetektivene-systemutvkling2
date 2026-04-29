import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const query = getQuery(event);

  return await api("/leaderboard/classroom", {
    method: "GET",
    query,
  });
});
