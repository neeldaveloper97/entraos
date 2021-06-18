<template>
  <div class="modal--container fixed" :style="zIndexStyle">
    <div ref="nav" class="nav-container">
      <slot name="nav" />
    </div>
    <div :style="[hasBottomSlot,hasNavSlot]">
      <div ref="content" class="wrap modal-scroll" :style="hasContentHeight">
        <slot name="content" />
      </div>
    </div>
    <div ref="bottom">
      <slot name="bottom" />
    </div>
  </div>
</template>
<script>
export default {
  name: "ModalFullscreen",
  props: {
    zIndex: {
      type: Number,
      default: 6
    }
  },
  data() {
    return {
      navHeight: 0,
      contentHeight: 0,
      bottomHeight: 0,
      tbodyHeight:0,
    }
  },
  computed: {
    zIndexStyle() {
      return {
        'z-index': this.zIndex
      }
    },
    hasBottomSlot() {
      return {
        'padding-bottom': this.bottomHeight + 'px'
      }
    },
    hasContentHeight(){
      return  {
        'height': this.tbodyHeight  -  this.bottomHeight  + 'px'
      }
    },
    hasNavSlot() {
      return {
        'padding-top': this.navHeight +  'px'
      }
    }
  },
  updated() {
    this.$nextTick(() => {
      this.handleResize();
    });
  },
  mounted() {
    window.addEventListener('resize', this.handleResize);
      this.handleResize();
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.handleResize);
  },
  methods: {
    handleResize() {
        this.navHeight = this.$slots.nav ? this.$refs.nav.clientHeight : 0;
        this.contentHeight = this.$slots.content ? this.$refs.content.firstChild.offsetHeight : 0;
        this.bottomHeight = this.$slots.bottom ? this.$refs.bottom.firstChild.offsetHeight : 0;
        const rect = this.$refs.content.getBoundingClientRect();
        const height = document.documentElement.clientHeight || window.innerHeight;
        this.tbodyHeight = height - rect.top;
    },

  }
}
</script>

<style lang="scss" scoped>

.modal--container {
  height: 100%;
  width: 100%;
}

.wrap {
  overflow-x: hidden;
  overflow-y: visible;
}

.wrap, .top-padding {
  height: 100%;
}

.bottom-padding {
  padding-bottom: 3rem;
  position: relative;
}

.nav-container{
  position: fixed;
  width:100%;
}


.fixed {
  width: 100%;
  height: 100%;
  background: white;
  position: fixed;
  left: 0;
  top: 0;
}
</style>
