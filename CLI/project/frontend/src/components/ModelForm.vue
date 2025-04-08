<template>
    <div class="modal" style="display: block;">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ model.id ? 'Edit Model' : 'Add Model' }}</h5>
            <button @click="$emit('close')" class="btn-close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="$emit('save', model)">
              <div class="mb-3">
                <label>Name</label>
                <input v-model="model.name" class="form-control" required />
              </div>
              <div class="mb-3">
                <label>Make</label>
                <select v-model="model.make" class="form-control" required>
                  <option :value="null" disabled>Select a make</option>
                  <option v-for="make in makes" :key="make.id" :value="make">
                    {{ make.name }}
                  </option>
                </select>
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
  import { fetchMakes } from '../api';
  
  export default {
    props: ['model'],
    emits: ['close', 'save'],
    setup(props) {
      const model = ref(props.model || { make: null, name: '' });
      const makes = ref([]);
  
      const loadMakes = async () => {
        try {
          const response = await fetchMakes();
          makes.value = response.data;
          if (props.model && props.model.make) {
            model.value.make = makes.value.find(m => m.id === props.model.make.id) || null;
          }
        } catch (error) {
          console.error('Error loading makes:', error);
        }
      };
  
      onMounted(loadMakes);
  
      return { model, makes };
    },
  };
  </script>