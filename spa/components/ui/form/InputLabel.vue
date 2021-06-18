<template>
  <label class="font--input-label">{{ text | titleize }}</label>
</template>

<script>
export default {
name: "InputLabel",
  props:{
  text:{
    required:true,
    type:String
  },

  }
}
</script>

<style lang="scss" scoped>

</style>
