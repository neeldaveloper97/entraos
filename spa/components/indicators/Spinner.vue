<template>
  <pulse-loader :loading="spinnerVisible" />
</template>

<script>
export default {
name: "Spinner",
  data () {
    return { spinnerVisible: false }
  },
  created() {
    this.$eventHub.$on('before-request', this.showSpinner);
    this.$eventHub.$on('request-error',  this.hideSpinner);
    this.$eventHub.$on('after-response', this.hideSpinner);
    this.$eventHub.$on('response-error', this.hideSpinner);
  },
  beforeDestroy() {
    this.$eventHub.$off('before-request', this.showSpinner);
    this.$eventHub.$off('request-error',  this.hideSpinner);
    this.$eventHub.$off('after-response', this.hideSpinner);
    this.$eventHub.$off('response-error', this.hideSpinner);
  },
  methods: {
    showSpinner() {
      console.log('show spinner');
      this.spinnerVisible = true;
    },
    hideSpinner() {
      console.log('hide spinner');
      this.spinnerVisible = false;
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
