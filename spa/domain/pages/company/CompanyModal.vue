<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav
          :cross-button="true"
          :title="getTitle"
          right-text="Save"
          @clicked="$emit('closed')"
          @saveClicked="save()"
        />
      </template>

      <template v-slot:content>
        <div class="m-5">
          <form>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Company Name</label>
                <RegularInput
                  v-model.trim="createCompany.name"
                  placeholder="Company Name"
                  @blur="$v.createCompany.name.$touch()"
                >
                </RegularInput>
                <div
                  v-if="$v.createCompany.name.$dirty && !$v.createCompany.name.required"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p class="error">
Company name required.
</p>
                  </div>
                </div>
              </div>
            </section>
            <section class="form-section">
              <div class="field">
                <label class="label is-small">Organisation number</label>
                <div>
                  <RegularInput
                    v-model.trim="createCompany.org_number"
                    placeholder="Organisation number"
                  >
                  </RegularInput>
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
import required from "vuelidate/lib/validators/required";
import { mapActions } from "vuex";

export default {
  components: {
    CompositionNav,
    ModalFullscreen,
    RegularInput,
  },
  props: ["company"],
  data() {
    return {
      createCompany: {
        name: "",
        org_number: "",
      },
      isEdit: false,
    };
  },
  validations: {
    createCompany: {
      name: {
        required,
      },
    },
  },
  computed: {
    getTitle() {
      return this.isEdit ? "Edit company" : "Create company";
    },
  },
  mounted() {
    if (this.company) {
      this.createCompany = this.lodash.cloneDeep(this.company);
      this.isEdit = true;
    }
  },
  methods: {
    ...mapActions("company", ["create_company", "update_company"]),

    save() {
      this.$v.$touch();
      if (!this.$v.$invalid) {
        const data = {
          name: this.createCompany.name,
          org_number: this.createCompany.org_number,
        };
        this.isEdit
          ? this.update_company({ payload: data, id: this.company.id })
          : this.create_company({ payload: data });
        this.$emit("closed");
      }
    },
  },
};
</script>

<style lang="scss" scoped>
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
