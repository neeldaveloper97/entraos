<template>
  <div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositeNav
          :cross-button="true"
          :title="`${edit? 'Edit contract' : 'Create contract'}`"
          right-text="save"
          @clicked="$emit('closed')"
          @saveClicked="saveContract"
        ></CompositeNav>
      </template>
      <template v-slot:content>
        <div class="card m-4">
          <form>
            <section class="form-section">
              <!--Company name -->
              
              <div class="field">
                <label class="label is-small">Company name</label>
                <RegularInput
                  v-model="company.name"
                  disabled
                  placeholder="Company Name"
                  :value="company.name"
                >
                </RegularInput>
              </div> 
              <!-- Category -->
              
              <div class="field">
                <label class="label is-small">Category</label>
                <RegularInput
                  v-model="category_name"
                  disabled
                  placeholder="Category"
                  :value="category_name"
                >
                </RegularInput>
              </div>
              
              <!-- Contract name -->
              
              <div class="field">
                <label class="label is-small">Contract Name</label>
                <RegularInput
                  v-model="$v.contract_name.$model"
                  placeholder="Contract Name"
                  @blur="$v.contract_name.$touch()"
                >
                </RegularInput>
                <div
                  v-if="$v.contract_name.$dirty && !$v.contract_name.required"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p class="error">
Contract name required.
</p>
                  </div>
                </div>
              </div>
              
              <!--Contract type -->
            
              <div class="field">
                <label class="label is-small">Contract Type </label>
                <Dropdown
                  v-model="$v.contract_type_name.$model"
                  :selection="Object.values(contract_type_map_id_name)"
                  :selected-prop="contract_type_name"
                  @click="$v.contract_type_name.$touch()"
                />
                <div v-if="!$v.contract_type_name.required" class="mb-2 align-center">
                  <div class="error-content">
                    <p class="error">
Contract type required.
</p>
                  </div>
                </div>
              </div> 
            
              <!-- description -->
              <div class="field">
                <label class="label is-small">Contract Description</label>
                <div>
                  <TextArea v-model="description" class="description"></TextArea>
                </div>
              </div>
              <!-- valid from -->
             
              <div class="field">
                <label class="label is-small">Valid From </label>
                <DatePickerRegular
                  v-model="$v.valid_from.$model"
                  placeholder="Valid from"
                />
                <div v-if="!$v.valid_from.required" class="mb-2 align-center">
                  <div class="error-content">
                    <p class="error">
Value required.
</p>
                  </div>
                </div>
              </div>
             
              <!-- valid to -->
             
              <div class="field">
                <label class="label is-small">Valid To </label>
                <DatePickerRegular v-model="$v.valid_to.$model" placeholder="Valid to" />
                <div v-if="!$v.valid_to.required" class="mb-2 align-center">
                  <div class="error-content">
                    <p class="error">
Value required.
</p>
                  </div>
                </div>
              </div>
              
              <!-- properties -->
              <div v-if="contract_properties.length>0" class="field">
              <label class="label is-small">Contract properties </label>
              <div
                v-for="(property, index) in $v.contract_properties.$each.$iter"
                :key="index"
              >
<div class="columns mt-1">
                  <div class="column is-two-fifths p-1">
                    <RegularInput v-model="property.name.$model" class="title is-5" disabled />
                  </div>
                  <div class="column p-1">
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
                  </div>
                </div>
                <p class="subtitle is-6 has-text-grey">
                  {{ property.$model.description }}
                </p>
              </div>
</div>
              <!-- upload documents -->
              <!-- quantity -->
              
              <div class="field">
                <label class="label is-small">Quantity</label>
                <RegularInput
                  v-model="$v.quantity.$model"
                  input-type="number"
                  min="1"
                  placeholder="Quantity"
                  @blur="$v.quantity.$touch()"
                >
                </RegularInput>
                <div
                  v-if="$v.quantity.$dirty && $v.quantity.$error"
                  class="mb-2 align-center"
                >
                  <div class="error-content">
                    <p class="error">
Quantity required in a numeric format.
</p>
                  </div>
                </div>
              </div>
             
              <!-- status -->
             
              <div v-if="hasSuperAdminRole || edit === true" class="field">
                <label class="label is-small">Status</label>
                <Dropdown
                  v-model="$v.status.$model"
                  :selection="status_enum"
                  :selected-prop="status"
                  :disabled="hasSuperAdminRole===false"
                  @click="$v.status.$touch()"
                />
                <div v-if="$v.status.$error" class="mb-2 align-center">
                  <div class="error-content">
                    <p class="error">
Status required.
</p>
                  </div>
                </div>
              </div>
              
              
              <div v-if="edit" class="field">
                <label class="label is-small">Last updated: {{ $moment(last_updated).format("DD/MM/YYYY") }}</label>
              </div>
              <div v-if="edit" class="field">
                <label class="label is-small">Created at: {{ $moment(created_at).format("DD/MM/YYYY") }}</label>
              </div>
</section>
</form>
</div>
        </template>
