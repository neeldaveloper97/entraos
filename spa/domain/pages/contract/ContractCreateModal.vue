<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav
          :cross-button="true"
          title="Create a contract"
          @clicked="$emit('closed')"
        />
      </template>

      <template v-slot:content>
        <div class="p-3 content">
          <p class="align-center mt-2">
Please select a template from the list below
</p>
          <div
            v-for="(category, index) in Object.keys(getTemplatesGroupedByCategory)"
            :key="index"
            class="card mb-2 mt-2"
          >
            <div class="card mb-2 mt-2">
              <div class="title is-5">
                {{ category }}
              </div>
            </div>
            <div
              v-if="getTemplatesGroupedByCategory[category].length > 0"
              class="listing-card card"
            >
              <div
                v-for="(template, index) in getTemplatesGroupedByCategory[category]"
                :key="index"
                class="button is-fullwidth contract-listing is-clickable"
                @click="showContractViewFromTemplate(template)"
              >
                <div class="is-align-self-center">
                  {{ template.contract_name }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </ModalFullscreen>
    <ContractViewEditModal
      v-if="show_modal"
      :contract="new_contract"
      :edit="false"
      :company="company"
      :contract_types="contract_types"
      :categories="categories"
      @closed="show_modal = false"
    />
  </div>
</template>

<script>
import CompositionNav from "@/components/ui/nav/MobileNav";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import RegularInput from "@/components/ui/Input/RegularInput";
import ContractViewEditModal from "@/domain/pages/contract/ContractViewEditModal";
import { mapActions } from "vuex";

export default {
  components: {
    CompositionNav,
    ModalFullscreen,
    RegularInput,
    ContractViewEditModal,
  },
  props: ["company", "categories", "contract_templates", "contract_types"],
  data() {
    return {
      show_modal: false,
      new_contract: {},
      category_map_id_name: [],
    };
  },
  computed: {
    getTemplatesGroupedByCategory() {
      if (this.categories === undefined || this.contract_templates === undefined) {
        return [];
      }

      return this.contract_templates.reduce((result, obj) => {
        const category_name = this.category_map_id_name[obj.contract_category_id];
        result[category_name] = result[category_name] || [];
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
  },
  methods: {
    showContractViewFromTemplate(template) {
      this.new_contract = { ...template };
      this.show_modal = true;
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
