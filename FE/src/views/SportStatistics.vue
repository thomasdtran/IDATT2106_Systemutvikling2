<template>
<div class="wrap">
    <div class="description">
        <h3 id="name">Navn</h3>
        <h3 id="totalPoints">Total poeng</h3>
        <h3 id="totalRanking">Total plassering</h3>
    </div>
    <div class="user">
        <ul>
            <li class="userInfo">
                <h1 class="name">{{username}}</h1>
                <h1 class="totalPoints">{{totalPoints}}</h1>
                <h1 class="totalrank">#{{totalRanking}}</h1>
            </li>
        </ul>
    </div>
    <div class="stats" v-if="sportStatsAvailable" >
        <div id="description">
            <h4 id="actvity">Aktivitet</h4>
            <h4 id="points">Poeng</h4>
            <h4 id=ranking>Plassering</h4>
        </div>
        <div v-for="(sportStat, index) in sportStats" :key="sportStat">
        <ul>
            <li class="sportPreview">
                <p class="type">{{sportStat.sport.type}}</p>
                <p class="points">{{sportStat.points}}</p>
                <p class="ranking">#{{ranks[index]}}</p>
            </li>
        </ul>
        </div>
    </div>
    <div v-else>
        <ul> 
            <li>
                <p class="noActivities">
                Delta på eller arrangere arkitivteter for å få poeng i de forskjellige aktivitetene</p>
            </li>
        </ul>
    </div>
</div>
</template>

<script>
import axios from "axios";
import utils from "../common/utils";

export default {
      name: "UserStatistics",
  data() {
    return {
      username: '',
      totalPoints: 0,
      totalRanking: 0,
      sportStats: [],
      sportStatsAvailable: true,
      sports: [],
      sportStatsById: [],
      ranks: []
    };
  },
    mounted: function () {  
        this.getUserAndTotalPoints();
        this.getTotaltRanking();
        this.getSportStats();
        this.getRankingInSports();
  },
  computed: {

    },
    methods: {
        getUserAndTotalPoints () {
            this.dburl = utils.apiUrl + "/api/v1/users/" + utils.getUser().id;
            axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                this.username = res.data.firstname + " " + res.data.lastname;
            });
        },

        getTotaltRanking() {
            this.dburl = utils.apiUrl + "/api/v1/users";
            axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                const users = res.data;
                this.dburl = utils.apiUrl + "/api/v1/users/" + utils.getUser().id;
                axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                    this.totalPoints = res.data.totalPoints;
                    for(var i = 0; i < users.length; i++) {
                    if(users[i][2] == this.totalPoints) {
                        this.totalRanking = i + 1;
                        break;
                    }
                }
                });
            });
        },

        getSportStats () {
            this.dburl = utils.apiUrl + "/api/v1/stats/users/" + utils.getUser().id;
            axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                this.sportStats = res.data;
                if(this.sportStats.length === 0) {
                    this.sportStatsAvailable = false;
                }
            });
        },

        getRankingInSports () {
            this.dburl = utils.apiUrl + "/api/v1/sports";
            axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                this.sports = res.data;
                for(var i = 0; i < this.sports.length; i++) {
                    this.dburl = utils.apiUrl + "/api/v1/stats/" + this.sports[i].id;
                    axios.get(this.dburl, utils.getConfigWithHeader()).then((res) => {
                        this.sportStatsById = res.data;
                        for(var j = 0; j < this.sportStatsById.length; j++) {
                            let sportUser = this.sportStatsById[j].user.id;
                                if(sportUser === utils.getUser().id){
                                    this.ranks.push(j + 1);
                                }
                        }
                    });
                }
            });   
        }
    }
}
</script>

<style scoped>
.wrap {
    margin-top: 6rem;
}

.description{
    display: grid;
    grid-template-columns: auto auto auto;
    padding-right: 3rem;
    padding-left: 3rem;
}

#name {
    grid-column: 1/2;
}

#totalPoints {
    grid-column: 2/3;
    text-align: center;
}

#totalRanking {
    grid-column: 3/4;
    text-align: right;
}

.userInfo {
    display: grid;
    grid-template-columns: auto auto auto;
    padding-right: 3rem;
    padding-left: 3rem;
    background: #3aafa9;
}

.name {
    grid-column: 1/2;
}

.totalPoints {
    grid-column: 2/3;
    padding-left: 10px;
}

.totalrank {
    grid-column: 3/4;
    text-align: right;
}

#description {
    display: grid;
    grid-template-columns: 20rem auto auto;
    padding-right: 3rem;
    padding-left: 4rem;
}

#activity {
    grid-column: 1/2;
    text-align: left;
}

#points {
    grid-column: 2/3;
    text-align: center;
}

#ranking {
    grid-column: 3/4;
    text-align: right;
}

.sportPreview {
    display: grid;
    grid-template-columns: 20rem auto auto;
}

.type {
    grid-column: 1/2;
    text-align: left;
}

.points {
    grid-column: 2/3;
    text-align: center;
}

.ranking {
    grid-column: 3/4;
    text-align: right;
}

.noActivities {
    text-align: center;
}

ul {
    padding-right: 3rem;
    padding-left: 3rem;
}

li {
    margin-bottom: 20px;
    background: #def2f1;
    border: 1px solid rgba(74, 74, 74, 0.075);
    box-shadow: 0 1px 1px 0 rgba(0, 0, 0, 0.2), 0 1px 1px 0 rgba(0, 0, 0, 0.19);
    border-radius: 10px;
    color: #17252a;
    font-size: 18px;
    list-style-type: none;
    padding-top: 1rem;
    padding-bottom: 1rem;
    padding-right: 2rem;
    padding-left: 2rem;  
}

@media only screen and (max-width: 700px) {
    .userInfo {
        column-gap: 10px;
    }
    .name {
        overflow: hidden;
    }

    #totalPoints {
        visibility: hidden;
    }

    .totalPoints {
        visibility: hidden;
    }

    #description {
        grid-template-columns: 110px auto auto;
        padding-left: 4rem;
    }

    .sportPreview {
        grid-template-columns: 110px auto auto;
    }

    .type {
        overflow: hidden;
    }
}
</style>