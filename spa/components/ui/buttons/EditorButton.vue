<template>
  <button @click.prevent="$emit('clicked')">
    <font-awesome-icon v-if="editorType ==='close'" :class="color" :icon="['fas', 'times']" />
    <font-awesome-icon v-if="editorType ==='add'" :class="color" :icon="['fas', 'plus']" />
    <font-awesome-icon v-if="editorType === 'edit'" class="color--main-brand" :icon="['fas', 'pen']" />
    <slot></slot>
  </button>
</template>

<script>
export default {
name: "CloseButton",
  props: {
  color:{
    type:String,
    default:'color--white'
  },
    editorType:{
    type:String,
      default:'close'
    }
  },

}
</script>

<style lang="scss" scoped>
button {
border:none;
  background: none;
  font-size: 1rem;
  padding:0 0.5rem;
  height:100%;
  white-space: nowrap;
}
</style>
