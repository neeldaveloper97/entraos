<template>
  <button :disabled="isDisabled" class="button"
          :class="[
            {'is-small': isSmall },
            {'is-medium': isMedium},
            {'is-large': isLarge},
            classColor,
            {'is-fullwidth' :isFullWith},
            {'is-outlined':isOutlined},
            {'is-inverted':isInverted},
            {'is-rounded':isRounded},
            {'is-light' :isLight}]"
          @click.prevent="$emit('clicked')"
>
    {{ text | titleize }}
    <slot />
  </button>
</template>

<script>
export default {
  name: "NormalButton",
  props: {
    text: {
      required: false,
      type: String,
      default: ''
    },
    classColor: {
      type: String,
      default: 'is-link'
    },
    isSmall: Boolean,
    isMedium: Boolean,
    isLarge: Boolean,
    isFullWith: Boolean,
    isOutlined: Boolean,
    isLight: Boolean,
    isInverted: Boolean,
    isRounded: Boolean,
    isDisabled: Boolean,
  }
}
</script>

<style lang="scss" scoped>

</style>
