<template>
  <div>
    <NavContainer>
      <NavDashboard />
    </NavContainer>
    <Nuxt />
  </div>
</template>

<script>
import NavContainer from "@/components/layout/nav/NavContainer";
import NavDashboard from "@/components/layout/nav/NavContainer/NavDashboard";
export default {
name: "Nav",
  components: {
    NavContainer,
    NavDashboard
  }
}
</script>

<style lang="scss" scoped>

</style>
