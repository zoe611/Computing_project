<template>
  <div class="content">
    <modal name="modal_svg"
           transition="nice-modal-fade"
           classes="demo-modal-class"
           :min-width="450"
           :min-height="400"
           :pivot-y="0.5"
           :adaptive="true"
           :scrollable="true"
           :reset="true"
           width="80%"
           height="auto"
           @opened="show_detail_relationship()">
      <div class="modal_relation">
        <p class="see_all" style="text-align: right; padding-right:5px">click outside the modal to close</p>
        <p class="see_all" style="text-align: right; padding-right:5px">click on the author node to search further</p>
      </div>
    </modal>
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
    <div class="author_bar" v-if="result.author">
      <p class="author_name">{{result.author.author_name}}, {{result.author.author_fname}}</p>
      <p class="author_des">{{result.author.author_des}}</p>
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
    <div class="visual" v-if="result.bar || result.rank || result.relation">
      <div class="bar_chart" v-if="result.bar">
        <p class="bar_title">The Number of Publications Each Year</p>
        <div class="bar"  ref="bar" style="width:100%" v-if="show_bar()"></div>
      </div>
      <div class="rank">
        <div class="loading2" v-if="loading2">
          <Spinner class="load2" name="folding-cube" color="#14C7FF"/>
        </div>
        <p class="rank_title" v-if="rank">Author Rank for {{request.term}}</p>
        <div class="rank-bar" v-for="(item,index) in rank" :key="item.id">
          <p class="rank_name">{{item.name}}</p>
          <p class="rank_value">{{item.value}}<span class="unit"> articles</span></p>
          <div class="bar" v-bind:style="{background: rank_color[index], width: (item.value/rank_max)*100 + '%'}" @click="search_rank(index)"></div>
        </div>
      </div>
      <div class="relation" v-if="showrelation()">
        <p class="relation_title" >Author Relationship</p>
        <p class="see_all" @click="show_relation_more()">click me to show all</p>
      </div>
    </div>
    <!--    filter by date  and sort modified -->
    <!--<div class = "search_filter" v-if="show()">-->
    <div style="height:20px;width:100%;"></div>
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
      <div class="filter_sort" >
        <h3 :class="request.sort === 'relative' ? 'active_filter': 'filter'" @click="search_sort('relative')">Sort by relevance</h3>
        <h3 :class="request.sort === 'sort_pubdate' ? 'active_filter': 'filter'" @click="search_sort('sort_pubdate')">Sort by date</h3>
      </div>
      <div class="filter_term" v-if="filter_keyword">
        <h3 class="filter_custom">Filter by keywords</h3>
        <div class="customTime">
          <input type="search" class = "custom_filter" placeholder="keyword" ref="keyword" maxlength="84" required>
          <button type="submit" class="click" v-on:click="search_filter_term()">
            Search
          </button>
          <button type="submit" class="click" v-on:click="search_filter_term_clear()">
            Clear
          </button>
        </div>
      </div>
    </div>
    <!--    result list -->
    <div class="loading" v-if="loading">
      <Spinner class="load" name="folding-cube" color="#14C7FF"/>
    </div>
    <p class="result_title_recent" v-if="request.method === 'search_recent' && result.result ">
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
    <div class="articles" v-if="result.result !== 'no result' && result.result && request.method != 'search_recent' ">
      <p class="result_title" v-show="result.total_num">
        About {{result.total_num}} results
      </p>
      <div class="article" v-for="(item, index) in result.result" :key="item.id">
        <p class="title" @click="goTo(index)">{{item.title}}</p>
        <p class="author">{{item.author}},{{item.pdate}}</p>
      </div>
    </div>
    <div class="articles" v-if="result.result === 'no result' && request.method != 'search'">
      <p class="result_title">
        There is no result for the search.
      </p>
    </div>
    <div class="no_result" v-if="result.result === 'no result' && request.method === 'search'">
      <p class="result_title">
        <span>
          There is no result for the search. If you are searching the author and the keywords at the same time,
        </span><span>
          make sure you have enter the full name of the author.
        </span><span>
          Or search the author first and then filter the article by the keywords!
        </span>
      </p>
    </div>
    <div class="duplica" v-if="result.duplicate">
      <p class="dup_title">Are you searching for?</p>
      <div class="duplicate">
        <div class="dup_form" v-for="(item, index) in result.duplicate" :key="item.id" @click="search_author_id(index)">
          <p class="name">{{item.author_name}},{{item.author_fname}}</p>
          <p class="des">{{item.des}}</p>
          <p class="des" v-if="item.num_term">{{item.num_term}} articles wrote about {{request.term}}</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import * as d3 from 'd3'
