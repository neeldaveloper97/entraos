<template>
  <div class="nav" :class="bgColor">
    <nuxt-link v-if="link !== undefined && link.length !== 0" :to="link">
      <font-awesome-icon :class="iconColor" :icon="['fas', 'angle-left']" />
    </nuxt-link>
    <a v-else @click.prevent="closeClicked">
      <font-awesome-icon v-show="!crossButton" :class="iconColor" :icon="['fas', 'angle-left']" />
      <font-awesome-icon v-show="crossButton" :class="iconColor" :icon="['fas', 'times']" />
    </a>
    <slot>
      <span v-if="title.length > 0" :class="titleColor" class="font--title-big center">{{ title | titleize }} </span>
    </slot>
    <div>
      <slot name="right" />
    </div>
    <a v-if="rightText.length > 0" :class="[iconColor]" @click.prevent="saveClicked">
      {{ rightText | titleize }}
    </a>
  </div>
</template>
<script>

export default {
  name: "ModalNav",
  props: {
    link: {
      type: String,
      default:''
    },
    title: {
      type: String,
      default:''
    },
    color: {
      type: String,
      default: 'main-brand'
    },
    crossButton:{
      type:Boolean,
      default:false
    },
    rightText:{
      type:String,
      default:''
    }
  },
  computed: {
    bgColor() {
      switch (this.color) {
        case 'main-brand':
          return 'bg-color--main-brand';
        case "white":
          return 'bg-color--white';
          case 'notification':
            return 'bg-color--notification'
        case 'dark-shades':
          return 'bg-color--dark-shades';
          case 'high-emphasis':
            return 'bg-color--high-emphasis'
        default:
          return 'main-brand';
      }
    },
    titleColor() {
      switch (this.color) {
        case "white":
          return 'color--high-emphasis';
        default:
          return 'color--white';
      }
    },

    iconColor() {
      switch (this.color) {
        case "white":
          return 'color--main-brand';
        default:
          return 'color--white';
      }
    },
  },
  methods:{
    saveClicked(){
      this.$emit('saveClicked');
    },
    closeClicked(){
      this.$emit('clicked')
    }
  }
}
</script>

<style lang="scss" scoped>
.nav {
  display: flex;
  justify-content: space-between;
  box-shadow: $shadow--navbar;
  height: 3rem;
  width:100%;
  z-index:1;
}
.right {
  padding:0 1rem;
}
.center {
  width: 100%;
}
span {
  line-height: 3rem;
}

a {
  line-height: 3rem;
  padding: 0 1rem;
  z-index: 3;
  font-weight:bold;
}

</style>
