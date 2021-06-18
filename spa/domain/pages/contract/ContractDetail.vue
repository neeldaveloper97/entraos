<template>
<div>
    <ModalFullscreen>
      <template v-slot:nav>
        <CompositionNav :link="contractLink" title="Contract details" />
      </template>
      <template v-slot:content>
         <pre>{{ JSON.stringify(contract, null, 2) }}</pre> 
     </template>
    </ModalFullscreen>
  </div>
</template>


<script>
import ModalFullscreen from "@/components/ui/containers/MobileContainer";
import ModalNavRegular from "@/components/ui/nav/ModalNav";
import CompositionNav from "@/components/ui/nav/MobileNav";

export default {
  components: {
   ModalFullscreen,
   ModalNavRegular,
   CompositionNav
  },
  props: ["contract"],
   computed: {
     contractLink() {
        return `/ui/tenant/${this.$route.params.companyid}/contracts`
    }
  },
};
</script>

<style scoped></style>
