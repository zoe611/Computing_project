<template>
  <div class="home">
    <div class="header">
      <h1>Spinal Cord Injury Search Hub</h1>
      <form class="search" action="">
        <input type="search" class = "ti" placeholder="Search author..." ref="author_input">
        <input type="search" class = "ti" placeholder="Search term..." ref="term_input">
        <button type="submit" v-on:click="search_click()">
          Search
        </button>
      </form>
    </div>
    <!--    filter by date  and sort modified -->
    <div class = "search_filter" v-if="show()">
      <div class="filters_time">
        <h3 class="filter" @click="search_filter('all')">Any time</h3>
        <h3 class="filter" @click="search_filter('>=2018')">Since 2018</h3>
        <h3 class="filter" @click="search_filter('>=2017')">Since 2017</h3>
        <h3 class="filter" @click="search_filter('>=2014')">Since 2014</h3>
        <h3 class="filter" @click="search_filter('<2014')">Before 2014</h3>
      </div>
      <div class="filters_sort" >
        <h3 class="filter" @click="search_sort('relative')">Sort by relevance</h3>
        <h3 class="filter" @click="search_sort('sort_pubdate')">Sort by date</h3>
      </div>
    </div>
    <!--    result list -->
    <div class="articles" v-show="result.result">
      <div class="article" v-for="(item, index) in result.result" :key="item.id">
        <p class="title" @click="goTo(index)">{{item.title}}</p>
        <p class="author">{{item.author}},{{item.pdate}}</p>
      </div>
    </div>
    <div class="duplicate" v-show="result.duplicate">
      <p class="dup_title">Are you searching for?</p>
      <div class="dup_form" v-for="(item, index) in result.duplicate" :key="item.id" @click="search_author_id(index)">
        <p class="name">{{item.author_name}},{{item.author_fname}}</p>
        <p class="des">{{item.des}}</p>
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
      <vue-paginate-al :totalPage="5" @btnClick="search(i)" v-if="show()"></vue-paginate-al>
    </div>
    <svg width="960" height="600"></svg>
  </div>

</template>

<script>
import * as d3 from 'd3'
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
        sort: 'relative',
        author_id: ''
      },
      result: {}
    }
  },
  mounted: function () {
    this.search_recent()
  },
  methods: {
    search (i) {
      this.result = {}
      this.request.author_search = this.$refs.author_input.value
      console.log(this.request.author_search)
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
    search_author_id (index) {
      this.request.author_id = this.result.duplicate[index].author_id
      this.request.method = 'search_author_id'
      this.search(0)
    },
    show () {
      if (this.request.method === 'search_recent' || this.result.duplicate || !this.result.result) {
        return false
      } else {
        return true
      }
    }
  }
}

var svg = d3.select('svg')
var width = +svg.attr('width')
var height = +svg.attr('height')

var color = d3.scaleOrdinal(d3.schemeCategory20)

var simulation = d3.forceSimulation()
  .force('link', d3.forceLink().id(function (d) { return d.id }))
  .force('charge', d3.forceManyBody())
  .force('center', d3.forceCenter(width / 2, height / 2))

d3.json('miserables.json', function (error, graph) {
  if (error) throw error

  var link = svg.append('g')
    .attr('class', 'links')
    .selectAll('line')
    .data(graph.links)
    .enter().append('line')
    .attr('stroke-width', function (d) { return Math.sqrt(d.value) })

  var node = svg.append('g')
    .attr('class', 'nodes')
    .selectAll('circle')
    .data(graph.nodes)
    .enter().append('circle')
    .attr('r', 5)
    .attr('fill', function (d) { return color(d.group) })
    .call(d3.drag()
      .on('start', dragstarted)
      .on('drag', dragged)
      .on('end', dragended))

  node.append('title')
    .text(function (d) { return d.id })

  simulation
    .nodes(graph.nodes)
    .on('tick', ticked)

  simulation.force('link')
    .links(graph.links)

  function ticked () {
    link
      .attr('x1', function (d) { return d.source.x })
      .attr('y1', function (d) { return d.source.y })
      .attr('x2', function (d) { return d.target.x })
      .attr('y2', function (d) { return d.target.y })

    node
      .attr('cx', function (d) { return d.x })
      .attr('cy', function (d) { return d.y })
  }
})

function dragstarted (d) {
  if (!d3.event.active) simulation.alphaTarget(0.3).restart()
  d.fx = d.x
  d.fy = d.y
}

function dragged (d) {
  d.fx = d3.event.x
  d.fy = d3.event.y
}

function dragended (d) {
  if (!d3.event.active) simulation.alphaTarget(0)
  d.fx = null
  d.fy = null
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
   height: 300px;
   width: 150px;
   margin: 60px 30px 200px 20px;
   padding:0;
   float: left;
 }
 .filters_time {
   height: 150px;
   width: 100px;
   display: inline-block;
   margin-top: 10px;
   float:left;
   font-weight: normal;
 }
 .filters_sort {
   height: 60px;
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
  .duplicate {
    margin-top: 10px;
    width:100%;
    /*float: left;*/
  }
  .dup_form {
    width: 70%;
    border-radius: 5px;
    border: 2px solid #fff;
    text-align: left;
    box-shadow: 5px 4px 11px 0px rgba(136, 136, 136, 0.61);
    margin: 30px 15%;
    cursor:pointer;
  }
  .dup_title {
    font-size: 33px;
    color: black;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
  }
  .name {
    font-size: 17px;
    color: black;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
  }
  .des {
    font-size: 14px;
    color: black;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
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
