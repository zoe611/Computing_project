<template>
  <div class="home">
    <div class="header">
      <h1>Spinal Cord Injury Search Hub</h1>
      <!--<h1>{{request.term}}</h1>-->
      <form class="search" action="">
        <input type="search" class = "ti" placeholder="Search author..." required ref="author_input">
        <input type="search" class = "ti" placeholder="Search term..." required ref="term_input">
        <button type="submit" v-on:click="search_func">
          Search
        </button>
      </form>
    </div>
    <!--    filter by date modified -->
    <div class="j-inline-search-filter">
      <label for="last-modified">Last Published:</label>
        <button>Year 2018</button>
        <button>Year 2017</button>
        <button>Year 2014</button>
        <button>Before 2014</button>
    </div>
    <!--    result list -->
    <div class="articles">
      <div class="article" v-for="(item, index) in result.result" :key="item.id">
        <h2 class="title" @click="toggleTitleDetail(index)">{{item.title}}</h2>
        <h3 class="author">{{item.author}}, %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% {{item.pdate}}</h3>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'Home',
  data () {
    return {
      api: 'http://localhost/test.php',
      request: {
        method: 'search',
        term: '',
        author_search: '',
        page: '',
        filter: '',
        sort: ''
      },
      result: {},
      res: {}
    }
  },
  mounted: function () {
    this.post_result()
  },
  methods: {
    search_func () {
      this.request.author_search = this.$refs.author_input.value
      this.request.term = this.$refs.term_input.value
      this.request.page = 'dfs'
      this.request.filter = 'dfs'
      this.request.sort = 'dfsd'
      this.$http.post(this.api, this.request)
        .then((response) => {
          console.log(response.data.term)
        })
    },
    post_result () {
      this.$http.post('http://localhost/test.php', {title: this.title, author: this.author, pdate: this.pdate},
        {emulateJSON: true}).then(function (result) {
        console.log(result.data)
        this.result = result.data
      })
    },
    toggleTitleDetail (index) {

    }
  }

}
</script>

<style scoped>
  .home {
    width:100%;
    height:800px;
    /*background-image:url("../../assets/search.jpg");*/
    background-repeat: no-repeat;
    background-size: cover;
  }
  h1{
    font-size: 2rem;
    font-weight: 900;
    text-align: center;
    margin: 0;
    text-transform: uppercase;
    padding: 0.5em;
    animation: background-move 10s infinite;
    background: url("../../assets/h1.jpeg");
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  h2{
    font-size: 15px;
    font-weight: bold;
    text-align: -webkit-left;
    margin-top:0;
    font-color:red;
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
  /* css for filter*/
/*  .j-inline-search-filter {
   margin: 0 0 15px;
 }*/
 .j-inline-search-filter {
   height: 800px;
   width: 100px;
   display: inline-block;
   margin: 70px 55px 100px 22px;
   float:left;
   font-size: 0.9em;
   font-family: Times, TimesNR, 'New Century Schoolbook', Georgia, 'New York', serif;
   font-weight: 900;
 }
 .j-inline-search-filter label {
   display: vertical-align;
 }
 .j-inline-search-filter button {
   max-width: 100px;
   float: left;
   height: 20px;
   display: vertical-align;
   margin: 3px 10px 10px 10px;
 }
  .j-inline-search-filter button:hover {
    background: lightgray;
    color:#444;}
  .j-inline-search-filter button:active {
    box-shadow: 0px 0px 12px 0px gray;}

  .j-inline-search-filter button:focus {outline: 0;}
  .search  {
    width: 700px;
    height: 40px;
    position:relative;
    margin: 1px auto;
  }

  .search input {
    width: 340px;
    height: 41px;
    padding: 10px 10px;
    margin-left: 10px;
    float: right;
    color: black;
    font-weight: bold;
    font-size: 1.1em;
/*    border: 0;
    background: transparent;*/
    border-radius: 3px 0 0 3px;
    border: 1px solid #fff;
    /*background: #444;*/
    background: rgba(0,0,0,.2);
  }

  .search input:focus {
    outline: 0;
    background:transparent;}

  .search button {
    position: relative;
    margin-top: 10px;
    float: right;
    padding: 0;
    cursor: pointer;
    height: 41px;
    width: 150px;
    /*color: #fff;*/
    color: rebeccapurple;
    font-weight: bold;
    font-size: 1.1em;
    background: rgba(0,0,0,.2);
    border: 1px solid darkgrey;
    border-radius: 0 3px 3px 0;}

  .search button:hover {
    background: #fff;
    color:#444;}
  .search button:active {
    box-shadow: 0px 0px 12px 0px rgb(213, 225, 210);}

  .search button:focus {outline: 0;}

  /*body {
    font: 12px Arial, sans-serif;
  }*/
  .articles{
    width: 1500px;
    height: auto;
    margin-top: 64px;
    margin-left:30px;
  }
  .title {
    float: none;
    font-size: 20px;
    margin: 0;
    padding: 0;
    text-align: -webkit-left;
  }
  .article{
    margin-bottom: 15px;
  }
  .author {
      font-size: 15px;
      float: none;
      margin: 0;
      padding: 0;
      text-align: -webkit-left;
  }
  /*article.post {
    border-bottom: 1px dotted rgba(180, 180,   180, 1);
    margin-top: 60px;
    margin-bottom: 20px;
    margin-left:180px;
    padding: 10px 110px 10px 110px;
    position: center;
    text-align: -webkit-left;
  &:last-of-type {
     border-bottom: 0;
     padding-bottom: 0;
   }
  &:after {
   @extend .clear;
   }

  .list time {
    display: block;
    font-size: 11px;
    font-weight: bold;
  }
  }*/
</style>
