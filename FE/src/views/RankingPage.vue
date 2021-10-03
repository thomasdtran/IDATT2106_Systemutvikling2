<template>
    <div id="container">
        <h1 id="title">{{title}}</h1>
        <div id="activity">
            <label id="activityTitle">Aktivitet:</label>
            <select id="activitiesList" @change="changeTable($event)">
                <option>
                    Totalt
                </option>
                <option v-for="activity in activities" :key="activity" :value="activity.id">
                    {{activity.type}}
                </option>
            </select>
        </div>
        <div id="leaderBoard">
            <div id="header">
                <h3 id="positionTitle">Plassering:</h3>
                <h3 id="nameTitle">Navn:</h3>
                <h3 id="pointsTitle">Poeng:</h3>
            </div>
            <div id="columns">
                <ul id="position">
                    <li v-for="num in top10Users.length" :key="num">
                        {{"#" + num}}
                    </li>
                    <li v-if="lowerThan10">
                        {{"#" + userPosition}}
                    </li>
                </ul>
                <ul v-if="totalPoints" class="name">
                    <li v-for="user in top10Users" :key="user">
                        {{user[0] + " " + user[1]}}
                    </li>
                    <li v-if="lowerThan10">
                        {{userName}}
                    </li>
                </ul>
                <ul v-else class="name">
                    <li v-for="user in top10Users" :key="user">
                        {{user.user.firstname + " " + user.user.lastname}}
                    </li>
                    <li v-if="lowerThan10">
                        {{userName}}
                    </li>
                </ul>
                <ul v-if="totalPoints" class="points">
                    <li v-for="user in top10Users" :key="user">
                        {{user[2]}}
                    </li>
                    <li v-if="lowerThan10">
                        {{userPoints}}
                    </li>
                </ul>
                <ul v-else class="points">
                    <li v-for="user in top10Users" :key="user">
                        {{user.points}}
                    </li>
                    <li v-if="lowerThan10">
                        {{userPoints}}
                    </li>
                </ul>
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
                dburl: "",
                title: "Totalt",
                lowerThan10: false,
                totalPoints: true,
                userPosition: 0,
                userName: "",
                userPoints: 0,
                users: {},
                activities: {},
                userProfile: "",
            };
        },
        methods: {
            changeTable(event) {
                let id = event.target.value;
                if (id == "Totalt") {
                    this.getTotalLeaderboard();
                }
                else {
                    this.getActivityLeaderboard(id);
                }
            },
            getActivities() {
                this.dburl = utils.apiUrl + "/api/v1/sports";
                axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                    this.activities = res.data;
                });
            },
            getTotalLeaderboard() {
                this.title = "Totalt";
                this.dburl = utils.apiUrl + "/api/v1/users";
                axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                    this.users = res.data;
                    for (let i = 0; i < this.users.length; i++) {
                        if (this.users[i][2] === this.userProfile.totalPoints){
                            this.userPosition = i+1;
                            this.userPoints = this.userProfile.totalPoints;
                            break;
                        }
                        else if (i === this.users.length-1) {
                            this.userPosition = 0;
                            this.userPoints = 0;
                        }
                    }
                    if (this.userPosition > 10) {
                        this.lowerThan10 = true;
                    } else {
                        this.lowerThan10 = false;
                    }
                });
                this.totalPoints = true;
            },
            getActivityLeaderboard(id) {
                this.title = this.activities[id - 1].type;
                this.dburl = utils.apiUrl + "/api/v1/stats/" + id;
                axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                    this.users = res.data;
                    this.totalPoints = false;
                    for (let i = 0; i < this.users.length; i++) {
                        if (this.users[i].user.id === utils.getUser().id) {
                            this.userPosition = i+1;
                            this.userPoints = this.users[i].points;
                        }
                        else if (i === this.users.length-1){
                            this.userPosition = 0;
                            this.userPoints = 0;
                        }
                    }
                    if (this.userPosition > 10) {
                        this.lowerThan10 = true;
                    } else {
                        this.lowerThan10 = false;
                    }
                });
            },
            getUser() {
                this.dburl = utils.apiUrl + "/api/v1/users/" + utils.getUser().id;
                axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                    this.userName = res.data.firstname + " " + res.data.lastname;
                    this.userProfile = res.data;
                });
            }
        },
        computed: {
            top10Users() {
                if (this.users.length > 10) {
                    return this.users.slice(0, 10);
                } else {
                    return this.users;
                }
            }
        },
        mounted() {
            window.scrollTo(0, 0);
            this.getActivities();
            this.getTotalLeaderboard();
            this.getUser();
        },
        name: "RankingPage"
    }
</script>

