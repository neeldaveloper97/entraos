<template>
  <div class="checkbox-container">
    <div class="cover" :class="unclickable ? 'unclickable' :''" />
    <label class="checkbox font--title-small">
      <input type="checkbox" :disabled="disabled" :checked="checked" :value="value" @change.prevent="delay">
      {{ title }}
    </label>
  </div>
</template>

<script>
export default {
name: "CheckBox",
  props:{
  value:String,
    checked:Boolean,
    unclickable:{
    type:Boolean,
      default:false
    },
    title:String
  },
  data() {
    return {
      disabled: false,
      timeout: null
    }
  },
  methods:{
    delay(payload) {
      this.disabled = true
      this.timeout = setTimeout(() => {
        this.disabled = false
      }, 250)
      this.toggle(payload)
    },
  toggle(payload){
    this.$emit('input', {value:payload.target.value, checked: payload.target.checked})
  }
  }
}
</script>

<style lang="scss" scoped>
.cover{
  width:100%;
  height:100%;
  background:transparent;
  position:absolute;
  z-index:2;
  top:0;
  left:0;
  display:none;
}

.unclickable {
  display:block;
}
.checkbox-container{
  position:relative;
  display:block;
}
</style>
