<template>
  <div class="container">
    <div class="editContainer">
      <div class="exitBtn" v-on:click="$emit('done')">X</div>
      <div>
        <p>Tittel</p>
        <input
            class="inputField"
            v-model="title"
            type="text"
            placeholder="Tittel"
            name="Tittel"
            maxlength="60"
            required
        />
      </div>
      <div>
        <div>
          <p>Dato</p>
          <input
              class="inputField"
              v-model="date"
              type="date"
              name="Dato"
              v-bind:min="today"
              v-bind:max="maxDate"
              required
          />
        </div>
        <div class="timeContainer">
          <div class="timeInputContainer">
            <p>Start</p>
            <input
                v-model="startTime"
                type="time"
                name="Start tid"
                class="timeInput"
                required
            />
          </div>
          <div class="timeInputContainer">
            <p>Slutt</p>
            <input
                v-model="endTime"
                type="time"
                name="Slutt tid"
                class="timeInput"
                required
            />
          </div>
        </div>
      </div>
      <div>
        <p>Maks Antall Deltakere</p>
        <input
            class="inputField"
            v-model="maxParticipants"
            type="number"
            placeholder="Antall plasser"
            min="1"
            max="200"
            name="Plasser"
            required
        />
      </div>
      <div>
        <p>Beskrivelse</p>
        <textarea
            class="inputField"
            name="Beskrivelse"
            id=""
            cols="40"
            rows="9"
            maxlength="280"
            placeholder="Beskrivelse, maks 280 karakterer"
            v-model="desc"
            required
        ></textarea>
      </div>

      <div>
        <p>Sted</p>
        <place-auto
            class="inputField"
            :apiKey="apiKey"
            :placeConfig="placeConfig"
            :default-val="location"
            @set-location="setLocation"
        />
      </div>

      <div>
        <p>Type Aktivitet</p>
        <select class="inputField" name="sports" id="sports" v-model="sport" required>
          <option
              v-for="sport in sports"
              v-bind:key="sport.id"
              v-bind:value="sport"
          >
            {{sport.type}}
          </option>
        </select>
      </div>
      <div id="intensityContainer">
        <p id="intensityHeader">Velg Treningsnivå</p>
        <div>
          <div>
            <input
                type="radio"
                id="radioLow"
                name="radioDiff"
                value="Lav"
                v-model="intensity"
                checked
            />
            <label for="radioLow" id="lowLabel">Lav</label>
          </div>

          <div>
            <input
                type="radio"
                id="radioMedium"
                name="radioDiff"
                value="Middels"
                v-model="intensity"
            />
            <label for="radioMedium" id="mediumLabel">Middels</label>
          </div>

          <div>
            <input
                type="radio"
                id="radioHigh"
                name="radioDiff"
                value="Høy"
                v-model="intensity"
            />
            <label for="radioHigh" id="highLabel">Høy</label>
          </div>
          <div id="create">
            <button @click="updateActivity" class="btn">Lagre Endringer</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import placeAuto from "./PlaceAuto.vue";
import utils from "../common/utils";
import axios from "axios";

export default {
  name: "EditActivity",
  data() {
    return {
      oldActivity: {},
      user: {},
      apiUrl: process.env.VUE_APP_API_URL,
      placeConfig: {
        componentRestrictions: { country: ["NO"] },
        fields: ["place_id", "geometry", "name"],
      },
      apiKey: "AIzaSyCs7kB9u2t0eM4t0puGPKMfZG2nm1BzuXk",
      title: "",
      date: "",
      startTime: "",
      endTime: "",
      maxParticipants: 1,
      desc: "",
      sport: {},
      intensity: "Lav",
      activityId: 0,
      sports: [],
      location: "",
      latitude: 0.0,
      longitude: 0.0,
      tools: [],
      newTool: {
        type: "",
        quantity: 0,
      },
    };
  },
  methods: {
    updateActivity() {
      const activity = {
        title: this.title,
        description: this.desc,
        date: this.date,
        startTime: this.startTime,
        endTime: this.endTime,
        intensity: this.intensity,
        location: this.location,
        latitude: this.latitude,
        longitude: this.longitude,
        sport: this.sport,
        maxParticipants: parseInt(this.maxParticipants),
      };

      axios
        .put(
          utils.apiUrl + "/api/v1/activities/" + this.id,
          activity,
          utils.getConfigWithHeader()
        )
        .then(() => {
          console.log("done");
          this.$emit("done");
        });
    },

    setLocation(location) {
      this.location = location.name;
      this.longitude =
        (location.geometry.viewport.La.i + location.geometry.viewport.La.g) / 2;
      this.latitude =
        (location.geometry.viewport.Ua.i + location.geometry.viewport.Ua.g) / 2;
    },
  },
  async mounted() {
    console.log(this.id);
    let response = await axios.get(
      utils.apiUrl + "/api/v1/activities/" + this.id
    );
    await axios
      .get(utils.apiUrl + "/api/v1/sports")
      .then((response) => (this.sports = response.data));
    let activity = response.data;
    console.log(activity.title);
    this.oldActivity = activity;
    this.title = activity.title;
    this.date = activity.date;
    this.startTime = activity.startTime;
    this.endTime = activity.endTime;
    this.maxParticipants = activity.maxParticipants;
    this.desc = activity.description;
    this.location = activity.location;
    this.sport.type = activity.sport;
    this.intensity = activity.intensity;
  },
  computed: {
    today: function () {
      var today = new Date();
      var dd = String(today.getDate()).padStart(2, "0");
      var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
      var yyyy = today.getFullYear();
      today = yyyy + "-" + mm + "-" + dd;
      return today;
    },
    maxDate: function () {
      var today = new Date();
      var dd = String(today.getDate()).padStart(2, "0");
      var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
      var yyyy = today.getFullYear() + 1;
      today = yyyy + "-" + mm + "-" + dd;
      return today;
    },
  },
  props: ["id"],
  components: {
    placeAuto,
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
  color: #17252a;
}

.container {
  top: 0;
  bottom: 0;
  position: fixed;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  overflow: scroll;
}

.editContainer {
  width: 35%;
  left: 0;
  right: 0;
  margin: 7rem auto 5rem;
  background-color: rgba(255,255,255, 0.9);
  padding: 1rem;
  border-radius: 0.5rem;
  position: relative;
}

.exitBtn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  font-size: 1.5rem;
  color: darkred;
  cursor: pointer;
}

.inputField {
  width: 100%;
}

.timeContainer {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.timeInputContainer {
  width: 40%;
}

.timeInput {
  width: 100%;
}

@media only screen and (max-width: 1100px) {
  .editContainer {
    width: 60%;
  }
}

@media only screen and (max-width: 768px) {
  .editContainer {
    width: 80%;
  }
}
</style>
