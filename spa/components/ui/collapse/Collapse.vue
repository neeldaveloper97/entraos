<template>
  <div class="underline">
    <div class="collapse flex-row">
      <button class="flex-row items-center" :disabled="disabled" @click="delay">
        <span :class="[titleClass,conditionalColor]">{{ title | titleize }}</span>
        <font-awesome-icon v-show="showContent === false" class="color--high-emphasis" :icon="['fas', 'angle-down']" />
        <font-awesome-icon v-show="showContent" class="color--high-emphasis" :icon="['fas', 'angle-up']" />
      </button>
      <slot name="right" />
    </div>
    <transition name="slide">
      <div v-show="showContent">
        <slot />
      </div>
    </transition>
</div>
</template>

<script>
import colorMixin from "@/mixins/colorMixin";

export default {
  name: "Collapse",
  mixins: [colorMixin],
  props: {
    title: {
      type: String,
      required: true
    },
    enableColor: {
      type: Boolean,
      default: false
    },
    startUncollapsed: {
      type: Boolean,
      required: false,
      default: false
    },
    titleSize: {
      type: String,
      default: 'regular'
    }
  },
  data() {
    return {
      showContent: false,
      disabled: false,
      timeout: null
    }
  },
  computed: {
    conditionalColor() {
      return (this.enableColor && this.showContent) ? this.fgColor : '';
    },
    titleClass() {
      switch (this.titleSize) {
        case 'regular':
          return 'font--title-regular';
        case "big":
          return 'font--title-big';
        default:
          return 'font--title-regular';
      }
    }
  },
  mounted() {
    this.showContent = this.startUncollapsed;
  },
  methods: {
    delay() {
      this.disabled = true
      this.timeout = setTimeout(() => {
        this.disabled = false
      }, 250)
      this.toggleContent()
    },
    toggleContent() {
      this.showContent = !this.showContent;
    }
  }
}
</script>

<style lang="scss" scoped>
button {
  border: none;

}
button:first-child {
  width: 100%;
}
.collapse {
  justify-content: space-between;

  background: white;
}

.collapse > * {
  padding: 1rem;
}


svg {
  margin: 0 0.5rem;
}

span {
  color: $color--high-emphasis;
}
</style>
