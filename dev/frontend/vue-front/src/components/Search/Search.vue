<template>
  <div class="content">
    <div class="header">
      <h1>Spinal Cord Injury Search Hub</h1>
      <form class="search" action="">
        <input type="search" class = "search_input_author" placeholder="Search author..." ref="author_input" maxlength="80">
        <input type="search" class = "search_input_term" placeholder="Search keywords..." ref="term_input" maxlength="80">
        <button type="submit" v-on:click="search_click()">
          Search
        </button>
      </form>
    </div>
    <div class="line" v-if="result.line">
      <p class="bar_title">The Number of Publications Each Year</p>
      <d3-line :data="result.line" :options="{
    // line config
    stroke : '#ABC8E2',
    strokeWidth : 2,

    // axis config
    axisXHeight : 25,
    axisYWidth : 35,
    axisFontSize : 14,

    // circle config
    circleRadius : 5,
    circleColor : '#2F60A1',

    // tooltip config
    circleTitle : d  => d.value,

    // curve config
    curve : 'curveCardinal',

    // axis label config
    axisXLabel : '',
    axisYLabel : '',
    axisXLabelHeight : 30,
    axisYLabelWidth : 20,
    axisLabelFontSize : 12,
    axisLabelFontWeight : 400,
    axisLabelFontOpacity : 0.5
}" width="100%" height="400px" :margin="{
    left: 130,
    top: 20,
    right: 150,
    bottom: 20
}"></d3-line>
    </div>
    <div class="visual" v-if="request.method === 'search' && result.result && result.result !== 'no result'">
      <div class="bar_chart" v-if="result.bar">
        <p class="bar_title">The Number of Publications Each Year</p>
        <d3-bar :data="result.bar" :options="{
    // bar config
    fill : '#ABC8E2',
    stroke : '#ABC8E2',
    fillOpacity : 1,
    strokeOpacity : 1,

    // axis font config
    axisFontSize : 12,
    axisFontWeight : 400,

    // axis label config
    axisYLabel : 'Number of publications',
    axisXLabel : 'Year',
    axisXLabelHeight : 30,
    axisYLabelWidth : 30,

    // axis label font config
    axisLabelFontSize : 10,
    axisLabelFontWeight : 350,
    axisLabelFontOpacity : 0.5,

    // axis lane config
    axisXHeight : 25,
    axisYWidth : 35,

    isVertical : false,
}" width="100%" height="400px" :margin="{
    left: 0,
    top: 10,
    right: 0,
    bottom: 10
}"></d3-bar>
      </div>
      <div class="rank" v-if="result.rank">
        <p class="rank_title">Author Rank for {{request.term}}</p>
        <div class="rank-bar" v-for="(item,index) in result.rank" :key="item.id">
          <p class="rank_name">{{item.name}}</p>
          <p class="rank_value">{{item.value}}<span class="unit"> articles</span></p>
          <div class="bar" v-bind:style="{background: rank_color[index], width: (item.value/rank_max)*100 + '%'}"></div>
        </div>
      </div>
    </div>
    <!--    filter by date  and sort modified -->
    <!--<div class = "search_filter" v-if="show()">-->
    <div style="height:10px;width:100%;"></div>
    <div class = "search_filter" v-if="show()">
      <div class="filters_time">
        <h3 :class="request.filter === 'all' ? 'active_filter' : 'filter'" @click="search_filter('all')">Any time</h3>
        <h3 :class="request.filter === '>=2018' ? 'active_filter': 'filter'" @click="search_filter('>=2018')">Since 2018</h3>
        <h3 :class="request.filter === '>=2016' ? 'active_filter': 'filter'" @click="search_filter('>=2016')">Since 2016</h3>
        <h3 :class="request.filter === '>=2012' ? 'active_filter': 'filter'" @click="search_filter('>=2012')">Since 2012</h3>
        <h3 :class="request.filter === '<2012' ? 'active_filter': 'filter'" @click="search_filter('<2012')">Before 2012</h3>
        <h3 class="filter" @click="clickFunc()">Custom range</h3>
        <div class="customTime" v-if="isCustom">
          <input type="search" class = "custom" placeholder="start" ref="startTime" maxlength="4" required>
          <input type="search" class = "custom" placeholder="end" ref="endTime" maxlength="4" required>
          <button type="submit" class="click" v-on:click="search_filter_custom()">
            Search
          </button>
        </div>
      </div>
      <div class="filters_sort" >
        <h3 :class="request.sort === 'relative' ? 'active_filter': 'filter'" @click="search_sort('relative')">Sort by relevance</h3>
        <h3 :class="request.sort === 'sort_pubdate' ? 'active_filter': 'filter'" @click="search_sort('sort_pubdate')">Sort by date</h3>
      </div>
    </div>
    <!--    result list -->
    <p class="result_title_recent" v-if="request.method === 'search_recent' && result.result">
      Here are 10 recent articles
    </p>
    <div class="articles_recent" v-if="request.method === 'search_recent' && result.result ">
      <p class="result_title" v-show="result.total_num">
        About {{result.total_num}} results:
      </p>
      <div class="article" v-for="(item, index) in result.result" :key="item.id">
        <p class="title" @click="goTo(index)">{{item.title}}</p>
        <p class="author">{{item.author}},{{item.pdate}}</p>
      </div>
    </div>
    <div class="articles" v-if="result.result !== 'no result' && result.result && request.method === 'search' ">
      <p class="result_title" v-show="result.total_num">
        About {{result.total_num}} results
      </p>
      <div class="article" v-for="(item, index) in result.result" :key="item.id">
        <p class="title" @click="goTo(index)">{{item.title}}</p>
        <p class="author">{{item.author}},{{item.pdate}}</p>
      </div>
    </div>
    <div class="articles" v-if="result.result === 'no result'">
      <p class="result_title">
        There is no result for the search
      </p>
    </div>
    <div class="duplica" v-if="result.duplicate">
      <p class="dup_title">Are you searching for?</p>
      <div class="dup_form" v-for="(item, index) in result.duplicate" :key="item.id" @click="search_author_id(index)">
        <p class="name">{{item.author_name}},{{item.author_fname}}</p>
        <p class="des">{{item.des}}</p>
        <p class="des" v-if="item.num_term">{{item.num_term}} articles wrote about </p>
      </div>
    </div>
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
      svg: null,
      graph: null,
      links: null,
      nodes: null,
      simulation: null,
      settings: {
        strokeColor: '#29B5FF',
        width: 100,
        svgWigth: 960,
        svgHeight: 600
      },
      result: {},
      isRecent: true,
      rank_max: -1,
      rank_color: {0: '#E1E6FA', 1: '#C4D7ED', 2: '#ABC8E2', 3: '#4B7FB0', 4: '#2F60A1'},
      isCustom: false
    }
  },
  mounted: function () {
    this.search_recent()
  },
  methods: {
    search () {
      this.isRecent = false
      this.request.author_search = this.$refs.author_input.value
      this.request.term = this.$refs.term_input.value
      this.$http.post(this.api, this.request)
        .then((response) => {
          console.log(response)
          if (response.bodyText !== 'empty search') {
            this.result = response.data
            this.rank_max = this.result.rank_max
          }
          console.log(this.result.result)
        })
    },
    goTo (index) {
      var id = this.result.result[index].id
      window.open('https://www.ncbi.nlm.nih.gov/pubmed/' + id)
    },
    search_click () {
      this.result = {}
      this.request.filter = 'all'
      this.request.sort = 'relative'
      this.request.method = 'search'
      this.isCustom = false
      this.search()
    },
    search_recent () {
      this.request.method = 'search_recent'
      this.search()
    },
    search_filter (filter) {
      this.request.filter = filter
      this.search()
    },
    search_sort (sort) {
      this.request.sort = sort
      this.search()
    },
    search_author_id (index) {
      this.result = {}
      this.request.author_id = this.result.duplicate[index].author_id
      if (this.request.term !== '') {
        this.request.method = 'search_author_id_term'
      } else {
        this.request.method = 'search_author_id'
      }
      this.search()
    },
    show () {
      if (this.request.method === 'search_recent' || this.result.duplicate || !this.result.result) {
        return false
      } else {
        return true
      }
    },
    clickFunc () {
      this.isCustom = true
    },
    search_filter_custom () {
      var start = this.$refs.startTime.value
      var end = this.$refs.endTime.value
      var filter = 'BETWEEN ' + start + ' AND ' + end
      this.search_filter(filter)
    }
  }
}

