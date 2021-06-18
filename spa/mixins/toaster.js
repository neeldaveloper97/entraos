export default {

  data() {
      return {
        unsubscribe:null
      }
  },
    
  beforeDestroy() {
   
    this.unsubscribe();
  },

  mounted() {


    this.unsubscribe = this.$store.subscribe((mutation, state) => {
        if (mutation.type.includes("ERROR")) {
          let text = "";
          if (mutation.payload.response && mutation.payload.response.data) {
            text = mutation.payload.response.data.message;
          } else {
            text = mutation.payload;
          }
          this.$toasted.show(text);
        } else if (mutation.type.includes("SUCCESS")) {
          const text = mutation.payload;
          this.$toasted.show(text);
        }
      });
    
  },
};
  
