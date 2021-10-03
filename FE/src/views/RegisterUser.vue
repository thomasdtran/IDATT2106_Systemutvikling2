<template>
  <link
    href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
    rel="stylesheet"
  />
  <div id="body">
    <div class="containter">
      <h2>Registrer</h2>
      <form @submit="registerNewUser()">
        <div>
          <label class="block" for="firstname"></label>
          <input
            v-model="user.firstname"
            required
            type="text"
            name="firstname"
            placeholder="Fornavn"
            id="firstname"
            class="nameInputFields"
            />
          </div>
        <div>
                  <label class="block" for="lastname"></label>
                  <input
                    v-model="user.lastname"
                    required
                    type="text"
                    name="lastname"
                    placeholder="Etternavn"
                    id="lastname"
                    class="nameInputFields"
                  />
                  <div
                    v-show="
                      (submitted && !user.firstname) ||
                      (submitted && !user.firstname)
                    "
                    class="invalid-feedback"
                  >
                    Skriv inn navn
                  </div>
              </div>
              <div id="emailInput">
                <i class="fa fa-envelope icon"></i>
                <label for="email"></label>
                <input
                  v-model="user.email"
                  required
                  type="text"
                  id="email"
                  name="email"
                  placeholder="Email"
                  class="inputFields"
                />
                <div v-show="submitted && !user.email" class="invalid-feedback">
                  Skriv inn email
                </div>
              </div>

              <div>
                <i class="fa fa-lock icon"></i>
                <label for="password"></label>
                <input
                  required
                  v-model="user.password"
                  type="password"
                  id="password"
                  name="password"
                  placeholder="Passord"
                  class="inputFields"
                />
                <p
                  class="invalid-feedback"
                  v-for="(error, i) in passwordValidation()"
                  v-bind:key="i"
                  :value="error"
                >
                  {{ error }}
                </p>
                <div
                  v-show="submitted && !user.password"
                  class="invalid-feedback"
                >
                  Skriv inn passord
                </div>
              </div>

              <div>
                <i class="fa fa-map-marker icon"></i>

                <place-auto
                    class="inputFields"
                    :api-key="apiKey"
                    :place-config="placeConfig"
                    @set-location="setLocation"
                    
                ></place-auto>

                <div v-show="submitted && !user.city" class="invalid-feedback">
                  Skriv inn sted
                </div>
              </div>

            <div id="genderField">
              <div class="toggle">
                <input
                  v-model="user.gender"
                  type="radio"
                  name="selector"
                  value="mann"
                  id="m"
                  checked="checked"
                />
                <label class="radioLabel" for="m">Mann</label>

                <input
                  v-model="user.gender"
                  type="radio"
                  name="selector"
                  value="kvinne"
                  id="f"
                />
                <label class="radioLabel" for="f">Kvinne</label>

                <input
                  v-model="user.gender"
                  type="radio"
                  name="selector"
                  value="annet"
                  id="o"
                />
                <label class="radioLabel" for="o">Annet</label>
              </div>
            </div>
              
              
              <div id="ageField">
                <label id="ageLabel" for="age">Fødselsår</label>
                <select
                  id="age"
                  v-model="user.birthyear"
                  :class="{ invalidSelect: submitted && !user.birthyear }"
                >
                  <option
                    v-for="(year, i) in years()"
                    v-bind:key="i"
                    :value="year"
                  >
                    {{ year }}
                  </option>
                </select>
              </div>

              <div
                v-show="submitted && !user.birthyear"
                class="invalid-feedback"
              ></div>

            <div class="flexContainer">
              <button class="btn" @click="$router.push('/login')">
                Tilbake
              </button>
              <button
                class="btn"
                :class="{ disabledButton: !user.isValidPassword }"
                type="button"
                @click="registerNewUser"
                :disabled="!user.isValidPassword"
              >
                Register
              </button>
            </div>
        <div v-show="user.authError" class="authError">
          <br>
          Email allerede registrert
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import utils from "../common/utils";
import axios from "axios";
import router from "../router/index";
import PlaceAuto from "@/components/PlaceAuto";

