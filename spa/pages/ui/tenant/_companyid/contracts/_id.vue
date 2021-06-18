<template>
  <div>
    <ContractDetail :contract="contract" />
  </div>
</template>
<script>
import { mapActions, mapGetters, mapState } from "vuex";
import ContractDetail from "@/domain/pages/contract/ContractDetail";
export default {
  components: {
    ContractDetail
  },
  layout: "Nav",
  computed: {
      ...mapState({
        contract: state => state.contract.storage.contract
    })
  },
  async mounted() {
      await this.get_contract_by_id({id:`${this.$route.params.id}`})
  },
  methods: {
      ...mapActions("contract",  ["get_contract_by_id"])
  }
};
</script>

<style scoped></style>
