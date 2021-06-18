<template>
  <label class="radio">
    <input type="radio" name="answer" @click="$emit('input',inputValue)">
    {{ name }}
  </label>
</template>

<script>
export default {
  name: "RadioButton",
  props:{
    name:{
      type:String,
      required:true
    },
    inputValue:{
      type: [String,Number,Boolean],
      required:true
    }
  }
}
</script>

<style scoped>

</style>
