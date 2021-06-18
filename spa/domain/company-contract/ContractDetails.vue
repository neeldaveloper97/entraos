<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav
          :title="title + `'s Contract`"
          @clicked="$emit('closed')"
          @saveClicked="$emit('closed')"
        />
      </template>
      <template v-slot:content>
        <div class="p-3 content">
          <button
            class="button is-link is-light is-fullwidth"
            @click="createContract"
          >
            <span class="new-template">
              Create a New Contract
            </span>
          </button>
          <div v-for="(category, index) in contract_categories" :key="index">
            <div class="card mb-2 mt-2">
              <div class="contract-title">
                {{ category.name }}
              </div>
            </div>
            <div v-if="category.template.length > 0" class="listing-card card">
              <div
                v-for="(template, index) in category.template"
                :key="index"
                class="button is-fullwidth contract-listing"
              >
                <div
                  class="column is-one-fifth is-one-third-mobile is-align-self-center"
                >
                  {{ template }}
                </div>
                <div class="column text-right">
                  <span class="dotA"></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </ModalFullscreen>
    <NewContract
      v-if="createNewContract"
      :contract="title"
      @saved="createContract"
      @closed="createContract"
    />
  </div>
</template>

<script>
import NewContract from "@/domain/company-contract/ContractTemplateList";
import CompositionNav from "@/components/ui/nav/MobileNav";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
export default {
  name: "ContractDetail",
  components: {
    CompositionNav,
    ModalFullscreen,
    NewContract
  },
  props: {
    title: String
  },
  data() {
    return {
      createNewContract: false,
      contract_categories: [
        {
          name: "Food And beverage",
          template: [
            "Free Lunch contract",
            "Sponsored Lunch contract",
            "Free dinner contract",
            "Free meal contract",
            "Sponsored drink contract"
          ]
        },
        {
          name: "Resources and services",
          template: ["Parking rental contract", "Working space contract"]
        }
      ]
    };
  },
  computed: {},
  methods: {
    createContract() {
      this.createNewContract = !this.createNewContract;
    }
  }
};
</script>

<style lang="scss" scoped>
.contract-listing {
  margin-top: 10px;
  border: 2px solid black;
}
.contract-title {
  padding: 5px;
  color: purple;
}

.title-2 {
  color: yellow;
}

.listing-card {
  padding: 10px;
}

.dotA {
  height: 15px;
  width: 15px;
  background-color: rgb(255, 174, 0);
  border-radius: 50%;
  display: inline-block;
}
.text-right {
  text-align: right;
}
</style>