/* var svg = d3.select('svg')
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
} */
</script>

<style scoped>
  .content {
    width:100%;
    height:1400px;
    background: #F2F2F2;
  }
  .header {
    margin-top:0;
    width:100%;
    height:180px;
    background-image:url("../../assets/searchnew.jpg");
    background-repeat: no-repeat;
    background-size: cover;
    box-shadow: 0 2px 2px rgba(0,0,1,0.4);
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
   width: 12%;
   margin-left: 2%;
   margin-right: 1%;
   margin-top: 20px;
   padding: 0.5%;
   float: left;
   background: #fff;
   box-shadow: 2px 2px 3px rgba(0,0,1,0.2);
 }
 .filters_time {
   height: 70%;
   width: 100%;
   display: inline-block;
 }
 .filters_sort {
   height: 30%;
   width: 100%;
   display: inline-block;
   margin-top: 15%;
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
  .active_filter {
    width: 150px;
    float: left;
    height: 10px;
    display: vertical-align;
    margin-top: 6px;
    text-align: left;
    cursor:pointer;
    font-size: 1.2em;
    color: red;
    font-family: Times, TimesNR, 'New Century Schoolbook', Georgia, 'New York', serif;
  }
  .customTime {
    width:150px;
    height:30px;
    padding-left: 0;
    margin-top:0;
    margin-left:-5px;
  }
  .custom {
    width: 35%;
    height: 20px;
    padding: 5%;
    float: left;
    color: black;
    font-weight: bolder;
    font-size: 0.8em;
    border-radius: 2px;
    border: 1px solid black;
    background: white;
    margin-top: 5px;
    margin-left: 4%;
  }
  .click {
    position: relative;
    margin-top: 10px;
    margin-left: 4%;
    float:left;
    padding: 0;
    cursor: pointer;
    height: 20px;
    width: 50px;
    color: black;
    font-weight: bold;
    font-size: 0.3em;
    background: lightgrey;
    border: 1px solid black;
    border-radius:2px;}

  .click:hover {
    background: #fff;
    color:#EE6352;}
  .click:active {
    box-shadow: 0px 0px 12px 0px rgb(213, 225, 210);}

  .click:focus {outline: 0;}

  .search  {
    width: 100%;
    height: auto;
    position:relative;
    margin: 1px auto;
  }

  .search input {
    width: 40%;
    height: 41px;
    padding: 10px 10px;
    float: left;
    color: #14C7FF;
    font-weight: bolder;
    font-size: 1.2em;
    border-radius: 5px;
    border: 2px solid #fff;
    background: rgba(0, 0, 0, 0.45);
  }
  .search_input_author {
    margin-left: 8%;
    margin-right: 2%;
  }
  .search_input_term {
    margin-left: 2%;
    margin-right: 8%;
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
    margin-right: 8%;
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
  .articles_recent{
    width: 84%;
    height: 600px;
    margin-top: 20px;
    margin-left: 8%;
    margin-right: 8%;
    background: #fff;
    overflow: scroll;
    box-shadow: 2px 2px 3px rgba(0,0,1,0.2);
  }
  .articles{
    width: 82.5%;
    height: 600px;
    margin-top: 20px;
    margin-left: 0.5%;
    margin-right: 2%;
    background: #fff;
    overflow: scroll;
    -webkit-box-shadow: 2px 2px 3px rgba(0,0,1,0.2);
    box-shadow: 2px 2px 3px rgba(0,0,1,0.2);
    float: left;
  }
  .result_title_recent {
    font-size: 28px;
    margin-top: 20px;
    font-weight: bolder ;
    font-family: "Times New Roman", Times, serif;
  }
  .title {
    float: none;
    width: 100%;
    font-size: 20px;
    color: black;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    margin: 0;
    padding: 0;
    text-align: -webkit-left;
    cursor:pointer;
  }
  .title:hover{
    background: transparent;
    text-decoration:underline;color: #257A9E;}
  .article {
    width: 100%;
    padding: 15px 30px;
  }
  .result_title {
    text-align: left;
    padding-top: 2%;
    padding-left: 30px;
    font-size: 1.5em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    color: #337ab7 ;
  }
  .author {
      font-size: 15px;
      color: dimgrey;
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
  .bar_chart {
    width: 45%;
    /*background: #fff;*/
    padding-right:10px;
    margin-left: 2%;
    margin-top: 20px;
    float: left;
  }
  .bar_title {
    font-size: 1.5em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    color: #337ab7 ;
  }
  .rank {
    width:45%;
    float: right;
    margin-right: 2%;
    margin-top: 20px;
  }
  .rank_name {
    float: left;
    color: #001C1C;
    line-height: 50px;
    height: 40px;
    font-size: 22px;
    padding-left: 10px;
    font-family: Georgia;
  }
  .rank_value {
    float: right;
    color: #001C1C;
    line-height: 50px;
    height: 40px;
    font-size: 24px;
    padding-right: 10px;
    font-family: Georgia;
  }
  .unit {
    font-size: 18px;
  }
  .bar {
    height:100%;
    box-shadow: 0 0 5px 5px rgba(0,0,1,0.03);
  }
  .rank-bar {
    width: 100%;
    height: 60px;
  }
  .visual {
    height: 450px;
  }
  .rank_title {
    font-size: 1.5em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    color: #337ab7 ;
    margin-bottom: 40px;
  }
  .line {
    width: 84%;
    background: #fff;
    box-shadow: 2px 2px 3px rgba(0,0,1,0.2);
    margin-left: 8%;
    margin-right: 8%;
    margin-top: 50px;
    padding-top: 30px;
  }
  .svg-container {
    display: table;
    border: 1px solid #f8f8f8;
    box-shadow: 1px 2px 4px rgba(0, 0, 0, .5);
  }
  label {
    display: block;
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
