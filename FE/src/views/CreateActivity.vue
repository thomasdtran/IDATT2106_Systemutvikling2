<template>
  <div class="container">
  
    <div id="left">
      <div class="inputContainer">
        <p>Tittel</p>
        <input
          v-model="title"
          type="text"
          placeholder="Tittel"
          name="Tittel"
          maxlength="60"
          required
        />
        <p v-if="errors.includes(1)" class= "error">
          <b>Skriv inn tittel</b>
        </p>
      </div>
      <div>
        <div class="inputContainer">
          <p>Dato</p>
          <input
            v-model="date"
            type="date"
            name="Dato"
            v-bind:min="today"
            v-bind:max="maxDate"
            required
          />
          <p v-if="errors.includes(2)" class= "error">
            <b >Velg Dato</b>
          </p>
        </div>
        <div class="timeContainer">
          <div class="inputContainer">
            <p>Start</p>
            <input
              v-model="startTime"
              type="time"
              name="Start tid"
              class="timeInput"
              required
            />
            <p v-if="errors.includes(3)" class= "error">
            <b>Velg starttid</b>
          </p>
          </div>
          <div class="inputContainer">
            <p>Slutt</p>
            <input
              v-model="endTime"
              type="time"
              name="Slutt tid"
              class="timeInput"
              required
            />
            <p v-if="errors.includes(4)" class= "error">
            <b>Velg sluttid</b>
          </p>
          </div>
        </div>
      </div>
      <div class="inputContainer">
        <p>Maks Antall Deltakere</p>
        <input
          v-model="maxParticipants"
          type="number"
          placeholder="Antall plasser"
          min="1"
          max="200"
          name="Plasser"
          required
        />
          <p v-if="errors.includes(5)" class= "error">
            <b>Bestem maks deltakere</b>
          </p>
      </div>
      <div class="inputContainer">
        <p>Beskrivelse</p>
        <textarea
          name="Beskrivelse"
          id=""
          cols="40"
          rows="9"
          maxlength="280"
          placeholder="Beskrivelse, maks 280 karakterer"
          v-model="desc"
          required
        ></textarea>
        <p v-if="errors.includes(6)" class= "error">
          <b>Skriv inn beskrivelse</b>
        </p>
      </div>
    </div>
    <div id="right">
      <div class="inputContainer">
        <p>Type Aktivitet</p>
        <select name="sports" id="sports" v-model="sport" required>
          <option
            v-for="sport in sports"
            v-bind:key="sport.id"
            v-bind:value="sport"
          >
            {{sport.type}}
          </option>
        </select>
        <p v-if="errors.includes(7)" class= "error">
          <b>Velg sport</b>
        </p>
      </div>
      <div class="inputContainer" id="intensityContainer">
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
        </div>
      </div>

      <div id="inputLocation">
        <p>Sted</p>
        <place-auto
          :apiKey="apiKey"
          :placeConfig="placeConfig"
          @set-location="setLocation"
        />
        <p v-if="errors.includes(8)" class= "error">
          <b>Velg sted</b>
        </p>
      </div>

      <div id="tools">
        <p>Utstyr</p>
        <div id="equipmentList">
          <div
            class="timeContainer smallText"
            v-for="(tool,i) in tools"
            :key="i"
            id="equipment"
          >
            <div>
              <b>{{ tool.type }}</b>
            </div>
            <div>{{ tool.neededQuantity }}</div>
            <div id="deleteEquipment" @click="removeEquipment(i)"><span id="x">X</span></div>
          </div>
        </div>
        <br />
        <div class="timeContainer">
          <input
            id="toolType"
            v-model="newTool.type"
            type="text"
            name="utstyr"
            class="inputFields"
            placeholder="Utstyr"
          />
          <input
            v-model="newTool.quantity"
            id="toolQnt"
            type="number"
            placeholder="Qnt"
            min="1"
            max="200"
            name="Mengde"
          />
        </div>
        <div id="centerBtn">
          <button class="btn" id="addToolsBtn" @click="addTool()">Legg til utstyr</button>
        </div>
      </div>
    </div>
  </div>
    <div class= "buttons">
      <button @click="createActivity" class="btn">Lag Aktivitet</button>
    </div>
    <div class= "buttons">
      <button @click="routeBack('/')" class="btn">Avbryt</button>
    </div>
</template>

<script>
import placeAuto from "../components/PlaceAuto.vue";
import utils from "../common/utils";
import axios from "axios";

