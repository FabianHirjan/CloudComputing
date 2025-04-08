<template>
    <div class="modal" style="display: block;">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ make.id ? 'Edit Make' : 'Add Make' }}</h5>
            <button @click="$emit('close')" class="btn-close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="$emit('save', make)">
              <div class="mb-3">
                <label>Name</label>
                <input v-model="make.name" class="form-control" required />
              </div>
              <button type="submit" class="btn btn-primary">Save</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  
  export default {
    props: ['make'],
    emits: ['close', 'save'],
    setup(props) {
      const make = ref(props.make || {});
      return { make };
    },
  };
  </script>