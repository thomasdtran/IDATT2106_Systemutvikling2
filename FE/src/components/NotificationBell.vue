<template>
    <div class="container">
        <div id="bell" @click="openedBell">
            <img src="../assets/bell/bell.png">
            <div id="notification" v-show="unread > 0">
                {{unread}}
            </div>
             <span class="tooltiptext">Notifications</span>
        </div>
        <activity-stream v-show="bellIsClicked" :messages="messages.sort(compare)"/>
    </div>

</template>

<script>
import ActivityStream from './ActivityStream.vue';
import axios from "axios";
import utils from "../common/utils";

export default {
  components: { ActivityStream },
    import:{
        ActivityStream
    },
    mounted(){
        this.fetchData();
        this.pollData();
    },
    beforeUnmount(){
	    clearInterval(this.pollInterval);
    },   
    data(){
        return{
            test:false,
            unread:0,
            bellIsClicked:false,
            messages:[],
            notReadMessages:[],
            polling:null,
        }
    },
    methods:{
        fetchData(){
          axios
              .get(utils.apiUrl + "/api/v1/notification/" + utils.getUser().id)
              .then((response) => {
                if (this.messages.length < response.data.length) {
                  this.messages = response.data
                }
              });
        },
        pollData(){
            this.polling = setInterval(this.fetchData,5000);
        },
        compare(a,b) {
            if (a.id > b.id){
                return -1;
            }
            if (a.id < b.id){
                return 1;
            }
            return 0;
        },
        openedBell(){
            if(this.bellIsClicked === false){
                this.bellIsClicked = true;
                this.notReadMessages.forEach(message => {
                    console.log(message.id);
                    axios.put(utils.apiUrl + "/api/v1/notification/" + message.id)
                });
                this.notReadMessages = [];
                this.unread = 0;
            }else{
                this.bellIsClicked = false;
            }
            
        }
    },
    watch:{
        messages: function(){
            this.unread = 0;
            this.messages.forEach(message => {
                if(!message.read){
                    this.notReadMessages.push(message);
                    this.unread ++;
                }
            });
        }
    }
    
}
</script>

<style scoped>

.container{
    position: relative;
    display: inline-block;
    top: 0.45rem;
    left: 8rem;
    float: right;
}

#bell{
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    background-color:#def2f1;
    position: relative;
    display: inline-block;
}

#bell img{
    width: 100%;
}

#bell:hover{
    background-color: #c7d9d8;
    cursor: pointer;
}

#bell:active{
    background-color: #b1c1c0;
    width: 2.44rem;
    height: 2.44rem;
}

#notification{
    position: absolute;
    width: 1.2rem;
    height: 1.1rem;
    background-color: red;
    border-radius: 50%;
    top: 0rem;
    left: 1.8rem;
    text-align: center;
    color: white;
    font-size: 1rem;
}

#bell .tooltiptext {
  visibility: hidden;
  width: 120px;
  background-color: #555;
  color: #fff;
  text-align: center;
  padding: 5px 0;
  border-radius: 6px;

  /* Position the tooltip text */
  position: absolute;
  top: 100%;
  left: 50%;
  margin-left: -60px; /* Use half of the width (120/2 = 60), to center the tooltip */

  /* Fade in tooltip */
  opacity: 0;
  transition: opacity 0.3s;

  font-size: 13px;
}

/* Show the tooltip text when you mouse over the tooltip container */
#bell:hover .tooltiptext {
  visibility: visible;
  opacity: 1;
}

@media only screen and (max-width: 1000px){
    .container{
        display: contents;
    }
    #bell{
        margin-top: 0.8rem;
    }
    .content{
        width: 18rem;
        height: 30rem;
        right: 2rem;
        margin-top: 2rem;
    }
    
  
}
</style>