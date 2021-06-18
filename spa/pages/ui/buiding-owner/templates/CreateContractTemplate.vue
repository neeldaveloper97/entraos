<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositeNav
          :cross-button="true"
          title="Create a Contract Template"
          right-text="save"
          @clicked="$router.push({ path: '/ui/buiding-owner/templates/' });"
          @saveClicked="save"
        ></CompositeNav>
      </template>
      <template v-slot:content>
        <div class="card m-4">
          <form>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Contract Category</label>
                <Dropdown
                  v-model="createContract.category"
                  :selection="categories"
                  :selected-prop="createContract.category"
                  @click="$v.createContract.category.$touch()"
                />
                <div
                  v-if="$v.createContract.category.$dirty && !createContract.category"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p
                      v-if="$v.createContract.category.$dirty"
                      class="error"
                    >
                      Category required.
                    </p>
                  </div>
                </div>
              </div>
            </section>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Contract Name </label>
                <RegularInput
                  v-model="createContract.contractName"
                  placeholder="Contract Name"
                  @blur="$v.createContract.contractName.$touch()"
                >
                </RegularInput>
                <div
                  v-if="$v.createContract.contractName.$dirty && !createContract.contractName"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p
                      v-if="$v.createContract.contractName.$dirty"
                      class="error"
                    >
                      Contract name required.
                    </p>
                  </div>
                </div>
              </div>
            </section>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Contract Type </label>
                <Dropdown
                  v-model="createContract.contractType"
                  :selection="contractTypes"
                  :selected-prop="createContract.contractType"
                  @click="$v.createContract.contractType.$touch()"
                />
                <div
                  v-if="$v.createContract.contractType.$dirty && !createContract.contractType"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p
                      v-if="$v.createContract.contractType.$dirty"
                      class="error"
                    >
                      Contract type required.
                    </p>
                  </div>
                </div>
              </div>
            </section>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Contract Description</label>
                <div>
                    <textarea
                    v-model="createContract.description"
                    class="description"
                    @blur="$v.createContract.description.$touch()"
                  ></textarea>
                </div>
                <div
                  v-if="$v.createContract.description.$dirty && !createContract.description"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p
                      v-if="$v.createContract.description.$dirty"
                      class="error"
                    >
                      Description required.
                    </p>
                  </div>
                </div>
              </div>
            </section>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Contract Properties </label>
                <button
                  class="button is-info"
                  type="button"
                  @click="addProperties"
                >
                  Create Properties
                </button>
                <div v-if="createContract.contractProperties.length">
                  <div class="columns mt-2">
                    <div class="column is-one-fifth">
                      Property Name
                    </div>
                    <div class="column is-one-third is-one-fifth-desktop">
                      Value
                    </div>
                  </div>
                  <div
                    v-for="(property, index) in $v.createContract.contractProperties.$each.$iter"
                    :key="index"
                  >
                    <tr class="columns mt-1">
                      <td class="column is-one-fifth">
                        <Dropdown
                          v-model="property.propertyName.$model"
                          :selection="contractProperties"
                          :selected-prop="property.propertyName.$model"
                          @click="isChanged=true; property.propertyName.$touch()"
                          @input="changePropertySelection(index)"
                        />
                        <div
                          v-if="property.propertyName.$error"
                          class="mb-2 align-center"
                        >
                          <div class="error-content">
                            <p v-if="!property.propertyName.required" class="error">
                              Category required.
                            </p>
                          </div>
                        </div>
                      </td>
                      <td class="column is-one-third is-one-fifth-desktop">
                        <RegularInput
                          v-model="property.value.$model"
                          placeholder="value"
                          @blur="property.value.$touch()"
                        />
                        <div v-if="property.value.$error" class="mb-2 align-center">
                          <div class="error-content">
                            <p v-if="!property.value.required" class="error">
                              Value required.
                            </p>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </div>
                </div>
              </div>
            </section>
          </form>
        </div>
      </template>
    </ModalFullscreen>
  </div>
</template>

