<template>
  <div class="flex-row">
    <div class="timeline pr-2 flex-column items-center timeline-left">
      <div v-show="showLine" class="vertical-line" :class="bgColor" />
      <div class="circle-container">
        <div class="border-circle" :class="borderColor" />
      </div>
    </div>
    <NormalButton is-outlined :class="color" size="small" text="Add" />
  </div>
</template>

<script>

import colorMixin from "@/mixins/colorMixin";

export default {
  name: "MilestoneAdd",
  mixins: [colorMixin],

  props: {
    showLine: Boolean
  }
}
</script>

<style lang="scss" scoped>
.vertical-line {
  width: 2px;
  height: 50%;
  top: 0;
  position: absolute;
  z-index: -1;
}

.border-circle {
  border-radius: 100%;
  border: 2px solid;
  width: 1rem;
  height: 1rem;
  background: white;

}

.timeline {
  position: relative;
}

.circle-container {
  display: flex;
  align-items: center;
  height: 100%;
}
</style>
