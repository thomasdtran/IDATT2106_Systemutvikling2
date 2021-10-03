<template>
  <div id="navBar">
    <div id="logo">
      <img class="logoImg" @click="$router.push('/')" src="../assets/Icons/NavbarLogo.png" />
    </div>
    <div
      class="button"
      @mouseover="activityHover = true"
      @mouseleave="activityHover = false"
    >
      <img src="../assets/Icons/ActivityIcon.png" />
      <div class="text" @click="routeToPage('/')">
        <p>Aktiviteter</p>
        <div class="arrowDown"><img src="../assets/Icons/arrowDown.png" alt="Arrow down" id="arrowDownImg"></div>
      </div>
      <div class="dropdown" v-if="activityHover">
        <div class="dropdownButton" @click="routeToPage('/')">
          Alle aktiviteter
        </div>
        <div class="dropdownButton" @click="routeToPage('/signed-on')">Påmeldte aktiviteter</div>
        <div class="dropdownButton" @click="routeToPage('/my-activities')">Mine aktiviteter</div>
      </div>
    </div>
    <div class="button"  @click="routeCalendar">
      <img src="../assets/Icons/CalendarIcon.png" />
      <div class="text" v-bind:class="{active: isCalendar}">
        <p>Kalender</p>
      </div>
    </div>
    <div class="button" @click="routeToPage('/ranking')">
      <img src="../assets/Icons/RankingIcon.png"/>
      <div class="text" v-bind:class="{active: isRanking}">
        <p>Topp 10</p>
      </div>
    </div>
    <div
      class="button"
      @mouseover="profileHover = true"
      @mouseleave="profileHover = false"
    >
      <img src="../assets/Icons/ProfileIcon.png" />
      <div class="text" @click="routeToPage('/profile')">
        <p v-if="!loggedIn">Logg inn</p>
        <p v-else>{{name}}</p>
        <div class="arrowDown"><img src="../assets/Icons/arrowDown.png" alt="Arrow down" id="arrowDownImg"></div>
      </div>
      <div class="dropdown" v-if="profileHover && loggedIn">
        <div class="dropdownButton" @click="routeToPage('/profile')">
          Min profil
        </div>
        <div class="dropdownButton" @click="routeToPage('/sport-statistics')">
        Spill statistikk</div>
        <div v-on:click="logOut" v-if="loggedIn"  class="dropdownButton">Logg ut</div>
        <div v-else v-on:click="logOut"  class="dropdownButton">Logg inn</div>
      </div>
    </div>
    <Bell/>
    <div id="menuButton">
      <div id="menu">
        <img id="menuImg" v-on:click="clickMenu" src="../assets/Icons/menu.png" />
        <div class="menuDropdown" v-if="menuClicked">
          <div
            class="dropdownButton"
            v-on:click="clickActivity"
            v-if="!activityClicked && !profileClicked"
          >
            <img src="../assets/Icons/ActivityIcon.png" />
            <p class="text">Aktiviteter</p>
          </div>
          <div
            class="dropdownButton"
            @click="routeCalendar"
            v-if="!activityClicked && !profileClicked"
          >
            <img src="../assets/Icons/CalendarIcon.png" />
            <p class="text">Kalender</p>
          </div>
          <div
            class="dropdownButton"
            v-on:click="routeToPage('/ranking')"
            v-if="!activityClicked && !profileClicked"
          >
            <img src="../assets/Icons/RankingIcon.png" />
            <p class="text">Topp 10</p>
          </div>
          <div
            class="dropdownButton"
            v-on:click="clickProfile"
            v-if="!activityClicked && !profileClicked && loggedIn"
          >
            <img src="../assets/Icons/ProfileIcon.png" />
            <p class="text">{{name}}</p>
          </div>
          <div
                  class="dropdownButton"
                  v-on:click="routeToPage('/login')"
                  v-if="!activityClicked && !profileClicked && !loggedIn"
          >
            <img src="../assets/Icons/ProfileIcon.png" />
            <p class="text">Logg inn</p>
          </div>
          <div class="menuDropdown" v-if="activityClicked">
            <img
              class="backImg"
              src="../assets/Icons/BackButton.png"
              v-on:click="clickActivity"
            />
            <div @click="routeToPage('/')" class="dropdownButton">
              <p class="dropdownText">Alle aktiviteter</p>
            </div>
            <div @click="routeToPage('/signed-on')" class="dropdownButton">
              <p class="dropdownText">Påmeldte aktiviteter</p>
            </div>
            <div class="dropdownButton" @click="routeToPage('/my-activities')">
              <p class="dropdownText">Mine aktiviteter</p>
            </div>
          </div>
          <div class="menuDropdown" v-if="profileClicked && loggedIn">
            <img
              class="backImg"
              src="../assets/Icons/BackButton.png"
              v-on:click="clickProfile"
            />
            <div @click="routeToPage('/profile')" class="dropdownButton">
              <p class="dropdownText">Min profil</p>
            </div>
            <div @click="routeToPage('/sport-statistics')" class="dropdownButton">
              <p class="dropdownText">Spill statistikk</p>
            </div>
            <div v-on:click="logOut" class="dropdownButton">
              <p class="dropdownText" v-if="loggedIn">Logg ut</p>
              <p class="dropdownText" v-else>Logg inn</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import utils from "../common/utils";
  import axios from "axios";
  import Bell from "./NotificationBell.vue"
