<template>
  <div>
    <CompanyList :companies="companies" />
  </div>
</template>
<script>
import { mapActions, mapGetters, mapState } from "vuex";
import CompanyList from "@/domain/pages/company/CompanyList";
import toaster from "@/mixins/toaster";
export default {
  components: {
    CompanyList,
  },
  mixins: [toaster],
  computed: {
    ...mapState({
      companies: (state) => state.company.storage.companies,
    }),
  },
  async mounted() {
    await this.get_companies();
  },
  methods: {
    ...mapActions("company", ["get_companies"]),
  },
};
</script>

<style scoped></style>
