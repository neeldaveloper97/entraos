<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav :link="backLink" :title="`${company.name}'s contracts`" />
      </template>
      <template v-slot:content>
        <div class="p-3 content">
          <div class="action-wrapper pt-1 pb-1">
            <button
              class="button is-link is-light is-fullwidth"
              @click="createContract()"
            >
              <span class="create-company"> Create a New Contract </span>
            </button>
          </div>
          <ContractListView
            :contracts="contracts"
            :categories="categories"
            :personcontractmapping="personcontractmapping"
            @contract-selected-event="contractSelected"
          />
        </div>
      </template>
    </ModalFullscreen>
    <ContractCreateModal
      v-if="show_create_modal"
      :company="company"
      :categories="categories"
      :contract_templates="contract_templates"
      :contract_types="contract_types"
      @closed="show_create_modal = false"
    />
    <ContractViewEditModal
      v-if="show_edit_modal"
      :company="company"
      :contract_types="contract_types"
      :categories="categories"
      :contract="selected_contract"
      :edit="true"
      @closed="show_edit_modal = false"
    />
  </div>
</template>

<script>
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import ModalNavRegular from "@/components/ui/nav/ModalNav";
import CompositionNav from "@/components/ui/nav/MobileNav";
import ContractListView from "@/domain/pages/contract/ContractListView";
import ContractViewEditModal from "@/domain/pages/contract/ContractViewEditModal";
import ContractCreateModal from "@/domain/pages/contract/ContractCreateModal";
export default {
  components: {
    ModalFullscreen,
    ModalNavRegular,
    CompositionNav,
    ContractListView,
    ContractViewEditModal,
    ContractCreateModal,
  },
  props: [
    "contracts",
    "company",
    "categories",
    "contract_types",
    "contract_templates",
    "personcontractmapping",
  ],
  data() {
    return {
      show_create_modal: false,
      show_edit_modal: false,
      selected_contract: null,
    };
  },
  computed: {
    backLink() {
      let user_role = localStorage.getItem("userrole");
      user_role = JSON.parse(user_role);

      if (user_role) {
        if (
          user_role.roles.includes("SUPERADMIN") &&
          user_role.roles.includes("COMPANYADMIN")
        ) {
          return "/ui";
        } else if (user_role.roles.includes("SUPERADMIN")) {
          return "/ui/buiding-owner/companies";
        } else if (user_role.roles.includes("COMPANYADMIN")) {
          //return `/ui/tenant/${this.$route.params.companyid}/contracts`;
          return "/ui";
        }
      } else {
        return "/ui";
      }
    },
  },
  async mounted() {},
  methods: {
    createContract() {
      this.show_create_modal = true;
    },
    contractSelected(contract) {
      this.show_edit_modal = true;
      this.selected_contract = contract;
    },
  },
};
</script>

<style scoped>
.contract-listing {
  margin-top: 10px;
  border: 2px solid black;
}
.contract-title {
  text-align: center;
  padding: 5px;
  color: purple;
}

.title-2 {
  color: yellow;
}

.align-center {
  text-align: center;
}

.listing-card {
  padding: 10px;
}

.text-right {
  text-align: right;
}

.chip-item {
  margin: 0.25rem;
}
</style>
