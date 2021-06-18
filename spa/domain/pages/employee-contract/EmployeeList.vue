<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav link="/ui" title="Employees" />
      </template>
      <template v-slot:content>
        <div class="search-wrapper">
          <p class="control has-icons-right">
            <input
              v-model="query"
              class="input"
              type="search"
              placeholder="Filter email/phone no"
              @keyup="
                (event) => {
                  if (event.keyCode === 13) {
                    // Cancel the default action, if needed
                    event.preventDefault();
                    // Trigger the button element with a click
                    lookup();
                  }
                }
              "
            />
            <span v-if="query != ''" class="icon is-small is-right">
              <button class="delete is-small" @click="query = ''"></button>
            </span>
          </p>

          <p class="control">
            <a @click.prevent="lookup">
              <font-awesome-icon
                class="color--main-brand mt-3"
                :icon="['fas', 'search']"
              />
            </a>
          </p>
        </div>

        <div class="person-list-wrapper columns">
          <div
            v-for="(item, i) in personListFiltered"
            :key="i"
            class="card person-item column is-3"
          >
            <div class="card-content">
              <p class="title is-4">
                {{
                  item.person.firstName +
                  " " +
                  (item.person.middleName ? item.person.middleName + " " : "") +
                  item.person.lastName
                }}
              </p>
              <div class="content">
                <p v-if="item.person.personProfile.communicateTo" class="subtitle is-6">
                  Contact: {{ item.person.personProfile.communicateTo.value }}
                </p>
                <p v-if="item.created_at" class="subtitle is-6">
                  Date added:
                  <time datetime="2016-1-1">
                    {{ $moment(item.created_at).format("DD/MM/YYYY") }}</time>
                </p>
              </div>
            </div>

            <div class="chip-list-wrapper card-content">
              <div
                v-for="(c, j) in contractsGroupedByPersonRef[item.person_ref]"
                :key="j"
                class="chip-item tag is-success"
              >
                {{ c.contract_name }}
                <button
                  v-if="item.existing"
                  class="delete is-small"
                  @click="withdrawContract(item, c)"
                ></button>
              </div>
            </div>

            <footer class="card-footer">
              <button
                v-if="item.existing"
                class="card-footer-item button is-info is-outlined"
                @click="assignContract(item)"
              >
                Assign
              </button>
              <button
                v-if="!item.existing"
                class="card-footer-item button is-success is-outlined"
                @click="addPerson(item)"
              >
                Add
              </button>
              <button
                v-if="item.existing"
                class="card-footer-item button is-danger is-outlined"
                @click="removePerson(item)"
              >
                Delete
              </button>
            </footer>
          </div>
        </div>
      </template>
    </ModalFullscreen>
    <ContractAssignmentModal
      v-if="show_contract_assignment_modal === true"
      :person="selected_person"
      @closed="show_contract_assignment_modal = false"
      @assigned="$emit('refresh_mappinglist')"
    />
  </div>
</template>

<script>
import SearchInput from "@/components/ui/Input/SearchInput";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import ModalNavRegular from "@/components/ui/nav/ModalNav";
import CompositionNav from "@/components/ui/nav/MobileNav";
import NormalButton from "@/components/ui/buttons/NormalButton";
import ContractAssignmentModal from "@/domain/pages/employee-contract/ContractAssignmentModal";
import { mapActions, mapGetters, mapState } from "vuex";

