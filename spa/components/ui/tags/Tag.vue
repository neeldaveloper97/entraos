<template>
  <span class="tag" :class="[large,medium,normal,classColor]">{{ text }}
    <slot>
      <button v-if="hasDelete" class="delete" @click="$emit('delete')"></button>
    </slot>
</span>
</template>

<script>
export default {
  name: "Tag",
  props: {
    text: String,
    hasDelete: Boolean,
    isLarge:Boolean,
    isMedium:Boolean,
    isNormal:Boolean,
    classColor:String,
  },
  computed:{
    large(){
      return this.isLarge ? 'is-large' : ':'
    },
    medium(){
      return this.isMedium ? 'is-medium' : '';
    },
    normal(){
      return this.isNormal ? 'is-normal' : '';
    }
  }
}
</script>

<style scoped>

</style>
