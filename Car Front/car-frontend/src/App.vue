<template>
  <div class="container">
    <nav v-if="$route.path !== '/'" class="navbar navbar-expand-lg navbar-light bg-light mb-4">
      <div class="container-fluid">
        <router-link class="nav-link" to="/">CC HW - Index</router-link>
        <div class="collapse navbar-collapse">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" to="/cars">Cars</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/makes">Makes</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/models">Models</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/books">Books</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/reviews">Reviews</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>