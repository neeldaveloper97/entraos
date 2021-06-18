<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav title="Select a template" @clicked="$emit('closed')" />
      </template>
      <template v-slot:content>
        <div class="p-3 content">
          <tr class="mb-3">
            <td style="vertical-align: middle">
              <b>
                Company Name
              </b>
            </td>
            <td class="pl-3">
              <div class="control">
                <input class="input" type="text" :value="contract" disabled />
              </div>
            </td>
          </tr>
          <p class="align-center mt-2">
            Select a template from the list below
          </p>
          <div
            v-for="(category, index) in contract_categories"
            :key="index"
            class="card mb-2 mt-2"
          >
            <div class="card mb-2 mt-2">
              <div class="contract-title">
                {{ category.name }}
              </div>
            </div>
            <div v-if="category.template.length > 0" class="listing-card card">
              <div
                v-for="(template, index) in category.template"
                :key="index"
                class="button is-fullwidth contract-listing is-clickable"
                @click="createContract(template, category.name)"
              >
                <div class=" is-align-self-center ">
                  {{ template }}
                </div>
              </div>
            </div>
          </div>
          <CreateContract
            v-if="createNewContract"
            :company="contract"
            :contract_name="selectedTemplate"
            :category="selectedCategory"
            @saved="toggleContractEvent"
            @closed="toggleContractEvent"
          />
        </div>
      </template>
    </ModalFullscreen>
  </div>
</template>

<script>
import CompositionNav from "@/components/ui/nav/MobileNav";
import CreateContract from "@/domain/company-contract/CreateContract";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
export default {
  name: "NewContract",
  components: {
    CompositionNav,
    ModalFullscreen,
    CreateContract
  },
  props: {
    contract: String
  },
  data() {
    return {
      createNewContract: false,
      selectedTemplate: null,
      selectedCategory: null,
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
  mounted() {
    this.selectedTemplate = null;
  },
  methods: {
    createContract(template, category) {
      this.selectedCategory = category;
      this.selectedTemplate = template;
      this.toggleContractEvent();
    },
    toggleContractEvent() {
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
</style>
