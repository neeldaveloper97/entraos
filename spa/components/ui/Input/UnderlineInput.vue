<template>
<input :maxlength="maxLength" :value="value" :placeholder="placeholder" @input="$emit('input',$event.target.value)">
</template>

<script>
export default {
name: "UnderlineInput",
  props:{
    placeholder:{
      type:String
    },
    value:{
      type:String
    },
    maxLength:{
      type:Number,
      default:524288
    }
  }
}
</script>

<style lang="scss" scoped>
input {
  border-bottom: 1px solid $color--line!important;
  width:100%;
  border: 0;
  padding: 0.25rem;
  font-size:0.889rem;
}
</style>
