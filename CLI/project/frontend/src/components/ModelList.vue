<template>
    <div>
      <h2>Models</h2>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Make</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="model in models" :key="model.id">
            <td>{{ model.id }}</td>
            <td>{{ model.name }}</td>
            <td>{{ model.make ? model.make.name : 'N/A' }}</td>
            <td>
              <button @click="editModel(model)" class="btn btn-warning btn-sm">Edit</button>
              <button @click="deleteModel(model.id)" class="btn btn-danger btn-sm">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
      <button @click="showForm = true" class="btn btn-primary">Add Model</button>
      <ModelForm v-if="showForm" :model="selectedModel" @close="closeForm" @save="saveModel" />
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import apiClient from '../api';
  import ModelForm from './ModelForm.vue';
  
  export default {
    components: { ModelForm },
    setup() {
      const models = ref([]);
      const showForm = ref(false);
      const selectedModel = ref(null);
  
      const fetchModels = async () => {
        try {
          const response = await apiClient.get('/car/model');
          models.value = response.data;
        } catch (error) {
          console.error('Error fetching models:', error);
        }
      };
  
      const deleteModel = async (id) => {
        if (confirm('Are you sure?')) {
          try {
            await apiClient.delete(`/car/model/${id}`);
            models.value = models.value.filter(m => m.id !== id);
          } catch (error) {
            console.error('Error deleting model:', error);
          }
        }
      };
  
      const editModel = (model) => {
        selectedModel.value = { ...model };
        showForm.value = true;
      };
  
      const closeForm = () => {
        showForm.value = false;
        selectedModel.value = null;
      };
  
      const saveModel = async (model) => {
        try {
          const modelToSave = {
            ...model,
            make: { id: model.make.id },
          };
          if (model.id) {
            const response = await apiClient.put(`/car/model/${model.id}`, modelToSave);
            const index = models.value.findIndex(m => m.id === model.id);
            models.value[index] = response.data;
          } else {
            const response = await apiClient.post('/car/model', modelToSave);
            models.value.push(response.data);
          }
          closeForm();
        } catch (error) {
          console.error('Error saving model:', error);
        }
      };
  
      onMounted(fetchModels);
  
      return { models, showForm, selectedModel, deleteModel, editModel, closeForm, saveModel };
    },
  };
  </script>