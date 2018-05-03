<template>
  <div class="home">
    <div class="header">
      <h1>Spinal Cord Injury Search Hub</h1>
      <form class="search" action="">
        <input type="search" class = "ti" placeholder="Search author..." required ref="author_input">
        <input type="search" class = "ti" placeholder="Search term..." required ref="term_input">
        <button type="submit" v-on:click="search_click()">
          Search
        </button>
      </form>
    </div>
    <!--    filter by date  and sort modified -->
    <div class = "search_filter">
      <div class="filters_time" v-if="show()">
        <h3 class="filter" @click="search_filter('all')">Any time</h3>
        <h3 class="filter" @click="search_filter('=2018')">Since 2018</h3>
        <h3 class="filter" @click="search_filter('=2017')">Since 2017</h3>
        <h3 class="filter" @click="search_filter('=2014')">Since 2014</h3>
        <h3 class="filter" @click="search_filter('<2014')">Before 2014</h3>
      </div>
      <div class="filters_sort" v-if="show()">
        <h3 class="filter" @click="search_sort('relative')">Sort by relevance</h3>
        <h3 class="filter" @click="search_sort('sort_pubdate')">Sort by date</h3>
      </div>
    </div>
    <!--    result list -->
    <div class="articles">
      <div class="article" v-for="(item, index) in result.result" :key="item.id">
        <h2 class="title" @click="goTo(index)">{{item.title}}</h2>
        <h3 class="author">{{item.author}},{{item.pdate}}</h3>
      </div>
    </div>
    <div>
      <!--<paginate
              :page-count="20"
              :click-handler="search"
              :prev-text="'Prev'"
              :next-text="'Next'"
              :container-class="'className'">
      </paginate>-->
      <vue-paginate-al :totalPage="5" @btnClick="search(i)"></vue-paginate-al>
    </div>
  </div>

</template>

<!--<script src="https://d3js.org/d3.v4.min.js"></script>-->
<script>
import VuePaginateAl from 'vue-paginate-al'
export default {
  name: 'Home',
  components: {
    VuePaginateAl
  },
  data () {
    return {
      api: 'http://43.240.98.137/test2.php',
      request: {
        method: '',
        term: '',
        author_search: '',
        page: '',
        filter: 'all',
        sort: 'relative'
      },
      result: {}
    }
  },
  mounted: function () {
    this.search_recent()
  },
  methods: {
    search (i) {
      this.request.author_search = this.$refs.author_input.value
      this.request.term = this.$refs.term_input.value
      this.request.page = i
      this.$http.post(this.api, this.request)
        .then((response) => {
          console.log(response)
          if (response.bodyText !== 'empty search') {
            this.result = response.data
          }
          console.log(this.result.result)
        })
    },
    goTo (index) {
      var id = this.result.result[index].id
      window.open('https://www.ncbi.nlm.nih.gov/pubmed/' + id)
    },
    search_click () {
      this.request.filter = 'all'
      this.request.sort = 'relative'
      this.request.method = 'search'
      this.search(0)
    },
    search_recent () {
      this.request.method = 'search_recent'
      this.search(0)
    },
    search_filter (filter) {
      this.request.filter = filter
      this.search(0)
    },
    search_sort (sort) {
      this.request.sort = sort
      this.search(0)
    },
    show () {
      if (this.request.method === 'search_recent') {
        return false
      } else {
        return true
      }
    }
  }
}
</script>

<style scoped>
  .home {
    width:100%;
    height:800px;
    background-repeat: no-repeat;
    background-size: cover;
  }
  .header {
    margin-top:0;
    width:100%;
    height:180px;
    background-image:url("../../assets/searchnew.jpg");
    background-repeat: no-repeat;
    background-size: cover;
    border-radius: 0px 0px 70px 70px;
  }
  h1{
    font-size: 3rem;
    font-weight: 900;
    text-align: center;
    margin: 0;
    text-transform: uppercase;
    padding: 0.5em;
    animation: background-move 10s infinite;
    background: url("../../assets/images-13.jpeg");
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
  /* css for filter*/
 .search_filter {
   height: 260px;
   width: 150px;
   margin: 60px 30px 200px 20px;
   padding:0;
 }
 .filters_time {
   height: 100px;
   width: 100px;
   display: inline-block;
   margin-top: 10px;
   float:left;
   font-weight: normal;
 }
 .filters_sort {
   height: 100px;
   width: 150px;
   display: inline-block;
   margin-top: 10px;
   float:left;
   font-weight: normal;
  }
  .filter:hover {
    background: transparent;
    color:#EE6352;}
  .filter {
    width: 150px;
    float: left;
    height: 10px;
    display: vertical-align;
    margin-top: 6px;
    text-align: left;
    cursor:pointer;
    font-size: 1.2em;
    font-family: Times, TimesNR, 'New Century Schoolbook', Georgia, 'New York', serif;
  }
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
    color: #14C7FF;
    font-weight: bolder;
    font-size: 1.2em;
    border-radius: 5px;
    border: 2px solid #fff;
    background: rgba(0, 0, 0, 0.45);
  }
  ::placeholder {
    color: #D0CED2;
    opacity: 1; /* Firefox */
  }

  :-ms-input-placeholder { /* Internet Explorer 10-11 */
    color: #D0CED2;
  }

  ::-ms-input-placeholder { /* Microsoft Edge */
    color: #D0CED2;
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
    color: #14C7FF;
    font-weight: bold;
    font-size: 1.1em;
    background: rgba(0, 0, 0, 0.45);
    border: 2px solid rgb(249,249,249);
    border-radius:5px;}

  .search button:hover {
    background: #fff;
    color:#444;}
  .search button:active {
    box-shadow: 0px 0px 12px 0px rgb(213, 225, 210);}

  .search button:focus {outline: 0;}

  .articles{
    width: 1200px;
    height: auto;
    margin-top: 64px;
    margin-left:30px;
  }
  .title {
    float: none;
    width: 980px;
    font-size: 20px;
    color: black;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    margin: 0;
    padding: 0;
    text-align: -webkit-left;
   /* overflow: hidden;
    text-overflow:ellipsis;
    white-space: nowrap;*/
    cursor:pointer;
  }
  .title:hover{
    background: transparent;
    text-decoration:underline;color: #0074D9;}
  .article{
    width: 980px;
    margin-bottom: 15px;
  }
  .author {
      font-size: 15px;
      color: black;
      font-family: "Times New Roman", Times, serif;
      float: none;
      margin: 0;
      padding: 0;
      text-align: -webkit-left;
  }
  .links line {
    stroke: #999;
    stroke-opacity: 0.6;
  }

  .nodes circle {
    stroke: #fff;
    stroke-width: 1.5px;
  }

</style>
