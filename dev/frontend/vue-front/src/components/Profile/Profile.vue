<template>
  <div class="home">
  </div>
</template>

<script>
import * as d3 from 'd3'
export default {
  name: 'Home',
  data () {
    return {
      nodes: [
        {
          name: 'Alice',
          id: 2407,
          color: '#D24D25'
        },
        {
          name: 'Bob',
          id: 2247,
          color: '#FFCD34'
        },
        {
          name: 'Carol',
          id: 4567,
          color: '#FFCD34'
        },
        {
          name: 'Dog',
          id: 5678,
          color: '#FFCD34'
        }
      ],
      links: [
        {
          source: 0,
          target: 1,
          value: 2
        },
        {
          source: 0,
          target: 2,
          value: 3
        },
        {
          source: 0,
          target: 3,
          value: 4
        }
      ],
      settings: {
        strokeColor: '#29B5FF',
        width: 100,
        svgWigth: 960,
        svgHeight: 600
      },
      msg: 'Welcome to Your Vue.js App',
      svg: null,
      graph: null,
      simulation: null,
      circle: null
    }
  },
  methods: {
    showSvg () {
      console.log('rerer')
      var that = this
      that.svg = d3.select('.relation')
        .append('svg')
        .attr('width', 960)
        .attr('height', 600)
      that.simulation = d3.forceSimulation(that.nodes)
        .force('link', d3.forceLink(that.links).distance(100).strength(0.1))
        .force('charge', d3.forceManyBody())
        .force('center', d3.forceCenter(that.settings.svgWigth / 2, that.settings.svgHeight / 2))
      var links = d3.select('svg').append('g')
        .attr('class', 'links')
        .selectAll('line')
        .data(that.links)
        .enter().append('line')
        .attr('stroke', '#0080A4')
        .attr('stroke-width', function (d) { return d.value })
      var nodes = d3.select('svg').append('g')
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
      that.circle = nodes
        .append('circle')
        .attr('r', 20)
        .data(that.nodes)
        .attr('fill', function (d) {
          return d.color
        })
        .on('click', function (d) {
          console.log(d.name)
        })
        .call(d3.drag()
          .on('start', function dragstarted (d) {
            if (!d3.event.active) that.simulation.alphaTarget(0.3).restart()
            d.fx = d.x
            d.fy = d.y
          })
          .on('drag', function dragged (d) {
            d.fx = d3.event.x
            d.fy = d3.event.y
          })
          .on('end', function dragended (d) {
            if (!d3.event.active) that.simulation.alphaTarget(0)
            d.fx = null
            d.fy = null
          })
        )
      that.simulation.on('tick', function ticked () {
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
  h1, h2 {
    font-weight: normal;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    display: inline-block;
    margin: 0 10px;
  }
  a {
    color: #42b983;
  }
</style>