import VuePaginateAl from 'vue-paginate-al'
import Spinner from 'vue-spinkit'
export default {
  name: 'Home',
  components: {
    Spinner
  },
  data () {
    return {
      bar: null,
      relation: null,
      nodes: null,
      links: null,
      api: 'http://43.240.98.120/search.php',
      //      api:'http://43.240.98.137/test2.php',
      //      api: 'http://localhost/test/Computing_project/search.php',
      request: {
        method: '',
        term: '',
        author_search: '',
        page: '',
        filter: 'all',
        sort: 'relative',
        author_id: '',
        author_fname: '',
        author_name: '',
        author_des: ''
      },
      rank_request: {
        method: 'rank',
        term: ''
      },
      settings: {
        strokeColor: '#29B5FF',
        width: 100,
        svgWigth: 450,
        svgHeight: 370
      },
      svg: null,
      graph: null,
      simulation: null,
      circle: null,
      loading: false,
      result: {},
      isRecent: true,
      rank_max: -1,
      rank_color: {0: '#E1E6FA', 1: '#C4D7ED', 2: '#ABC8E2', 3: '#4B7FB0', 4: '#2F60A1'},
      isCustom: false,
      isFilter: false,
      rank: null,
      loading2: false,
      filter_keyword: false
    }
  },
  mounted: function () {
    this.search_recent()
    this.$modal.hide('modal_svg')
  },
  methods: {
    search () {
      this.isRecent = false
      this.loading = true
      this.$http.post(this.api, this.request)
        .then((response) => {
          console.log(response)
          if (response.bodyText !== 'empty search') {
            this.result = response.data
            if (this.request.method === 'search' && this.request.author_search === '') {
              this.loading2 = true
            }
            this.loading = false
            if (this.result.author) {
              this.request.author_id = this.result.author.author_id
              this.request.author_name = this.result.author.author_name
              this.request.author_fname = this.result.author.author_fname
              this.request.author_des = this.result.author.author_des
            }
            if (this.result.bar) {
              this.formBar()
            }
          }
          console.log(this.result.result)
        })
    },
    goTo (index) {
      var id = this.result.result[index].id
      window.open('https://www.ncbi.nlm.nih.gov/pubmed/' + id)
    },
    search_click () {
      var authorSearch = this.$refs.author_input.value
      var termSearch = this.$refs.term_input.value
      this.request.filter = 'all'
      this.request.sort = 'relative'
      this.request.method = 'search'
      this.request.author_id = ''
      this.request.author_name = ''
      this.request.author_fname = ''
      this.request.author_des = ''
      this.isCustom = false
      this.loading2 = false
      this.result = {}
      this.rank = null
      if (authorSearch === '' && termSearch === '') {
        alert('Please enter author or topic!')
      } else if (this.testString(authorSearch) || this.testString(termSearch)) {
        alert('No special characeter allowed!')
      } else {
        if (termSearch === '') {
          this.filter_keyword = true
        } else {
          this.filter_keyword = false
        }
        this.request.author_search = authorSearch
        this.request.term = termSearch
        if (this.request.author_search === '') {
          this.rank_request.term = this.request.term
          this.$http.post(this.api, this.rank_request).then((response) => {
            console.log(response)
            if (response.data.term === this.request.term) {
              this.loading2 = false
              this.rank = response.data.rank
              this.rank_max = response.data.rank_max
            }
          })
        }
        this.search()
      }
    },
    search_recent () {
      this.request.method = 'search_recent'
      this.search()
    },
    search_filter (filter) {
      this.result.result = null
      this.request.filter = filter
      if (this.request.term !== '' && this.request.author_search !== '') {
        this.request.method = 'search_author_id_term'
      } else if (this.request.term === '') {
        this.request.method = 'search_author_id'
      } else {
        this.request.method = 'search_term'
      }
      this.search()
    },
    search_sort (sort) {
      this.result.result = null
      this.request.sort = sort
      if (this.request.term !== '' && this.request.author_search !== '') {
        this.request.method = 'search_author_id_term'
      } else if (this.request.term === '') {
        this.request.method = 'search_author_id'
      } else {
        this.request.method = 'search_term'
      }
      this.search()
    },
    search_author_id (index) {
      this.request.author_id = this.result.duplicate[index].author_id
      this.request.author_name = this.result.duplicate[index].author_name
      this.request.author_fname = this.result.duplicate[index].author_fname
      this.request.author_des = this.result.duplicate[index].des
      if (this.request.term !== '') {
        this.request.method = 'search_author_id_term'
      } else {
        this.request.method = 'search_author_id'
      }
      this.result = {}
      this.search()
    },
    search_rank (index) {
      this.request.filter = 'all'
      this.request.sort = 'relative'
      this.request.author_id = this.rank[index].author_id
      this.request.author_name = this.rank[index].name
      this.request.author_fname = this.rank[index].author_fname
      this.request.author_des = this.rank[index].author_des
      this.$refs.author_input.value = this.rank[index].name
      this.request.method = 'search_author_id_term'
      this.request.author_search = this.rank[index].name
      this.result = {}
      this.rank = null
      this.search()
    },
    search_filter_term () {
      this.request.filter = 'all'
      this.request.sort = 'relative'
      this.request.method = 'search_author_id_term'
      this.request.term = this.$refs.keyword.value
      this.result.result = null
      this.search()
    },
    search_filter_term_clear () {
      this.request.filter = 'all'
      this.request.sort = 'relative'
      this.request.method = 'search_author_id'
      this.request.term = ''
      this.result.result = null
      this.search()
    },
    show () {
      if (this.request.method === 'search_recent' || this.result.duplicate || !this.result.bar) {
        return false
      } else {
        return true
      }
    },
    show_bar () {
      if (this.result.bar) {
        d3.select('.bar_svg').remove()
        this.formBar()
        this.show_bar_svg()
        return true
      } else {
        return false
      }
    },
    search_relation (authorId, authorName, authorFname, authorDes) {
      this.request.filter = 'all'
      this.request.sort = 'relative'
      this.request.author_id = authorId
      this.request.author_name = authorName
      this.request.author_fname = authorFname
      this.request.author_des = authorDes
      this.request.term = ''
      this.request.method = 'search_author_id'
      this.$refs.author_input.value = authorName
      this.$refs.term_input.value = ''
      this.result = {}
      this.search()
    },
    clickFunc () {
      this.isCustom = true
    },
    search_filter_custom () {
      var start = this.$refs.startTime.value
      var end = this.$refs.endTime.value
      var filter = 'BETWEEN ' + start + ' AND ' + end
      this.search_filter(filter)
    },
    formBar () {
      this.result.bar.forEach(function (item) {
        item.value = Number(item.value)
      })
      this.bar = this.result.bar
    },
    testString (str) {
      var pattern = new RegExp('[~!@#$^&*()=|{}:;,\\[\\].<>《》/?~！@#￥……&*（）——|{}【】‘；：”“。，、？]')
      if (pattern.test(str)) {
        return true
      } else {
        return false
      }
    },
    showrelation () {
      if (this.result.relation) {
        d3.select('.test').remove()
        this.nodes = this.result.relation.nodes
        this.links = this.result.relation.links
        this.showSvg()
        return true
      } else {
        return false
      }
    },
    show_relation_more () {
      this.$modal.show('modal_svg')
    },
    show_detail_relationship () {
      this.showSvgMore()
    },
    show_bar_svg () {
      var that = this
      var tooltip = d3.select('.content').append('div')
        .attr('class', 'tooltip')
        .style('display', 'none')
        .style('background', '#fff')
        .style('padding', '5px')
        .style('opacity', '1')
        .style('font-size', '16px')
        .style('border-radius', '2px')
      var svg = d3.select('.bar')
        .append('svg')
        .attr('class', 'bar_svg')
        .attr('width', 400)
        .attr('height', 400)
      d3.select('.bar_svg').style('overflow', 'inherit')
      var width = 400
      var height = 400
      var padding = {left: 0, right: 30, top: 20, bottom: 20}
      var xScale = d3.scaleBand().rangeRound([0, width]).paddingInner(0.05).align(0.1).domain(that.bar.map(function (d) { return d.key }))
      var yScale = d3.scaleLinear()
        .domain([0, d3.max(that.bar, function (d) { return d.value })]).nice()
        .range([height - padding.top - padding.bottom, 0])
      var xAxis = d3.axisBottom().scale(xScale)
      var max = d3.max(that.bar, function (d) { return d.value })
      console.log('max' + max)
      var yAxis
      var rectPadding = 4
      if (max >= 4) {
        yAxis = d3.axisLeft().scale(yScale).ticks(5)
      } else if (max === 3) {
        yAxis = d3.axisLeft().scale(yScale).ticks(3)
      } else if (max === 2) {
        yAxis = d3.axisLeft().scale(yScale).ticks(2)
      } else if (max === 1) {
        yAxis = d3.axisLeft().scale(yScale).ticks(1)
      }
      var rects = svg.selectAll('.MyRect')
        .data(that.bar)
        .enter()
        .append('rect')
        .attr('class', 'MyRect')
        .attr('transform', 'translate(' + padding.left + ',' + padding.top + ')')
        .attr('x', function (d, i) {
          return xScale(d.key) + rectPadding / 2
        })
        .attr('y', function (d) {
          return yScale(d.value)
        })
        .attr('width', xScale.bandwidth() - rectPadding)
        .attr('height', function (d) {
          return height - padding.top - padding.bottom - yScale(d.value)
        })
        .attr('fill', '#ABC8E2')
        .on('mouseover', function (d) {
          d3.select(this).attr('fill', '#2F60A1')
          tooltip.style('display', 'inline')
        })
        .on('mouseout', function (d, i) {
          d3.select(this).attr('fill', '#ABC8E2')
          tooltip.style('display', 'none')
        })
        .on('mousemove', function (d, i) {
          d3.select(this).attr('fill', '#2F60A1')
          tooltip
            .html(d.value)
            .style('left', (d3.event.pageX + 12) + 'px')
            .style('top', (d3.event.pageY - 20) + 'px')
        })
      svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(' + padding.left + ',' + (height - padding.bottom) + ')')
        .call(xAxis)
      svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(' + padding.left + ',' + padding.top + ')')
        .call(yAxis)
    },
    showSvgMore () {
      var that = this
      var svg = d3.select('.modal_relation')
        .append('svg')
        .attr('class', 'test')
        .attr('width', 800)
        .attr('height', 800)
      var simulation = d3.forceSimulation(that.nodes)
        .force('link', d3.forceLink(that.links).distance(100).strength(0.1))
        .force('charge', d3.forceManyBody())
        .force('center', d3.forceCenter(400, 400))
      var links = d3.select('.test').append('g')
        .attr('class', 'links')
        .selectAll('line')
        .data(that.links)
        .enter().append('line')
        .attr('stroke', '#0080A4')
        .attr('stroke-width', function (d) {
          return d.value
        })
      var nodes = d3.select('.test').append('g')
        .attr('class', 'nodes')
        .selectAll('g')
        .data(that.nodes)
        .enter().append('g')
        .on('mouseover', function (d, i) {
          d3.select(this).append('text').text(function (d) {
            return d.name
          })
            .attr('x', 6)
            .attr('y', 3)
          d3.select(this).append('title')
            .text(function (d) {
              return d.name
            })
        })
        .on('mouseout', function (d, i) {
          d3.select(this).select('title').remove()
          d3.select(this).select('text').remove()
        })
      var circle = nodes
        .append('circle')
        .attr('r', 10)
        .data(that.nodes)
        .attr('fill', function (d) {
          return d.color
        })
        .on('click', function (d) {
          that.$modal.hide('modal_svg')
          that.search_relation(d.id, d.name, d.fname, d.des)
        })
        .call(d3.drag()
          .on('start', function dragstarted (d) {
            if (!d3.event.active) simulation.alphaTarget(0.3).restart()
            d.fx = d.x
            d.fy = d.y
          })
          .on('drag', function dragged (d) {
            d.fx = d3.event.x
            d.fy = d3.event.y
          })
          .on('end', function dragended (d) {
            if (!d3.event.active) simulation.alphaTarget(0)
            d.fx = null
            d.fy = null
          })
        )
      simulation.on('tick', function ticked () {
        nodes
          .attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')'
          })
        links
          .attr('x1', function (d) {
            return d.source.x
          })
          .attr('y1', function (d) {
            return d.source.y
          })
          .attr('x2', function (d) {
            return d.target.x
          })
          .attr('y2', function (d) {
            return d.target.y
          })
      })
    },
    showSvg () {
      var that = this
      var svg = d3.select('.relation')
        .append('svg')
        .attr('class', 'test')
        .attr('width', that.settings.svgWigth)
        .attr('height', that.settings.svgHeight)
        .call(d3.zoom().scaleExtent([1, 10]).on('zoom', function () {
          svg.attr('transform', d3.event.transform)
        }))
      var simulation = d3.forceSimulation(that.nodes)
        .force('link', d3.forceLink(that.links).distance(100).strength(0.1))
        .force('charge', d3.forceManyBody())
        .force('center', d3.forceCenter(that.settings.svgWigth / 2, that.settings.svgHeight / 2))
      var links = d3.select('.test').append('g')
        .attr('class', 'links')
        .selectAll('line')
        .data(that.links)
        .enter().append('line')
        .attr('stroke', '#0080A4')
        .attr('stroke-width', function (d) { return d.value })
      var nodes = d3.select('.test').append('g')
        .attr('class', 'nodes')
        .selectAll('g')
        .data(that.nodes)
        .enter().append('g')
        .on('mouseover', function (d, i) {
          d3.select(this).append('text').text(function (d) {
            return d.name
          })
            .attr('x', 6)
            .attr('y', 3)
          d3.select(this).append('title')
            .text(function (d) { return d.name })
        })
        .on('mouseout', function (d, i) {
          d3.select(this).select('title').remove()
          d3.select(this).select('text').remove()
        })
      var circle = nodes
        .append('circle')
        .attr('r', 10)
        .data(that.nodes)
        .attr('fill', function (d) {
          return d.color
        })
        .on('click', function (d) {
          console.log(d.id)
          that.search_relation(d.id, d.name, d.fname, d.des)
        })
        .call(d3.drag()
          .on('start', function dragstarted (d) {
            if (!d3.event.active) simulation.alphaTarget(0.3).restart()
            d.fx = d.x
            d.fy = d.y
          })
          .on('drag', function dragged (d) {
            d.fx = d3.event.x
            d.fy = d3.event.y
          })
          .on('end', function dragended (d) {
            if (!d3.event.active) simulation.alphaTarget(0)
            d.fx = null
            d.fy = null
          })
        )
      simulation.on('tick', function ticked () {
        nodes
          .attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')'
          })
        links
          .attr('x1', function (d) { return d.source.x })
          .attr('y1', function (d) { return d.source.y })
          .attr('x2', function (d) { return d.target.x })
          .attr('y2', function (d) { return d.target.y })
      })
    }
  }
}
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
   height: 500px;
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
   height: 44%;
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
  .filter_sort {
    height: 30px;
  }
  .filter_custom {
    width: 150px;
    height: 10px;
    display: vertical-align;
    margin-top: 30px;
    text-align: left;
    cursor:pointer;
    font-size: 1.2em;
    font-family: Times, TimesNR, 'New Century Schoolbook', Georgia, 'New York', serif;
  }
  .custom_filter {
    margin-left: 5px;
    margin-top: 10px;
    width: 100px;
    float:left;
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
  .tooltip{
    position      : absolute;
    text-align    : center;
    max-width     : 70px;
    max-height    : 30px;
    padding       : 8px;
    border        : none;
    border-radius : 8px;
    margin-top    : -30px;
    font          : 10px sans-serif;
    background    : black;
    color         : white;
    pointer-events: none;
    height: auto;
    width: 100px;
    background: white;
    border-radius: 5px;
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
  .no_result {
    width: 82%;
    height: 600px;
    margin-top: 20px;
    margin-left: 9%;
    margin-right: 9%;
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
    padding-right: 30px;
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
    padding: 10px;
    background: #fff;
  }
  .dup_title {
    width: 100%;
    font-size: 33px;
    color: #337ab7;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
  }
  .name {
    font-size: 18px;
    color: black;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
  }
  .des {
    font-size: 15px;
    color: dimgrey;
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
    margin-left: 4%;
    margin-top: 20px;
    float: left;
  }
  .bar_title {
    font-size: 1.5em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    color: #337ab7 ;
  }
  .bar {
    overflow: inherit;
  }
  .bar_svg {
    overflow: inherit;
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
  .rank-bar {
    width: 100%;
    height: 60px;
  }
  .visual {
    height: 500px;
    width: 100%;
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
  .load {
    width:150px;
    height: 150px;
    margin-top: 200px;
    margin-left: 43%;
    margin-right: 8%;
  }
  .load2 {
    width: 100px;
    height: 100px;
    margin-top: 150px;
    margin-left: 40%;
    margin-right: 40%;
  }
  .relation {
    width:45%;
    float: right;
    margin-right: 4%;
    margin-left: 1%;
  }
  .relation_title {
    font-size: 1.5em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    color: #337ab7 ;
    width: 100%;
  }
  .author_bar {
    margin-top: 30px;
    background: #fff;
    width: 96%;
    margin-right: 2%;
    margin-left: 2%;
    padding: 10px;
    box-shadow: 2px 2px 3px rgba(0,0,1,0.2);
  }
  .author_name {
    text-align: left;
    color: black;
    font-size: 1.5em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
  }
  .author_des {
    text-align: left;
    margin-top: 5px;
    color: dimgrey;
    font-size: 1em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
  }
  .duplicate {
    height: 500px;
    overflow: scroll;
  }
  .modal_relation {
    background: #fff;
  }
  .see_all {
    margin-bottom: 0;
    color: #337ab7;
    cursor: pointer;
  }
</style>
