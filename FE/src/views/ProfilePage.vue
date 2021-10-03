<template>
    <link
    href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
    rel="stylesheet"
  />
<div id="profilePage">
    <div id="header">
        <h1 class="myProfile"><i class="fa fa-user"></i> Min Profil</h1>
      
      <div id="starAlign">
        <star-rating
            id="star"
            v-if="user.rating"
            :rating="user.rating.ratingScore"
            :read-only="true"
            :star-size="22"
            :show-rating="false">
        </star-rating>
      </div>

  </div>





    <table id="userInfoTable">
        <tr>
            <td>
                <p class="infoFields">{{user.firstname}}</p>
            </td>
            <td>
                <input 
                    v-show="isChangingUserInfo"
                    v-model="newUserInfo.firstname"
                    @keypress="testInput($event)"
                    type="text" 
                    placeholder="Nytt fornavn"
                >
            </td>
        </tr>
        <tr>
            <td>
                <p class="infoFields">{{user.lastname}}</p> 
            </td>
            <td>
                <input
                    v-show="isChangingUserInfo"
                    v-model="newUserInfo.lastname"
                    @keypress="testInput($event)"
                    type="text" 
                    placeholder="Nytt etternavn"
                /> 
            </td>
        </tr>
        <tr>
            <td>
                <p class="infoFields">{{user.gender}}</p> 
            </td>
            <td>
                <select 
                    v-show="isChangingUserInfo" 
                    v-model="newUserInfo.gender"
                >
                    <option value="" selected disabled hidden>Velg</option>
                    <option>Mann</option>
                    <option>Kvinne</option>    
                    <option>Annet</option>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <p class="infoFields"><i class="fa fa-globe" aria-hidden="true"></i> {{user.city}}</p> 
            </td>
            <td>
                <place-auto
                    v-show="isChangingUserInfo"
                    :apiKey="apiKey"
                    :placeConfig="placeConfig"
                    @set-location="setLocation"
                /> 
            </td>
        </tr>
        <tr>
            <td>
                <p class="infoFields"><i class="fa fa-calendar" aria-hidden="true"></i> {{user.birthyear}}</p>
            </td>
            <td>
                <select 
                    v-show="isChangingUserInfo" 
                    v-model="newUserInfo.birthyear" 
                    :class="{invalidSelect: !user.birthyear}"
                >
                    <option value="" selected disabled hidden>Velg</option>
                    <option v-for="(year, i) in years()" v-bind:key="i" :value="year">{{year}}</option>
                </select>
            </td>
        </tr>
    </table>

    <div v-show="isChangingUserInfo" id="submitInfoChange">
        <input id="passwordInput" v-model="userInfoPass" placeholder="Bekreft passord" type="password" @keyup.enter="sendNewUserInfo">
        <button class="btn" id="changeInfoBtn" @click="sendNewUserInfo">Lagre endringer</button>
    </div>
    <div>
        <button v-if="!isChangingUserInfo" class="btn" @click="showChangeFields(0)">Endre informasjon</button>
        <button v-else class="btn" @click="showChangeFields(0)">Avbryt</button>
    </div>
    <div id="changePass">
        <li v-show="isChangingPassword">
            <ul><input v-model="oldPass" type="password" placeholder="Gammelt passord"></ul>
            <ul><input v-model="pass1" type="password" placeholder="Nytt passord"></ul>
            <ul><input v-model="pass2" type="password" placeholder="Bekreft nytt passord" @keyup.enter="submitNewPass()"></ul>
            <ul><button class="btn" id="submitPass" @click="submitNewPass()">Bytt passord</button></ul>
        </li>
        <button v-if="!isChangingPassword" class="btn" id="showNewPassFields" @click="showChangeFields(1)">Bytt Passord</button>
        <button v-else class="btn" id="showNewPassFields" @click="showChangeFields(1)">Avbryt</button>
        
    </div>

    <div class="flexContainer">
        <button class="btn" @click="logOut">Logg ut</button>
        <button class="btn" id="deleteUser" @click="deleteUser">Slett bruker</button>
    </div>

</div>
</template>

<script>
import axios from "axios";
import placeAuto from "../components/PlaceAuto.vue";
import pencil from "../assets/Icons/pencil.png";
import utils from "../common/utils";
import StarRating from "vue-star-rating";


let nordalpha = /^[A-ZÆØÅa-zæøå ]+$/;
let passwordFormat = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

