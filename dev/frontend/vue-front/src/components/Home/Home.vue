<template>
  <div class="home">
    <!--     title -->
  <h1>Spinal Cord Injury Search Hub</h1>
    <div class="buttons">
      <button type="submit"
              class="button"
              id="above"
              v-on:click="show">
        Let's
        Search
      </button>
      <button type="submit"
              class="button"
              v-on:click="see">
        Visualize
        All
      </button>
    </div>

  </div>
</template>

<script>

export default {
  name: 'Home',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App'
    }
  },
  methods: {
    show: function () {
      window.open('http://43.240.98.120:8080/?#/search')
    },
    see: function () {
      window.open('http://43.240.98.120:8080/?#/profile')
    }
  }
}
</script>

@import "compass/css3";
<style scoped>
  .home {
    margin-top:0;
    width:100%;
    height: 100%;
    background-image:url("../../assets/new.jpg");
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center center;
    background-attachment: fixed;
  }
/* style for the title */
  h1{
    font-size: 5rem;
    font-weight: 900;
    margin-top: 0;
    text-align: center;
    text-transform: uppercase;
    padding: 2em;
    animation: background-move 10s infinite;
    background: url("../../assets/h1new.jpg");
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  @keyframes background-move {
    0% {
      background-position: 0% 0%;
    }

    25% {
      background-position: 5% 10%;
    }

    50% {
      background-position: 15% 15%;
    }

    75% {
      background-position: 10% 5%;
    }

    100% {
      background-position: 0% 0%;
    }
  }

  @font-face {
    font-family: 'Open Sans';
    font-style: normal;
    font-weight: 400;
    src: local('Open Sans'), local('OpenSans'), url(http://themes.googleusercontent.com/static/fonts/opensans/v7/cJZKeOuBrn4kERxqtaUH3bO3LdcAZYWl9Si6vvxL-qU.woff) format('woff');
  }
  .buttons {
    position: absolute;
    top: 16px;
    left: 0;
    right: 0;
    bottom: 0;
    height: 140px;
    width: 300px;
    margin: auto;
  }
  .button {
    display: block;
    margin: 10px auto;
    padding: 0;
    cursor: pointer;
    height: 60px;
    width: 300px;
    background: transparent;
    font-size: 2.0em;
    font-weight: bold;
    color:#9FD2EE;
    border: 2.5px solid rgb(249,249,249);
    border-radius: 3px;
  }
  .button:hover {
    background: #fff;
    color:#444;}
  .button:active {
    box-shadow: 0px 0px 12px 0px rgba(225, 225, 225, 1);}

  .button:focus {outline: 0;}
</style>