export default {
  components:{
    Bell,
  },
  name: "NavBar",
  data() {
    return {
      activityHover: false,
      profileHover: false,
      menuClicked: false,
      activityClicked: false,
      profileClicked: false,
      windowWidth: this.windowWidth,
      loggedIn: true,
      name: "",
    };
  },
  created() {
    this.findName();
    window.addEventListener("resize", this.onRezise);
    this.onRezise();
  },
  // eslint-disable-next-line vue/no-deprecated-destroyed-lifecycle
  destroyed() {
    window.removeEventListener("resize", this.onRezise);
  },
    watch: {
        $route() {
            this.$nextTick(this.routeLoaded);
        }
    },
  methods: {
    clickMenu() {
      if (this.menuClicked) {
        this.menuClicked = false;
        this.activityClicked = false;
        this.profileClicked = false;
      } else {
        this.menuClicked = true;
      }
    },
    clickActivity() {
      if (this.activityClicked) {
        this.activityClicked = false;
      } else {
        this.activityClicked = true;
      }
    },
    clickProfile() {
      if (this.profileClicked) {
        this.profileClicked = false;
      } else {
        this.profileClicked = true;
      }
    },
    onRezise() {
      this.windowWidth = window.innerWidth;
      if (this.windowWidth > 1000) {
        this.menuClicked = false;
        this.activityClicked = false;
        this.profileClicked = false;
      }
    },
    routeToPage(path){
      this.$router.push({ path: path})
      this.clickMenu();
    },
    routeCalendar(){
      this.$router.push({ path: '/calendar'})
      this.clickMenu();
    },
    logOut() {
      utils.logOut();
      this.clickMenu();
      this.profileHover = false;
      this.$router.push({ path: '/login' });
    },
      routeLoaded() {
        this.findName();
      },
    findName() {
      let dburl = utils.apiUrl + "/api/v1/users/" + utils.getUser().id;
      axios.get(dburl, utils.getConfigWithHeader()).then(res => {
        this.name = res.data.firstname;
      });
    },
  },
  computed:{
    isCalendar: function(){
      return this.$route.name === "Calendar";
    },
    isRanking: function(){
      return this.$route.name === "RankingPage";
    },
  }
};
</script>

