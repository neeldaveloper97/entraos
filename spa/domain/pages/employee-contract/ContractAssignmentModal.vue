<template>
  <div v-if="contracts && personcontractmapping && categories">
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav
          :cross-button="true"
          :title="getName()"
          right-text="Assign"
          @clicked="$emit('closed')"
          @saveClicked="save()"
        />
      </template>

      <template v-slot:content>
        <div class="p-3 content">
          <div class="chip-list-wrapper card-content mb-3">
            <div
              v-for="(c, j) in selected_contracts"
              :key="j"
              class="chip-item tag is-success"
            >
              {{ c.contract_name }}
              <button class="delete is-small" @click="remove(c)"></button>
            </div>
          </div>

          <p class="align-center mt-2">
Select a contract from the list below if any.
</p>
          <ContractListView
            :contracts="contracts_clone"
            :categories="categories"
            @contract-selected-event="contractSelected"
            @contract-filtered-event="contractFiltered"
          />
          <!--
          <div
            v-for="(category, index) in Object.keys(getContractsGroupedByCategory)"
            :key="index"
            class="card mb-2 mt-2"
          >
            <div class="card mb-2 mt-2">
              <div class="contract-title">
                {{ category }}
              </div>
            </div>
            <div
              v-if="getContractsGroupedByCategory[category].length > 0"
              class="listing-card card"
            >
              <div
                v-for="(contract, index) in getContractsGroupedByCategory[category]"
                :key="index"
                class="button is-fullwidth contract-listing is-clickable"
                @click="addToList(contract)"
              >
                <div class="is-align-self-center">
                  {{ contract.contract_name }}
                </div>
              </div>
            </div>
          </div>

-->
        </div>
      </template>
    </ModalFullscreen>
  </div>
</template>

<script>
import ContractListView from "@/domain/pages/contract/ContractListView";
import CompositionNav from "@/components/ui/nav/MobileNav";
import CreateContract from "@/domain/company-contract/CreateContract";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import { mapActions, mapGetters, mapState } from "vuex";

export default {
  components: {
    CompositionNav,
    ModalFullscreen,
    CreateContract,
    ContractListView,
  },
  props: ["person"],
  data() {
    return {
      selected_contracts: [],
      contracts_clone: [],
      contract_count_mapping: {},
      person_assigned_contracts: {},
    };
  },
  computed: {
    ...mapState({
      contracts: (state) => state.contract.storage.contracts,
      personcontractmapping: (state) =>
        state.person_contract.storage.personcontractmapping,
      categories: (state) => state.contract_category.storage.categories,
    }),

    /*
    getContractsGroupedByCategory() {
      if (
        this.categories === undefined ||
        this.personcontractmapping === undefined ||
        this.contracts === undefined
      ) {
        return [];
      }

      const category_collections = this.categories.reduce((result, item) => {
        result[`${item.id}`] = item.name;
        return result;
      }, {});

      const contract_count_mapping = this.personcontractmapping.reduce((result, item) => {
        if (result[`${item.contract_id}`] === undefined) {
          result[`${item.contract_id}`] = 1;
        } else {
          result[`${item.contract_id}`]++;
        }
        return result;
      }, {});

      const person_assigned_contracts = this.person.contractRoles.map(
        (e) => e.contractId
      );

      return this.contracts_clone.reduce((result, obj) => {
        const category_name = category_collections[obj.contract_category_id];
        result[category_name] = result[category_name] || [];
        //skip adding the contract already assigned
        if (person_assigned_contracts.includes(obj.id)) {
          return result;
        }
        //skip adding the contract exceeds its quantity
        if (
          contract_count_mapping[obj.id] &&
          contract_count_mapping[obj.id] >= obj.quantity
        ) {
          return result;
        }
        result[category_name].push(obj);

        return result;
      }, {});
    },
  
  */
  },
  async mounted() {
    //fectch all categories
    await this.get_contract_categories({});
    //fetch all conpany contracts
    await this.get_contracts_by_companyid({
      company_id: `${this.$route.params.companyid}`,
    });
    //fetch all assigned contracts for this company
    await this.get_person_contract_mapping_by_companyid({
      company_id: `${this.$route.params.companyid}`,
    });

    this.contracts_clone = this.lodash.cloneDeep(this.contracts);
    this.contract_count_mapping = this.personcontractmapping.reduce((result, item) => {
      if (result[`${item.contract_id}`] === undefined) {
        result[`${item.contract_id}`] = 1;
      } else {
        result[`${item.contract_id}`]++;
      }
      return result;
    }, {});

    this.person_assigned_contracts = this.person.contractRoles.map((e) => e.contractId);
  },

  methods: {
    ...mapActions("contract", ["get_contracts_by_companyid"]),
    ...mapActions("contract_category", ["get_contract_categories"]),
    ...mapActions("person_contract", [
      "get_person_contract_mapping_by_companyid",
      "assign_contract",
    ]),

    contractSelected(contract) {
      this.addToList(contract);
    },

    contractFiltered(contract, callbackfunc) {
      //only allow activated contracts
      if (contract.status !== "Activated") {
        callbackfunc(false);
        return;
      }
      //skip expired contracts
      if (this.$moment().isAfter(this.$moment(contract.valid_to))) {
        callbackfunc(false);
        return;
      }

      //skip the contract already assigned
      if (this.person_assigned_contracts.includes(contract.id)) {
        callbackfunc(false);
        return;
      }
      //skip adding the contract exceeds its quantity
      if (
        this.contract_count_mapping[contract.id] &&
        this.contract_count_mapping[contract.id] >= contract.quantity
      ) {
        callbackfunc(false);
        return;
      }
      callbackfunc(true);
    },

    getName() {
      return this.person.firstName + " " + this.person.lastName;
    },
    createContract(template, category) {
      this.selectedCategory = category;
      this.selectedTemplate = template;
      this.toggleContractEvent();
    },
    toggleContractEvent() {
      this.createNewContract = !this.createNewContract;
    },
    addToList(contract) {
      this.selected_contracts.push(contract);
      const index = this.contracts_clone.findIndex((e) => e.id === contract.id);
      this.contracts_clone.splice(index, 1);
    },
    remove(contract) {
      const index = this.selected_contracts.findIndex((e) => e.id === contract.id);
      this.contracts_clone.push(this.selected_contracts[index]);
      this.selected_contracts.splice(index, 1);
    },
    save() {
      if (this.selected_contracts.length > 0) {
        this.selected_contracts.forEach((element) => {
          this.assign_contract({
            company_id: `${this.$route.params.companyid}`,
            person_ref: this.person.id,
            contract_id: element.id,
          });
        });
        this.$emit("assigned");
        this.$emit("closed");
      }
    },
  },
};
</script>

<style lang="scss" scoped>
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
