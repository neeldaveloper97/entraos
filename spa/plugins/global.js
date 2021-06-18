export default (context, inject) => {
  const global = context => ({
    //HUY just made this for experimenting

    //returns a promise: just for experimenting
    async fetchMyOverlord() {
      await context.store.dispatch("overlord2/on_get_myoverlord");
      return context.store.state.overlord2.storage.my_overlord;
    },
    //returns a promise: just for experimenting
    async fetchUserInfo() {
      await context.store.dispatch("platform2/on_get_userinfo");
      return context.store.state.platform2.storage.userinfo;
    },

    uuid() {
      let uuidValue = "";
      let k;
      let randomValue;
      for (k = 0; k < 32; k++) {
        randomValue = (Math.random() * 16) | 0;

        if (k === 8 || k === 12 || k === 16 || k === 20) {
          uuidValue += "-";
        }
        uuidValue += (k === 12
          ? 4
          : k === 16
          ? (randomValue & 3) | 8
          : randomValue
        ).toString(16);
      }
      return uuidValue;
    }
  });

  //@Appratiff, do you think it is an appropriate place to put all the constant values?
  inject("global", global(context));
};