<style scoped>
    #container {
        position: absolute;
        margin-top: 5.6rem;
        width: 100%;
        height: 100%;
    }
    #title {
        position: relative;
        left: 45%;
        width: 200px;
        margin-top: 10px;
    }
    #activity {
        position: relative;
        float: right;
        bottom: 50px;
        right: 50px;

    }
    #activity label {
        position: relative;
        float: left;
        padding-right: 5px;
        font-size: 1.2rem;
    }
    #activities select {
        position: relative;
        float: left;
    }
    #leaderBoard {
        position: relative;
        background-color: #3aafa9;
        margin: auto;
        height: 80%;
        width: 80%;
        border-radius: 3px;
    }
    #header {
        position: relative;
        width: 100%;
        height: 45px;
        background-color: #17252a;
        border-radius: 3px;
    }
    #positionTitle {
        position: relative;
        float: left;
        margin-left: 2rem;
        color: #DEF2F1;
        margin: 0;
        margin-top: 10px;
        padding: 0;
    }
    #nameTitle {
        position: relative;
        float: left;
        left: 39%;
        color: #DEF2F1;
        margin: 0;
        margin-top: 10px;
        padding: 0;
    }
    #pointsTitle {
        position: relative;
        float: left;
        left: 70%;
        color: #DEF2F1;
        margin: 0;
        margin-top: 10px;
        padding: 0;
    }
    #columns {
        position: relative;
        float: left;
        width: 100%;
    }
    #position {
        position: relative;
        float: left;
        font-size: 1.7rem;
        padding: 0;
    }
    #position li {
        padding: 0.3rem;
    }
    .name {
        position: relative;
        float: left;
        font-size: 1.7rem;
        left: 35%;
        padding: 0;
    }
    .name li {
        padding: 0.3rem;
    }
    .points {
        position: relative;
        float: left;
        font-size: 1.7rem;
        left: 60%;
        padding: 0;
    }
    .points li {
        padding: 0.3rem;
    }
    ul {
        list-style-type: none;
    }
    select {
        color: black;
        font-size: 1rem;
        background-color: #def2f1;
        width: 6rem;
        height: 1.5rem;
        border-radius: 5px;
        border: solid 1px #17252a;
        opacity: 0.8;
        overflow: hidden;
    }
    @media only screen and (max-width: 900px) {
        #nameTitle{
            left: 25%;
        }
        #pointsTitle{
            left: 60%;
        }
        .name{
            left: 20%;
        }
        .points{
            left: 40%;
        }
    }
    @media only screen and (max-width: 700px) {
        #positionTitle{
            font-size: 1rem;
        }
        #nameTitle{
            left: 30%;
            font-size: 1rem;
        }
        #pointsTitle{
            left: 60%;
            font-size: 1rem;
        }
        #position{
            font-size: 1.2rem;
        }
        .name{
            left: 28%;
            font-size: 1.2rem;
        }
        .points{
            left: 45%;
            font-size: 1.2rem;
        }
    }
    @media only screen and (max-width: 500px) {
        #title{
            font-size: 2rem;
            left: 20%;
        }
        #positionTitle{
            font-size: 1rem;
        }
        #nameTitle{
            left: 20%;
            font-size: 1rem;
        }
        #pointsTitle{
            left: 45%;
            font-size: 1rem;
        }
        #position{
            font-size: 0.8rem;
        }
        .name{
            left: 25%;
            font-size: 0.8rem;
        }
        .points{
            left: 40%;
            font-size: 0.8rem;
        }
        @media only screen and (max-width: 320px) {
            #title{
                font-size: 1.5rem;
                left: 10%;
            }
            #activity{
                bottom: 40px;
                right: 0px;
            }
            #positionTitle{
                font-size: 0.8rem;
            }
            #nameTitle{
                left: 20%;
                font-size: 0.8rem;
            }
            #pointsTitle{
                left: 45%;
                font-size: 0.8rem;
            }
            #position{
                font-size: 0.8rem;
            }
            .name{
                left: 25%;
                font-size: 0.8rem;
            }
            .points{
                left: 40%;
                font-size: 0.8rem;
            }
        }
        @media only screen and (max-width: 280px){
            #title{
                font-size: 1.5rem;
                left: 10%;
            }
            #activity{
                bottom: 40px;
                right: 0px;
            }
            #positionTitle{
                font-size: 0.65rem;
            }
            #nameTitle{
                left: 20%;
                font-size: 0.65rem;
            }
            #pointsTitle{
                left: 45%;
                font-size: 0.65rem;
            }
            #position{
                font-size: 0.7rem;
            }
            .name{
                left: 20%;
                font-size: 0.7rem;
            }
            .points{
                left: 30%;
                font-size: 0.7rem;
            }
        }
    }
</style>