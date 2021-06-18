<template>
  <header class="card-header">
    <p v-if="title" class="card-header-title">
      {{ title }}
    </p>
    <slot>
    </slot>
  </header>
</template>

<script>
export default {
  name: "CardHeader",
  props: ['title']
}
</script>

<style scoped>

</style>
