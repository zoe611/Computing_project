<template>
  <div class="home">
    <div class="topic">
      <p class="topic_title">Visualize for All Topics</p>
    </div>
    <svg width="80%" height="700" font-family="sans-serif" font-size="10" text-anchor="middle"></svg>
  </div>
</template>

<script>
import * as d3 from 'd3'
export default {
  name: 'Home',
  data () {
    return {
      data: [
        {
          cat: 'library',
          name: 'D3',
          value: 30,
          desc: 'D3.js (or just D3 for Data-Driven Documents) is a JavaScript library for\n' +
                    '\t\t\t\tproducing dynamic, interactive data visualizations in web browsers.\n' +
                    '\t\t\t\tIt makes use of the widely implemented SVG, HTML5, and CSS standards.<br>\n' +
                    '\t\t\t\tThis infographic you are viewing is made with D3.'
        }, {
          cat: 'library',
          name: 'Raphaël',
          value: 10,
          desc: 'aphaël is a cross-browser JavaScript library that draws Vector graphics for web sites.\n' +
                    '\t\t\t\tIt will use SVG for most browsers, but will use VML for older versions of Internet Explorer'

        }, {
          cat: 'library',
          name: 'Relay',
          value: 70,
          desc: 'A JavaScript framework for building data-driven React applications.\n' +
                    '\t\t\t\tIt uses GraphQL as the query language to exchange data between app and server efficiently.\n' +
                    '\t\t\t\tQueries live next to the views that rely on them, so you can easily reason\n' +
                    '\t\t\t\tabout your app. Relay aggregates queries into efficient network requests to fetch only what you need.'
        }, {
          cat: 'library',
          name: 'Three.js',
          value: 40,
          desc: 'Three.js allows the creation of GPU-accelerated 3D animations using\n' +
                    '\t\t\t\tthe JavaScript language as part of a website without relying on\n' +
                    '\t\t\t\tproprietary browser plugins. This is possible thanks to the advent of WebGL.'
        }, {
          cat: 'library sub',
          name: 'Lodash',
          value: 30,
          desc: 'Lodash is a JavaScript library which provides <strong>utility functions</strong> for\n' +
                    '\t\t\t\tcommon programming tasks using the functional programming paradigm.'
        }
      ]
      /* settings: {
        strokeColor: '#29B5FF',
        width: 100,
        svgWigth: 960,
        svgHeight: 600
      },
      msg: 'Welcome to Your Vue.js App',
      svg: null,
      graph: null,
      simulation: null,
      circle: null */
    }
  },
  mounted: function () {
    this.showSvg()
  },
  methods: {
    showSvg () {
      var that = this
      var svg = d3.select('svg')
      console.log(svg)
      var width = document.body.clientWidth // get width in pixels
      console.log(width)
      var height = +svg.attr('height')
      var centerX = width * 0.5
      var centerY = height * 0.5
      var strength = 0.05
      var focusedNode
      var format = d3.format(',d')
      // let scaleColor = d3.scaleOrdinal(d3.schemeCategory20)
      // use pack to calculate radius of the circle
      var pack = d3.pack()
        .size([width, height])
        .padding(1.5)
      var forceCollide = d3.forceCollide(d => d.r + 1)
      // use the force
      var simulation = d3.forceSimulation()
      // .force('link', d3.forceLink().id(d => d.id))
        .force('charge', d3.forceManyBody())
        .force('collide', forceCollide)
      // .force('center', d3.forceCenter(centerX, centerY))
        .force('x', d3.forceX(centerX).strength(strength))
        .force('y', d3.forceY(centerY).strength(strength))
      // reduce number of circles on mobile screen due to slow computation
      if ('matchMedia' in window && window.matchMedia('(max-device-width: 767px)').matches) {
        data = data.filter(el => {
          return el.value >= 50
                })
      }
      var root = d3.hierarchy({children: that.data})
        .sum(d => d.value)
      // we use pack() to automatically calculate radius conveniently only
      // and get only the leaves
      var nodes = pack(root).leaves().map(node => {
        console.log('node:', node.x, (node.x - centerX) * 2)
        const data = node.data
        return {
          x: centerX + (node.x - centerX) * 3, // magnify start position to have transition to center movement
          y: centerY + (node.y - centerY) * 3,
          r: 0, // for tweening
          radius: node.r, // original radius
          id: data.cat + '.' + (data.name.replace(/\s/g, '-')),
          cat: data.cat,
          name: data.name,
          value: data.value,
          desc: data.desc
        }
      })
      simulation.nodes(nodes).on('tick', ticked)
      svg.style('background-color', '#eee')
      var node = svg.selectAll('.node')
        .data(nodes)
        .enter().append('g')
        .attr('class', 'node')
        .call(d3.drag()
          .on('start', (d) => {
            if (!d3.event.active) simulation.alphaTarget(0.2).restart()
            d.fx = d.x
            d.fy = d.y
          })
          .on('drag', (d) => {
            d.fx = d3.event.x
            d.fy = d3.event.y
          })
          .on('end', (d) => {
            if (!d3.event.active) simulation.alphaTarget(0)
            d.fx = null
            d.fy = null
          }))
      node.append('circle')
        .attr('id', d => d.id)
        .attr('r', 0)
        .style('fill', 'red')
        .transition().duration(2000).ease(d3.easeElasticOut)
        .tween('circleIn', (d) => {
          let i = d3.interpolateNumber(0, d.radius)
          return (t) => {
            d.r = i(t)
            simulation.force('collide', forceCollide)
          }
        })
      node.append('clipPath')
        .attr('id', d => `clip-${d.id}`)
        .append('use')
        .attr('xlink:href', d => `#${d.id}`)
      // display text as circle icon
      node.append('text')
        .attr('clip-path', d => `url(#clip-${d.id})`)
        .selectAll('tspan')
        .enter()
        .append('tspan')
        .attr('x', 0)
        .attr('y', (d, i, nodes) => (13 + (i - nodes.length / 2 - 0.5) * 10))
        .text(name => name)
      /* display image as circle icon
      node.append('image')
        .attr('clip-path', d => `url(#clip-${d.id})`)
        .attr('x', d => -d.radius * 0.7)
        .attr('y', d => -d.radius * 0.7)
        .attr('height', d => d.radius * 2 * 0.7)
        .attr('width', d => d.radius * 2 * 0.7) */
      node.append('title')
        .text(d => (d.cat + '::' + d.name + '\n' + format(d.value)))
      var legendOrdinal = d3.legendColor()
        .scale('red')
        .shape('circle')
      var legend = svg.append('g')
        .classed('legend-color', true)
        .attr('text-anchor', 'start')
        .attr('transform', 'translate(20,30)')
        .style('font-size', '12px')
        .call(legendOrdinal)
      var sizeScale = d3.scaleOrdinal()
        .domain(['less use', 'more use'])
        .range([5, 10])
      var legendSize = d3.legendSize()
        .scale(sizeScale)
        .shape('circle')
        .shapePadding(10)
        .labelAlign('end')
      var legend2 = svg.append('g')
        .classed('legend-size', true)
        .attr('text-anchor', 'start')
        .attr('transform', 'translate(150, 25)')
        .style('font-size', '12px')
        .call(legendSize)
      /*
        <foreignObject class="circle-overlay" x="10" y="10" width="100" height="150">
            <div class="circle-overlay__inner">
                <h2 class="circle-overlay__title">ReactJS</h2>
                <p class="circle-overlay__body">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ullam, sunt, aspernatur. Autem repudiandae, laboriosam. Nulla quidem nihil aperiam dolorem repellendus pariatur, quaerat sed eligendi inventore ipsa natus fugiat soluta doloremque!</p>
            </div>
        </foreignObject>
        */
      var infoBox = node.append('foreignObject')
        .classed('circle-overlay hidden', true)
        .attr('x', -350 * 0.5 * 0.8)
        .attr('y', -350 * 0.5 * 0.8)
        .attr('height', 350 * 0.8)
        .attr('width', 350 * 0.8)
        .append('xhtml:div')
        .classed('circle-overlay__inner', true)
      infoBox.append('h2')
        .classed('circle-overlay__title', true)
        .text(d => d.name)
      infoBox.append('p')
        .classed('circle-overlay__body', true)
        .html(d => d.desc)
      node.on('click', (currentNode) => {
        d3.event.stopPropagation()
        console.log('currentNode', currentNode)
        var currentTarget = d3.event.currentTarget // the <g> el
        if (currentNode === focusedNode) {
          // no focusedNode or same focused node is clicked
          return
        }
        var lastNode = focusedNode
        focusedNode = currentNode
        simulation.alphaTarget(0.2).restart()
        // hide all circle-overlay
        d3.selectAll('.circle-overlay').classed('hidden', true)
        // don't fix last node to center anymore
        if (lastNode) {
          lastNode.fx = null
          lastNode.fy = null
          node.filter((d, i) => i === lastNode.index)
            .transition().duration(2000).ease(d3.easePolyOut)
            .tween('circleOut', () => {
              let irl = d3.interpolateNumber(lastNode.r, lastNode.radius)
              return (t) => {
                lastNode.r = irl(t)
              }
            })
            .on('interrupt', () => {
              lastNode.r = lastNode.radius
            })
        }
        // if (!d3.event.active) simulation.alphaTarget(0.5).restart();
        d3.transition().duration(2000).ease(d3.easePolyOut)
          .tween('moveIn', () => {
            console.log('tweenMoveIn', currentNode)
            let ix = d3.interpolateNumber(currentNode.x, centerX)
            let iy = d3.interpolateNumber(currentNode.y, centerY)
            let ir = d3.interpolateNumber(currentNode.r, centerY * 0.5)
            return function (t) {
              // console.log('i', ix(t), iy(t));
              currentNode.fx = ix(t)
              currentNode.fy = iy(t)
              currentNode.r = ir(t)
              simulation.force('collide', forceCollide)
            }
          })
          .on('end', () => {
            simulation.alphaTarget(0)
            let $currentGroup = d3.select(currentTarget)
            $currentGroup.select('.circle-overlay')
              .classed('hidden', false)
          })
          .on('interrupt', () => {
            console.log('move interrupt', currentNode)
            currentNode.fx = null
            currentNode.fy = null
            simulation.alphaTarget(0)
          })
      })
      // blur
      d3.select(document).on('click', () => {
        let target = d3.event.target
        // check if click on document but not on the circle overlay
        if (!target.closest('#circle-overlay') && focusedNode) {
          focusedNode.fx = null
          focusedNode.fy = null
          simulation.alphaTarget(0.2).restart()
          d3.transition().duration(2000).ease(d3.easePolyOut)
            .tween('moveOut', function () {
              console.log('tweenMoveOut', focusedNode)
              let ir = d3.interpolateNumber(focusedNode.r, focusedNode.radius)
              return function (t) {
                focusedNode.r = ir(t)
                simulation.force('collide', forceCollide)
              }
            })
            .on('end', () => {
              focusedNode = null
              simulation.alphaTarget(0)
            })
            .on('interrupt', () => {
              simulation.alphaTarget(0)
            })
          // hide all circle-overlay
          d3.selectAll('.circle-overlay').classed('hidden', true)
        }
      })

      function ticked () {
        node
          .attr('transform', d => `translate(${d.x},${d.y})`)
          .select('circle')
          .attr('r', d => d.r)
      }
    }
  }
}
</script>

<style scoped>
  .topic_title {
    font-size: 1.5em;
    font-weight: bold;
    font-family: "Times New Roman", Times, serif;
    color: #337ab7 ;
  }
</style>
