<template>
  <div class="flex-column dashboard-container">
    <div class="gradient-background" />
    <section class="welcome-section">
      <div class="profile flex-row">
        <div class="picture">
          <QImage is-rounded is48x48 src="" />
        </div>
        <i class="font--title-regular color--white">Welcome Master Rebel <br />{{ $auth.user.first_name }}
          {{ $auth.user.last_name }}!</i>
      </div>
    </section>
    <div v-if="userrole && userrole.roles.includes('SUPERADMIN')">
      <BuidingOwnerWorkspace />
    </div>
    <div v-if="userrole && userrole.roles.includes('COMPANYADMIN')">
      <TenantWorkspace :userrole="userrole" />
    </div>
  </div>
</template>

<script>
import DashboardCard from "@/domain/pages/dashboard/DashboardCard";
import { mapActions, mapGetters, mapState } from "vuex";
import QImage from "@/components/ui/image/QImage.vue";
import BuidingOwnerWorkspace from "./dashboard/BuidingOwnerWorkspace.vue";
import TenantWorkspace from "./dashboard/TenantWorkspace.vue";
export default {
  name: "Dashboard",
  components: {
    DashboardCard,
    QImage,
    BuidingOwnerWorkspace,
    TenantWorkspace,
  },
  props: [],
  methods: {
    ...mapActions("user_role", ["get_userrole"]),
  },
  data() {
    return {};
  },
  computed: {
    ...mapState({
      userrole: (state) => state.user_role.storage.userrole,
    }),
  },
  watch: {
    userrole() {
      localStorage.setItem(
        "userrole",
        JSON.stringify(this.lodash.cloneDeep(this.userrole))
      );
    },
  },
  async mounted() {
    await this.get_userrole();
  },
};
</script>

<style lang="scss" scoped>
.profile {
  align-items: center;
}
.welcome-section {
  padding: 1.5rem 1rem;
}
.picture {
  padding-right: 0.5rem;
}
.dashboard-container {
  background: $color--light-shades;
  height: 100%;
  z-index: 1;
  position: relative;
}
.row {
  margin: 0 1rem 1rem;
}
.flex-row {
  flex-wrap: wrap;
  width: 100%;
}
.gradient-background {
  display: block;
  content: "";
  width: 100vw;
  height: 205px;
  background: $color--main-brand;
  position: absolute;
  top: 0px;
  z-index: -1;
  left: 0px;
}
</style>
