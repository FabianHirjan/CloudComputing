import axios from 'axios';

// API-ul intern definit pentru backend-ul local (pe care-l vei servi din backend)
const apiClient = axios.create({
  baseURL: '/api',  // foloseşte URL relativ 
  headers: {
    'X-API-Key': '123',
    'Content-Type': 'application/json',
  },
});

export default apiClient;
export const fetchMakes = () => apiClient.get('/car/make');
export const fetchModels = () => apiClient.get('/car/model');

// Apel către API-ul Chuck Norris (exemplu extern)
const chuckNorrisApi = axios.create({
  baseURL: 'https://api.chucknorris.io',
});
export const fetchChuckJoke = () => chuckNorrisApi.get('/jokes/random');


// API Ninja – lasă-l așa cum e, pentru că e un apel către un API extern
const ninjaApi = axios.create({
  baseURL: 'https://api.api-ninjas.com/v1',
  headers: {
    'X-Api-Key': 'LEsf/b0RTo1ZxEOekpwr4Q==6YzmkRZgzlKDdOHE',
  },
});
export const fetchCarDrive = (model) =>
  ninjaApi.get(`/cars?model=${model}`).then((response) => {
    return response.data[0]?.drive || 'N/A';
  });
