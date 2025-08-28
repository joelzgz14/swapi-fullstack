<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '../services/api'

const people = ref([])
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')
const sort = ref('name')
const dir = ref('asc')
const size = 15
const loading = ref(false)
const error = ref('')

async function loadPeople() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get('/people', {
      params: { page: page.value, size, search: search.value, sort: sort.value, dir: dir.value }
    })
    people.value = data.content || []
    totalPages.value = data.totalPages || 1
    totalElements.value = data.totalElements || 0
  } catch (e) {
    error.value = 'Error fetching people'
    console.error(e)
  } finally {
    loading.value = false
  }
}

function toggleSort(key) {
  if (sort.value === key) {
    dir.value = dir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sort.value = key
    dir.value = 'asc'
  }
  loadPeople()
}

watch(search, () => {
  page.value = 1
  const handler = setTimeout(loadPeople, 300)
  return () => clearTimeout(handler)
})

onMounted(loadPeople)
</script>

<template>
  <section>
    <h2>People</h2>
    <div class="controls">
      <input v-model="search" placeholder="Search by name..." />
    </div>

    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="loading">Loading...</div>

    <table v-else class="grid">
      <thead>
        <tr>
          <th @click="toggleSort('name')">Name</th>
          <th>Height</th>
          <th>Gender</th>
          <th @click="toggleSort('created')">Created</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in people" :key="p.url || p.name">
          <td>{{ p.name }}</td>
          <td>{{ p.height }}</td>
          <td>{{ p.gender }}</td>
          <td>{{ p.created }}</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <button :disabled="page===1" @click="page--; loadPeople()">Prev</button>
      <span>Page {{ page }} / {{ totalPages }} ({{ totalElements }} items)</span>
      <button :disabled="page===totalPages" @click="page++; loadPeople()">Next</button>
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
