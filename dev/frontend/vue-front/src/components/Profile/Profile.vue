<template>
  <div class="home">
    <h1>This is profile</h1>
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
          id: 'Alice'
        },
        {
          id: 'Bob'
        },
        {
          id: 'Carol'
        }
      ],
      links: [
        {
          source: 0,
          target: 1,
          value: 2
        },
        {
          source: 1,
          target: 2,
          value: 3
        }
      ],
      msg: 'Welcome to Your Vue.js App',
      svg: null,
      graph: null,
      simulation: null,
      settings: {
        strokeColor: '#29B5FF',
        width: 100,
        svgWigth: 960,
        svgHeight: 600
      }
    }
  },
  mounted: function () {
    var that = this
    that.svg = d3.select('.home')
      .append('svg')
      .attr('width', 960)
      .attr('height', 600)
    that.simulation = d3.forceSimulation(that.nodes)
      .force('link', d3.forceLink(that.links).distance(100).strength(0.1))
      .force('charge', d3.forceManyBody())
      .force('center', d3.forceCenter(that.settings.svgWigth / 2, that.settings.svgHeight / 2))
    that.nodes = d3.select('svg').append('g')
      .attr('class', 'nodes')
      .selectAll('circle')
      .data(that.nodes)
      .enter().append('circle')
      .attr('r', 20)
      .attr('fill', 'red')
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
        }))
    console.log(that.nodes)
    console.log('testtest')
    that.links = d3.select('svg').append('g')
      .attr('class', 'links')
      .selectAll('line')
      .data(that.links)
      .enter().append('line')
      .attr('stroke', 'blue')
      .attr('stroke-width', function (d) { return d.value})
    console.log(that.links)
    console.log('testtest')
    that.simulation.on('tick', function ticked () {
      that.links
        .attr('x1', function (d) { return d.source.x })
        .attr('y1', function (d) { return d.source.y })
        .attr('x2', function (d) { return d.target.x })
        .attr('y2', function (d) { return d.target.y })
      that.nodes
        .attr('cx', function (d) { return d.x })
        .attr('cy', function (d) { return d.y })
      })
    console.log('testtest')
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
