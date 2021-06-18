<template>
  <div>
    <nuxt-link :to="link">
      <slot />
      {{ text }}
    </nuxt-link>
  </div>
</template>

<script>
export default {
name: "LinkWithIcon",
  props: {
  link:{
    type:String,
    required:true
  },
    text:{
    type:String,
      required:true
    }
  }
}
</script>

<style lang="scss" scoped>
a {
  display:inline-block;
}
a:first-child {
  margin-right:0.5rem;
}
</style>
