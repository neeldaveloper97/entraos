<template>
  <div v-if="personcompanymapping && contracts && categories && personcontractmapping">
    <EmployeeList
      :personcompanymapping="personcompanymapping"
      :contracts="contracts"
      :categories="categories"
      :personcontractmapping="personcontractmapping"
    />
  </div>
</template>
<script>
import { mapActions, mapGetters, mapState } from "vuex";
import EmployeeList from "@/domain/pages/employee-contract/EmployeeList";
import toaster from "@/mixins/toaster";
export default {
  components: {
    EmployeeList,
  },
  mixins: [toaster],
  computed: {
    ...mapState({
      personcompanymapping: (state) => state.person_company.storage.personcompanymapping,
      contracts: (state) => state.contract.storage.contracts,
      categories: (state) => state.contract_category.storage.categories,
      personcontractmapping: (state) =>
        state.person_contract.storage.personcontractmapping,
    }),
  },

  mounted() {
    this.get_person_company_mapping_by_companyid({
      company_id: `${this.$route.params.companyid}`,
    });
    this.get_contracts_by_companyid({
      company_id: `${this.$route.params.companyid}`,
    });
    this.get_contract_categories({});
    this.get_person_contract_mapping_by_companyid({
      company_id: `${this.$route.params.companyid}`,
    });
  },

  methods: {
    ...mapActions("person_company", ["get_person_company_mapping_by_companyid"]),
    ...mapActions("contract", ["get_contracts_by_companyid"]),
    ...mapActions("contract_category", ["get_contract_categories"]),
    ...mapActions("person_contract", ["get_person_contract_mapping_by_companyid"]),
  },
};
</script>

<style scoped></style>
