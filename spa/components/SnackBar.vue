<template>
  <!--
  <v-snackbar
    v-model="snackbar"
    :bottom="y === 'bottom'"
    :color="color"
    :left="x === 'left'"
    :multi-line="mode === 'multi-line'"
    :right="x === 'right'"
    :timeout="timeout"
    :top="y === 'top'"
    :vertical="mode === 'vertical'"
  >
    {{ text }}

    <template v-slot:action="{ attrs }">
      <v-btn dark text v-bind="attrs" @click="show = false">
Close
</v-btn>
    </template>
  </v-snackbar>
  -->

  <v-snackbar v-model="snackbar" :multi-line="multiLine">
    {{ text }}

    <template v-slot:action="{ attrs }">
      <v-btn :color="color" text v-bind="attrs" @click="snackbar = false">
Close
</v-btn>
    </template>
  </v-snackbar>
</template>
<script>
export default {
  data() {
    return {
      multiLine: true,
      color: "red",
      //mode: "",
      //timeout: 5000,
      //x: null,
      //y: "top",
      text: "XXXXXXX",
      snackbar: true,
    };
  },
  created() {
    /*this.$store.subscribe((mutation, state) => {
      if (mutation.type.includes("ERROR")) {
        console.log(mutation.payload);
        if (mutation.payload.response && mutation.payload.response.data) {
          this.text = mutation.payload.response.data.message;
        } else {
          this.text = mutation.payload;
        }
        this.snackbar = true;
        this.color = "error";
      } else if (mutation.type.includes("SUCCESS")) {
        this.text = mutation.payload;
        this.snackbar = true;
        this.color = "success";
      }
    });*/
  },
};
</script>
