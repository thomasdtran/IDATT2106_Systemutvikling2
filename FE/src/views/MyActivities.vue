<template>
    <div class="searchWrapper">
        <div class="search">
            <input type="text" v-model="search" class="searchTerm" placeholder="Søk etter arrangement">
            <button type="submit" class="searchButton">
                <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
                <i class="fa fa-search"></i>
            </button>
        </div>
    </div>
    <div class="checkWrapper">
        <div class="check">
            <label class="checkBox">
                <input type="checkbox" value="lav" v-model="check">
                Lav
            </label>
            <label class="checkBox">
                <input type="checkbox" value="middels" v-model="check">
                Middels
            </label>
            <label class="checkBox">
                <input type="checkbox" value="høy" v-model="check">
                Høy
            </label>
        </div>
    </div>
    <h1 id="pageName">Mine aktiviteter</h1>
    <div v-if="activities.length" class="activity-container">
        <ul>
            <li v-for="activity in filteredList" :key="activity.id" @click="$router.push(
                {
                name: 'OneActivityPage',
                params: { id: activity.id}
                }
            )">
                {{activity.organizer.firstname}} {{activity.organizer.lastname}}
                <star-rating
                        :rating="activity.organizer.rating.ratingScore"
                        :round-start-rating="false"
                        :read-only="true"
                        :star-size="22"
                        :show-rating="false"
                ></star-rating>

                <div class="activityContainer">
                    <!-- {{activity.image}} -->
                    <div class="image">
                    </div>
                    <div class="theTitle">
                        <h3>{{ activity.title }}</h3>
                    </div>
                    <div class="description">
                        <p>"{{ activity.description }}"</p>
                    </div>
                    <!-- Endre så den blir meld deg av hvis bruker er meldt på fra start -->
                    <div class="signupbutton">
                        <button class="btn" @click="$router.push(
                {
                name: 'OneActivityPage',
                params: { id: activity.id}
                }
            )"
                        >Se mer</button>
                    </div>
                </div>
            </li>
        </ul>
    </div>
</template>

<script>
    import axios from "axios";
    import utils from "../common/utils";
    import StarRating from 'vue-star-rating'



    export default {
        name: "MyActivities",
        components: {
            StarRating
        },
        data() {
            return {
                search: '',
                allActivities: [],
                activities: [],
                check: [],
            };
        },
        mounted: function () {
            window.scrollTo(0, 0);
            axios
              .get(utils.apiUrl + "/api/v1/activities")
              .then((response) => {
                this.allActivities = response.data;
                let index = 0;
                for (let i = 0; i < this.allActivities.length; i++) {
                  if (this.allActivities[i].organizer.id === utils.getUser().id) {
                    this.activities[index] = this.allActivities[i];
                    index++;
                  }
                }
              });
        },
        computed: {
            filteredList() {
                return this.activities.filter(activity => {
                    if(this.check.length === 0) {
                        return activity.title.toLowerCase().includes(this.search.toLowerCase())
                    } else {
                        return activity.title.toLowerCase().includes(this.search.toLowerCase()) && this.check.includes(activity.intensity.toLowerCase())
                    }
                })
            },
        },
    };
</script>

