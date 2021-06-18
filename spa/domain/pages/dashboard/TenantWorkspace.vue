<template>
  <div v-if="selected_company && userrole.company_ids.length > 0">
    <div class="row">
      <div class="flex-row company-selection-section">
        <v-row align="center">
          <v-col cols="3">
            <v-subheader> Company name </v-subheader>
          </v-col>

          <v-col cols="9">
            <v-select
              v-model="selected_company"
              :hint="`org. number ${selected_company.org_number}`"
              :items="companies"
              item-text="name"
              item-value="id"
              label="Select"
              persistent-hint
              return-object
              single-line
              @change="selectCompany"
            ></v-select>
          </v-col>
        </v-row>
      </div>
    </div>

    <div class="row">
      <div class="flex-row">
        <DashboardCard
          :link="contractLink"
          text="CONTRACTS"
          color="color--main-brand"
          sub-text="View/edit contracts"
        >
          <template v-slot:icon>
            <font-awesome-icon class="color--main-brand" :icon="['fas', 'user']" />
          </template>
        </DashboardCard>
        <DashboardCard
          :link="employeeLink"
          text="EMPLOYEES' CONTRACTS"
          color="color--main-brand"
          sub-text="View/assign contracts to employees"
        >
          <template v-slot:icon>
            <font-awesome-icon class="color--main-brand" :icon="['fas', 'chart-bar']" />
          </template>
        </DashboardCard>
      </div>
    </div>
  </div>
</template>

<script>
import DashboardCard from "@/domain/pages/dashboard/DashboardCard";
import { mapActions, mapGetters, mapState } from "vuex";

export default {
  components: {
    DashboardCard,
  },
  props: ["userrole"],
  data() {
    return {
      selected_company: {},
      contract: "",
      employee: "",
    };
  },
  methods: {
    ...mapActions("company", ["get_companies_by_ids"]),
    selectCompany(item) {
      localStorage.setItem("selected_company_id", item.id);
    },
  },
  computed: {
    ...mapState({
      companies: (state) => state.company.storage.companies,
    }),
    contractLink() {
      return `/ui/tenant/${this.selected_company.id}/contracts`;
    },
    employeeLink() {
      return `/ui/tenant/${this.selected_company.id}/employees`;
    },
  },
  async mounted() {
    if (this.userrole.company_ids.length > 0) {
      await this.get_companies_by_ids({ ids: this.userrole.company_ids.join() });

      if (localStorage.getItem("selected_company_id")) {
        this.selected_company = this.companies.find(
          (i) => i.id === localStorage.getItem("selected_company_id")
        );
        if (!this.selected_company) {
          this.selected_company = this.companies.find((e) => e.name === "areo.io");
          if (!this.selected_company) {
            this.selected_company = this.companies[0];
          }
          localStorage.setItem("selected_company_id", this.selected_company.id);
        }
      } else {
        this.selected_company = this.companies.find((e) => e.name === "areo.io");
        if (!this.selected_company) {
          this.selected_company = this.companies[0];
        }
        localStorage.setItem("selected_company_id", this.selected_company.id);
      }
    }
  },
};
</script>

<style scoped>
.row {
  margin: 0 1rem 1rem;
}
.flex-row {
  flex-wrap: wrap;
  width: 100%;
}
.company-selection-section {
  background-color: white;
  padding: 1rem;
  border-radius: 10px;
}
</style>
