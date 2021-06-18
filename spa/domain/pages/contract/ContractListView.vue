<template>
  <div>
    <div
      v-for="(category, index) in Object.keys(getContractsGroupedByCategory)"
      :key="index"
      class="card mb-2 mt-2"
    >
      <div
        v-if="getContractsGroupedByCategory[category].length > 0"
        class="card mb-2 mt-2"
      >
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
          class="contract-listing p-1 m-1 is-fullwidth is-clickable"
        >
          <div class="contract-item-wrapper">
            <div
              class="contract-detail-wrapper"
              @click="$emit('contract-selected-event', contract)"
            >
              <div class="title is-5 wordwrap">
                {{ contract.contract_name }}
              </div>
              <div class="subtitle is-6">
                {{ displayQuantity(contract) }}
              </div>
            </div>
            <div class="contract-operation-wrapper">
              <span
                :class="{
                  dotA: contract.status === 'Activated' && !contractExpired(contract),
                  dotB:
                    contract.status === 'Pending_For_Confirmation' &&
                    !contractExpired(contract),
                  dotC: contract.status === 'Inactivated' || contractExpired(contract),
                }"
              ></span>
              <button
                v-if="hasSuperAdminRole"
                class="button is-danger is-small is-outlined"
                @click="deleteContract(contract)"
              >
                <span>Delete</span>
                <span class="icon is-small">
                  <i class="fas fa-times"></i>
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import ModalNavRegular from "@/components/ui/nav/ModalNav";
import CompositionNav from "@/components/ui/nav/MobileNav";
import roleMixin from "@/mixins/roleMixin";
import { mapActions, mapState } from "vuex";

export default {
  components: {
    ModalFullscreen,
    ModalNavRegular,
    CompositionNav,
  },
  mixins: [roleMixin],
  props: ["contracts", "categories", "personcontractmapping"],
  data() {
    return {
      category_map_id_name: [],
      contract_assigned_count_mapping: null,
    };
  },
  computed: {
    getContractsGroupedByCategory() {
      if (this.categories === undefined || this.contracts === undefined) {
        return [];
      }

      return this.contracts.reduce((result, obj) => {
        const category_name = this.category_map_id_name[obj.contract_category_id];
        result[category_name] = result[category_name] || [];

        let take = true;
        this.$emit("contract-filtered-event", obj, (x) => (take = x));
        if (!take) {
          return result;
        }
        result[category_name].push(obj);
        return result;
      }, {});
    },
  },
  mounted() {
    this.category_map_id_name = this.categories.reduce((result, item) => {
      result[`${item.id}`] = item.name;
      return result;
    }, {});

    if (this.personcontractmapping) {
      this.contract_assigned_count_mapping = this.personcontractmapping.reduce(
        (result, item) => {
          if (result[`${item.contract_id}`] === undefined) {
            result[`${item.contract_id}`] = 1;
          } else {
            result[`${item.contract_id}`]++;
          }
          return result;
        },
        {}
      );
    }
  },
  methods: {
    ...mapActions("contract", ["remove_contract"]),
    ...mapActions("person_contract", ["remove_person_contract_by_contract_id"]),
    contractExpired(contract) {
      return this.$moment().isAfter(this.$moment(contract.valid_to));
    },
    deleteContract(contract) {
      if (
        confirm(
          "Deleting this contract will also withdraw this contract from all persons. Are you sure to process?"
        )
      ) {
        //withdraw this contract
        this.remove_person_contract_by_contract_id({
          contract_id: contract.id,
          callbackfunc: () => {
            //remove this contract
            this.remove_contract({
              contract_id: contract.id,
            });
          },
        });
      }
    },
    displayQuantity(contract) {
      if (this.contract_assigned_count_mapping != null) {
        let count = this.contract_assigned_count_mapping[`${contract.id}`];
        if (!count) {
          count = 0;
        }
        return `${count} / ${contract.quantity} being assigned`;
      } else {
        return `Quantity: ${contract.quantity}`;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.wordwrap {
  white-space: pre-wrap; /* CSS3 */
  white-space: -moz-pre-wrap; /* Firefox */
  white-space: -o-pre-wrap; /* Opera 7 */
  word-wrap: break-word; /* IE */
}
.contract-listing {
  border: 1px solid black;
  border-radius: 0.25rem;
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

.contract-item-wrapper {
  display: flex;
  flex-direction: row;
}

.contract-detail-wrapper {
  display: flex;
  align-items: center;
  flex-direction: column;
  flex-grow: 2;
}

.contract-operation-wrapper {
  flex-grow: 0;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-direction: column;
}
.dotA {
  height: 15px;
  width: 15px;
  background-color: green;
  border-radius: 50%;
  display: inline-block;
}
.dotB {
  height: 15px;
  width: 15px;
  background-color: yellow;
  border-radius: 50%;
  display: inline-block;
}
.dotC {
  height: 15px;
  width: 15px;
  background-color: red;
  border-radius: 50%;
  display: inline-block;
}
</style>