<style scoped>
* {
  -webkit-tap-highlight-color: transparent;
}
img {
  user-select: none;
}
div {
  outline: none;
}
p{
  color: #333333;
}
#navBar {
  padding: 0.8rem;
  z-index: 3;
  height: 54px;
  background-color:#3AAFA9;
  width: 100%;
  left: 0;
  top: 0;
  display: flex;
  flex-wrap: wrap;
  position: fixed;
}
#navBar div {
  float: left;
}
#logo {
  flex: 8%;
}
.logoImg {
  position: relative;
  float: left;
  top: 3px;
  left: 20px;
  width: 100px;
}
.button {
  flex: 1%;
  max-height: 62px !important;
  font-size: 18.5px;
  border-radius: 10px;
}
.button img {
  position: relative;
  float: left;
  top: 1.1rem;
  left: 2.2rem;
  width: 1.35rem;
  height: 1.35rem;
}
.button:hover {
  background-color: #2b7a78;
  cursor: pointer;
}
.text {
  position: relative;
  float: left;
  left: 25%;
  user-select: none;
  height: 80%;
}
.dropdown {
  position: relative;
  float: left;
  background-color: #17252a;
  min-width: 140px;
  z-index: 1;
  border-radius: 10px;
  margin-top: 0.815rem;
}
.dropdownButton {
  width: 100%;
  font-size: 18px;
  min-height: 30px;
  text-align: center;
  color:white;
  padding-top: 10px;
  padding-bottom: 10px;
}
.dropdownButton:hover {
  opacity: 0.7;
  cursor: pointer;
}
.logoImg:hover {
  cursor: pointer;
}

.arrowDown{
  position: absolute;
  left: 4rem;
  top: 0.2rem;
}

#arrowDownImg{
  width: 0.8rem;
  height: 0.8rem;
}

#menuButton {
  flex: 4%;
}
#menuImg {
  display: none;
  width: 35px;
  height: 35px;
}

.active{
  border-bottom: 3px solid #333333;
  font-weight: bold;
}

@media only screen and (max-width: 1000px) {
  #navBar {
    height: 65px;
    background-color: #3aafa9;
    width: 100%;
    left: 0;
    top: 0;
    display: flex;
    flex-wrap: wrap;
    font-size: 15px;
    position: fixed;
  }
  .logoImg {
    left: 20px;
    width: 100px;
    top: 10px;
  }
  .button {
    display: none;
  }
  #menuButton {
    display: contents;
    width: 100%;
  }
  #menu {
    float: right;
    position: relative;
    width: 80px;
    height: 65px;
  }
  #menu:hover {
    cursor: pointer;
  }
  #menuImg {
    display: flex;
    position: fixed;
    float: right;
    top: 8px;
    right: 10px;
    height: 50px;
    width: 40px;
    margin-top: 0.7rem;
  }
  .menuDropdown {
    position: fixed;
    float: left;
    top: 65px;
    left: 0;
    width: 100%;
    text-align: center;
    display: block;
    background-color: #def2f1;
    border: solid black 1px;
  }
  .dropdownButton {
    font-size: 32px;
    height: 50px;
    float: left;
  }
  .dropdownButton img {
    position: relative;
    float: left;
    top: 25%;
    left: 30%;
    width: 35px;
    height: 35px;
  }
  .text {
    position: relative;
    float: left;
    text-align: center;
    left: 32%;
    top: -20px;
  }
  .dropdownText {
    position: relative;
    float: left;
    text-align: center;
    left: 20%;
    top: -20px;
    font-size: 28px;
    user-select: none;
  }
  .backImg {
    position: fixed;
    left: 20px;
    top: 75px;
    width: 35px;
    height: 35px;
  }
  .dropdown {
    position: fixed;
    float: left;
    top: 65px;
    width: 100%;
    text-align: center;
    display: block;
    background-color: #def2f1;
    border: solid black 1px;
  }
  #container{
        display: contents;
        margin-top: 2rem;
  }  
}

@media only screen and (min-width: 1001px) {
  .menuDropdown {
    display: none;
  }
  #menuButton {
    height: 1px;
    width: 1px;
  }
}
</style>
