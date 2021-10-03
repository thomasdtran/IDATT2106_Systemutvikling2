<template>
  <div id="map"></div>
</template>

<script>
import { Loader } from '@googlemaps/js-api-loader';
export default {
  name: "GMap",
  props: {
    center: { lat: Number, lng: Number },
    zoom: Number,
    mapType: String,
    disableUI: Boolean
  },
  async mounted(){
    let a = this;
    const loader = new Loader({
        apiKey: process.env.VUE_APP_GOOGLEMAP_KEY,
        version: "weekly",
        libraries:["places"]    
    });
    loader.load().then(() => {
        new window.google.maps.Map(document.getElementById("map"), {
            center: { lat: a.center.lat, lng: a.center.lng },
            zoom: 15,
        });
    });
  }
};
</script>

<style lang="css" scoped>
.map {
  width: 100%;
  height: 100%;
  background-color: azure;
}
</style>