<template>
  <div class="home">
    <!--       title -->
  <h1>Spinal Cord Injury Search Hub</h1>
    <!--       search form -->
    <form class="search" action="">
      <input type="search" placeholder="Search here..." required>
      <button type="submit" href = "http://w3school.com.cn">
        Search
      </button>
    </form>
    <!--       filter by search methods -->
    <div class="j-inline-search-filter">
      <label for="file-types">Filter by type:</label>
      <select id="file-types">
        <option>Terms</option>
        <option>Authors</option>
      </select>
    </div>
    <!--       filter by date modified -->
    <div class="j-inline-search-filter">
      <label for="last-modified">Last Modified:</label>
      <select id="last-modified">
        <option>All time</option>
        <option>1 year</option>
        <option>5 years</option>
        <option>10 years</option>
      </select>
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
  }
}
</script>

@import "compass/css3";
<style scoped>
  .home {
    width:100%;
    height:800px;
    background-image:url("../../assets/images.png");
    background-repeat: no-repeat;
    background-size: cover;
  }
/* style for the title */
  h1{
    font-size: 3rem;
    font-weight: 900;
    text-align: center;
    /*margin: 4rem;*/
    text-transform: uppercase;
    padding: 2em;
    animation: background-move 10s infinite;
    background: url(https://blog.spoongraphics.co.uk/wp-content/uploads/2012/retro-pattern/triangle-pattern.jpg);
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
  .j-inline-search-filter {
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
  }

  .search {
    width: 500px;
    height: 40px;
    margin: 150px auto;
    background: #444;
    background: rgba(0,0,0,.2);
    border-radius:  3px;
    border: 1px solid #fff;}

  .search input {
    width: 370px;
    height: 41px;
    padding: 10px 10px;
    float: center;
    color: #ccc;
    border: 0;
    background: transparent;
    border-radius: 3px 0 0 3px;}

  .search input:focus {
    outline: 0;
    background:transparent;}

  .search button {
    position: relative;
    float: right;
    border: 0;
    padding: 0;
    cursor: pointer;
    height: 41px;
    width: 120px;
    color: #fff;
    background: transparent;
    border-left: 1px solid #fff;
    border-radius: 0 3px 3px 0;}

  .search button:hover {
    background: #fff;
    color:#444;}
  .search button:active {
    box-shadow: 0px 0px 12px 0px rgba(225, 225, 225, 1);}

  .search button:focus {outline: 0;}
</style>
