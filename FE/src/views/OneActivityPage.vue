<template>
  <div class="activity-container">
  <div id="activity-page">
  <img :src="image" class="sportImage"/>

    <div id="details">
      <li>
        <ul>
          <b>Arrangør:</b>
          {{ hostname }}
        </ul>
        <ul>
          <b>Sport:</b>
          {{ sport.type }}
        </ul>
        <ul>
          <b>Sted:</b>
          {{ activity.location }}
        </ul>
        <ul>
          <b>Treningsnivå:</b>
          {{ activity.intensity }}
        </ul>
        <ul>
          <b>Dato:</b>
          {{ activity.date }}
        </ul>
        <ul>
          <b>Tid:</b>
          {{ activity.startTime }} - {{ activity.endTime }}
        </ul>
        <ul>
          <b>Påmeldte:</b>
          {{ participants + "/" + activity.maxParticipants }}
        </ul>
        <ul>
          <b>På venteliste:</b>
          {{
            numberOnWaitingList
          }}
        </ul>
        <ul>
          <div v-if="canRateActivity && isRatingOpen">
            <star-rating
              :rating="rating"
              @update:rating="setRating"
              :star-size="22"
              :show-rating="true"
            ></star-rating>
          </div>
          <div v-else>
            <star-rating
              :rating="rating"
              :star-size="22"
              :show-rating="true"
              :read-only="true"
            ></star-rating>
          </div>
        </ul>
      </li>
    </div>

    <div id="descNequip">
      <div id="description">
        <h2 class="descHeaders">{{activity.title}}</h2>
        <h3 class="descHeaders"></h3>
        <br />
        {{ activity.description }}
      </div>

      <div id="equipView">
        <h3 class="descHeaders">Utstyr som trengs til aktivitet:</h3>
        <br />
        <li id="equipList" v-for="(item, i) in allEquipments" v-bind:key="i">
          <div id="equipListElement">
            {{ item.quantity }}/{{ item.neededQuantity }} {{ item.type }}
            <input
              v-if="isSignedUp"
              class="numberinput"
              type="number"
              placeholder="antall"
              v-model="addedQuants[i]"
              onkeypress="return event.charCode >= 48 && event.charCode <= 57"
            />
          </div>
        </li>
        <div class="center" v-if="isSignedUp && allEquipments.length > 0">
          <button class="btn btn-small" v-on:click="submitAddEquip">Legg til utstyr</button>
        </div>
      </div>
    </div>

    <div id="map">
      <g-map
        v-if="mapIsReady"
        :disableUI="true"
        :zoom="14"
        :mapType="roadmap"
        :center="{ lat: activity.latitude, lng: activity.longitude }"
      >
      </g-map>
    </div>

    <div id="forecast" v-if="forecastIsReady">
      <p>Forventet temperatur: {{ avgTemp }}° C</p>
      <img class="imgI" :src="require(`../assets/weather/png/${wicon}.png`)" />
      <h5>hentet fra meteorologisk institutt</h5>
    </div>

    <div class="bottom">
      <div v-if="!isOrganizer" id="signUp">
        <button v-bind:class="btnClass" @click="signUp">
          {{ signUpButtonName }}
        </button>
      </div>
      <div v-else>
        <div class="flexContainer">
          <button @click="editActivity" class="btn">Rediger</button>
          <button @click="deleteActivity" class="btn-red">Slett</button>
        </div>
      </div>
    </div>
  </div>
  <editActivity
      v-if="showEditActivity"
      :id="id"
      @done="
          updateActivity();
          editActivity();
        "
  >
  </editActivity>
  </div>
</template>

<script>
import utils from "../common/utils";
import axios from "axios";
import gMap from "../components/GoogleMap";
import editActivity from "../components/EditActivity";
import StarRating from "vue-star-rating";
import moment from 'moment'

