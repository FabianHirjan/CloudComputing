<template>
    <div>
      <h2>Makes</h2>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="make in makes" :key="make.id">
            <td>{{ make.id }}</td>
            <td>{{ make.name }}</td>
            <td>
              <button @click="editMake(make)" class="btn btn-warning btn-sm">Edit</button>
              <button @click="deleteMake(make.id)" class="btn btn-danger btn-sm">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
      <button @click="showForm = true" class="btn btn-primary">Add Make</button>
      <MakeForm v-if="showForm" :make="selectedMake" @close="closeForm" @save="saveMake" />
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import apiClient from '../api';
  import MakeForm from './MakeForm.vue';
  
  export default {
    components: { MakeForm },
    setup() {
      const makes = ref([]);
      const showForm = ref(false);
      const selectedMake = ref(null);
  
      const fetchMakes = async () => {
        try {
          const response = await apiClient.get('/car/make');
          makes.value = response.data;
        } catch (error) {
          console.error('Error fetching makes:', error);
        }
      };
  
      const deleteMake = async (id) => {
        if (confirm('Are you sure?')) {
          try {
            await apiClient.delete(`/car/make/${id}`);
            makes.value = makes.value.filter(m => m.id !== id);
          } catch (error) {
            console.error('Error deleting make:', error);
          }
        }
      };
  
      const editMake = (make) => {
        selectedMake.value = { ...make };
        showForm.value = true;
      };
  
      const closeForm = () => {
        showForm.value = false;
        selectedMake.value = null;
      };
  
      const saveMake = async (make) => {
        try {
          if (make.id) {
            const response = await apiClient.put(`/car/make/${make.id}`, make);
            const index = makes.value.findIndex(m => m.id === make.id);
            makes.value[index] = response.data;
          } else {
            const response = await apiClient.post('/car/make', make);
            makes.value.push(response.data);
          }
          closeForm();
        } catch (error) {
          console.error('Error saving make:', error);
        }
      };
  
      onMounted(fetchMakes);
  
      return { makes, showForm, selectedMake, deleteMake, editMake, closeForm, saveMake };
    },
  };
  </script>