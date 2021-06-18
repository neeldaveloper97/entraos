<template>
<div class="nav">
    <slot />
</div>
</template>

<script>
export default {
    name: "NavContainer"
}
</script>

<style lang="scss" scoped>
.nav {
    //z-index: 2;
    box-shadow: $shadow--navbar;
    position: relative;
}
</style>
