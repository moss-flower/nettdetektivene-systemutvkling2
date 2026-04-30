import { routePaths } from "~/composables/utils/routePaths";

const PREVIEW_ROUTE_FLAG = "1";

export const useGamePreview = () => {
  const route = useRoute();

  const isPreview = computed(() => String(route.query.preview ?? "") === PREVIEW_ROUTE_FLAG);
  const classroomId = computed(() => {
    const value = route.query.classroomId;
    return typeof value === "string" ? value.trim() : "";
  });
  const hasPreviewContext = computed(() => isPreview.value && classroomId.value.length > 0);

  const buildPreviewRoute = (path: routePaths) => ({
    path,
    query: {
      preview: PREVIEW_ROUTE_FLAG,
      classroomId: classroomId.value,
    },
  });

  return {
    isPreview,
    classroomId,
    hasPreviewContext,
    buildPreviewRoute,
  };
};
