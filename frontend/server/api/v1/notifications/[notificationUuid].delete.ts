import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const uuid = getRouterParam(event, 'notificationUuid');

  return await api(`/notifications/${uuid}`, {
    method: 'DELETE',
  });
});
