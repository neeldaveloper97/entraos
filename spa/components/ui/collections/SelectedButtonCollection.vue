<template>
<div class="control">
  <slot />
</div>
</template>

<script>
export default {
name: "SelectedButtonCollection"
}
</script>

<style lang="scss" scoped>
div{
  display:flex;
  flex-direction: row;
  flex-wrap: wrap;
  margin-bottom:0.5rem;
}
</style>