</modalfullscreen>
</div>
      </template>
    </ModalFullscreen>
  </div>
</template>

<script>
import {required, numeric, minLength} from "vuelidate/lib/validators";
import CompositeNav from "@/components/ui/nav/MobileNav";
import DatePickerRegular from "@/components/ui/date-picker/DatePickerRegular";
import RegularInput from "@/components/ui/Input/RegularInput";
import Dropdown from "@/components/ui/dropdown/Dropdown";
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import { mapActions, mapState } from "vuex";
import TextArea from '@/components/ui/Input/TextArea.vue';
import roleMixin from '@/mixins/roleMixin';

export default {
  components: {
    Dropdown,
    CompositeNav,
    RegularInput,
    DatePickerRegular,
    ModalFullscreen,
    TextArea
  },
  mixins: [roleMixin],
  props: ["contract", "company", "edit", "contract_types", "categories"],
  data() {
    return {
      category_map_id_name: [],
      category_map_name_id: [],
      contract_type_map_id_name: [],
      contract_type_map_name_id: [],
      status_enum: ["Activated", "Pending_For_Confirmation", "Inactivated"],

      //contract fields
      contract_name: '',
      contract_type_name: null,
      category_name: null,
      description: null,
      contract_properties: [],
      valid_from: null,
      valid_to: null,
      last_updated: null,
      created_at: null,
      quantity: 1,
      status: "Pending_For_Confirmation",
      docs: [],
    };
  },
  mounted() {
    
    //make the mapping id-name
    this.category_map_id_name = this.categories.reduce((result, item) => {
      result[`${item.id}`] = item.name;
      return result;
    }, {});
    this.category_map_name_id = this.categories.reduce((result, item) => {
      result[`${item.name}`] = item.id;
      return result;
    }, {});

    
    //make the mapping id-name
    this.contract_type_map_id_name = this.contract_types.reduce((result, item) => {
      result[`${item.id}`] = item.name;
      return result;
    }, {});
    this.contract_type_map_name_id = this.contract_types.reduce((result, item) => {
      result[`${item.name}`] = item.id;
      return result;
    }, {});

    this.copyContractDataToState();

  },
  methods: {
    ...mapActions("contract", ["update_contract", "create_contract"]),

    copyContractDataToState() {

      if (!this.contract.valid_from) {
        //default is today
        this.valid_from = this.$moment().format("DD/MM/YYYY");
      } else {
        this.valid_from = this.$moment(this.contract.valid_from).format("DD/MM/YYYY");
      }
      if (this.contract.valid_to) {
        this.valid_to = this.$moment(this.contract.valid_to).format("DD/MM/YYYY");
      }
      this.contract_name = this.contract.contract_name;
      this.contract_type_name = this.contract_type_map_id_name[
        this.contract.contract_type_id
      ];
      this.category_name = this.category_map_id_name[this.contract.contract_category_id];
      this.description = this.contract.description;
      this.contract_properties = this.lodash.cloneDeep(this.contract.contract_properties);
      this.last_updated = this.contract.last_updated;
      this.created_at = this.contract.created_at;
      if (this.contract.quantity) {
        this.quantity = this.contract.quantity;
      }
      if (this.contract.status) {
        this.status = this.contract.status;
      } else {
        this.status = "Activated";
      }
     
      this.docs = this.contract.docs;
    },

    saveContract() {
      
      if (!this.$v.$invalid) {
        
        let text = "";
        if (this.hasSuperAdminRole) {
          text = "Are you sure to " + (this.edit ? "update" : "create") + " this contract";
        } else {
          text = "This contract " + this.contract_name + " must be confirmed by the building owner admin before it can be used. Are you sure to commit?"
        }

          //must confirm 
        if (confirm(text)) {
          const payload = {
          id: this.edit ? this.contract.id : null,
          company_id: this.company.id,
          contract_name: this.contract_name,
          contract_category_id: this.category_map_name_id[this.category_name],
          contract_type_id: this.contract_type_map_name_id[this.contract_type_name],
          description: this.description,
          contract_properties: this.contract_properties,
          quantity: this.quantity,
          status: this.hasSuperAdminRole ? this.status : 'Pending_For_Confirmation',
          docs: this.docs,
          valid_from: this.$moment(this.valid_from, "DD/MM/YYYY").toISOString() ,
          valid_to: this.$moment(this.valid_to, "DD/MM/YYYY").toISOString() ,
     
        };
        this.edit
          ? this.update_contract({ payload, id: this.contract.id, callbackfunc:() => this.$emit("closed") })
          : this.create_contract({ payload, callbackfunc:() => this.$emit("closed") });

        }
        
        
      }
    },
  },
  validations: {
    contract_name: {
      required,
    },
    category_name: {
      required,
    },
    contract_type_name: {
      required,
    },
    quantity: {
      required,
      numeric,
      minLength: minLength(1)
    },
    valid_from: {
      required,
    },
    valid_to: {
      required,
    },
    status: {
      required
    },
    contract_properties: {
      $each: {
        name: {
          required,
        },
        value: {
          required,
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
