<template>
    <input type="text" ref="autocomplete" placeholder="Skriv inn sted" id="autocomplete" required>
</template>

<script>
import { Loader } from '@googlemaps/js-api-loader';

export default{
    props:{
        placeConfig:Object,
        apiKey:String,
        defaultVal: String,
    },
    data(){
        return{
            autocomplete:null,
            autocompleteField:null
        }
    },
    async mounted(){
        const loader = new Loader({
            apiKey: this.apiKey,
            version:"weekly",
            libraries:["places"]
        });
        if(typeof window.google  === 'object' && typeof window.google.maps === 'object'){
            this.initAutocomplete();
        }else{
            loader
            .load()
            .then(() => {
                this.initAutocomplete();
            })
            .catch(e => {
                console.log(e);
            })
        }
    },
    methods:{
        initAutocomplete(){
            this.autocompleteField = this.$refs.autocomplete;
            this.autocomplete = new window.google.maps.places.Autocomplete(this.autocompleteField, this.placeConfig);
            //Add listener for when a user selects a place
            this.autocomplete.addListener('place_changed', this.onPlaceChanged);
        },
        onPlaceChanged(){
            var place = this.autocomplete.getPlace();
            //Checks if the selected place is valid
            if(!place.geometry){
                this.autocompleteField.value = "";
                this.$emit('setLocation', "");
            }else{
                this.$emit('setLocation', place);
                console.log(place)
            }
        }
    },
  watch: {
      defaultVal: function () {
        if(this.defaultVal !== undefined)
          document.getElementById("autocomplete").value = this.defaultVal;
      }
  }
}
</script>
