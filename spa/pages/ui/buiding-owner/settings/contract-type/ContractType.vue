<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav
          link="/ui/settings"
          title="Misc/Contract type"
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
                  v-model="contractType.name"
                  placeholder="Contract Type Name"
                  @blur="$v.contractType.name.$touch()"
                >
                </RegularInput>
                <div
                  v-if="$v.contractType.name.$dirty && !contractType.name"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="$v.contractType.name.$dirty" class="error">
                      Category name required.
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
                    v-model="contractType.description"
                    class="description"
                    @blur="$v.contractType.description.$touch()"
                  ></textarea>
                </div>
                <div
                  v-if="
                    $v.contractType.description.$dirty &&
                      !contractType.description
                  "
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="$v.contractType.description.$dirty" class="error">
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
  name: "ContractType",
  components: {
    CompositionNav,
    ModalFullscreen,
    RegularInput,
    TextArea
  },
  data() {
    return {
      contractType: [
        {
          name: "",
          description: "",
          id: ""
        }
      ],
      isEdit: false,
      selectedID: null
    };
  },
  async mounted() {
    this.isEdit = !!this.$route.params.id;
    this.selectedID = this.$route.params.id ?? null;

    await this.getContractTypeDetails();
  },
  methods: {
    ...mapActions("contracts", [
      "addContractType",
      "updateContractType",
      "getContractTypeByID"
    ]),
    async getContractTypeDetails() {
      if (this.isEdit && this.selectedID) {
        try {
          const { data } = await this.getContractTypeByID(this.selectedID);
          this.contractType = data;
        } catch (error) {
          this.$router.push({ path: "/ui/settings" });
        }
      }
    },
    Save() {
      this.$v.$touch();
      if (!this.$v.$invalid) {
        const data = {
          id: this.contractType.id,
          name: this.contractType.name,
          description: this.contractType.description
        };
        this.isEdit
          ? this.updateContractType(data)
          : this.addContractType(data);
        this.$router.push({ path: "/ui/settings" });
      }
    }
  },
  validations: {
    contractType: {
      name: {
        required
      },
      description: {
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
.description {
  height: 100px;
  width: 100%;
  background-color: white;
  border: 1px solid #dbdbdb;
  border-radius: 4px;
}
</style>
