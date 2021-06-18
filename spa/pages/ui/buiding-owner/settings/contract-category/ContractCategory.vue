<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav
          link="/ui/settings"
          title="Misc/Category"
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
                  v-model="createCategory.name"
                  placeholder="Category Name"
                  @blur="$v.createCategory.name.$touch()"
                >
                </RegularInput>
                <div
                  v-if="$v.createCategory.name.$dirty && !createCategory.name"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p v-if="$v.createCategory.name.$dirty" class="error">
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
                    v-model="createCategory.description"
                    class="description"
                    @blur="$v.createCategory.description.$touch()"
                  ></textarea>
                </div>
                <div
                  v-if="
                    $v.createCategory.description.$dirty &&
                      !createCategory.description
                  "
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p
                      v-if="$v.createCategory.description.$dirty"
                      class="error"
                    >
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
  name: "ContractCategory",
  components: {
    CompositionNav,
    ModalFullscreen,
    RegularInput,
    TextArea
  },
  data() {
    return {
      createCategory: [
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

    await this.getContractCategoryDetails();
  },
  methods: {
    ...mapActions("contracts", [
      "addContractCategory",
      "updateContractCategory",
      "getContractCategoryByID"
    ]),
    async getContractCategoryDetails() {
      if (this.isEdit && this.selectedID) {
        try {
          const { data } = await this.getContractCategoryByID(this.selectedID);
          this.createCategory = data;
        } catch (error) {
          this.$router.push({ path: "/ui/settings" });
        }
      }
    },
    Save() {
      this.$v.$touch();
      if (!this.$v.$invalid) {
        const data = {
          id: this.createCategory.id,
          name: this.createCategory.name,
          description: this.createCategory.description
        };
        this.isEdit
          ? this.updateContractCategory(data)
          : this.addContractCategory(data);
        this.$router.push({ path: "/ui/settings" });
      }
    }
  },
  validations: {
    createCategory: {
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
