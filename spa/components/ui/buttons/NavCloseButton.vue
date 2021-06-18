<template>
  <button @click.prevent="$emit('clicked')">
    <font-awesome-icon :class="color" :icon="['fas', 'times']" />
  </button>
</template>

<script>
export default {
  name: "NavCloseButton",
  props: {
    color:{
      default:'color--white'
    }
  }
}
</script>

<style lang="scss" scoped>
button {
  border:none;
  background: none;
  font-size: 1rem;
  padding:0 1rem;
  height:100%;
}
</style>
