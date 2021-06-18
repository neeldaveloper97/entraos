<template>
<div>
<nuxt-link to="/ui/search" class="flex-row">
  <span class="color--high-emphasis">Search</span>
  <font-awesome-icon class="color--main-brand" :icon="['fas', 'search']" />
</nuxt-link>
</div>
</template>

<script>
export default {
name: "SearchButton"
}
</script>

<style lang="scss" scoped>
a{
  padding-top:0.25rem;
  align-items: center;
}
span {
  border-bottom: 1px solid $color--line;
  padding-right:3rem;
  padding-bottom: 0.25rem;
}
</style>
