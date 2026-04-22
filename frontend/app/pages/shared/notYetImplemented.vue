<template >
    <h1>Ikke implementert enda</h1>
    <p>Dette er en midlertidig side som vises for funksjoner som ikke er implementert enda.</p>
    <p>Gå tilbake <NuxtLink :to="backPath">her</NuxtLink>.</p>
</template>

<script setup lang="ts">
    const route = useRoute();
    const backPath = computed(() => {
        const queryBackPath = route.query.backPath;

        if (typeof queryBackPath === 'string' && queryBackPath.length > 0) {
            return queryBackPath;
        }

        return routePaths.home;
    });
</script>

<style scoped lang="scss"></style>