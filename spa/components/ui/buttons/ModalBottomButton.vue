<template>
<button class="button" :class="[classColor]" :disabled="active===false" @click.prevent="$emit('clicked')">
  {{ text }}
</button>
</template>

<script>
export default {
name: "ModalBottomButton",
  props:{
  text:{
    required:true,
    type:String
  },
    classColor:{
    type:String,
      default:'is-link'
    },
    active:{
    type:Boolean,
      default:true
    }
  }
}
</script>

<style lang="scss" scoped>
button{
  height:3rem;
  width:100%;
  position: fixed;
  bottom:0;
  border-radius: 0;
}
</style>
