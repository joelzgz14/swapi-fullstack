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
const expandedRows = ref(new Set())

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

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

function formatArray(array) {
  if (!array || array.length === 0) return 'None'
  return array.length > 2 ? `${array.length} items` : array.join(', ')
}

function toggleRowExpansion(personUrl, fieldName) {
  const key = `${personUrl}_${fieldName}`
  if (expandedRows.value.has(key)) {
    expandedRows.value.delete(key)
  } else {
    expandedRows.value.add(key)
  }
}

function isRowExpanded(personUrl, fieldName) {
  return expandedRows.value.has(`${personUrl}_${fieldName}`)
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
    
    <!-- Modern Loading Animation -->
    <div v-if="loading" class="loading-container">
      <div class="loading-content">
        <div class="spinner">
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
        </div>
        <div class="loading-text">
          <span class="loading-word">Loading</span>
          <span class="loading-dots">
            <span>.</span>
            <span>.</span>
            <span>.</span>
          </span>
        </div>
        <div class="loading-subtitle">Fetching Star Wars people data</div>
      </div>
    </div>

    <div v-else class="table-container">
      <table class="grid">
        <thead>
          <tr>
            <th @click="toggleSort('name')" class="sortable">
              Name
              <span class="sort-icon">{{ sort === 'name' ? (dir === 'asc' ? '▲' : '▼') : '↕️' }}</span>
            </th>
            <th @click="toggleSort('created')" class="sortable">
              Created
              <span class="sort-icon">{{ sort === 'created' ? (dir === 'asc' ? '▲' : '▼') : '↕️' }}</span>
            </th>
            <th>Height</th>
            <th>Mass</th>
            <th>Hair Color</th>
            <th>Skin Color</th>
            <th>Eye Color</th>
            <th>Birth Year</th>
            <th>Gender</th>
            <th>Homeworld</th>
            <th>Films</th>
            <th>Species</th>
            <th>Vehicles</th>
            <th>Starships</th>
            <th>Edited</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in people" :key="p.url || p.name">
            <td class="name-cell">{{ p.name }}</td>
            <td class="date-cell">{{ formatDate(p.created) }}</td>
            <td>{{ p.height || 'unknown' }}</td>
            <td>{{ p.mass || 'unknown' }}</td>
            <td>{{ p.hair_color || 'unknown' }}</td>
            <td>{{ p.skin_color || 'unknown' }}</td>
            <td>{{ p.eye_color || 'unknown' }}</td>
            <td>{{ p.birth_year || 'unknown' }}</td>
            <td>{{ p.gender || 'unknown' }}</td>
            <td>{{ p.homeworld || 'unknown' }}</td>
            
            <!-- Films Column with Expandable List -->
            <td class="list-cell">
              <div v-if="!p.films || p.films.length === 0">None</div>
              <div v-else-if="p.films.length <= 2">
                {{ p.films.join(', ') }}
              </div>
              <div v-else class="expandable-list">
                <button 
                  @click="toggleRowExpansion(p.url, 'films')"
                  class="expand-btn"
                >
                  🎬 {{ p.films.length }} films
                  <span class="expand-icon">{{ isRowExpanded(p.url, 'films') ? '▼' : '▶' }}</span>
                </button>
                <div v-if="isRowExpanded(p.url, 'films')" class="expanded-content">
                  <div v-for="(film, index) in p.films" :key="index" class="list-item">
                    {{ film }}
                  </div>
                </div>
              </div>
            </td>
            
            <!-- Species Column with Expandable List -->
            <td class="list-cell">
              <div v-if="!p.species || p.species.length === 0">None</div>
              <div v-else-if="p.species.length <= 2">
                {{ p.species.join(', ') }}
              </div>
              <div v-else class="expandable-list">
                <button 
                  @click="toggleRowExpansion(p.url, 'species')"
                  class="expand-btn"
                >
                  🧬 {{ p.species.length }} species
                  <span class="expand-icon">{{ isRowExpanded(p.url, 'species') ? '▼' : '▶' }}</span>
                </button>
                <div v-if="isRowExpanded(p.url, 'species')" class="expanded-content">
                  <div v-for="(specie, index) in p.species" :key="index" class="list-item">
                    {{ specie }}
                  </div>
                </div>
              </div>
            </td>
            
            <!-- Vehicles Column with Expandable List -->
            <td class="list-cell">
              <div v-if="!p.vehicles || p.vehicles.length === 0">None</div>
              <div v-else-if="p.vehicles.length <= 2">
                {{ p.vehicles.join(', ') }}
              </div>
              <div v-else class="expandable-list">
                <button 
                  @click="toggleRowExpansion(p.url, 'vehicles')"
                  class="expand-btn"
                >
                  🚗 {{ p.vehicles.length }} vehicles
                  <span class="expand-icon">{{ isRowExpanded(p.url, 'vehicles') ? '▼' : '▶' }}</span>
                </button>
                <div v-if="isRowExpanded(p.url, 'vehicles')" class="expanded-content">
                  <div v-for="(vehicle, index) in p.vehicles" :key="index" class="list-item">
                    {{ vehicle }}
                  </div>
                </div>
              </div>
            </td>
            
            <!-- Starships Column with Expandable List -->
            <td class="list-cell">
              <div v-if="!p.starships || p.starships.length === 0">None</div>
              <div v-else-if="p.starships.length <= 2">
                {{ p.starships.join(', ') }}
              </div>
              <div v-else class="expandable-list">
                <button 
                  @click="toggleRowExpansion(p.url, 'starships')"
                  class="expand-btn"
                >
                  🚀 {{ p.starships.length }} starships
                  <span class="expand-icon">{{ isRowExpanded(p.url, 'starships') ? '▼' : '▶' }}</span>
                </button>
                <div v-if="isRowExpanded(p.url, 'starships')" class="expanded-content">
                  <div v-for="(starship, index) in p.starships" :key="index" class="list-item">
                    {{ starship }}
                  </div>
                </div>
              </div>
            </td>
            
            <td class="date-cell">{{ formatDate(p.edited) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pager">
      <button :disabled="page===1" @click="page--; loadPeople()">Prev</button>
      <span>Page {{ page }} / {{ totalPages }} ({{ totalElements }} items)</span>
      <button :disabled="page===totalPages" @click="page++; loadPeople()">Next</button>
    </div>
  </section>
</template>

<style scoped>
.controls {
  margin: 1.5rem 0;
  display: flex;
  justify-content: center;
}

.controls input {
  padding: 12px 20px;
  border: 2px solid #e1e5e9;
  border-radius: 25px;
  font-size: 16px;
  width: 300px;
  background: linear-gradient(145deg, #ffffff, #f0f0f0);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  outline: none;
}

.controls input:focus {
  border-color: #4ade80;
  box-shadow: 0 0 0 3px rgba(74, 222, 128, 0.1), 0 8px 25px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.controls input::placeholder {
  color: #a0a0a0;
  font-style: italic;
}

section {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  margin: 2rem;
  position: relative;
  overflow: hidden;
}

section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #84cc16, #22c55e, #10b981, #06b6d4);
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

h2 {
  color: white;
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 2rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  font-weight: 300;
  letter-spacing: 2px;
  animation: fadeInDown 0.8s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Modern Loading Styles */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  backdrop-filter: blur(10px);
  margin: 2rem 0;
}

.loading-content {
  text-align: center;
  color: white;
}

.spinner {
  position: relative;
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
}

.spinner-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 3px solid transparent;
  border-radius: 50%;
  border-top-color: rgba(255, 255, 255, 0.8);
  animation: spin 1.5s linear infinite;
}

.spinner-ring:nth-child(2) {
  width: 80%;
  height: 80%;
  top: 10%;
  left: 10%;
  border-top-color: rgba(255, 255, 255, 0.6);
  animation-duration: 2s;
  animation-direction: reverse;
}

.spinner-ring:nth-child(3) {
  width: 60%;
  height: 60%;
  top: 20%;
  left: 20%;
  border-top-color: rgba(255, 255, 255, 0.4);
  animation-duration: 3s;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 1.5rem;
  font-weight: 300;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.loading-word {
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.loading-dots span {
  animation: bounce 1.4s ease-in-out infinite both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.loading-subtitle {
  font-size: 0.9rem;
  opacity: 0.8;
  font-style: italic;
  animation: fadeInOut 3s ease-in-out infinite;
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 0.4; }
}

.table-container {
  overflow-x: auto;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  background: white;
}

.grid {
  width: 100%;
  min-width: 1400px;
  border-collapse: separate;
  border-spacing: 0;
  background: white;
  border-radius: 15px;
  overflow: hidden;
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.grid th {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  padding: 18px 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  position: relative;
  transition: all 0.3s ease;
  font-size: 12px;
  white-space: nowrap;
}

.grid th.sortable {
  cursor: pointer;
}

.grid th.sortable:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.grid th.sortable:active {
  transform: translateY(0);
}

.sort-icon {
  position: absolute;
  right: 8px;
  opacity: 0.8;
  font-size: 10px;
  color: #ffffff;
  transition: all 0.3s ease;
}

.grid th.sortable:hover .sort-icon {
  opacity: 1;
  transform: scale(1.2);
}

.grid td {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  background: white;
  font-size: 13px;
  max-width: 120px;
  overflow: visible;
  vertical-align: top;
}

.name-cell {
  font-weight: 600;
  color: #16a34a;
  max-width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.list-cell {
  max-width: 140px;
  position: relative;
}

.date-cell {
  max-width: 120px;
  font-size: 11px;
  color: #666;
  white-space: nowrap;
}

.expandable-list {
  position: relative;
}

.expand-btn {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 15px;
  cursor: pointer;
  font-size: 11px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
  width: 100%;
  justify-content: space-between;
}

.expand-btn:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.expand-icon {
  font-size: 10px;
  transition: transform 0.3s ease;
}

.expanded-content {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  z-index: 10;
  max-height: 200px;
  overflow-y: auto;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.list-item {
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 11px;
  color: #333;
  transition: background-color 0.2s ease;
}

.list-item:last-child {
  border-bottom: none;
}

.list-item:hover {
  background: #f0fdf4;
}

.grid tbody tr {
  transition: all 0.3s ease;
}

.grid tbody tr:nth-child(even) {
  background: linear-gradient(90deg, #f0fdf4, #ffffff);
}

.grid tbody tr:hover {
  background: linear-gradient(90deg, #dcfce7, #f0fdf4);
  transform: scale(1.01);
  box-shadow: 0 5px 20px rgba(34, 197, 94, 0.15);
}

.pager {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  animation: fadeIn 1s ease-out 0.4s both;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.pager button {
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.pager button:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #16a34a, #15803d);
}

.pager button:active:not(:disabled) {
  transform: translateY(-1px);
}

.pager button:disabled {
  background: #cccccc;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.pager span {
  color: white;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
}

.error {
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid rgba(255, 107, 107, 0.3);
  border-radius: 10px;
  padding: 12px 20px;
  margin: 1rem 0;
  text-align: center;
  font-weight: 500;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

@media (max-width: 768px) {
  .table-container {
    margin: 0 -1rem;
  }
  
  .grid {
    min-width: 900px;
    font-size: 11px;
  }
  
  .grid th, .grid td {
    padding: 8px 6px;
  }
  
  section {
    margin: 1rem;
    padding: 1rem;
  }
  
  .controls input {
    width: 100%;
    max-width: 300px;
  }
  
  h2 {
    font-size: 2rem;
  }
  
  .loading-container {
    min-height: 200px;
  }
  
  .spinner {
    width: 40px;
    height: 40px;
  }
  
  .loading-text {
    font-size: 1.2rem;
  }
  
  .expanded-content {
    position: fixed;
    bottom: 50%; 
    left: 50%;
    transform: translate(-50%, 50%);
    width: 90%;
    max-width: 300px;
  }
}
</style>