<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const planets = ref([])
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')
const sort = ref('name')
const dir = ref('asc')
const size = 15
const loading = ref(false)
const error = ref('')

async function loadPlanets() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get('/planets', {
      params: { page: page.value, size, search: search.value, sort: sort.value, dir: dir.value }
    })
    planets.value = data.content || []
    totalPages.value = data.totalPages || 1
    totalElements.value = data.totalElements || 0
  } catch (e) {
    error.value = 'Error fetching planets'
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(loadPlanets)
</script>

<template>
  <section>
    <h2>Planets</h2>
    <div class="controls">
      <input v-model="search" @input="loadPlanets" placeholder="Search by name..." />
    </div>

    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="loading">Loading...</div>

    <table v-else class="grid">
      <thead>
        <tr>
          <th @click="sort='name'; dir = dir==='asc'?'desc':'asc'; loadPlanets()">Name</th>
          <th>Climate</th>
          <th>Terrain</th>
          <th @click="sort='created'; dir = dir==='asc'?'desc':'asc'; loadPlanets()">Created</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in planets" :key="p.url || p.name">
          <td>{{ p.name }}</td>
          <td>{{ p.climate }}</td>
          <td>{{ p.terrain }}</td>
          <td>{{ p.created }}</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <button :disabled="page===1" @click="page--; loadPlanets()">Prev</button>
      <span>Page {{ page }} / {{ totalPages }} ({{ totalElements }} items)</span>
      <button :disabled="page===totalPages" @click="page++; loadPlanets()">Next</button>
    </div>
  </section>
</template>

<style scoped>
.controls { margin: 0.5rem 0; }
.grid { width: 100%; border-collapse: collapse; }
.grid th, .grid td { border: 1px solid #ddd; padding: 0.5rem; }
.grid th { cursor: pointer; text-align: left; }
.pager { display: flex; gap: 0.5rem; align-items: center; margin-top: 0.5rem; }
.error { color: #b00020; margin: 0.5rem 0; }
</style>
