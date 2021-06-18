<template>
  <p>Logging in...</p>
</template>

<script>
export default {
  name: "Login",
  auth: false,
  mounted() {
    const token = this.$route.query.access_token;
    this.$auth.ctx.app.$axios.setHeader("Authorization", "Bearer " + token);
  },
  methods: {},
};
</script>

<style scoped></style>