export default {
  name: "CreateActivity",
  components: {
    placeAuto,
  },
  data() {
    return {
      user: {},
      apiUrl: process.env.VUE_APP_API_URL,
      placeConfig: {
        componentRestrictions: { country: ["NO"] },
        fields: ["place_id", "geometry", "name"],
      },
      errors: [],
      apiKey: process.env.VUE_APP_GOOGLEMAP_KEY,
      title: "",
      date: "",
      startTime: "",
      endTime: "",
      maxParticipants: 1,
      desc: "",
      sport: {},
      intensity: "Lav",
      activityId: 0,
      sports: "",
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
  
    createActivity(e) {
      this.errors = [];
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
      //Unntakshåndtering
      for (let attribute in activity) {
        if (activity[attribute] == null || activity[attribute] == "") {
          if (!this.title) {
            this.errors.push (1)
          }
          if (!this.date) {
            this.errors.push (2)
          }
          if (!this.startTime) {
            this.errors.push (3)
          }
          if (!this.endTime) {
            this.errors.push (4)
          }
          if (!this.maxParticipants) {
            this.errors.push (5)
          }
          if (!this.desc) {
            this.errors.push (6)
          }
          if (!this.sport.type) {
            this.errors.push (7)
          }
          if (!this.location) {
            this.errors.push (8)
          }
          
          e.preventDefault();
          return;
        }
      }
      console.log(this.url);
      axios
        .post(
          utils.apiUrl + "/api/v1/activities/" + utils.getUser().id,
          activity,
          utils.getConfigWithHeader()
        )
        .then((response) => {
          let activity = response.data;
          if (this.tools.length === 0) {
            this.$router.push("/activity/" + activity.id);
            return;
          }
          axios
            .post(
              utils.apiUrl +
                "/api/v1/activities/" + activity.id + "/equipment",
              this.tools,
              utils.getConfigWithHeader()
            )
            .then(() => this.$router.push("/activity/" + activity.id));
        })
        .catch((err) => {
          //todo: handle error
          console.log(err);
        });
    },
    setLocation(location) {
      this.location = location.name;
      this.longitude = (location.geometry.viewport.La.i + location.geometry.viewport.La.g)/2
      this.latitude =  (location.geometry.viewport.Ua.i + location.geometry.viewport.Ua.g)/2
    },
    addTool() {
      let type = this.newTool.type;
      let quantity = this.newTool.quantity;
      if (parseInt(quantity) === 0 || type === "") return;
      this.tools.push({ type: type, neededQuantity: quantity });
      this.newTool.type = "";
      this.newTool.quantity = 1;
    },
    removeEquipment(index){
      this.tools.splice(index,1);
    },
    routeBack(path){
      this.$router.push({ path: path})
      this.clickMenu();
    },
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
  mounted: function() {
    window.scrollTo(0, 0);
    axios
      .get(utils.apiUrl + "/api/v1/sports")
      .then((response) => (this.sports = response.data));
  },
};
</script>

<style scoped>
* {
  color: #17252a;
}
.container {
  margin: auto;
  margin-top: 5rem;
  width: 50%;
  display: flex;
  flex-direction: row;
}

.timeContainer {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.timeInput {
  width: 11rem;
}

.inputContainer {
  display: flex;
  flex-direction: column;
}

.smallText {
  font-size: 0.8rem;
}

#deleteEquipment{
  border-radius: 6px;
  height: 1rem;
  width: 1.6rem;
  text-align: center;
}

#deleteEquipment:hover{
    font-weight: bold;
    font-size: 1rem;
    cursor: pointer;
}

#x{
  color:#af4c3a;
}

#equipmentList{
  height: 11.3rem;
  overflow: scroll;
}

#equipment{
  min-height: 2rem; 
  font-size: 0.9rem;
}

#tools{
  min-height:19.8rem;
  padding: 0.8rem;
  border:1px solid black;
  border-radius: 10px;
}

#addToolsBtn{
  padding: 0.5rem 1rem;
  font-size: 0.8rem;
}
#intensityContainer {
  margin-top: 2rem;
}

#intensityHeader {
  margin: 0;
  margin-bottom: 1.1rem;
}
#inputLocation {
  margin-top: 1.8rem;
}

#autocomplete {
  width: 100%;
  padding: 12px 20px;
  margin-bottom: 0.7rem;
  box-sizing: border-box;
}

#left {
  display: flex;
  flex-direction: column;
  margin-right: 4%;
}
#right {
  display: flex;
  flex-direction: column;
}
.error {
  margin-top: 0px;
  margin-bottom: 20px;
  font-size: smaller;
  position: relative;
  text-align: center;
}
.error b {
  color: red;
}
.buttons {
  margin: auto;
  width: 50%;
  margin-top: 1%;
  top: 30px;
}

.buttons button {
  width: 200px;
}

#map {
  margin-top: 1rem;
  height: 22.4vw;
  width: 25vw;
  border: 2px solid #17252a;
  border-radius: 5%;
}

#sports {
  width: 40vh;
  height: 5vh;
}

#left input {
  padding: 12px 20px;
  margin-bottom: 3%;
  box-sizing: border-box;
}


option,
select,
input {
  font-size: 0.9rem;
}

label {
  margin: 3%;
}

p {
  margin: 0.7rem;
  font-weight: bold;
}
textarea {
  resize: none;
  font-size: 1rem;
  padding: 1rem;
  outline: none;
  line-height: 1.5rem;
}

#toolQnt {
  width: 30%;
}

#toolType {
  width: 60%;
}

#centerBtn {
  margin-top: 1rem;
  text-align: center;
}

@media only screen and (max-width: 700px) {
  .container {
    flex-direction: column;
    justify-content: center;
    width: 19rem;
  }

  .timeInput {
    width: 8.8rem;
  }

  .buttons {
    margin-top: 20px;
  }

  #map {
    width: 100%;
    height: 15rem;
    margin-bottom: 1rem;
  }
}
</style>
