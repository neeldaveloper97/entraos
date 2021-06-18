<template>
  <div>
    <div class="tabs is-centered flex-column">
      <ul>
        <li v-for="(tab, index) in tabs" :key="index" :class="[{'is-active': tab.isActive},fullWidth]">
          <a @click="selectTab(tab)">
            {{ tab.name }}
          </a>
        </li>
      </ul>
      <div v-if="underline" class="tab-underline" />
    </div>
    <div class="tab-details">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  name: "TabsOld",
  components: {},
  props: {
    underline: {
      required: false,
      type: Boolean,
      default: false
    },
    isFullWidth: {
      type: Boolean,
      default: false
    },
    bubbles: {
      type: Array
    }
  },
  data() {
    return {
      tabs: [],
    }
  },
  computed: {
    fullWidth() {
      return this.isFullWidth ? 'full-width' : '';
    }
  },
  created() {
    this.tabs = this.$children;
  },
  methods: {
    selectTab(selectedTab) {
      this.tabs.forEach(
        function (tab) {
          tab.isActive = (selectedTab.name === tab.name)
        }
      )
    }
  }
}
</script>
<style lang="scss" scoped>
.tab-underline {
  height: 0.25rem;
  background: $color--line;
  width: 100%;
}
</style>
