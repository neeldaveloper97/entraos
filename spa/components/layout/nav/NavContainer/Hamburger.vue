<template>
  <div class="hamburger--container">
    <div class="hamburger--content">
      <div class="profile flex-column">
        <div class="picture">
          <q-image is64x64 is-rounded :src="require('@/assets/img/avatar.png')"></q-image>
        </div>
        <p class="font--title-big">
          {{ $auth.user.first_name }} {{ $auth.user.last_name }}
        </p>
      </div>

      <ul class="fa-ul">
        <li>
          <n-link to="/" class="color--white">
            <span class="fa-li"><font-awesome-icon :icon="['fas', 'home']" /></span>Home
          </n-link>
        </li>
        <li>
          <a
            class="color--white"
            @click="
              () => {
                $auth.logout();
              }
            "
          >
            <span class="fa-li"><font-awesome-icon
                class="color--white"
                :icon="['fas', 'sign-out-alt']"
/></span>Logout
          </a>
        </li>
      </ul>
    </div>
    <div class="circle--big" />
    <div class="circle--smaller" />
    <button class="button-close" @click.prevent="clicked">
      <font-awesome-icon class="color--white" :icon="['fas', 'times']" />
    </button>
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import QImage from "@/components/ui/image/QImage";
export default {
  name: "Hamburger",
  components: {
    QImage,
  },
  computed: {},
  methods: {
    clicked() {
      this.$emit("closed");
    },
  },
};
</script>

<style lang="scss" scoped>
.circle--smaller,
.circle--big {
  border-bottom-right-radius: 100%;
  height: 50vh;
  //top: 40%;
  position: absolute;
}
.circle--big {
  width: 105%;
  background: $color--main-brand;
  z-index: 400;
}
.circle--smaller {
  width: 130%;
  background: #679bff;
  z-index: 300;
}
.hamburger--container {
  width: 100%;
  height: 100%;
  background: white;
  position: fixed;
  z-index: 200;
  left: 0;
  top: 0;
}

.hamburger--content {
  background: $color--main-brand;
  padding: 1.5rem 1rem;
}
.picture {
  margin: auto;
}
.profile {
  text-align: center;
  margin-bottom: 1rem;
  p {
    color: white;
  }
}

ul li {
  color: white;
  font-size: 1rem;
}
ul li:not(:last-child) {
  margin-bottom: 1rem;
}

.button-close {
  height: 3rem;
  width: 3rem;
  background: $color--main-brand;
  bottom: 0;
  position: fixed;
  border-radius: 100%;
  left: 0;
  right: 0;
  margin-left: auto;
  margin-right: auto;
  border: none;
  margin-bottom: 1.5rem;
  text-align: center;
  -webkit-box-shadow: 0px 10px 4px 0px rgba(66, 128, 248, 0.25);
  -moz-box-shadow: 0px 10px 4px 0px rgba(66, 128, 248, 0.25);
  box-shadow: 0px 10px 4px 0px rgba(66, 128, 248, 0.25);
}
.button-close svg {
  line-height: 3rem;
  vertical-align: middle;
  height: 3rem;
}
</style>
