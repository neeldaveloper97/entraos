<template>
  <div class="select" :class="isFullWidth">
    <select
      v-model="selected"
      :disabled="disabled"
      @click="$emit('click')"
      @input="onInput"
    >
      <option value="" disabled selected>
Select your option
</option>
      <option v-for="(item, index) in selection" :key="index">
        {{ item }}
      </option>
    </select>
  </div>
</template>

<script>
export default {
  name: "DropdownSelect",
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    selection: {
      type: Array,
    },
    selectedProp: {
      type: String,
      default: "",
    },
    hasFullWidth: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      selected: null,
    };
  },
  computed: {
    isFullWidth() {
      return this.hasFullWidth ? "is-fullwidth" : "";
    },
  },
  watch: {
    selectedProp: {
      handler(data) {
        this.selected = data.length === 0 ? this.selection[0] : data;
      },
    },
  },
  mounted() {
    this.selected = this.selectedProp;
  },
  methods: {
    onInput($event) {
      this.selected = $event.target.value;
      this.$emit("input", this.selected);
    },
  },
};
</script>

<style lang="scss" scoped></style>
