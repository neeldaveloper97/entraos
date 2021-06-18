<template>
  <div>
    <h1>Contract List</h1>
    <ul>
      <li>
        <nuxt-link
          :to="`/ui/buiding-owner/companies/${this.$route.params.id}/contract/12345`"
        >
          Contract 1
        </nuxt-link>
      </li>
      <li>
        <nuxt-link
          :to="`/ui/buiding-owner/companies/${this.$route.params.id}/contract/67890`"
        >
          Contract 2
        </nuxt-link>
      </li>
      <li>
        <nuxt-link
          :to="`/ui/buiding-owner/companies/${this.$route.params.id}/contract/10203`"
        >
          Contract 3
        </nuxt-link>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  components: {}
};
</script>

<style scoped></style>
