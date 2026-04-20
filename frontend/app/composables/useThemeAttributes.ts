import { onMounted, onBeforeMount } from "vue";

export function useThemeAttributes(theme: "game" | "teacher") {
    onMounted(() => {
        document.documentElement.dataset.theme = theme;
    })
    onBeforeMount(() => {
        delete document.documentElement.dataset.theme;
    })
}