export default {
  components: {
    StarRating,
    gMap,
    editActivity,
  },
  data() {
    return {
      activity: { organizer: {} },
      weatherResponse: {},
      hostname: "",
      equipmentList: [{ count: 0, name: "kommer senere ig" }],
      participants: 0,
      numberOnWaitingList: 0,
      mapIsReady: false,
      forecastIsReady: false,
      avgTemp: 0,
      wicon: "",
      rating: 0,
      addedQuants: [],
      canRateActivity: true,
      displayRating: true,
      showEditActivity: false,
      sport: {},
      image:""
    };
  },
  async mounted() {
      window.scrollTo(0, 0);
    await this.updateActivity();
    this.setInitialRatingScore();
    this.setCanRateActivity();
    this.isRatingOpen();
  },
  methods: {
    signUp() {
      if (!this.isSignedUp && !this.isOnWaitList) {
        if (!utils.getUser()) {
          this.$router.push("/login");
          return;
        }
        let signOn = confirm("Er du sikker på at du vil melde deg på?")
        if(!signOn) return;
        axios
          .post(
            utils.apiUrl +
              "/api/v1/participants/" +
              this.id +
              "/" +
              utils.getUser().id,
            {},
            utils.getConfigWithHeader()
          )
          .then(this.updateActivity);
      } else if (this.isSignedUp || this.isOnWaitList) {
        let signOf = confirm("Er du sikker på at du vil melde deg av?")
        if(!signOf) return;
        axios
          .delete(
            utils.apiUrl +
              "/api/v1/participants/" +
              this.id +
              "/" +
              utils.getUser().id,
            utils.getConfigWithHeader()
          )
          .then(
            //sletter utstyr denne brukeren skulle ta med
            this.activity.allEquipments.forEach((element) => {
              axios.delete(
                utils.apiUrl +
                  "/api/v1/activities/" +
                  this.activity.id +
                  "/equipment/" +
                  element.id +
                  "/" +
                  utils.getUser().id,
                  utils.getConfigWithHeader()
              ); //ta med header lul
            })
          )
          .then(this.updateActivity);
      }
    },
    async updateActivity() {
      await axios
        .get(utils.apiUrl + "/api/v1/activities/" + this.id)
        .then((response) => {
          let resActivity = response.data;
          this.activity = resActivity;
          this.hostname = resActivity.organizer.firstname + " " + resActivity.organizer.lastname;
          this.rating = resActivity.organizer.rating.ratingScore;
          this.participants = resActivity.participants.length;
          this.numberOnWaitingList = resActivity.waitlist.length;
          this.sport = resActivity.sport;
          this.mapIsReady = true;
          this.image = require("../assets/sports/" + resActivity.sport.type + ".jpg");
          console.log(this.image)
          // "..assets/sports/" +sport.type +".jpg"
        });

      //henter værdata på aktivitetens koordinater fra meteorologisk institutt sitt API
      await axios
        .get(
          "https://api.met.no/weatherapi/locationforecast/2.0/compact.json?lat=" +
            this.activity.latitude +
            "&lon=" +
            this.activity.longitude
        )
        .then((res) => {
          let times = res.data.properties.timeseries;

          //henter status fra 6 på morgenen på den aktuelle datoen for å vise status for de neste 12 timene
          let statusAt6 = this.activity.date + "T06:00:00Z";
          let statusIcon = "nan";
          let avgTemp = 0;

          let counter = 0; //mer detaljer jo nærmere datoen er
          times.forEach((element) => {
            if (element.time.includes(this.activity.date)) {
              counter++;
              avgTemp += element.data.instant.details.air_temperature;
            }
            if (element.time === statusAt6) {
              try {
                statusIcon = element.data.next_12_hours.summary.symbol_code;
              } catch (err) {
                //for lenge til for presis værdata
                statusIcon = times[0].data.next_6_hours.summary.symbol_code;
              }
            }
          });
          avgTemp = parseInt(avgTemp / counter);

          this.wicon = statusIcon;
          console.log(statusIcon)
          this.avgTemp = avgTemp
          if(statusIcon !== 'nan') this.forecastIsReady=true;
      });
    },
    setRating(rating) {
      let confirmChoice = confirm("Vil du gi tilbakemelding til arrangøren?")
      if (!confirmChoice) return ; 
      axios
        .post(
          utils.apiUrl +
            "/api/v1/activities/" +
            this.activity.id +
            "/rating/" +
            utils.getUser().id +
            "/" +
            rating
        )
        .then((response) => {
          console.log(response);
          // when user has rated, user is redirected to home page
          this.updateActivity();
        })
        .catch((err) => {
          console.log(err);
        });

      this.canRateActivity = false;
      return rating;
    },
    setInitialRatingScore() {
      this.rating = this.activity.organizer.rating.ratingScore;
    },
    setCanRateActivity() {
      if (this.getParticipant() === null || this.getParticipant().hasRated || this.getParticipant() === this.activity.organizer
          || this.isOrganizer) {
        this.canRateActivity = false;
      }
    },
    isRatingOpen(){
      // Time format which is given from activity
      moment.defaultFormat = "YYYY-MM-DD HH:mm"

      // Setting now-date and activity end date to given format
      let nowDate = moment(moment().format(moment.defaultFormat)).toDate();
      let activityEndDate = moment(this.activity.date + " " +this.activity.endTime, moment.defaultFormat).toDate();
      console.log("nå" + nowDate)
      console.log("activity" + activityEndDate);
      // Checks if activity is finished => can rate activity
      if (moment(activityEndDate).isBefore(nowDate)){
        console.log("rating is open");
        return true;
      }
      console.log("rating is closed");
      return false;
    },
    editActivity() {
      this.showEditActivity = !this.showEditActivity;
    },
    getParticipant() {
      for (let p of this.activity.participants) {
        if (p.user.id === utils.getUser().id) {
          return p;
        }
      }
      return null;
    },
    deleteActivity() {
      let doDelete = confirm("Are you sure you want to delete this activity?");
      if (doDelete) {
        axios
          .delete(
            utils.apiUrl + "/api/v1/activities/" + this.id,
            utils.getConfigWithHeader()
          )
          .then(() => {
            this.$router.push("/");
          });
      }
    },
    submitAddEquip() {
      for (let i = 0; i < this.addedQuants.length; i++) {
        let currentElement = this.activity.allEquipments[i];
        let addedEquipmentType = currentElement.type;
        let amountToAdd = this.addedQuants[i];
        let missingAmount =
          currentElement.neededQuantity - currentElement.quantity;

        if (this.addedQuants[i] > missingAmount) {
          console.log("mer enn nødnvendig");
          amountToAdd = missingAmount;
          console.log("legger derfor til " + amountToAdd) + " istedet";
        }
        console.log(addedEquipmentType, amountToAdd);
        amountToAdd = parseInt(amountToAdd);
        axios
          .put(
            utils.apiUrl +
              "/api/v1/activities/" +
              this.activity.id +
              "/equipment",
            {
              userId: utils.getUser().id,
              equipmentId: currentElement.id,
              quantity: amountToAdd,
            },
              utils.getConfigWithHeader()
          )
          .then(() => {
            this.updateActivity();
            this.addedQuants[i] = "";
          });
      }
    },
  },
  computed: {
    isSignedUp: function () {
      if (utils.getUser() === null) return false;
      if (this.activity.participants === undefined) return false;
      for (let participant of this.activity.participants) {
        let user = participant.user;
        console.log(user.id === utils.getUser().id);
        if (user.id === utils.getUser().id) return true;
      }
      return false;
    },

    isOnWaitList: function () {
      if (utils.getUser() === null) return false;
      if (this.activity.participants === undefined) return false;
      for (let waiting of this.activity.waitlist) {
        if (waiting.id === utils.getUser().id) return true;
      }
      return false;
    },

    signUpButtonName: function () {
      if (this.isSignedUp) return "Meld deg av";
      else if (this.isOnWaitList) return "Meld deg av ventelisten";
      else if (
        this.participants === this.activity.maxParticipants &&
        !this.isSignedUp
      )
        return "Meld deg på ventelisten";
      else return "Meld deg på";
    },
    allEquipments: function () {
      return this.activity.allEquipments;
    },
    isOrganizer: function () {
      return this.activity.organizer.id === utils.getUser().id;
    },
    btnClass: function () {
      if (this.isSignedUp || this.isOnWaitList) return "btn-red";
      else return "btn";
    },
  },
  props: ["id"],
};
</script>

