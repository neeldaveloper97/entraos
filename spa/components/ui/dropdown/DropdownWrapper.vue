<template>
  <div v-click-outside="away" @click="toggle">
    <slot name="toggler">
      <font-awesome-icon v-if="!hasEllipsisDisabled" :icon="['fas', 'ellipsis-v']" />
    </slot>
    <slot />
  </div>
</template>

<script>
import vClickOutside from 'v-click-outside'

export default {
  name: "DropdownWrapper",
  directives: {
    clickOutside: vClickOutside.directive
  },
  props: {
    hasEllipsisDisabled: {
      type: Boolean,
      required: false
    }
  },
  provide() {
    return {
      sharedState: this.sharedState
    }
  },
  data() {
    return {
      sharedState: {
        active: false
      }
    }
  },
  methods: {
    toggle() {
      this.sharedState.active = !this.sharedState.active
    },
    away() {
      this.sharedState.active = false
    }
  }
}
</script>

<style lang="scss" scoped>
div {
  position: relative
}
</style>
