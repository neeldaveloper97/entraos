<template>
  <figure class="image" :class="[size]">
    <img :class="{ 'is-rounded': isRounded }" :src="src" />
  </figure>
</template>

<script>
export default {
  name: "QImage",
  props: {
    src: { type: String},
    isRounded: { type: Boolean, default: false },
    is16x16: { type: Boolean }, // 16x16
    is24x24: { type: Boolean }, // 24x24px
    is32x32: { type: Boolean }, // 32x32px
    is48x48: { type: Boolean }, // 48x48px
    is64x64: { type: Boolean }, // 64x64px
    is96x96: { type: Boolean }, // 96x96px
    is128x128: { type: Boolean } // 128x128px
  },
  computed: {
    size() {
      const vm = this;
      const sizes = {
        is16x16: "is-16x16",
        is24x24: "is-24x24",
        is32x32: "is-32x32",
        is48x48: "is-48x48",
        is64x64: "is-64x64",
        is96x96: "is-96x96",
        is128x128: "is-128x128"
      };
      return Object.keys(sizes)
        .reduce((acc, item) => (vm[item] ? [...acc, sizes[item]] : acc), [])
        .join(" ");
    }
  }
};
</script>
