import { createRouter, createWebHistory } from "vue-router";

import ActivityDisplay from "../views/ActivityDisplay";
import Login from "../views/Login";
import RegisterUser from "../views/RegisterUser";
import CreateActivity from "../views/CreateActivity";
import OneActivityPage from "../views/OneActivityPage";
import ProfilePage from "../views/ProfilePage";
import RecoverPassword from "../views/RecoverPassword";
import RankingPage from "../views/RankingPage";
import Calendar from "../views/Calendar";
import SignedOnActivities from "../views/SignedOnActivities";
import MyActivities from "../views/MyActivities";
import SportStatistics from "../views/SportStatistics";

const routes = [
  {
    path: "/",
    name: "Home",
    component: ActivityDisplay,
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/register",
    name: "RegisterUser",
    component: RegisterUser,
  },
  {
    path: "/profile",
    name: "Profile",
    component: ProfilePage,
  },
  {
    path: "/create-activity",
    name: "CreateActivity",
    component: CreateActivity,
  },
  {
    path: "/activity/:id",
    name: "OneActivityPage",
    component: OneActivityPage,
    props: true,
  },
  {
    path: "/recover",
    name: "RecoverPassword",
    component: RecoverPassword,
  },
  {
    path: "/ranking",
    name: "RankingPage",
    component: RankingPage,
  },
  {
    path: "/calendar",
    name: "Calendar",
    component: Calendar,
  },
  {
    path: "/signed-on",
    name: "SignedOn",
    component: SignedOnActivities
  },
  {
    path: "/my-activities",
    name: "MyActivities",
    component: MyActivities
  },
  {
    path: "/sport-statistics",
    name: "SportStatistics",
    component: SportStatistics
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
