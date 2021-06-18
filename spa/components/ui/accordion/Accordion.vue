<template>
<div class="accordion-wrapper">
<div v-if="toggled" class="accordion-content">
    <slot>
</slot>
</div>
</div>
</template>

<script>
export default {
  name: "Accordion",
  data() {
    return {
      toggle:false
    }
  },
  computed:{
    toggled(){
      if (this.toggle)
        return {
          'has-text-link': this.hasAddons
        }
      else
        return {

        }
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
