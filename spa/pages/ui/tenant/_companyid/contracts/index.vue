<template>
  <div
    v-if="
      contracts &&
      company &&
      categories &&
      contract_types &&
      contract_templates &&
      personcontractmapping
    "
  >
    <ContractList
      :contracts="contracts"
      :company="company"
      :categories="categories"
      :contract_types="contract_types"
      :contract_templates="contract_templates"
      :personcontractmapping="personcontractmapping"
    />
  </div>
</template>
<script>
import { mapActions, mapGetters, mapState } from "vuex";
import ContractList from "@/domain/pages/contract/ContractList";
import toaster from "@/mixins/toaster";
export default {
  components: {
    ContractList,
  },
  mixins: [toaster],
  computed: {
    ...mapState({
      contracts: (state) => state.contract.storage.contracts,
      company: (state) => state.company.storage.company,
      categories: (state) => state.contract_category.storage.categories,
      contract_types: (state) => state.contract_type.storage.contract_types,
      contract_templates: (state) => state.contract_template.storage.contract_templates,
      personcontractmapping: (state) =>
        state.person_contract.storage.personcontractmapping,
    }),
  },
  async mounted() {
    await this.get_contracts_by_companyid({
      company_id: `${this.$route.params.companyid}`,
    });
    await this.get_company_by_id({
      id: `${this.$route.params.companyid}`,
    });
    await this.get_contract_categories({});
    await this.get_contract_types({});
    await this.get_contract_templates({});
    await this.get_person_contract_mapping_by_companyid({
      company_id: `${this.$route.params.companyid}`,
    });
  },
  methods: {
    ...mapActions("contract", ["get_contracts_by_companyid"]),
    ...mapActions("company", ["get_company_by_id"]),
    ...mapActions("contract_category", ["get_contract_categories"]),
    ...mapActions("contract_type", ["get_contract_types"]),
    ...mapActions("contract_template", ["get_contract_templates"]),
    ...mapActions("person_contract", ["get_person_contract_mapping_by_companyid"]),
  },
};
</script>

<style scoped></style>