<style scoped>
* {
  box-sizing: border-box;
  padding: 0;
  margin: 0;
}

.sportImage{
  width: 32rem;
  height: 14.25rem;
  border-radius: 5px;
  display: flex;
}

template {
  align-content: center;
}

.activity-container {
  position: relative;
  bottom: 0;
  height: 100%;
  width: 100%;
}

.center {
  width: 100%;
  margin: auto;
  text-align: center;
}

.btn-small {
  margin-top: 0.8rem;
  margin-bottom: 0.8rem;
  padding: 1rem;
  font-size: 0.8rem;
}

.numberinput {
  width: 6rem;
  padding-top: 0;
  padding-bottom: 0;
}

#activity-page {
  display: grid;
  grid-template-areas:
    "title details"
    "description map"
    "description forecast"
    "bottom bottom";
  grid-template-rows: 1fr 2fr 2fr 1fr;
  grid-template-columns: 5fr 4fr;
  width: 62vw;
  height: 120vh;
  margin: auto;
  grid-gap: 13px;
  margin-top: 6.7rem;
  margin-bottom: 3rem;
}

input {
  margin: 0.8rem;
  font-size: 16px;
  padding: 0.4rem;
  width: 14rem;
  border: 1px solid rgba(10, 180, 180, 1);
  border-top: none;
  border-left: none;
  border-right: none;
  background: transparent;
  color: #17252a;
  outline: none;
}

