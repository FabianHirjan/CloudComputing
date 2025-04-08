import { createRouter, createWebHistory } from 'vue-router';
import LandingPage from './views/LandingPageView.vue';
import CarsView from './views/CarView.vue';
import MakesView from './views/MakesView.vue';
import ModelsView from './views/ModelsView.vue';
import BooksView from './views/BooksView.vue';
import ReviewsView from './views/ReviewsView.vue';

const routes = [
  { path: '/', component: LandingPage, name: 'Home' },
  { path: '/cars', component: CarsView, name: 'Cars' },
  { path: '/makes', component: MakesView, name: 'Makes' },
  { path: '/models', component: ModelsView, name: 'Models' },
  { path: '/books', component: BooksView, name: 'Books' },
  { path: '/reviews', component: ReviewsView, name: 'Reviews' },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;