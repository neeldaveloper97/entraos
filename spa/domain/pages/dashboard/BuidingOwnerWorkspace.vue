<template>
  <div>
    <div class="row">
      <div class="flex-row">
        <DashboardCard
          link="/ui/buiding-owner/templates/"
          text="CONTRACT TEMPLATE"
          color="color--main-brand"
          sub-text="Number of templates"
          :sub-value="0"
        >
          <template v-slot:icon>
            <font-awesome-icon class="color--main-brand" :icon="['fas', 'user']" />
          </template>
        </DashboardCard>
        <DashboardCard
          link="/ui/buiding-owner/companies"
          text="COMPANY CONTRACTS"
          color="color--main-brand"
          sub-text="Number of companies"
          :sub-value="0"
        >
          <template v-slot:icon>
            <font-awesome-icon class="color--main-brand" :icon="['fas', 'chart-bar']" />
          </template>
        </DashboardCard>
      </div>
    </div>

    <div class="row">
      <div class="flex-row">
        <DashboardCard
          link="/ui/settings"
          color="color--light-accent"
          text="MISC"
          sub-text="Contract Categories, Contract Types, Contract Property Settings"
        >
          <template v-slot:icon>
            <font-awesome-icon class="color--light-accent" :icon="['fas', 'user']" />
          </template>
        </DashboardCard>
      </div>
    </div>
  </div>
</template>

<script>
import DashboardCard from "@/domain/pages/dashboard/DashboardCard";

export default {
  components: {
    DashboardCard,
  },
};
</script>

<style scoped>
.row {
  margin: 0 1rem 1rem;
}
.flex-row {
  flex-wrap: wrap;
  width: 100%;
}
</style>