export default {
  components: {
    ModalFullscreen,
    ModalNavRegular,
    CompositionNav,
    SearchInput,
    NormalButton,
    ContractAssignmentModal,
  },
  props: ["personcompanymapping", "contracts", "categories", "personcontractmapping"],
  data() {
    return {
      query: "",
      searchQuery: "",
      show_contract_assignment_modal: false,
      selected_person: {},
      map_person_contracts: {},
    };
  },
  computed: {
    contractsGroupedByPersonRef() {
      const map_person_assignedcontractIds = this.personcontractmapping.reduce(
        (result, item) => {
          result[`${item.person_ref}`] = result[`${item.person_ref}`] || [];
          result[`${item.person_ref}`].push(
            this.contracts.find((i) => i.id === item.contract_id)
          );
          return result;
        },
        {}
      );

      return this.personcompanymapping.reduce((result, item) => {
        result[`${item.person_ref}`] =
          map_person_assignedcontractIds[`${item.person_ref}`] || [];
        return result;
      }, {});
    },
    personListFiltered() {
      const personList = this.personcompanymapping.map((item) => {
        return {
          ...item,
          existing: true,
        };
      });

      if (this.query === "") {
        return personList;
      }

      const arr = personList.filter(
        (i) =>
          i.person.personProfile.communicateTo == null ||
          i.person.personProfile.communicateTo.value.includes(this.query.toLowerCase()) ||
          i.person.personProfile.defaultEmailContactInformation == null ||
          i.person.personProfile.defaultEmailContactInformation.value
            .toLowerCase()
            .includes(this.query.toLowerCase()) ||
          i.person.personProfile.defaultCellPhoneContactInformation == null ||
          i.person.personProfile.defaultCellPhoneContactInformation.value
            .toLowerCase()
            .includes(this.query.toLowerCase())
      );
      if (this.$store.state.company.storage.search_result) {
        if (Array.isArray(this.$store.state.company.storage.search_result)) {
          this.$store.state.company.storage.search_result.forEach((element) => {
            if (!personList.find((e) => e.person.id === element.id)) {
              arr.push({ person: element, existing: false });
            }
          });
        }
      }
      return arr;
    },
  },
  mounted() {},
  methods: {
    ...mapActions("company", ["search", "clear_searchresult"]),
    ...mapActions("contract", ["get_contracts_by_companyid"]),
    ...mapActions("person_company", ["add_person", "remove_person"]),
    ...mapActions("person_contract", [
      "withdraw_contract",
      "get_person_contract_mapping_by_companyid_and_personref",
    ]),

    assignContract({ person }) {
      this.selected_person = person;
      this.show_contract_assignment_modal = true;
    },
    withdrawContract({ person }, { id }) {
      if (confirm("Are you sure to withdraw this contract?")) {
        this.withdraw_contract({
          company_id: `${this.$route.params.companyid}`,
          person_ref: person.id,
          contract_id: id,
        });
      }
    },
    addPerson({ person }) {
      this.add_person({
        company_id: `${this.$route.params.companyid}`,
        person_ref: person.id,
        callbackfunc: () => {
          this.get_contracts_by_companyid({
            company_id: `${this.$route.params.companyid}`,
          });

          this.get_person_contract_mapping_by_companyid_and_personref({
            company_id: `${this.$route.params.companyid}`,
            person_ref: person.id,
          });
        },
      });
      //clear search
      this.query = "";
      this.clear_searchresult();
    },
    lookup() {
      this.search({
        company_id: `${this.$route.params.companyid}`,
        keyword: this.query,
      });
    },
    removePerson({ id, person }) {
      if (
        confirm(
          "Are you sure to remove " +
            person.firstName +
            " " +
            (person.middleName ? person.middleName + " " : "") +
            person.lastName +
            " from this company?"
        )
      ) {
        this.remove_person({
          company_id: `${this.$route.params.companyid}`,
          person_ref: person.id,
          id,
        });
      }
    },
  },
};
</script>

<style scoped>
.person-list-wrapper {
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
}

.person-item {
  padding: 0.5rem;
  box-sizing: border-box;
  margin: 0.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.chip-list-wrapper {
  flex-grow: 3;
}
.chip-item {
  margin: 0.25rem;
}

.search-wrapper {
  justify-content: center;
  margin-left: 1rem;
  margin-right: 1rem;
  margin-top: 1rem;
  display: flex;
  flex-wrap: wrap;
}
.search-wrapper :last-child {
  margin-left: 4px;
}

.action-wrapper {
  flex-grow: 1;
  display: flex;
  flex-wrap: nowrap;
  justify-content: space-between;
}
</style>
