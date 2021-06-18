<template>
  <v-app>
    <Dashboard />
  </v-app>
</template>

<script>
import Dashboard from "@/domain/pages/Dashboard";
import SnackBar from "@/components/SnackBar";
import { mapState, mapActions } from "vuex";

export default {
  name: "Index",
  components: {
    Dashboard,
  },
  layout: "Nav",
  data() {
    return {
      text: "",
      unsubscribe: null,
    };
  },
  computed: {},

  methods: {},
};
</script>

<style scoped></style>
