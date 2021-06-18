<template>
<button class="font--description color--light-blue" @click.prevent="clicked">
  <span>
    {{ text }}
  </span>
  <font-awesome-icon class="color--main-brand" :icon="['fas', 'times']" />
</button>
</template>

<script>

export default {
name: "SelectedButton",
  components:{
  },
  props:{
  text:{
    required:true,
    type:String
  },
    id:{
    required:false,
      type:String,
      default:''
    }
  },
  methods:{
  clicked(){
    const payload = this.id.length === 0 ? this.text : this.id;
    this.$emit('clicked',payload);
  }
  }
}
</script>

<style lang="scss" scoped>
button {
  border-radius: 4px;
  border:none;
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  background: $color--light-blue;
  color:$color--main-brand;
  padding: 0.25rem 0.5rem;
  margin-bottom:1rem;
}
button{
  margin: 0 0.25rem 0.25rem;
}
span {
  padding-right:0.5rem;
}
</style>
