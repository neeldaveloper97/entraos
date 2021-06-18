<template>
<p class="error">
<slot></slot>
</p>
</template>

<script>
export default {
  name: "InputError",
}
</script>

<style lang="scss" scoped>
.error {
  margin-left: 14px;
  margin-top: -1.6875rem;
  font-size:0.75rem;
  color:$color--notification;
  margin-bottom: 0.9375rem;
  line-height: 1;
}
</style>