<script>
import required from "vuelidate/lib/validators/required";
import CompositeNav from "@/components/ui/nav/MobileNav";
import RegularInput from "@/components/ui/Input/RegularInput";
import { mapActions, mapState } from "vuex";
import Dropdown from "@/components/ui/dropdown/Dropdown";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import TextArea from '@/components/ui/Input/TextArea.vue';
export default {
  name: "CreateContract",
  components: {
    Dropdown,
    CompositeNav,
    RegularInput,
    ModalFullscreen,
    TextArea
  },
  data() {
    return {
      createContract: {
        category: "",
        contractType: "",
        contractName: "",
        description: "",
        contractProperties: []
      },
      categories: [],
      // category: null,
      contractTypes: ["Food And beverage", "Resources and services"],
      contractProperties: [],
      isChanged: false,
      template: [],
      isEdit: false,
      selectedID: null
    };
  },
  computed: {
    ...mapState("template", ["templates", "contract_categories"]),
    ...mapState("template", ["templates", "contract_properties"]),
    ...mapState("template", ["templates", "contract_types"])
  },
  async mounted() {
    this.isEdit = !!this.$route.params.id;
    this.selectedID = this.$route.params.id ?? null;

    await this.getContractCategories();
    await this.getContractTypes();
    await this.getContractProperties();

    this.getContract();
  },
  methods: {
    ...mapActions("template", [
      "createTemplate",
      "updateTemplate",
      "getTemplateByID",
      "getContractCategories",
      "getContractTypes",
      "getContractProperties"
    ]),

    getCategories() {
      this.categories = this.contract_categories.map(category => {
        return category.name;
      });
    },
    getContractsTypes() {
      this.contractTypes = this.contract_types.map((types, index) => {
        return types.name;
      });
    },
    getContractsProperties() {
      this.contractProperties = this.contract_properties.map((types, index) => {
        return types.name;
      });
    },
    getCategory() {
      return this.contract_categories.filter(
        category => category.id === this.template.contract_category_id
      )[0]?.name;
    },
    getCategoryID() {
      return this.contract_categories.filter(
        category => category.name === this.createContract.category
      )[0].id;
    },
    getContractType() {
      return this.contract_types.filter(
        category => category.id === this.template.contract_type_id
      )[0]?.name;
    },
    getContractTypeID() {
      return this.contract_types.filter(
        category => category.name === this.createContract.contractType
      )[0].id;
    },
    getContractProperty() {
      this.template.contract_properties.forEach(item => {
        const value = this.contract_properties.filter(
          property => property.id === item.id
        )[0]?.name;
        this.createContract.contractProperties.push({
          propertyName: value,
          value: item.value
        });
      });
    },
    getContractPropertyValue() {
      const result = [];

      this.createContract.contractProperties.forEach(contractProperty => {
        const contractPropertyID = this.contract_properties.filter(
          contract => contract.name === contractProperty.propertyName
        )[0]?.id;
        result.push({
          description: "",
          id: contractPropertyID,
          name: contractProperty.propertyName,
          value: contractProperty.value
        });
      });
      return result;
    },
    async getContract() {
      if (this.isEdit && this.selectedID) {
        try {
          const result = await this.getTemplateByID(this.selectedID);
          this.template = result.data;

          this.createContract.category = this.getCategory();
          this.createContract.contractType = this.getContractType();
          this.createContract.contractName = this.template.contract_name;
          this.createContract.description = this.template.description;
          this.getContractProperty();
          this.getDropdownValues();
        } catch (error) {
          this.$router.push({ path: "/ui/buiding-owner/templates/" });
        }
      } else {
        this.getDropdownValues();
      }
    },
    save() {
      this.$v.$touch()
      if (!this.$v.$invalid) {
        const data = {
          id: this.selectedID,
          contract_category_id: this.getCategoryID(),
          contract_type_id: this.getContractTypeID(),
          contract_name: this.createContract.contractName,
          description: this.createContract.description,
          contract_properties: this.getContractPropertyValue()
        }
        this.isEdit
          ? this.updateTemplate(data)
          : this.createTemplate(data);
        this.$router.push({ path: "/ui/buiding-owner/templates/"});
      }
    },
    selectMenuGroup(e) {
      const index = this.createContract.menuGroup.indexOf(e);
      index > -1
        ? this.createContract.menuGroup.splice(index, 1)
        : this.createContract.menuGroup.push(e);
    },
    addProperties() {
      this.createContract.contractProperties.push({
        propertyName: "",
        value: ""
      });
    },
    changePropertySelection(index) {
      if (this.isChanged) {
        this.createContract.contractProperties[
          index
        ].value = this.contract_properties.filter(
          property =>
            property.name ===
            this.createContract.contractProperties[index].propertyName
        )[0]?.value;
      }
    },
    getDropdownValues() {
      this.getCategories();
      this.getContractsTypes();
      this.getContractsProperties();
    }
  },
  validations: {
    createContract: {
      category: {
        required
      },
      contractType: {
        required
      },
      contractName: {
        required
      },
      description: {
        required
      },
      contractProperties: {
        $each: {
          propertyName: {
            required
          },
          value: {
            required
          }
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.tag {
  cursor: pointer;
}

.align-center {
  // text-align: center;
}
.label {
  font-size: 16px !important;
  font-weight: 600;
}

.error-content {
  border-left: 4px solid red;
  padding: 10px;
  margin-top: 16px;
}

.error-header {
  color: red;
}
.card {
  background-color: #efefef;
  border-radius: 10px;
}

.error {
  display: contents;
  margin-left: 14px;
  margin-top: -1.6875rem;
  font-size: 0.75rem;
  color: red;
  margin-bottom: 0.9375rem;
  line-height: 1;
}

.description {
  height: 100px;
  width: 100%;
  background-color: white;
}
</style>
