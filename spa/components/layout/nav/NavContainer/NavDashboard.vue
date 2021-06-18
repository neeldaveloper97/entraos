<template>
<div class="dashboard-container">
  <div class="flex-row space-between full-width">
    <a @click="showHamburger = true">
      <font-awesome-icon class="color--main-brand" :icon="['fas', 'bars']" />
    </a>
    
    <div />
  </div>
  <transition name="slide">
  <Hamburger v-if="showHamburger" @closed="showHamburger = false" />
  </transition>
</div>
</template>

<script>
import Hamburger from "@/components/layout/nav/NavContainer/Hamburger";
import SearchButton from "@/components/layout/nav/NavContainer/NavDashboard/SearchButton";
export default {
name: "NavigationDashboard",
  components: {
  Hamburger, SearchButton
  },
  data() {
    return {
      showHamburger:false
    }
  }
}
</script>

<style lang="scss" scoped>
svg{
margin:1rem;
}

.dashboard-container{
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding-right:1rem;
  height:3rem;
}
.dashboard-container > div {
  align-items: center;
}

</style>
