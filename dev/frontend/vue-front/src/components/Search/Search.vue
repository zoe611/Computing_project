<template>
  <div class="home">
    <h1>Spinal Cord Injury Search Hub</h1>
      <h1>{{request.term}}</h1>
    <form class="search" action="">
      <input type="search" class = "ti" placeholder="Search author..." required ref="author_input">
      <input type="search" class = "ti" placeholder="Search term..." required ref="term_input">
      <button type="submit" v-on:click="search_func">
        Search
      </button>
    </form>
       <!--    filter by date modified -->
    <div class="j-inline-search-filter">
      <label for="last-modified">Last Published:</label>
        <button>Year 2018</button>
        <button>Year 2017</button>
        <button>Year 2014</button>
        <button>Before 2014</button>
    </div>
    <!--    result list -->
      <div class = "list">
        <article class="post">
        <h2 id="result"><a href="#">Article title</a></h2>
        <p id="author">author</p>
        <time id="time" datetime="2012-01-01">01/01/2012</time>
        <p id="abstract">Donec id elit non mi porta gravida at  </p>
        <!--<a href="#" class="cta">Article link</a>-->
        </article>
        <article class="post">
          <h2><a href="#">Article title</a></h2>
          <time datetime="2012-01-01">01/01/2012</time>
          <p>onsectetur adipiscing elit. Maecenas faucibus mollis interdum. </p>
          <a href="#" class="cta">Article link</a>
        </article>
        <article class="post">
          <h2><a href="#">Article title</a></h2>
          <time datetime="2012-01-01">01/01/2012</time>
          <p>onsectetur adipiscing elit. Maecenas faucibus mollis interdum. </p>
          <!--<a href="#" class="cta">Article link</a>-->
        </article>
        <article class="post">
          <h2><a href="#">Article title</a></h2>
          <time datetime="2012-01-01">01/01/2012</time>
          <p>onsectetur adipiscing elit. Maecenas faucibus mollis interdum. </p>
          <a href="#" class="cta">Article link</a>
        </article>
        <article class="post">
          <h2><a href="#">Article title</a></h2>
          <time datetime="2012-01-01">01/01/2012</time>
          <p>onsectetur adipiscing elit. Maecenas faucibus mollis interdum. </p>
          <a href="#" class="cta">Article link</a>
        </article>
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
      res: {}
    }
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
          this.set('res', response.data)
          this.request.term = response.response.term
        })
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
    margin:0;
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
   width: 100px;
   display: inline-block;
   margin: 70px 10px 100px 22px;
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
  .list{
    width: 620px;
    height: 400px;
    margin: 0 px;
  }
  article.post {
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
  }
</style>