export default {
  data() {
    return {
      dburl: "",
      user: {},
      pencil: pencil,
      placeConfig: {
        componentRestrictions: { country: ["NO"] },
        fields: ["place_id", "geometry", "name"],
      },
      apiKey: process.env.VUE_APP_GOOGLEMAP_KEY,
      location: "",
      isChangingUserInfo: false,
      isChangingPassword: false,
      newUserInfo: {
        firstname: '',
        lastname: '',
        gender: '',
        city: '',
        birthyear: ''
      },
      userInfoPass: '',
      oldPass: '',
      pass1: '',
      pass2: '',
    };
  },
  components: {
    placeAuto,
    StarRating
  },
  methods: {
    testInput($event){
        if($event.charCode === 0 || nordalpha.test(String.fromCharCode($event.charCode))) return true;
        else $event.preventDefault();
    },
    setLocation(location) {
        this.newUserInfo.city = location.name;
        this.user.city = location.name;
        console.log(location, this.newUserInfo.city);
    },
    async sendNewUserInfo(){
        let checkres = await this.checkPassword(this.userInfoPass);
        if(checkres){
            for(let x in this.newUserInfo){
                if (this.newUserInfo[x]==='' || this.newUserInfo[x]===' '){ 
                    this.newUserInfo[x] = this.user[x];
                }
            } 
            axios.put(utils.apiUrl+'/api/v1/users/'+utils.getUser().id, this.newUserInfo, utils.getConfigWithHeader())
            this.user=this.newUserInfo;
            this.isChangingUserInfo = false;
            this.newUserInfo={
                firstname: '',
                lastname: '',
                gender: '',
                city: '',
                birthyear: ''
            }
            this.userInfoPass='';
        } else {
            alert('Skriv inn riktig passord for å lagre endringer');
            this.userInfoPass='';            
        }
    },
    showChangeFields(e){
        if(e===0) this.isChangingUserInfo = !this.isChangingUserInfo;
        else this.isChangingPassword = !this.isChangingPassword;
    },
    submitNewPass() {
        if (!passwordFormat.test(this.pass1)) {
            alert(
            "passord trenger en 8 karakterer liten bokstav, en stor bokstav, et tall og en spesiell karakter"
            );
        } else if (this.pass1 !== this.pass2) {
            alert("passordene er ikke like");
        } else if (!this.checkPassword(this.oldPass)){
            alert('gammelt passord feil');
        } else {
            axios.put(this.dburl, { password: this.pass1 }, utils.getConfigWithHeader());
            this.isChangingPassword = false;
            this.pass1='';
            this.pass2=''
        }
    },
    checkPassword: async function(newPass){
        try{
            await axios.post(utils.apiUrl+'/api/v1/users/'+utils.getUser().id+'/confirm-password', {
            password: newPass
            }, utils.getConfigWithHeader());
            return true;
        } catch (err){
            return false;
        }
    },
    deleteUser() {
        if(confirm("trykk ok for å slette din bruker")){
            console.log("deleting user");
            axios.delete(this.dburl, utils.getConfigWithHeader());
            this.logOut();
        }
        //else console.log('deletion cancelled');       
    },
    years() {
      const year = new Date().getFullYear();
      return Array.from(
        { length: year - 1900 },
        (value, index) => 1901 + index
      ).reverse();
    },
    logOut() {
      utils.logOut();
      this.$router.push({ path: '/login' });
    },
  },
   async mounted() {
    window.scrollTo(0, 0);
    this.dburl = utils.apiUrl + "/api/v1/users/" + utils.getUser().id;
     await axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
      this.user = res.data;
     });
  }
};
</script>

<style scoped>

.btn{
    display: inline;
    width: 228px;
    }

#profilePage{
  background-color: #def2f1;
  width: 60%;
  text-align: center;
  margin: 5rem auto auto;
  padding-bottom: 3rem;
  border-radius: 10px;
  align-items: center;
  position: relative;
    
}


#header {
    font-size: x-large;
}

.myProfile {
    margin: 0px;
    padding-top: 10px;
}


#starAlign{
  width: 12%;
  margin: auto;
  text-align: center;

}

#changeInfoBtn {
    margin-bottom: 10px;
}

#userInfoTable{
    position: relative;
    justify-self: center;
    text-align: center;
    margin: 0 auto auto;
}

.infoFields {
  margin: 1rem;
  font-size: 20px;
  padding: 0.4rem;
  width: 15rem;
  border: 1px solid rgba(10, 180, 180, 1);
  border-top: none;
  border-left: none;
  border-right: none;
  background: transparent;
  color: #17252a;
  outline: none;
}

input{
    margin: 0.8rem;
    font-size: 20px;
    padding: 0.4rem;
    width: 15rem;
    border: 1px solid rgba(10, 180, 180, 1);
    border-top: none;
    border-left: none;
    border-right: none;
    background: transparent;
    color: #17252a;
    outline: none;
    text-align: center;
}

select {
    background: transparent;
    font-size: 20px;
    padding: 8px 16px;
    border: 1px solid rgba(10, 180, 180, 1);
    color: black;
    cursor: pointer;
    outline: none;
}

select option {
    background-color: #def2f1;
    color:grey;
}

#passwordInput {
    margin-right: 4rem;
}

img.penIcon {
  width: 22px;
  height: 22px;
}

img.penIcon:active{
    background-color: #3AAFA9;
}

#changePass{
    padding-top: 1rem;
    padding-bottom: 1rem;
}

#changePass li{
    list-style-type: none;
    margin-left: -40px;
}

.flexContainer {
  margin: auto;
}

.divider{
    width:10rem;
    height:auto;
    display:inline-block;
}

#deleteUser{
    background-color:  #17252a;
}

@media only screen and (max-width: 768px) {

    #profilePage{
        width: 90vw;
        margin: 0;
        margin-top: 5rem;
        padding: 5vw;
    }

    #header{
        font-size: large;
    }

#userInfoTable {
    justify-self: center;
    align-self: center;
    text-align: center;
    font-size: large;
}

.infoFields {
    font-size: 20px;
    justify-self: center;
    align-self: center;
}

    th, td, tr{display: block;}

    input{
        width: 7rem;
    }

    input{
    margin: 0.8rem;
    font-size: 20px;
    padding: 0.4rem;
    width: 15rem;
    border: 1px solid rgba(10, 180, 180, 1);
    border-top: none;
    border-left: none;
    border-right: none;
    background: transparent;
    color: #17252a;
    outline: none;
    text-align: center;
}

#submitInfoChange {
    padding-top: 40px;
}

#passwordInput {
    margin: 0;
    margin-bottom: 10px;
}

    #showNewPassFields{
        outline: none;
    }

}
</style>
