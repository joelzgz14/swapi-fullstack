<script setup>
import PeopleTable from './components/PeopleTable.vue'
import PlanetsTable from './components/PlanetsTable.vue'
</script>

<template>
  <main class="container">
    <h1>SWAPI Vue Frontend</h1>
    <PeopleTable />
    <hr />
    <PlanetsTable />
  </main>
</template>

<style scoped>
.container { max-width: 960px; margin: 0 auto; padding: 1rem; font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; }
h1 { margin-bottom: 1rem; }
hr { margin: 1.5rem 0; }
</style>
