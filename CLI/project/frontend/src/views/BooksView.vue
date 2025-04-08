<template>
  <div>
    <h2>Books</h2>
    <table class="table table-striped">
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Author</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="book in books" :key="book.id">
          <td>{{ book.id }}</td>
          <td>{{ book.title }}</td>
          <td>{{ book.author }}</td>
          <td>
            <button @click="editBook(book)" class="btn btn-warning btn-sm">Edit</button>
            <button @click="deleteBook(book.id)" class="btn btn-danger btn-sm">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
    <button @click="showForm = true" class="btn btn-primary">Add Book</button>

    <div v-if="showForm" class="modal" style="display: block;">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ selectedBook.id ? 'Edit Book' : 'Add Book' }}</h5>
            <button @click="closeForm" class="btn-close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveBook">
              <div class="mb-3">
                <label>Title</label>
                <input v-model="selectedBook.title" class="form-control" required />
              </div>
              <div class="mb-3">
                <label>Author</label>
                <input v-model="selectedBook.author" class="form-control" required />
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
import { fetchBooks, createBook, updateBook, deleteBook } from '../api';

export default {
  setup() {
    const books = ref([]);
    const showForm = ref(false);
    const selectedBook = ref({ title: '', author: '' });

    const fetchBooksData = async () => {
      try {
        const response = await fetchBooks();
        books.value = response.data;
      } catch (error) {
        console.error('Error fetching books:', error);
      }
    };

    const deleteBookHandler = async (id) => {
      if (confirm('Are you sure?')) {
        try {
          await deleteBook(id);
          books.value = books.value.filter(b => b.id !== id);
        } catch (error) {
          console.error('Error deleting book:', error);
        }
      }
    };

    const editBook = (book) => {
      selectedBook.value = { ...book };
      showForm.value = true;
    };

    const closeForm = () => {
      showForm.value = false;
      selectedBook.value = { title: '', author: '' };
    };

    const saveBook = async () => {
      try {
        if (selectedBook.value.id) {
          const response = await updateBook(
            selectedBook.value.id,
            selectedBook.value.title,
            selectedBook.value.author
          );
          const index = books.value.findIndex(b => b.id === selectedBook.value.id);
          books.value[index] = response.data;
        } else {
          const response = await createBook(selectedBook.value.title, selectedBook.value.author);
          books.value.push(response.data);
        }
        closeForm();
      } catch (error) {
        console.error('Error saving book:', error);
      }
    };

    onMounted(fetchBooksData);

    return { books, showForm, selectedBook, deleteBook: deleteBookHandler, editBook, closeForm, saveBook };
  },
};
</script>