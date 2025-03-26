<template>
    <div>
      <h2>Reviews</h2>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Book ID</th>
            <th>Rating</th>
            <th>Comment</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="review in reviews" :key="review.id">
            <td>{{ review.id }}</td>
            <td>{{ review.bookId }}</td>
            <td>{{ review.rating }}</td>
            <td>{{ review.comment }}</td>
            <td>
              <button @click="editReview(review)" class="btn btn-warning btn-sm">Edit</button>
              <button @click="deleteReview(review.id)" class="btn btn-danger btn-sm">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
      <button @click="showForm = true" class="btn btn-primary">Add Review</button>
  
      <!-- Formular pentru creare/editare -->
      <div v-if="showForm" class="modal" style="display: block;">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ selectedReview.id ? 'Edit Review' : 'Add Review' }}</h5>
              <button @click="closeForm" class="btn-close"></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="saveReview">
                <div class="mb-3">
                  <label>Book</label>
                  <select v-model="selectedReview.bookId" class="form-control" required>
                    <option :value="null" disabled>Select a book</option>
                    <option v-for="book in books" :key="book.id" :value="book.id">
                      {{ book.title }} by {{ book.author }}
                    </option>
                  </select>
                </div>
                <div class="mb-3">
                  <label>Rating (1-5)</label>
                  <input v-model.number="selectedReview.rating" type="number" min="1" max="5" class="form-control" required />
                </div>
                <div class="mb-3">
                  <label>Comment</label>
                  <textarea v-model="selectedReview.comment" class="form-control" required></textarea>
                </div>
                <button type="submit" class="btn btn-primary">Save</button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import { fetchReviews, fetchBooks, createReview, updateReview, deleteReview } from '../api';
  
  export default {
    setup() {
      const reviews = ref([]);
      const books = ref([]);
      const showForm = ref(false);
      const selectedReview = ref({ bookId: null, rating: 1, comment: '' });
  
      const fetchReviewsData = async () => {
        try {
          const response = await fetchReviews();
          reviews.value = response.data;
        } catch (error) {
          console.error('Error fetching reviews:', error);
        }
      };
  
      const fetchBooksData = async () => {
        try {
          const response = await fetchBooks();
          books.value = response.data;
        } catch (error) {
          console.error('Error fetching books:', error);
        }
      };
  
      const deleteReviewHandler = async (id) => {
        if (confirm('Are you sure?')) {
          try {
            await deleteReview(id);
            reviews.value = reviews.value.filter(r => r.id !== id);
          } catch (error) {
            console.error('Error deleting review:', error);
          }
        }
      };
  
      const editReview = (review) => {
        selectedReview.value = { ...review };
        showForm.value = true;
      };
  
      const closeForm = () => {
        showForm.value = false;
        selectedReview.value = { bookId: null, rating: 1, comment: '' };
      };
  
      const saveReview = async () => {
        try {
          if (selectedReview.value.id) {
            const response = await updateReview(
              selectedReview.value.id,
              selectedReview.value.bookId,
              selectedReview.value.rating,
              selectedReview.value.comment
            );
            const index = reviews.value.findIndex(r => r.id === selectedReview.value.id);
            reviews.value[index] = response.data;
          } else {
            const response = await createReview(
              selectedReview.value.bookId,
              selectedReview.value.rating,
              selectedReview.value.comment
            );
            reviews.value.push(response.data);
          }
          closeForm();
        } catch (error) {
          console.error('Error saving review:', error);
        }
      };
  
      onMounted(() => {
        fetchReviewsData();
        fetchBooksData();
      });
  
      return { reviews, books, showForm, selectedReview, deleteReview: deleteReviewHandler, editReview, closeForm, saveReview };
    },
  };
  </script>