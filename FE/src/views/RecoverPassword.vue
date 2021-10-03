<template>
  <div id="body">
    <div class="container">
      <img src="../assets/Icons/GiddCut.png" alt="Gidd logo"/>
        <div class="innerContainer">
          <h2>Glemt passord?</h2>

        <div class="descriptionContainer">
          <p>Nytt passord sendes på mail</p>
        </div>

        <div>
          <input
                 v-model="user.email"
                 name="email"
                 id="email"
                 class="inputFields"
                 placeholder="Email"
                 required
          />
          <div v-show="submitted && !user.email" class="invalidFeedback">
            Skriv inn email
          </div>

        </div>

        <div class="flexButtons">
          <button class="btn" @click="sendMail()">Send</button>
          <button class="btn" @click="$router.push('/login')">Tilbake</button>
        </div>
          <p>{{response}}</p>
      </div>


    </div>
  </div>

</template>


<script>

import axios from "axios";
import utils from "@/common/utils";

export default {
  data() {
    return {
      user: {
        email: "",
      },
      submitted: false,
      response: ""
    };
  },
  methods: {
    sendMail(){
      this.submitted = true;
      if (this.user.email){
        axios.post(utils.apiUrl + "/api/v1/users/recover-password", this.user)
        .then( () => {
          this.response = "Nytt passord ble sendt til email";
        }).catch( (err) => {
          if (err.response.status === 500){
            this.response = "Serverfeil"
          } else {
            this.response = "Ingen bruker med denne email"
          }
        });
      }
    }
  }
}



</script>


<style scoped>

*{
  overflow: auto;
}

#body {
  width: 100%;
  height: 100% !important;
  min-height: 100vh;
  background-color: #17252a;
  position: relative;
}

.btn{
  padding: 0;
  height: 2.4rem;
  width: 35%;
}

.descriptionContainer{
  font-size: 1.2rem;
  margin-top: 5%;

}

.container{
  background-color: #def2f1;
  margin: 5rem auto auto;
  width: 30rem;
  text-align: center;
  border-radius: 10px;
}

.invalidFeedback{
  color: red;
  font-size: smaller;
}

.innerContainer{
  font-size: 1.5rem;
  color: #17252a;
}

.flexButtons{
  display: flex;
  width: 70%;
  margin: 2rem auto 6rem;
}

img{
  width: 60%;
  height: auto;
  margin: 5%;
  border-radius: 15px;
}

.inputFields{
  margin-top: 1rem;
  margin-bottom: 0.4rem;
  font-size: 16px;
  width: 14rem;
  border: 1px solid rgba(10, 180, 180, 1);
  border-top: none;
  border-left: none;
  border-right: none;
  background: transparent;
  color: #17252a;
  outline: none;
}

@media only screen and (max-width: 700px) {
  .container{
    background-color: #def2f1;
    margin: 10% auto auto;
    width: 80vw;
  }
}

</style>