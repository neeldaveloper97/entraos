<template>
  <div class="shape" :style="bg" @click="$emit('clicked')">
    <p class="color--white full-width">
      {{ value }}
    </p>
    <div v-if="showDot" class="dot-container">
      <div class="dot" />
    </div>
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "FancyCircle",
  props: {
    value: [Number, String],
    colorValue:[Number,String],
    showDot:Boolean
  },
  computed: {
    bg() {
      return {
        //'background': `${this.bgColor[0]}`,
        'background': `linear-gradient(45deg, ${this.bgColor[0]} 0%, ${this.bgColor[1]} 100%)`
      }
    },
    bgColor(){
      if (this.colorValue >= 0 && this.colorValue <= 35)
        return ['#f14668','#ee2950']
      if (this.colorValue > 35 && this.colorValue <= 65)
        return ['#F7A15C','#f58e3b']
      if (this.colorValue > 65 && this.colorValue <= 85)
        return ['#f7e12a','#f5dd0e']
      if (this.colorValue >= 85 && this.colorValue <= 95)
        return ['#08D28F','#07bd80']
      if (this.colorValue >= 95)
        return ['#4280F8','#236bf6']
      return ['#a4aeb7','#909ca7']
    }
  }
}
</script>

<style lang="scss" scoped>

.shape {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  justify-content: center;
  align-content: stretch;
  align-items: center;
  //background: linear-gradient(45deg, lighten($color--light-accent, 20%) 0%, $color--light-accent 100%);
  //animation: morph 8s ease-in-out infinite;
  border-radius: 50%;
  height: 50px;
  transition: all 1s ease-in-out;
  width: 50px;
  z-index: 2;
  box-sizing: border-box;
}
.square{
  border-radius: 10px !important;
  width:110px;
}

.square .dot-container{
  right: 9px!important;
  top: -65%;
}
.dot-container {
  position: relative;
  transition: all 1s ease-in-out;
  float: right;
  width: 0;
  height: 0;
  top: -50%;
  right:14px;
}

.dot {
  position: absolute;
  height: 18px;
  width: 18px;
  border: 4px solid white;
  border-radius: 50%;
  background: $color--notification;
}

p{
  text-align: center;
  transition: all 1s ease-in-out;
}
@keyframes morph {
  0% {
    border-radius: 50% 50% 50% 50% / 50% 50% 50% 50%;
  }
  50% {
    border-radius: 30% 60% 70% 40% / 50% 60% 30% 60%;
  }
  100% {
    border-radius: 50% 50% 50% 50% / 50% 50% 50% 50%;
  }
}

</style>
