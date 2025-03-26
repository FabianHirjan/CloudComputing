import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'X-API-Key': '123',
    'Content-Type': 'application/json',
  },
});

export default apiClient;

export const fetchMakes = () => apiClient.get('/car/make');
export const fetchModels = () => apiClient.get('/car/model');

const chuckNorrisApi = axios.create({
  baseURL: 'https://api.chucknorris.io',
});
export const fetchChuckJoke = () => chuckNorrisApi.get('/jokes/random');

const booksApi = axios.create({
  baseURL: 'http://localhost:8000',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const fetchBooks = () => booksApi.get('/books');
export const fetchBookById = (id) => booksApi.get(`/books/${id}`);
export const createBook = (title, author) => booksApi.post('/books', { title, author });
export const updateBook = (id, title, author) => booksApi.put(`/books/${id}`, { title, author });
export const deleteBook = (id) => booksApi.delete(`/books/${id}`);
export const fetchReviews = () => booksApi.get('/reviews');
export const fetchReviewsByBookId = (bookId) => booksApi.get(`/reviews/book/${bookId}`);
export const createReview = (bookId, rating, comment) =>
  booksApi.post('/reviews', { bookId, rating, comment });
export const updateReview = (id, bookId, rating, comment) =>
  booksApi.put(`/reviews/${id}`, { bookId, rating, comment });
export const deleteReview = (id) => booksApi.delete(`/reviews/${id}`);

// API Ninja pentru drive
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