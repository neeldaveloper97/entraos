<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav link="/ui" title="Company Contract" />
      </template>
      <template v-slot:content>
        <div v-for="(company, index) in companyContract" :key="index">
          <div class="card p-2 m-5">
            <div class="columns add-padding">
              <div
                class="column is-one-fifth is-one-third-mobile is-align-self-center is-clickable"
                @click="checkContractDetails(company.name)"
              >
                {{ company.name }}
              </div>
              <div class="column text-right">
                <span class="dotA"></span>
              </div>
            </div>
          </div>
        </div>
        <div class="p-2 m-5">
          <tr>
            <td><span class="dotA"></span></td>
            <td class="pl-4">
              All Approved and valid.
            </td>
          </tr>
          <tr>
            <td><span class="dotB"></span></td>
            <td class="pl-4">
              At least one contract expired or invalidated.
            </td>
          </tr>
          <tr>
            <td><span class="dotC"></span></td>
            <td class="pl-4">
              At least one contract has been updated by this tenent and been
              pending for aproval.
            </td>
          </tr>
        </div>

        <ContractDetail
          v-if="isCompanySelected"
          :title="companySelected"
          @saved="changeCompanyStatus"
          @closed="changeCompanyStatus"
        />
      </template>
    </ModalFullscreen>
  </div>
</template>

<script>
import CompositionNav from "@/components/ui/nav/MobileNav";
import ContractDetail from "@/domain/company-contract/ContractDetails";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import { mapActions, mapState } from "vuex";

export default {
  name: "CompanyContractTemplate",
  components: {
    CompositionNav,
    ModalFullscreen,
    ContractDetail
  },
  data() {
    return {
      companySelected: null,
      isCompanySelected: false,
      companyContract: []
    };
  },
  async mounted() {
    await this.getCompanyContracts();
    await this.setCompanyContracts();
  },
  computed: {
    ...mapState("company_contract", ["company_contracts"])
  },
  methods: {
    ...mapActions("company_contract", ["getCompanyContracts"]),

    setCompanyContracts() {
      this.companyContract = this.company_contracts;
    },
    changeCompanyStatus() {
      this.isCompanySelected = !this.isCompanySelected;
    },
    checkContractDetails(companyName) {
      this.changeCompanyStatus();
      this.companySelected = `${companyName}`;
    }
  }
};
</script>

<style lang="scss" scoped>
.new-template {
  color: black;
  font-weight: 600;
}
.card {
  background-color: #efefef;
  border-radius: 10px;
}
.dotA {
  height: 15px;
  width: 15px;
  background-color: green;
  border-radius: 50%;
  display: inline-block;
}
td {
  padding-top: 15px;
}
.dotB {
  height: 15px;
  width: 15px;
  background-color: red;
  border-radius: 50%;
  display: inline-block;
}
.text-right {
  text-align: right;
}
.add-padding {
  padding: 0px 40px;
}
.dotC {
  height: 15px;
  width: 15px;
  background-color: yellow;
  border-radius: 50%;
  display: inline-block;
}
</style>
