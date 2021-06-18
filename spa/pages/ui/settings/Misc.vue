<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav link="/ui" title="Misc" />
      </template>
      <template v-slot:content>
        <div>
          <div class="panel-heading m-5">
            <p class="has-text-weight-normal contract-category">
              Contract category
            </p>
          </div>
          <div class="box m-5">
            <nuxt-link :to="`/ui/buiding-owner/settings/contract-category`">
              <button class="button is-link is-small">
                New
              </button>
            </nuxt-link>
            <div class="mt-5 list-content">
              <div v-for="(category, index) in contractCategories" :key="index">
                <span class="tag is-success">
                  <nuxt-link
                    :to="
                      `/ui/buiding-owner/settings/contract-category/${category.id}/`
                    "
                  >
                    {{ category.name }}
                  </nuxt-link>
                  <button
                    class="delete is-small"
                    @click.prevent="removeContractCategory(category.id)"
                  ></button>
                </span>
              </div>
            </div>
          </div>
        </div>
        <div>
          <div class="panel-heading m-5">
            <p class="has-text-weight-normal has-text-primary">
              Contract type
            </p>
          </div>
          <div class="box m-5">
            <nuxt-link :to="`/ui/buiding-owner/settings/contract-type`">
              <button class="button is-link is-small">
                New
              </button>
            </nuxt-link>
            <div class="mt-5 list-content">
              <div v-for="(type, index) in contractTypes" :key="index">
                <span class="tag is-success">
                  <nuxt-link
                    :to="`/ui/buiding-owner/settings/contract-type/${type.id}/`"
                  >
                    {{ type.name }}
                  </nuxt-link>
                  <button
                    class="delete is-small"
                    @click.prevent="removeContractType(type.id)"
                  ></button>
                </span>
              </div>
            </div>
          </div>
        </div>
        <div>
          <div class="panel-heading m-5">
            <p class="has-text-weight-normal has-text-danger">
              Contract property
            </p>
          </div>
          <div class="box m-5">
            <nuxt-link :to="`/ui/buiding-owner/settings/contract-property`">
              <button class="button is-link is-small">
                New
              </button>
            </nuxt-link>
            <div class="mt-5 list-content">
              <div v-for="(property, index) in contractProperties" :key="index">
                <span class="tag is-success">
                  <nuxt-link
                    :to="
                      `/ui/buiding-owner/settings/contract-property/${property.id}/`
                    "
                  >
                    {{ property.name }}
                  </nuxt-link>
                  <button
                    class="delete is-small"
                    @click.prevent="removeContractProperty(property.id)"
                  ></button>
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
    </ModalFullscreen>
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import CompositionNav from "@/components/ui/nav/MobileNav";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";

export default {
  name: "Misc",
  components: {
    CompositionNav,
    ModalFullscreen
  },
  data() {
    return {
      contractCategories: [],
      contractProperties: [],
      contractTypes: []
    };
  },
  computed: {
    ...mapState("contracts", ["contract_categories"]),
    ...mapState("contracts", ["contract_properties"]),
    ...mapState("contracts", ["contract_types"])
  },
  async mounted() {
    await this.getContractCategories();
    await this.getContractProperties();
    await this.getContractTypes();

    await this.initializer();
  },
  methods: {
    ...mapActions("contracts", [
      "getContractCategories",
      "getContractTypes",
      "getContractProperties",
      "deleteContractCategory",
      "deleteContractType",
      "deleteContractProperty"
    ]),
    initializer() {
      this.contractCategories = this.contract_categories;
      this.contractProperties = this.contract_properties;
      this.contractTypes = this.contract_types;
    },
    async removeContractCategory(id) {
      try {
        await this.deleteContractCategory(id);
        await this.getContractCategories();
        this.initializer();
      } catch (error) {}
    },
    async removeContractType(id) {
      try {
        await this.deleteContractType(id);
        await this.getContractTypes();
        this.initializer();
      } catch (error) {}
    },
    async removeContractProperty(id) {
      try {
        await this.deleteContractProperty(id);
        await this.getContractProperties();
        this.initializer();
      } catch (error) {}
    }
  }
};
</script>

<style scoped>
.panel-heading {
  border-radius: 0px;
}
.contract-category {
  color: #bb6bd9;
}
.box {
  border-radius: 0px;
  box-shadow: none;
  border: 2px dashed #9b9b9b;
}
.tag {
  border-radius: 15px;
}
.tag:not(body) {
  justify-content: space-between;
  width: 100%;
}
.tag a {
  color: #fff;
}
.delete {
  vertical-align: middle;
}
.list-content {
  display: grid;
  grid-gap: 1rem;
  margin: 0 auto;
}

@media (min-width: 600px) {
  .list-content {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 900px) {
  .list-content {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>
