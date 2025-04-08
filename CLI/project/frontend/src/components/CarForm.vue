<template>
    <div class="modal" style="display: block;">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ car.id ? 'Edit Car' : 'Add Car' }}</h5>
            <button @click="$emit('close')" class="btn-close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="$emit('save', car)">
              <div class="mb-3">
                <label>VIN</label>
                <input v-model="car.vin" class="form-control" required />
              </div>
              <div class="mb-3">
                <label>Model</label>
                <select v-model="car.model" class="form-control" required>
                  <option :value="null" disabled>Select a model</option>
                  <option v-for="model in models" :key="model.id" :value="model">
                    {{ model.name }} (Make: {{ model.make.name }})
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label>Color</label>
                <input v-model="car.color" class="form-control" required />
              </div>
              <div class="mb-3">
                <label>Year</label>
                <input v-model.number="car.year" type="number" class="form-control" required />
              </div>
              <div class="mb-3">
                <label>License Plate</label>
                <input v-model="car.licensePlate" class="form-control" required />
              </div>
              <div class="mb-3">
                <label>Fuel Type</label>
                <input v-model="car.fuelType" class="form-control" required />
              </div>
              <button type="submit" class="btn btn-primary">Save</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import { fetchModels } from '../api';
  
  export default {
    props: ['car'],
    emits: ['close', 'save'],
    setup(props) {
      const car = ref(props.car || { model: null, vin: '', color: '', year: 0, licensePlate: '', fuelType: '' });
      const models = ref([]);
  
      const loadModels = async () => {
        try {
          const response = await fetchModels();
          models.value = response.data;
          if (props.car && props.car.model) {
            car.value.model = models.value.find(m => m.id === props.car.model.id) || null;
          }
        } catch (error) {
          console.error('Error loading models:', error);
        }
      };
  
      onMounted(loadModels);
  
      return { car, models };
    },
  };
  </script>