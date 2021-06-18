<template v-slot:nav>
  <div class="content">
    <div v-for="(category, index) in contract_categories" :key="index">
      <div class="card mb-2 mt-2">
        <div class="contract-title">
          {{ category.name }}
        </div>
      </div>
      <div v-if="getTemplates(category.id).length > 0" class="listing-card card">
        <div
          v-for="(template, index) in getTemplates(category.id)" :key="index"
          class="button is-fullwidth contract-listing"
        >
          <nuxt-link :to="`/ui/buiding-owner/templates/${template.id}/`">
            {{ template.contract_name }}
          </nuxt-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "ContractListing",
  components: {},
  data() {
    return {};
  },
  computed: {
    ...mapState('template', ['templates', 'contract_categories']),

    getTemplate() {
      return this.templates;
    }
  },
  methods: {
    getTemplates(categoryId) {
      return this.getTemplate.filter(template => template.contract_category_id === categoryId);
    }
  }
};
</script>

<style lang="scss" scoped>
.contract-listing {
  margin-top: 10px;
  text-align: center;
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
</style>
