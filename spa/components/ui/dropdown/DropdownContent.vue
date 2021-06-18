<template>
  <div v-if="active" :class="{ 'dropdown-bottom': bottom}">
    <slot />

    <div class="background"></div>
  </div>
</template>

<script>
export default {
  name: "DropdownContent",
  inject: ['sharedState'],
  props:{
    bottom:{
      type:Boolean,
      default:false
    }
  },
  computed: {
    active () {
      return this.sharedState.active
    }
  }
}
</script>

<style lang="scss" scoped>
div{
  z-index:1000;
}

.dropdown-bottom{
  position: fixed;
  bottom: 0;
  width: 100vw;
  left: 0;
}
.background {
  background: rgba(66, 128, 248, 0.25); // Make sure this color has an opacity of less than 1
  backdrop-filter: blur(3px); // This be the blur
  position: fixed;
  width: 100vw;
  height: 100vh;
  z-index: -1;
  top:0;
}
</style>
