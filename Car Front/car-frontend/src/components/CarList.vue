<template>
  <div>
    <h2>Cars</h2>
    <table class="table table-striped">
      <thead>
        <tr>
          <th>ID</th>
          <th>VIN</th>
          <th>Color</th>
          <th>Year</th>
          <th>License Plate</th>
          <th>Fuel Type</th>
          <th>Drive</th>
          <th>Make</th>
          <th>Model</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="car in cars" :key="car.id">
          <td>{{ car.id }}</td>
          <td>{{ car.vin }}</td>
          <td>{{ car.color }}</td>
          <td>{{ car.year }}</td>
          <td>{{ car.licensePlate }}</td>
          <td>{{ car.fuelType }}</td>
          <td>{{ car.drive || 'N/A' }}</td>
          <td>
            <button @click="fetchMake(car.id)" class="btn btn-link">View Make</button>
            <span v-if="car.make">{{ car.make.name }}</span>
          </td>
          <td>
            <button @click="fetchModel(car.id)" class="btn btn-link">View Model</button>
            <span v-if="car.model">{{ car.model.name }}</span>
          </td>
          <td>
            <button @click="editCar(car)" class="btn btn-warning btn-sm">Edit</button>
            <button @click="deleteCar(car.id)" class="btn btn-danger btn-sm">Delete</button>
            <button @click="fetchDrive(car)" class="btn btn-info btn-sm">Fetch Drive</button>
          </td>
        </tr>
      </tbody>
    </table>
    <button @click="showForm = true" class="btn btn-primary">Add Car</button>
    <CarForm v-if="showForm" :car="selectedCar" @close="closeForm" @save="saveCar" />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import apiClient from '../api';
import CarForm from './CarForm.vue';
import { fetchCarDrive } from '../api';

export default {
  components: { CarForm },
  setup() {
    const cars = ref([]);
    const showForm = ref(false);
    const selectedCar = ref(null);

    const fetchCars = async () => {
      try {
        const response = await apiClient.get('/cars');
        cars.value = response.data;
      } catch (error) {
        console.error('Error fetching cars:', error);
      }
    };

    const fetchMake = async (id) => {
      try {
        const response = await apiClient.get(`/cars/${id}/make`);
        const car = cars.value.find((c) => c.id === id);
        car.make = response.data;
      } catch (error) {
        console.error('Error fetching make:', error);
      }
    };

    const fetchModel = async (id) => {
      try {
        const response = await apiClient.get(`/cars/${id}/model`);
        const car = cars.value.find((c) => c.id === id);
        car.model = response.data;
      } catch (error) {
        console.error('Error fetching model:', error);
      }
    };

    const fetchDrive = async (car) => {
      try {
        const drive = await fetchCarDrive(car.model.name.toLowerCase());
        car.drive = drive;
      } catch (error) {
        console.error('Error fetching drive:', error);
        car.drive = 'N/A'; 
      }
    };

    const deleteCar = async (id) => {
      if (confirm('Are you sure?')) {
        try {
          await apiClient.delete(`/cars/${id}`);
          cars.value = cars.value.filter((c) => c.id !== id);
        } catch (error) {
          console.error('Error deleting car:', error);
        }
      }
    };

    const editCar = (car) => {
      selectedCar.value = { ...car };
      showForm.value = true;
    };

    const closeForm = () => {
      showForm.value = false;
      selectedCar.value = null;
    };

    const saveCar = async (car) => {
      try {
        const carToSave = {
          ...car,
          model: { id: car.model.id },
        };
        if (car.id) {
          const response = await apiClient.put(`/cars/${car.id}`, carToSave);
          const index = cars.value.findIndex((c) => c.id === car.id);
          cars.value[index] = response.data;
        } else {
          const response = await apiClient.post('/cars', carToSave);
          cars.value.push(response.data);
        }
        closeForm();
      } catch (error) {
        console.error('Error saving car:', error);
      }
    };

    onMounted(fetchCars);

    return {
      cars,
      showForm,
      selectedCar,
      fetchMake,
      fetchModel,
      fetchDrive,
      deleteCar,
      editCar,
      closeForm,
      saveCar,
    };
  },
};
</script>