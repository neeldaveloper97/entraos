<template>
  <div class="nav-container">
    <div class="nav">
      <div class="left">
        <NavCloseButton @clicked="$emit('clicked')" />
      </div>
      <div class="center font--title-big">
        {{ text | titleize }}
      </div>
      <div>
        <a v-if="rightButton.length > 0" class="right" @click.prevent="$emit('saveClicked')">
          {{ rightButton | capitalize }}
        </a>
      </div>
    </div>
  </div>
</template>
<script>
import NavCloseButton from "@/components/ui/buttons/NavCloseButton";
export default {
  name: "ModalNavRegular",
  components:{
    NavCloseButton

  },
  props:{
    text:{
      type:String,
      default:''
    },
    rightButton:{
      type:String,
      default:''
    },
    parentZ:{
      type:Number,
      default:500
    }
  }
}
</script>
<style lang="scss" scoped>
.nav-container {
  position: fixed;
  width:100%;
  top:0;
  z-index: 1;
}

.nav{
  display: flex;
  justify-content: space-between;
  background: $color--main-brand;
  box-shadow: 0px 4px 10px rgba(184, 186, 206, 0.37);
}


.left,.center,.right{
  line-height: 3rem;
}
.right{
  padding:0 1rem;
  color:white;
}
.center{
  color:white;
  text-align: center;
  left: -1rem;
  position: relative;
}

</style>
