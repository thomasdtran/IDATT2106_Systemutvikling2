<template>
  <div class="calendar-wrapper">
    <div class="header">
      <div class="backward" @click.prevent="buildCalendar('backward')">
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
        <i class="fa fa-arrow-left" aria-hidden="true"></i>
      </div>
      <h1>{{formattedMonth}}</h1>
      <div class="forward" @click.prevent="buildCalendar('forward')">
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
        <i class="fa fa-arrow-right" aria-hidden="true"></i>
      </div>
    </div>
    <div class="weekday">
      <div class="weekday-cell" v-for="day in days" :key="day">
        {{ day }}
      </div>
    </div>
    <div class="calendar">
      <div class="calendar-row" v-for="x in 6" :key="x">
        <div class="calendar-cell" v-for="x in 7" :key="x">
          <p class="date"></p>
          <div class="infos">
          </div>  
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment';
import 'moment/locale/nb';
import axios from "axios";
import utils from "../common/utils";

export default {
  name: 'Calendar',
  props: [
    'leftArrow',
    'rightArrow'
  ],
  mounted () {
    window.scrollTo(0, 0);
    this.buildCalendar();
  },
  data () {
    return {
      days: [
        'Mandag',
        'Tirsdag',
        'Onsdag',
        'Torsdag',
        'Fredag',
        'Lørdag',
        'Søndag'
      ],
      month: moment(),
      activities: [],
      formattedMonth: moment().format('MMMM YYYY')
    }
  },
  computed: {
  },
  methods: {
    buildCalendar (go = '') {
        moment().locale('nb');
      const month = go === 'forward'
        ? this.getNextMonth()
        : go === 'backward'
          ? this.getPreviousMonth()
          : this.month.get('month')
      const daysInMonth = this.getDaysInMonth(month)
      const monthStartDay = this.getMonthStartDay(month)
      const cells = this.$el.querySelectorAll('.calendar-cell')
      let count = 1
      for (const cell of cells) {
        cell.classList.remove('disabled')
        cell.classList.remove('today')
        cell.setAttribute('data-date', '')
        cell.querySelector('.infos').innerHTML = ''
        cell.querySelector('.date').innerHTML = ''
      }
      axios
          .get(utils.apiUrl + "/api/v1/activities")
          .then((response) => {
              this.activities = response.data
        for (const cell of cells) {
          if (count < monthStartDay || count >= (daysInMonth + monthStartDay)) {
            cell.classList.add('disabled')
          } else if ((count - monthStartDay + 1) === moment().get('date') && this.month.get('month') === moment().get('month')) {
            cell.querySelector('.date').innerHTML = count - monthStartDay + 1
            const today = moment().year(this.month.get('year')).month(this.month.get('month')).date(count - monthStartDay + 1)
            cell.setAttribute('data-date', today.format('YYYY-MM-DD'))
                for(const activity of this.activities) {
                    for(const participant of activity.participants) {
                        if (participant.user.id === utils.getUser().id) {
                              var date = activity.date
                              const myMomentObject = moment(date, 'YYYY-MM-DD')
                              if(moment(today.format('YYYY-MM-DD')).isSame(myMomentObject)){
                                  var newDiv = document.createElement("div");
                                  newDiv.className = "info";
                                  newDiv.style = "margin: 6px; text-align: center; text-justify: center; align-self: center; padding: 5px; background-color: #3aafa9; border-radius: 20px; height: 1rem; overflow: hidden;";
                                  newDiv.onclick = () => {
                                      this.$router.push({ 
                                          name: 'OneActivityPage',
                                          params: { id: activity.id}
                                      })
                                  };
                                  var text = document.createTextNode(activity.title);
                                  newDiv.appendChild(text);
                                  cell.querySelector('.infos').appendChild(newDiv);
                                  console.log(cell);
                              }
                          }
                      }
                  }
            cell.classList.add('today')
          } else {
            cell.querySelector('.date').innerHTML = count - monthStartDay + 1
            const today = moment().year(this.month.get('year')).month(this.month.get('month')).date(count - monthStartDay + 1)
            cell.setAttribute('data-date', today.format('YYYY-MM-DD'))
                for(const activity of this.activities) {
                  for(const participant of activity.participants) {
                        if (participant.user.id === utils.getUser().id) {
                              var date = activity.date
                              const myMomentObject = moment(date, 'YYYY-MM-DD')
                              if(moment(today.format('YYYY-MM-DD')).isSame(myMomentObject)){
                                  var newDiv = document.createElement("div");
                                  newDiv.className = "info";
                                  newDiv.style = "margin: 6px; text-align: center; text-justify: center; align-self: center; padding: 5px; background-color: #3aafa9; border-radius: 20px; height: 1rem; overflow: hidden;";
                                  newDiv.onclick = () => {
                                      this.$router.push({ 
                                          name: 'OneActivityPage',
                                          params: { id: activity.id}
                                      })
                                  };
                                  var text = document.createTextNode(activity.title);
                                  newDiv.appendChild(text);
                                  cell.querySelector('.infos').appendChild(newDiv);
                                  console.log(cell);
                              }
                          }
                      }
              }
          }
          count++
        }
      });
      this.formattedMonth = this.month.format('MMMM YYYY')
    },
    getDaysInMonth (month) {
      return moment().month(month).daysInMonth()
    },
    getMonthStartDay (month) {
      return moment().month(month).startOf('month').get('isoWeekday')
    },
    getNextMonth () {
      const month = this.month.add(1, 'months')
      this.month = month
      return month.get('month')
    },
    getPreviousMonth () {
      this.month = this.month.subtract(1, 'months')
      return this.month.get('month')
    },
  }
}
</script>
<style scoped>

.calendar-wrapper {
    margin-top: 4.55rem;
    padding-left: 3rem;
    padding-right: 3rem;
}
.weekday {
    display: flex;
    border: 2px solid #3aafa9;;
    padding: 5px 0 0;
    background: #fff;
}
.weekday-cell {
    text-align: center;
    width: 14.285714%;
    margin-bottom: 5px;
    padding-left: 5px;
}
.calendar {
    border: 1px solid darken(#fff, 20%);
    justify-content: center;
    align-content: center;
}
.calendar-row {
    display: flex;
    border-bottom: 1px solid darken(#fff, 20%);
}
.calendar-cell {
    height: 120px;
    width: 14.285714%;
    background: #fff;
    box-sizing: border-box;
    cursor: pointer;
    border: 1px solid #3aafa9;
}
.calendar-cell:hover {
    background: #def2f1;
}
.calendar-cell.disabled {
    background:  #f0f0f0
}
.calendar-cell.today p{
    background: #b1e0df;
    border-radius: 20px;

}
.date {
    padding: 5px;
    position: absolute;
    margin: 2px;
}

.infos{
    margin-top: 35px;
    justify-self: center;
    align-self: center;
}
.header {
    display: flex;
    justify-content: center;
    align-content: center;
    background: #fff;
    box-sizing: border-box;
}
h1 {
    text-align: center;
    color: #3aafa9;
    
}
.backward {
    cursor: pointer;
    margin: auto 20px;
    user-select: none;
}
.forward {
    cursor: pointer;
    margin: auto 20px;
    user-select: none;
}
img {
    height: 50px;
    transition: all 0.3s cubic-bezier(.25,.8,.25,1);
}

@media only screen and (max-width: 700px) {
.calendar-wrapper {
    padding-left: 0;
    padding-right: 0;
}

.calendar-cell {
    height: 94px;
}
.infos {
    margin-top: 32px;
}
}
</style>