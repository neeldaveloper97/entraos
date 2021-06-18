export default {
  props: {
    color: {
      type: String,
      default: 'main-brand'
    },
  },
  computed: {
    bgColor() {
      switch (this.color) {
        case 'main-brand':
          return 'bg-color--main-brand';
        case "white":
          return 'bg-color--white';
        case "dark-shades":
          return 'bg-color--dark-shades'
        case "light-accent":
          return 'bg-color--light-accent';
        case 'notification':
          return 'bg-color--notification';
        default:
          return 'bg-color--main-brand';
      }
    },
    fgColor() {
      switch (this.color) {
        case 'main-brand':
          return 'color--main-brand';
        case "white":
          return 'color--white';
        case "dark-shades":
          return 'color--dark-shades';
        case "light-accent":
          return 'color--light-accent';
        case 'notification':
          return 'color--notification';
        default:
          return 'color--main-brand';
      }
    },
    borderColor(){

        switch (this.color) {
          case 'main-brand':
            return 'border-color--main-brand';
          case "white":
            return 'border-color--white';
          case "dark-shades":
            return 'border-color--dark-shades';
          case "light-accent":
            return 'border-color--light-accent';
          case "notification":
            return 'border-color--notification';
          case "medium-emphasis":
            return 'border-color--medium-emphasis';
          default:
            return 'border-color--main-brand';
        }

    }
  }
};
