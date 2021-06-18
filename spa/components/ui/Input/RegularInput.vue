<template>
  <p class="control" :class="[hasIcon]">
    <input
      :disabled="disabled"
      :value="value"
      class="input"
      :placeholder="placeholder"
      :type="inputType"
      :min="min"
      :max="max"
      @input="$emit('input', $event.target.value)"
      @blur="$emit('blur')"
    />
    <span v-if="isSlotRightExists" class="icon is-small is-right">
      <slot name="icon-right" />
    </span>
    <span v-if="isSlotLeftExists" class="icon is-small is-left">
      <slot name="icon-left" />
    </span>
  </p>
</template>
<script>
export default {
  name: "InputRegular",
  props: {
    placeholder: {
      type: String,
    },
    disabled: Boolean,
    min: {},
    max: {},
    value: {},
    inputType: {
      type: String,
      required: false,
      default: "text",
    },
    error: Boolean,
  },
  computed: {
    hasIcon() {
      return {
        "has-icons-right": this.$slots["icon-right"],
        "has-icons-left": this.$slots["icon-left"],
      };
    },
    isSlotLeftExists() {
      return !!this.$slots["icon-left"];
    },
    isSlotRightExists() {
      return !!this.$slots["icon-right"];
    },
    hasIconsRight() {
      return this.isSlotLeftExists() ? "" : "has-icons-right";
    },
    hasIconsLeft() {
      return this.isSlotRightExists() ? "" : "has-icons-left";
    },
  },
};
</script>

<style lang="scss" scoped></style>
