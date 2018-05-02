<template>
  <div class="home">
    <!--     title -->
  <h1>Spinal Cord Injury Search Hub</h1>
    <!--       search form -->
    <form id="search" class="search" action="">
     <!-- <input type="search" class = "ti" placeholder="Search author..." required ref="author_input">
      <input type="search" class = "ti" placeholder="Search term..." required ref="term_input">-->
      <button type="submit" v-on:click= "show">
        Let's Search
      </button>
    </form>
    <!--       filter by search methods -->
    <!--<form action="#">
      <p>
        <input type="checkbox" id="test1" />
        <label for="test1">Term</label>
        <input type="checkbox" id="test2" checked="checked" />
        <label for="test2">Author</label>
      </p>
    </form>-->
    <!--<div class="j-inline-search-filter">
      <label for="file-types">Filter by type:</label>
      <select id="file-types">
        <option>Terms</option>
        <option>Authors</option>
      </select>
    </div>-->
    <!--       filter by date modified -->
    <!--<div class="j-inline-search-filter">-->
    <!--  <label for="last-modified">Last Modified:</label>
      <select id="last-modified">
        <option>All time</option>
        <option>1 year</option>
        <option>5 years</option>
        <option>10 years</option>
      </select>
    </div>-->
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
      window.open('http://localhost:8080/?#/search')
    }
  }
}
</script>

@import "compass/css3";
<style scoped>
  .home {
    margin-top:0;
    width:100%;
    height:800px;
    background-image:url("../../assets/new.jpg");
    background-repeat: no-repeat;
    background-size: cover;
  }
/* style for the title */
  h1{
    font-size: 3rem;
    font-weight: 900;
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
  /* Style for the filter */
  /* Base for label styling */
  /*[type="checkbox"]:not(:checked),
  [type="checkbox"]:checked {
    position: absolute;
    left: -9999px;
  }
  [type="checkbox"]:not(:checked) + label,
  [type="checkbox"]:checked + label {
    position: relative;
    padding-left: 1.95em;
    cursor: pointer;
    font-size: 0.9em;
    font-family: Times, TimesNR, 'New Century Schoolbook', Georgia, 'New York', serif;
    font-weight: 900;
  }*/

  /* checkbox aspect */
  /*[type="checkbox"]:not(:checked) + label:before,
  [type="checkbox"]:checked + label:before {
    content: '';
    position: absolute;
    left: 0; top: 0;
    width: 1.25em; height: 1.25em;
    border: 2px solid transparent;
    background: lightgrey;
    border-radius: 4px;
    box-shadow: inset 0 1px 3px rgba(0,0,0,.1);
  }
  !* checked mark aspect *!
  [type="checkbox"]:not(:checked) + label:after,
  [type="checkbox"]:checked + label:after {
    content: '✔';
    position: absolute;
    top: .2em; left: .275em;
    font-size: 1.5em;
    line-height: 0.8;
    color: black;
    transition: all .2s;
    font-family: Helvetica, Arial, sans-serif;
  }
  !* checked mark aspect changes *!
  [type="checkbox"]:not(:checked) + label:after {
    opacity: 0;
    transform: scale(0);
  }
  [type="checkbox"]:checked + label:after {
    opacity: 1;
    transform: scale(1);
  }*/
  /* disabled checkbox */
  /*[type="checkbox"]:disabled:not(:checked) + label:before,
  [type="checkbox"]:disabled:checked + label:before {
    box-shadow: none;
    border-color: transparent;
    background-color: transparent;
  }
  [type="checkbox"]:disabled:checked + label:after {
    color: transparent;
  }
  [type="checkbox"]:disabled + label {
    color: transparent;
  }
  !* accessibility *!
  [type="checkbox"]:checked:focus + label:before,
  [type="checkbox"]:not(:checked):focus + label:before {
    border: 2px dotted transparent;
  }

  !* hover style just for information *!
  label:hover:before {
    border: 2px solid transparent!important;
  }*/
  /*.j-inline-search-filter {
    margin: 0 0 15px;
  }
  .j-inline-search-filter {
    display: inline-block;
    margin: 0 20px 15px 0;
  }
  .j-form .j-inline-search-filter label {
    display: inline-block;
  }
  .j-form .j-inline-search-filter select {
    max-width: 200px;
  }*/

  .search {
    width: 700px;
    height: 40px;
    position:relative;
    margin: 1px auto;
    margin-top: 220px; }

  /*.search input {
    width: 340px;
    height: 41px;
    padding: 10px 10px;
    margin-left: 10px;
    float: right;
    color: black;
    font-weight: bold;
    font-size: 1.1em;
    !*    border: 0;
        background: transparent;*!
    border-radius: 3px 0 0 3px;
    border: 1.3px solid #fff;
    !*background: #444;*!
    background: rgba(0,0,0,.2);}

  .search input:focus {
    outline: 0;
    background:transparent;}*/

  .search button {
    position: relative;
    margin-top: 10px;
    border: 0;
    padding: 0;
    cursor: pointer;
    height: 60px;
    width: 300px;
    background: transparent;
    font-size: 1.5em;
    font-weight: bold;
    color:rgb(240,250,255);
    border: 2.5px solid rgb(249,249,249);
    border-radius: 0 3px 3px 0;}

  .search button:hover {
    background: #fff;
    color:#444;}
  .search button:active {
    box-shadow: 0px 0px 12px 0px rgba(225, 225, 225, 1);}

  .search button:focus {outline: 0;}
</style>
