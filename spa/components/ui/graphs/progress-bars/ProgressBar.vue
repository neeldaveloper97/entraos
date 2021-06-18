<template>
  <progress class="progress is-success" :value="progressbarValue" max="100">
</progress>
</template>

<script>
export default {
name: "ProgressBar",
  props:{
  value:{
    required:true,
    type: Number
  },
    rounded:{
    type:Boolean,
      default:false
    }
  },
  computed:{
    progressbarValue(){
      return this.value;
    }
  }
}
</script>

<style lang="scss" scoped>


.progress-bar--background{
  background: $color--light-shades;
  width:100%;
  height:0.5rem;
}
.progress-bar--bar {

  background: $color--light-accent;
  height:0.5rem;
}

</style>
