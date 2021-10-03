<template>
  <link
    href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
    rel="stylesheet"
  />

  <div id="body">
    <div class ="flexContainer container">
      <div id="about">
        <h2>Om Gidd</h2>
        <br>
        <p>Gidd! er en plattform som hjelper deg å møte nye mennesker samtidig som du er i aktivitet.</p>
        <br>
        <p>Gidd! lar alle brukerne legge ut aktiviteter de ønsker å gjøre, og alle andre som har registrert seg på plattformen kan melde seg på.</p>
      </div>
      <div id="login">
        <img id="logo" src="../assets/Icons/GiddCut.png" alt="Gidd logo" />
        <div id="login-inner">
          <ul class="noBullet">
            <li>
              <i class="fa fa-user icon"></i>
              <label for="email"></label>
              <input @keyup.enter="login"
                id="email"
                v-model="user.email"
                type="text"
                name="email"
                class="inputFields"
                placeholder="Email"
                required
              />
              <div
                v-show="submitted && !user.email"
                class="invalid-feedback"
              >
                Skriv inn email
              </div>
            </li>
            <li>
              <i class="fa fa-lock icon"></i>
              <label htmlFor="password"></label>
              <input @keyup.enter="login"
                id="password"
                v-model="user.password"
                type="password"
                name="password"
                class="inputFields"
                placeholder="Passord"
              />
              <div
                v-show="submitted && !user.password"
                class="invalid-feedback">
                Skriv inn passord
              </div>
              <div v-show="user.authError" class="invalid-feedback">
                Feil innlogging
              </div>
            </li>
          </ul>

          <div class="flexContainer">
            <button class="btn" @click="login()">Logg inn</button>
            <button class="btn" @click="$router.push('/register')">
              Registrer
            </button>
          </div>
        </div>

        <div class="textAlign" >
          <router-link to="/recover">Glemt passord?</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import utils from "../common/utils";
import axios from "axios";

export default {
  data() {
    return {
      user: {
        email: "",
        password: "",
        authError : false
      },
      submitted: false,
    };
  },

  created() {},
  methods: {
    login() {
      this.submitted = true;
      if (this.user.email && this.user.password) {
        axios
          .post(utils.apiUrl + "/api/v1/users/authenticate", {
            password: this.user.password,
            username: this.user.email,
          })
          .then((response) => {
            console.log(response);
            utils.updateUser({
              id: response.data.id,
              jwt: response.data.jwt,
              email: this.user.email,
            });
            this.$router.push("/");

          })
          .catch((err) => {
            if (err.response){
              this.user.authError = true;
            }
          });
      }
    },
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
  padding: 0;
  margin: 0;
}

p {
  font-size: 1.2rem;
}

#body {
  position: relative;
  width: 100%;
  height: 100% !important;
  min-height: 100vh;
  background-color: #17252a;
  overflow: auto;
}

.inputFields {
  margin: 1rem 5%;
  padding-bottom: 0.3rem;
  padding-top: 0.3rem;
  font-size: 16px;
  width: 80%;
  border: 1px none rgba(10, 180, 180, 1);
  border-bottom-style: solid;
  background: transparent;
  color: #17252a;
  outline: none;
}

#logo {
  border-radius: 20px;
  padding: 0.5em;
  margin: 2em;
  width: 80%;
}

.textAlign{
  margin-top: 3rem;
  text-align: center;
}

.container {
  width: 60vw;
  margin: 5rem auto auto;
  min-height: 60vh;
  background-color: #def2f1;
  border-radius: 10px;
  box-sizing: border-box;
  padding: 1rem;
}

#login {
  margin: 0 auto 0;
  text-align: center;
  width: 100%;
  border-left: solid gray 1px;
}

#about {
  width: 100%;
  padding-bottom: 2rem;
  padding-right: 1rem;
}

#login-inner {
  margin: auto;
  width:80%;
}

.invalid-feedback {
  color: red;
  font-size: smaller;
  text-align: center;
}

.noBullet {
  list-style-type: none;
  padding: 0;
  margin-bottom: 2rem;
}
.icon {
  color: #2b7a78;
  min-width: 10%;
  text-align: center;
}
button:hover {
  opacity: 0.8;
}

.btn{
  padding: 0;
  height: 2.4rem;
  width: 35%;
  font-size: 0.9rem;
}

@media only screen and (max-width: 900px) {
  .container {
    margin-top: 10%;
    flex-direction: column;
    width: 80vw;
  }

  #logo {
    border-radius: 2rem;
    padding: 1rem;
    margin: 0.5rem;
    width: 80%;
  }

  #login {
    order: -1;
    border-left: 0;
    border-bottom: solid gray 1px;
    margin-bottom: 1rem;
    padding-bottom: 1rem;
  }
}
</style>
