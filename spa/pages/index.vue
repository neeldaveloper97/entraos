<template>
  <div class="container">
    <div>
      <Logo />
      <h1 class="title">
EntraOS Tenant Contract Self Administration System
</h1>
      <div v-for="link in authLinks" :key="link.name">
        <button :class="link.style" @click="link.click ? link.click() : null">
          {{ link.text }}
        </button>
      </div>

      <div>
        <br />
        <a href="/swagger/">Swagger UI - API/development instruction</a>
      </div>
      <br />
      <b><i>Instructions for testing</i></b>
      <ul>
        <li>
          For <b>building owner</b> workspace, log in with <i>Petter Community-Leader</i>
        </li>
        <li>For <b>tenant workspace</b>, log in with <i>Milla Prosjektleder</i></li>
      </ul>
    </div>
  </div>
</template>

<script>
import Logo from "@/components/Logo";
export default {
  auth: false,
  components: {
    Logo,
  },

  computed: {
    authLinks() {
      if (this.$auth.loggedIn) {
        return [
          {
            style: "title",
            text: this.$auth.user.first_name + " " + this.$auth.user.last_name,
            to: "/ui",
            click: () => {
              this.$router.push({ path: "/ui" });
            },
          },
          {
            style: "button is-info is-light is-rounded",
            text: "Logout",
            click: () => {
              this.$auth.logout();
            },
          },
        ];
      } else {
        return [
          {
            style: "button  is-success is-rounded",
            text: "Login/Register",
            click: () => {
              this.whydahLogin();
            },
          },
        ];
      }
    },
  },

  methods: {
    whydahLogin() {
      try {
        this.$auth.loginWith("social");
      } catch {}
    },
    deletecookie(name) {
      document.cookie = name + "=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    },
  },
};
</script>

<style scoped>
.container {
  margin: 0 auto;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.subtitle {
  font-weight: 300;
  font-size: 42px;
  color: #526488;
  word-spacing: 5px;
  padding-bottom: 15px;
}

.links {
  padding-top: 15px;
}
</style>
