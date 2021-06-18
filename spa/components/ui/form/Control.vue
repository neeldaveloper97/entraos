<template>
  <p class="control" :class="[hasIcon,isShrinked,marginBottom,expanded]">
    <slot />
  </p>
</template>

<script>
export default {
name: "Control",

  props:{
    hasIconsRight: {
      type:Boolean,
      required:false
    },
    isExpanded:{
      type:Boolean,
      requireD:false
    },
    hasIconsLeft: {
      type:Boolean,
      required:false
    },
    shrink:{
      type:Boolean,
      required:false,
      default:false
    },
    hasSiblingErrorElement:{
      type:Boolean,
      required:false,
      default:false
    }
  },
  computed: {
    hasIcon() {
      return {
        'has-icons-right': this.hasIconsRight,
        'has-icons-left': this.hasIconsLeft,
      }
    },
    expanded(){
      return {
        'is-expanded': this.isExpanded
      }
    },
    marginBottom(){
      return {
        'mb-error':this.hasSiblingErrorElement
      }
    },
    isShrinked(){
      return {
        'shrink': this.shrink
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.mb-error {
  margin-bottom:2rem;
}

</style>
