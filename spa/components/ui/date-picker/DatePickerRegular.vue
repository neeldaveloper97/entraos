<template>
  <date-picker value-type="format"
               placeholder="dd/mm/yyyy"
               format="DD/MM/YYYY"
               :value="value"
               type="date"
               @input="$emit('input',$event)"
  />
</template>


<script>
import 'vue2-datepicker/index.css';
import DatePicker from 'vue2-datepicker';

export default {
  name: "DatePickerRegular",
  components: {
    DatePicker
  },
  props: {
    value: {
      type: String,
      default: ''
    },
    type: {
      String,
      default: 'date'
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
