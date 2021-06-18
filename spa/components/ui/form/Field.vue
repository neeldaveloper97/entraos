<template>
  <div class="field" :class="[hasGrouped,hasMultiline,addons,hasSpaced]">
    <slot />
  </div>
</template>

<script>
export default {
  name: "FieldTitle",
  props: {
    isGrouped: {
      type: Boolean,
      required: false,
      default: false
    },
    hasSpaceBetween: {
      type: Boolean,
      required: false,
      default: false
    },
    hasAddons: Boolean,
    isMultiline: {
      type: Boolean,
      required: false,
      default: false
    },
    wrap: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    addons() {
      return {
        'has-addons': this.hasAddons
      }
    },
    hasGrouped() {
      return {
        'is-grouped': this.isGrouped
      }
    },
    hasSpaced() {
      return {'space-between': this.hasSpaceBetween};
    },
    hasMultiline() {
      return {
        'is-grouped-multiline': this.isMultiline
      }
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
