<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav link="/ui" title="Companies" />
      </template>
      <template v-slot:content>
        <div class="action-wrapper pt-1 pb-1">
          <button class="button is-link is-light is-fullwidth" @click="createCompany()">
            <span class="create-company"> Create a New Company </span>
          </button>
        </div>

        <div v-for="(company, index) in companies" :key="index">
          <div class="card p-2 m-5">
            <div class="columns add-padding">
              <div
                class="column is-four-fifths is-clickable"
                @click="viewCompanyContract(company)"
              >
                <p class="title is-6">
                  {{ company.name }}
                </p>
                <p class="subtitle is-6">
Org No. {{ company.org_number }}
</p>
              </div>
              <!--
              <div class="column text-right">
                <span class="dotA"></span>
              </div>
              -->
            </div>
            <div class="action">
              <button class="button is-text" @click="editCompany(company)">
Edit
</button>

              <button class="mgl-1 button is-text" @click="viewCompanyContract(company)">
                Manage contracts
              </button>
            </div>
          </div>
        </div>
      </template>
    </ModalFullscreen>
    <CompanyModal
      v-if="show_modal"
      :company="selected_company"
      @closed="show_modal = false"
    />
  </div>
</template>

<script>
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import ModalNavRegular from "@/components/ui/nav/ModalNav";
import CompositionNav from "@/components/ui/nav/MobileNav";
import CompanyModal from "@/domain/pages/company/CompanyModal";
export default {
  components: {
    ModalFullscreen,
    ModalNavRegular,
    CompositionNav,
    CompanyModal,
  },
  props: ["companies"],
  data() {
    return {
      selected_company: undefined,
      show_modal: false,
    };
  },
  methods: {
    createCompany() {
      this.selected_company = undefined;
      this.show_modal = true;
    },
    viewCompanyContract(company) {
      this.$router.push({ path: `/ui/buiding-owner/companies/${company.id}/contract` });
    },
    editCompany(company) {
      this.selected_company = company;
      this.show_modal = true;
    },
  },
};
</script>

<style scoped>
.card {
  background-color: #efefef;
  border-radius: 10px;
}
.create-company {
  color: black;
  font-weight: 600;
}
.action {
  display: flex;
  justify-content: flex-start;
}
.action-create-wrapper {
  margin: 0 auto;
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
  background-color: red;
  border-radius: 50%;
  display: inline-block;
}

.dotC {
  height: 15px;
  width: 15px;
  background-color: yellow;
  border-radius: 50%;
  display: inline-block;
}

.text-right {
  text-align: right;
}
</style>
