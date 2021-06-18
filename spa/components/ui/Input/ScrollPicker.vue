<template>
<ul>
  <li v-for="(item,index) in values" :key="index" :data-page="index">
    {{ item }}
  </li>
</ul>
</template>

<script>
export default {
name: "ScrollPicker",
  props:{
  values:Array,
    id:{
    type:String,
      required:true
    }
  },
  handleResize() {
    const tbody = document.getElementById(this.id);
    const rect = tbody.getBoundingClientRect();

    const x = tbody.innerWidth / 2;
    const y = tbody.innerHeight / 2;
    const element = document.elementFromPoint(x, y);
    const page = element.getAttribute("data-page");
    document.querySelector(".middle") &&   document.querySelector(".middle").classList.remove("middle");
    element.classList.add("middle");
  },
  mounted() {
    document.addEventListener("scroll", this.handleScroll);
  }
}
</script>

<style lang="scss" scoped>
.middle{
  color:red;
}
</style>
