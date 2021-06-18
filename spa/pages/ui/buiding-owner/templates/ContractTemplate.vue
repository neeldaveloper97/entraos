<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav link="/ui" title="Contract Templates" />
      </template>
      <template v-slot:content>
        <div class="p-3">
          <nuxt-link :to="`/ui/buiding-owner/templates/add/`">
            <button
              class="button is-link is-light is-fullwidth"
              @click="createContract"
            >
              <span class="new-template">
                Create a New Template
              </span>
            </button>
          </nuxt-link>
          <br />
          <div class="p-2 mt-1 card">
            <ContractListing />
          </div>
        </div>
      </template>
    </ModalFullscreen>
  </div>
</template>

<script>
import CompositionNav from "@/components/ui/nav/MobileNav";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import { mapActions } from "vuex";
import ContractListing from "./ContractListing";
export default {
  name: "ContractTemplate",
  components: {
    CompositionNav,
    ModalFullscreen,
    ContractListing
  },
  data() {
    return {
      createNewContract: false
    };
  },
  computed: {},
  mounted() {
    this.getContractCategories();
    this.getTemplates();
  },
  methods: {
    ...mapActions('template', ['getTemplates', 'getContractCategories']),
    createContract() {
      this.createNewContract = !this.createNewContract;
    },
    saveNewContract() {
      this.getTemplates();
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
</style>
