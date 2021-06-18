<template>
  <div class="datepicker--modal">
    <div class="header">
      <div class="flex-row space-between">
        <NormalButton is-small is-light>
          <font-awesome-icon :icon="['fas', 'angle-left']" />
        </NormalButton>
        <span>2016</span>
        <NormalButton is-small is-light>
          <font-awesome-icon :icon="['fas', 'angle-right']" />
        </NormalButton>
      </div>
    </div>
    <div class="months flex-row">
      <div class="table-container">
        <table class="table is-bordered">
          <tbody>
          <tr>
            <th>January</th>
            <th>February</th>
            <th>March</th>
          </tr>
          <tr>
            <th>April</th>
            <th>May</th>
            <th>June</th>
          </tr>
          <tr>
            <th>July</th>
            <th>August</th>
            <th>September</th>
          </tr>
          <tr>
            <th>October</th>
            <th>November</th>
            <th>December</th>
          </tr>
          </tbody>
        </table>
      </div>
</div>
  </div>
</template>

<script>
import NormalButton from "@/components/ui/buttons/NormalButton";
export default {
  name: "MonthPicker",
  components: {
   NormalButton
  },
  props:{
    year:{
      type:Number,

    }
  },
  data() {
    return {

    }
  },
  mounted() {
    const d = new Date();
    const n = d.getFullYear();
  }
}
</script>

<style lang="scss" scoped>
.datepicker--modal {
  position: absolute;
  z-index: 4;
  border: 1px $color--line solid;
  background: white;
}

.header {
  color: $color--dark-shade;
  padding: 1rem 0.5rem;
}
tr {
  text-align: center;
}
th{
  color:$color--high-emphasis!important;
}
.content {
  padding: 0.5rem;
  background: white;
}
</style>
