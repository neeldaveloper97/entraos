<template>
 <span class="icon" :class="[smallSize]">
   <slot></slot>
  </span>
</template>

<script>
export default {
  name: "IconWrapper",
  props:{
    isSmall:Boolean
  },
  computed:{
    smallSize(){
      return this.isSmall ? 'is-small' : '';
    }
  }
}
</script>

<style scoped>

</style>
