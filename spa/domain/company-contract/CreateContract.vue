<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositeNav
          :cross-button="true"
          title="Create a contract"
          right-text="save"
          @clicked="$emit('closed')"
          @saveClicked="saveContractForm"
        ></CompositeNav>
      </template>
      <template v-slot:content>
        <div class="card m-4">
          <form>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Company name</label>
                <RegularInput
                  v-model="company"
                  disabled
                  placeholder="Company Name"
                  :value="company"
                >
                </RegularInput>
              </div>
              <div class="field">
                <label class="label is-small">Category</label>
                <RegularInput
                  v-model="category"
                  disabled
                  placeholder="Category"
                  :value="category"
                >
                </RegularInput>
              </div>
              <div class="field">
                <label class="label is-small">Contract Name</label>
                <RegularInput
                  v-model="contract_name"
                  disabled
                  placeholder="Contract"
                  :value="contract_name"
                >
                </RegularInput>
              </div>
              <div class="field">
                <label class="label is-small">Contract Type </label>
                <Dropdown
                  v-model="createContract.contractType"
                  :selection="contractTypes"
                  :selected-prop="createContract.contractType"
                />
                <div
                  v-if="!$v.createContract.contractType.required"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="!$v.createContract.contractType.required" class="error">
                      Contract type required.
                    </p>
                  </div>
                </div>
              </div>
              <div class="field">
                <label class="label is-small">Coverd Menu Group </label>
                <div>
                  <span
                    v-for="(munu, index) in contract_menus"
                    :key="index"
                    class="tag is-medium is-rounded m-2"
                    :class="
                      createContract.menuGroup.includes(munu.name)
                        ? 'is-success'
                        : 'is-light'
                    "
                    @click="selectMenuGroup(munu.name)"
                    >{{ munu.name }}</span>
                </div>
                <div
                  v-if="!$v.createContract.menuGroup.required"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="!$v.createContract.menuGroup.required" class="error">
                      Menu items required.
                    </p>
                  </div>
                </div>
              </div>
              <div class="field">
                <label class="label is-small">Valid From </label>
                <DatePickerRegular
                  v-model="createContract.validFrom"
                  placeholder="Valid from"
                />
                <div
                  v-if="!$v.createContract.validFrom.required"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="!$v.createContract.validFrom.required" class="error">
                      Value required.
                    </p>
                  </div>
                </div>
              </div>
              <div class="field">
                <label class="label is-small">Valid To </label>
                <DatePickerRegular
                  v-model="createContract.validTo"
                  placeholder="Valid to"
                />
                <div v-if="!$v.createContract.validTo.required" class="mb-2 align-center">
                  <div class="error-content">
                    <p v-if="!$v.createContract.validTo.required" class="error">
                      Value required.
                    </p>
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
import DatePickerRegular from "@/components/ui/date-picker/DatePickerRegular";
import RegularInput from "@/components/ui/Input/RegularInput";
import Dropdown from "@/components/ui/dropdown/Dropdown";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
export default {
  name: "CreateContract",
  components: {
    Dropdown,
    CompositeNav,
    RegularInput,
    DatePickerRegular,
    ModalFullscreen,
  },
  props: {
    company: String,
    category: String,
    contract_name: String,
  },
  data() {
    return {
      createContract: {
        companyName: this.company,
        category: this.category,
        contractType: null,
        contractName: this.contract_name,
        validFrom: null,
        validTo: null,
        menuGroup: [],
        contractProperties: [],
      },
      contract_menus: [{ name: "Lunch" }, { name: "Dinner" }, { name: "Breakfast" }],
      categories: [],
      // category: null,
      contractTypes: ["Food And beverage", "Resources and services"],
      // contractType: null
    };
  },
  computed: {},
  mounted() {},
  methods: {
    saveContractForm() {
      if (!this.$v.$invalid) {
        this.$emit("saved");
        this.$emit("closed");
      }
    },
    selectMenuGroup(e) {
      const index = this.createContract.menuGroup.indexOf(e);
      index > -1
        ? this.createContract.menuGroup.splice(index, 1)
        : this.createContract.menuGroup.push(e);
    },
  },
  validations: {
    createContract: {
      companyName: {
        required,
      },
      category: {
        required,
      },
      contractType: {
        required,
      },
      contractName: {
        required,
      },
      menuGroup: {
        required,
      },
      validFrom: {
        required,
      },
      validTo: {
        required,
      },
      contractProperties: {
        $each: {
          propertyName: {
            required,
          },
          value: {
            required,
          },
        },
      },
    },
  },
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
</style>