<style scoped>
    * {
        color: #17252a;
    }

    li {
        align-self: center;
        justify-self: center;
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
    li:hover{
      cursor: pointer;
      opacity: 0.8;
    }
    .activityContainer {
        display: grid;
        grid-template-columns: 70px 3fr 2fr;
        grid-template-rows: 40px 1fr 40px;
    }
    .logoImg {
        max-width: 100%;
        height: auto;
    }
    .image {
        align-self: center;
        justify-self: center;
        grid-column: 1/2;
        grid-row: 2/3;
    }
    .description {
        overflow: hidden;
        text-align: center;
        justify-self: center;
        align-self: center;
        margin-left: 10px;
        margin-right: 10px;
        grid-column: 2/5;
        grid-row: 2/3;
    }

    .theTitle {
        text-align: center;
        justify-self: center;
        align-self: center;
        grid-column: 2/5;
        grid-row: 1/2;
    }
    #pageName {
        position: relative;
        text-align: center;
        font-size: 2rem;
        bottom: 55px;
    }
    .seeMoreButton {
        grid-column: 5/7;
        grid-row: 2/3;
        align-self: center;
        justify-self: center;
    }
    .intensity {
        justify-self: center;
        align-self: center;
        grid-column: 2/5;
        grid-row: 3/4;
    }
    .dateTime {
        justify-self: center;
        align-self: center;
        grid-column: 2/5;
        grid-row: 4/5;
    }
    .search {
        width: 100%;
        position: relative;
        display: flex;
    }
    .searchTerm {
        width: 100%;
        border: 3px solid #3AAFA9;
        border-right: none;
        padding: 5px;
        height: 20px;
        border-radius: 5px 0 0 5px;
        outline: none;
        color: #3AAFA9;
    }

    .searchTerm:focus{
        color: black;
    }

    .searchButton {
        width: 40px;
        height: 36px;
        border: 1px solid #3AAFA9;
        background: #3AAFA9;
        text-align: center;
        color: black;
        border-radius: 0 5px 5px 0;
        cursor: pointer;
        font-size: 20px;
    }

    .searchWrapper{
        margin: auto;
        margin-top: 6.2rem;
        width:55%;
        display: flex;
        flex-direction: row;
    }

    .newActivityButton {
        position: absolute;
        right: 3rem;
    }

    .full-text{
        color:white;
    }

    .plus{
        display: none;
    }

    .checkWrapper{
        margin:auto;
        width: 55%;
        display: flex;
        flex-direction: row;
        margin-bottom: 5rem;
        margin-top:0.75rem;
    }

    .activityWrapper {
        display: flex;
        max-width: 444px;
        flex-wrap: wrap;
    }

    .activityPreview {
        box-shadow: rgba(0, 0, 0, 0.117647) 0px 1px 6px, rgba(0, 0, 0, 0.117647) 0px 1px 4px;
        max-width: 124px;
        margin: 12px;
        transition: .15s all ease-in-out;
    }
    .activityPreview:hover {
        background-color: #2B7A78;
        cursor: pointer;
    }

    ul {
        padding-right: 3rem;
        padding-left: 3rem;
    }
    .activityBtn {
        display: block;
        background-color: #3AAFA9;
        padding: 1.5rem 2.5rem;
        color:white;
        border: 1px solid #3AAFA9;
        font-size: 14px;
        letter-spacing: 2px;
        font-weight: bold;
        cursor: pointer;
        box-shadow: 0px 7px 8px 2px rgba(0,0,0,0.12);
        margin: 0 auto;
        margin-top: 1rem;
    }

    @media only screen and (max-width: 700px) {
        .btn {
            font-size: 10px;
            width: 70px;
            height: 60px;
            letter-spacing: 0;
            padding: 0;
        }
        .searchWrapper{
            width: 90%;
        }
        .checkWrapper{
            width: 90%
        }
        ul {
            padding-right: 1rem;
            padding-left: 1rem;
        }
        .full-text{
            display: none;
        }
        .plus {
            display: inline-block;
            color:white;
        }
        .newActivityButton {
            right: 2rem;
        }
        .activityBtn {
            margin-top: 1rem;
            border-radius: 15rem;
            padding: 1rem;
        }
        li {
            padding-right: 1rem;
            padding-left: 1rem;
        }
    }

    @media only screen and (max-width: 550px) {
        .activityContainer {
            display: grid;
            grid-template-columns: 4fr 1fr;
            grid-template-rows: 30px 50px;
        }
        .image {
            grid-column: 5/6;
            grid-row: 1/2;
        }
        .description {
            font-size: 12px;
            text-align: center;
            padding-top: 10px;
            margin-left: 10px;
            margin-right: 10px;
            grid-column: 1/5;
            grid-row: 2/3;
        }
        .intensity {
            grid-column: 1/5;
            grid-row: 3/4;
        }
        .theTitle {
            text-align: center;
            grid-column: 1/4;
            grid-row: 1/2;
        }
        .btn {
            align-self: center;
        }
        .seeMoreButton {
            grid-column: 5/6;
            grid-row: 2/4;
            align-content: center;
        }
        .image {
            display: none;
        }
    }
</style>