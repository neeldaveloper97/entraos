<template>
  <p class="control has-icons-right">
    <input class="input" type="search" :class="size" :placeholder="placeholder" @keyup="$emit('keyuped')" @input="$emit('input',$event.target.value)">
    <span class="icon is-small is-right">
      <font-awesome-icon class="color--main-brand" :icon="['fas', 'search']" />
    </span>
  </p>
</template>

<script>
export default {
  name: "SearchInput",
  props: {
    placeholder: {
      type: String
    },
    size:{
      type:String,
      default:''
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