export default {
  components: {PlaceAuto},
  data() {
    return {
      user: {
        firstname: "",
        lastname: "",
        email: "",
        password: "",
        gender: "mann",
        city: "",
        birthyear: 0,
        isValidPassword: false,
        authError: false
      },
      submitted: false,

      rules: [
        { message: "One lowercase letter required.", regex: /[a-z]+/ },
        { message: "One uppercase letter required.", regex: /[A-Z]+/ },
        { message: "8 characters minimum.", regex: /.{8,}/ },
        { message: "One number required.", regex: /[0-9]+/ },
        {
          message: "One special character required",
          regex: /[!@#$%^&*)(+=._-]/,
        },
      ],
      placeConfig: {
        componentRestrictions: { country: ["NO"] },
        fields: ["place_id", "geometry", "name"],
        types: ['(cities)']
      },
      apiKey: process.env.VUE_APP_GOOGLEMAP_KEY,
    };
  },

  created() {},
  methods: {
    registerNewUser() {
      this.submitted = true;
      if (
        this.user.firstname &&
        this.user.lastname &&
        this.user.email &&
        this.user.password &&
        this.user.gender &&
        this.user.city
      ) {
        axios
          .post(utils.apiUrl + "/api/v1/users", this.user)
          .then(() => {
            router.push("/login");
          })
          .catch((err) => {
            if (err.response){
              this.user.authError = true;

            }

          });
      }
    },
    setLocation(city){
      this.user.city = city.name;
    },
    years() {
      const year = new Date().getFullYear();
      return Array.from(
        { length: year - 1900 },
        (value, index) => 1901 + index
      ).reverse();
    },
    passwordValidation() {
      let errors = [];
      for (let i = 0; i < this.rules.length; i++) {
        if (!this.rules[i].regex.test(this.user.password)) {
          errors.push(this.rules[i].message);
        }
      }
      if (errors.length === 0) {
        this.user.isValidPassword = true;
      } else {
        this.user.isValidPassword = false;
      }
      return errors;
    },
    test() {
      console.log(this.user.birthyear);
    },
  },
};
</script>

<style scoped>
* {
  padding: 0;
  margin: 0;
}

#body {
  width: 100% !important;
  height: 100% !important;
  min-height: 100vh;
  background-color: #17252a;
  position: relative;
  overflow: auto;
}

.containter{
  background-color: #def2f1;
  width: 40%;
  text-align: center;
  margin: 5rem auto auto;
  border-radius: 10px;
  align-items: center;
  position: relative;
  padding-bottom: 3rem;
}

.invalid-feedback {
  color: red;
  font-size: x-small;
  position: relative;
  text-align: center;
  margin: 0;
}

.authError{
  color: red;
  font-size: small;
  position: relative;
  text-align: center;
}

.invalidSelect {
  background-color: red;
  opacity: 0.4;
}

h2 {
  padding-top: 1.5rem;
}

.inputFields {
  margin: 1rem 0;
  font-size: 16px;
  padding: 0.4rem;
  margin-left: 0.5rem;
  width: 13rem;
  border: 1px none rgba(10, 180, 180, 1);
  border-bottom-style: solid;
  background: transparent;
  color: #17252a;
  outline: none;
  position: relative;
}

.nameInputFields {
  margin: 1rem 0;
  font-size: 16px;
  padding: 0.4rem;
  width: 15rem;
  border: 1px none rgba(10, 180, 180, 1);
  border-bottom-style: solid;
  background: transparent;
  color: #17252a;
  outline: none;
}

.btn{
  padding: 0;
  height: 2.4rem;
  width: 35%;
  font-size: 0.9rem;
}

.flexContainer {
  width: 17rem;
  margin: auto;
  justify-content: space-evenly;
}

button:hover {
  opacity: 0.8;
}

.disabledButton {
  opacity: 0.6;
}

.disabledButton:hover {
  cursor: auto;
  opacity: 0.6;
}

.icon {
  color: #2b7a78;
  width: 1.2rem;
}

select {
  color: #332f35;
  margin: 1.8rem;
  padding: 0.1rem;
  width: max-content;
  border-radius: 10px;
  border: solid 1px #17252a;
  opacity: 0.8;
  overflow: hidden;
}

input[type="radio"] {
  position: absolute;
  visibility: hidden;
  display: none;
}

input[type="radio"]:checked + label {
  background: #17252a;
  opacity: 0.8;
  color: #def2f1;
}

label + input[type="radio"] + label {
  border-left: solid 1px #17252a;
}

.toggle {
  border-radius: 10px;
  border: solid 1px #17252a;
  display:inline-block;
  overflow: hidden;
}

.radioLabel {
  color: #17252a;
  display: inline-block;
  cursor: pointer;
  padding: 0.2rem 1.25rem;
}

@media only screen and (max-width: 700px) {
  .containter{
    margin-top: 10%;
    background-color: #def2f1;
    width: 80vw;
    height: 100%;
  }
}
</style>
