<template>
  <div class="modal" :class="modalActive">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">
          {{ title }}
        </p>
        <button class="delete" aria-label="close" @click="$emit('closed')"></button>
      </header>
      <section class="modal-card-body">
        <slot>
        </slot>
      </section>
      <footer class="modal-card-foot">
          <slot name="footer" />
      </footer>
    </div>
  </div>
</template>

<script>

export default {
  name: "Modal",
  props: ['isActive','title'],
  computed: {
    modalActive() {
      return this.isActive ? 'is-active' : '';
    }
  }
}
</script>

<style lang="scss" scoped>
.modal {
  z-index: 1000;
}
</style>
