<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav
          link="/ui/settings"
          title="Misc/Contract Property"
          right-text="save"
          @saveClicked="Save"
        />
      </template>
      <template v-slot:content>
        <div class="m-5">
          <form>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Name</label>
                <RegularInput
                  v-model="contractProperty.name"
                  placeholder="Contract Property Name"
                  @blur="$v.contractProperty.name.$touch()"
                >
                </RegularInput>
                <div
                  v-if="$v.contractProperty.name.$dirty && !contractProperty.name"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="$v.contractProperty.name.$dirty" class="error">
                      Category name required.
                    </p>
                  </div>
                </div>
              </div>
            </section>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Default Value</label>
                <div>
                  <textarea
                    v-model="contractProperty.value"
                    class="text-area"
                    @blur="$v.contractProperty.value.$touch()"
                  ></textarea>
                </div>
                <div
                  v-if="
                    $v.contractProperty.value.$dirty &&
                      !contractProperty.value
                  "
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="$v.contractProperty.value.$dirty" class="error">
                      Default Value required.
                    </p>
                  </div>
                </div>
              </div>
            </section>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Description</label>
                <div>
                  <textarea
                    v-model="contractProperty.description"
                    class="text-area"
                    @blur="$v.contractProperty.description.$touch()"
                  ></textarea>
                </div>
                <div
                  v-if="
                    $v.contractProperty.description.$dirty &&
                      !contractProperty.description
                  "
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="$v.contractProperty.description.$dirty" class="error">
                      Description required.
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
import CompositionNav from "@/components/ui/nav/MobileNav";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import RegularInput from "@/components/ui/Input/RegularInput";
import TextArea from "@/components/ui/Input/TextArea.vue";
import required from "vuelidate/lib/validators/required";
import { mapActions } from "vuex";

export default {
  name: "ContractProperty",
  components: {
    CompositionNav,
    ModalFullscreen,
    RegularInput,
    TextArea
  },
  data() {
    return {
      contractProperty: [
        {
          name: "",
          description: "",
          id: "",
          value: ""
        }
      ],
      isEdit: false,
      selectedID: null
    };
  },
  async mounted() {
    this.isEdit = !!this.$route.params.id;
    this.selectedID = this.$route.params.id ?? null;

    await this.getContractPropertyDetails();
  },
  methods: {
    ...mapActions("contracts", [
      "addContractProperty",
      "updateContractProperty",
      "getContractPropertyByID"
    ]),
    async getContractPropertyDetails() {
      if (this.isEdit && this.selectedID) {
        try {
          const { data } = await this.getContractPropertyByID(this.selectedID);
          this.contractProperty = data;
        } catch (error) {
          this.$router.push({ path: "/ui/settings" });
        }
      }
    },
    Save() {
      this.$v.$touch();
      if (!this.$v.$invalid) {
        const data = {
          id: this.contractProperty.id,
          name: this.contractProperty.name,
          description: this.contractProperty.description,
          value: this.contractProperty.value
        };
        this.isEdit
          ? this.updateContractProperty(data)
          : this.addContractProperty(data);
        this.$router.push({ path: "/ui/settings" });
      }
    }
  },
  validations: {
    contractProperty: {
      name: {
        required
      },
      description: {
        required
      },
      value: {
        required
      }
    }
  }
};
</script>

<style scoped>
.label {
  font-size: 16px !important;
  font-weight: 600;
}
.error-content {
  border-left: 4px solid red;
  padding: 10px;
  margin-top: 16px;
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
.text-area {
  height: 100px;
  width: 100%;
  background-color: white;
  border: 1px solid #dbdbdb;
  border-radius: 4px;
}
</style>
