<template>
  <div class="landing-page">
    <div class="welcome-section">
      <h1 class="welcome-title">Welcome to Car Manager</h1>
    </div>
    <div class="fun-fact-section">
      <h2 class="fun-fact-title">Fun Fact of the Day</h2>
      <div class="fact-box">
        <p v-if="fact" class="fact-text">{{ fact.value }}</p>
        <p v-else-if="error" class="text-danger">{{ error }}</p>
        <p v-else>Loading your daily dose of fun...</p>
      </div>
      <button @click="fetchFact" class="btn btn-fact mt-3">Get New Fun Fact</button>
    </div>
    <div class="navigation-section">
      <h3>Explore Features</h3>
      <div class="nav-buttons">
        <router-link to="/cars" class="btn btn-nav">Cars</router-link>
        <router-link to="/makes" class="btn btn-nav">Makes</router-link>
        <router-link to="/models" class="btn btn-nav">Models</router-link>
        <router-link to="/books" class="btn btn-nav">Books</router-link>
        <router-link to="/reviews" class="btn btn-nav">Reviews</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { fetchChuckJoke } from '../api';

export default {
  name: 'LandingPage',
  setup() {
    const fact = ref(null);
    const error = ref(null);

    const fetchFact = async () => {
      try {
        error.value = null;
        const response = await fetchChuckJoke();
        fact.value = response.data;
      } catch (err) {
        console.error('Error fetching fun fact:', err);
        error.value = 'Could not fetch a fun fact. Try again!';
      }
    };

    onMounted(() => {
      fetchFact();
    });

    return { fact, error, fetchFact };
  },
};
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  text-align: center;
  padding: 2rem;
  background: #f4f6f9;
}

.welcome-section {
  margin-top: 3rem;
}

.welcome-title {
  font-size: 3.5rem;
  font-family: 'Poppins', sans-serif;
  font-weight: 700;
  color: #2c3e50;
  animation: slideIn 1s ease-out;
}

.welcome-subtitle {
  font-size: 1.5rem;
  color: #7f8c8d;
  font-family: 'Poppins', sans-serif;
  margin-top: 0.5rem;
}

.fun-fact-section {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.fun-fact-title {
  font-size: 2rem;
  font-family: 'Poppins', sans-serif;
  font-weight: 600;
  color: #e67e22;
  margin-bottom: 1.5rem;
  animation: fadeIn 1.5s ease-in-out;
}

.fact-box {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  max-width: 700px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #e67e22;
  animation: fadeInUp 1s ease-in-out;
}

.fact-text {
  font-size: 1.3rem;
  color: #34495e;
  font-family: 'Roboto', sans-serif;
  line-height: 1.6;
}

.text-danger {
  font-size: 1.2rem;
  color: #e74c3c;
}

.btn-fact {
  background: #e67e22;
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 500;
  padding: 0.7rem 2rem;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.btn-fact:hover {
  background: #d35400;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.navigation-section {
  margin-bottom: 3rem;
}

.navigation-section h3 {
  font-size: 1.8rem;
  font-family: 'Poppins', sans-serif;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  animation: fadeIn 2s ease-in-out;
}

.nav-buttons {
  display: flex;
  justify-content: center;
  gap: 1.2rem;
  flex-wrap: wrap; 
}

.btn-nav {
  background: #3498db;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 500;
  padding: 0.7rem 1.8rem;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn-nav:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>