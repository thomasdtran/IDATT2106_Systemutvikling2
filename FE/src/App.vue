<template>
  <nav-bar v-if ="!['Login', 'RegisterUser', 'RecoverPassword'].includes($route.name) && firstLoadDone"></nav-bar>
  <router-view v-if="firstLoadDone"></router-view>
</template>

<script>
/* eslint-disable no-debugger, no-console */ 
import NavBar from "@/components/NavBar";
import utils from "./common/utils"
import axios from "axios";

export default {
  components: {
    NavBar,
  },
  data() {
    return {
      firstLoadDone: false,
    }
  },
  async created() {
    console.log("now");
    let user = utils.getUser();
    if(!user) {
      await this.$router.push("/login");
      this.firstLoadDone = true;
      return;
    }
    if(!user.id || !user.jwt || !user.email) {
      await this.$router.push("/login");
      this.firstLoadDone = true;
      return;
    }
    await axios.get(utils.apiUrl + "/api/v1/users/" + user.id, utils.getConfigWithHeader())
        .catch(() => {
          this.$router.push("/login");
          this.firstLoadDone = true;
        });
    this.firstLoadDone = true;
  },
};
</script>
