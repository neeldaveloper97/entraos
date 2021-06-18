<template>
  <button class="circle-button" @click="$emit('clicked')">
    {{ text }}
      <slot>
      </slot>
</button>
</template>

<script>
export default {
  name: "CircleButton",
  props: {
    text: String
  }
}
</script>

<style scoped>
button {
  height: 3rem;
  width:3rem;
  text-align: center;
  border-radius: 100%;
  box-shadow: 0px 4px 4px rgba(0, 89, 255, 0.15);
}
button > *{
  line-height: 3rem;
  vertical-align: middle;
  height: 3rem;
}
</style>