.imgI {
  border-radius: 0.3rem;
  max-width: 8rem;
  max-height: 8rem;
}

#descNequip {
  grid-area: description;
  display: grid;
  grid-template-rows: 1fr 1fr;
  grid-template-columns: 1fr;
}

#title {
  font-size: xx-large;
  grid-area: title;
  background-color: #3aafa9;
  text-align: center;
  padding: 25px;
  border-radius: 0.3rem;
}

#details {
  grid-area: details;
  background-color: #def2f1;
  padding: 15px;
  border-radius: 0.3rem;
}

#details > li {
  list-style-type: none;
}

#description {
  grid-column: 1/2;
  grid-row: 1/2;

  padding: 20px;
  border-radius: 0.3rem;
  background-color: #def2f1;
  font-size: large;
  align-content: center;
}

#signUp {
  grid-area: signUp;
  text-align: center;
}

.bottom {
  grid-area: bottom;
}

#signUpBtn {
  background-color: #3aafa9;
  padding: 5% 9%;
  color: white;
  border: 1px solid #3aafa9;
  font-size: 14px;
  letter-spacing: 2px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0px 7px 8px 2px rgba(0, 0, 0, 0.12);
}

#thumbnail {
  grid-area: thumbnail;
  background-color: #def2f1;
  border-radius: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.descHeaders {
  border-top: 10px;
}
#equipView -webkit-scrollbar {
  display: none;
}

#equipView {
  grid-column: 1/2;
  grid-row: 2/3;
  background-color: #def2f1;
  padding: 20px;
  overflow-y: scroll;

  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}

#equipList {
  border-top: 10px;
  padding-top: 20px;
  list-style-type: none;
  padding-left: 10px;
}

#equipListElement {
  min-height: 40px;
}

#map {
  grid-area: map;
  border-radius: 0.3rem;
  min-height: 12rem;
}

ul{
  margin: 2px;
}
#forecast {
  grid-area: forecast;
  border-radius: 0.3rem;
  background-color: #def2f1;
  text-align: center;
}

#forecast p {
  padding: 20px;
  font-size: x-large;
  font-weight: bold;
}

#forecast img {
  background-color: skyblue;
  border-radius: 35px;
  padding: 10px;
}

#forecast h5 {
  font-weight: lighter;
  font-style: italic;
}

@media only screen and (max-width: 768px) {
  #activity-page {
    grid-template-areas:
      "title"
      "details"
      "description"
      "map"
      "equip"
      "forecast"
      "signUp"
      "bottom";
    grid-template-rows: repeat(5, 1fr);
    grid-template-columns: 2fr;
    width: 90vw;
  }

  #title {
    font-size: 8vw;
  }

  #description {
    font-size: large;
    padding: 15px;
  }

  #signUpBtn {
    margin-bottom: 20px;
  }

  .sportImage{
    width: 100%;
    height: auto;
    border-radius: 5px;
    display: flex;

  }
}
</style>
