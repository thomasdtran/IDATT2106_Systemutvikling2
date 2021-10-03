<template>
    <div class="content">
        <h1>Notifikasjoner</h1>
        <div v-if="messages.length === 0" id="empty">
            <h2 id="emptyMessage">Ingen notifikasjoner tilgjengelig!</h2>
            <img src="../assets/bell/bell_white.png" alt="White bell">
        </div>

        <div v-else>
            <a v-for="message in messages" v-bind:key="message" v-bind:href="message.path" @mouseover="message.read = true" 
            v-bind:class="{notALink: message.path === '#'}">
                <div class="messageContainer">
                    <div id="message">
                        <h3 v-bind:class="{link: message.path !== '#'}">{{message.title}}</h3>
                        <p>{{message.description}}</p>
                        <p id="date">{{message.date}}   {{message.time}}</p>
                    </div>
                    <div id="newMessageCircle" v-show="(!message.read)"></div>
                </div>
            </a>
        </div>
        
    </div>
</template>

<script>

export default {
    props:{
        messages: Array
    },
    data(){
        return{
            unread:0,
        }
    },
}
</script>

<style scoped>
.messageContainer{
    display: grid;
    grid-template-columns: auto auto auto auto;
    overflow: hidden;
}

.link{
    color: #3aafa9;
}
.notALink:hover{
    cursor: text;
}

.content{
    padding: 1rem;
    background-color: #17252a;
    width: 20rem;
    box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
    position: absolute;
    right: -4.6rem;
    top: 3.25rem;
    border-radius: 15px;
    height: 43rem;
    overflow: scroll;
}

#message{
    width: 17rem;
    grid-column: 1/2;
    overflow: hidden;
    line-height: 1.4;
}

#newMessageCircle{
    width: 1rem;
    height: 1rem;
    border-radius: 50%;
    background-color: #3aafa9;
    grid-column: 3;
    margin-top: 0.5rem;
}

#emptyMessage{
    text-align: center;
    color: white;
    opacity: 0.8;
}

#empty{
    margin: auto;
}

#date{
    font-size: 13px;
}

a{
    color: white;
    width: 19rem;
    text-decoration: none;
    display: block;
    min-height: 7rem;
    line-height: 1em;
    margin-bottom: 7px;
    border-radius: 10px;
    background-color: #2e3a3f;
    padding-left: 1rem;
    display: block;
    overflow: auto;
}

img{
    width: 100%;
    margin: auto;
    opacity: 0.8;
}

a:hover{
 background-color: #455054;
}

h1{
    color: white;
    font-size: 1.9rem;
}

@media only screen and (max-width: 1000px){
    a{
        width: 17rem;
    }
}
</style>