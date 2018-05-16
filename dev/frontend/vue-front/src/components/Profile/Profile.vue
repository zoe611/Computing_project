<template>
  <div class="home">
    <div class="topic">
      <p class="topic_title">Visualize for All Topics</p>
    </div>
    <el-select v-model="value" placeholder="Choose Year" @change="changeBubble()">
      <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
      </el-option>
    </el-select>
    <svg width="100%" height="700" font-family="sans-serif" font-size="10" text-anchor="middle"></svg>
  </div>
</template>

<script>
import * as d3 from 'd3'
export default {
  name: 'Home',
  data () {
    return {
      filter: '',
      sort: '',
      options: [{
        value: '0',
        label: 'all'
      }, {
        value: '1',
        label: '2018'
      }, {
        value: '2',
        label: '2017'
      }, {
        value: '3',
        label: '2016'
      }, {
        value: '4',
        label: '2015'
      }, {
        value: '5',
        label: '2014'
      }, {
        value: '6',
        label: '2013'
      }, {
        value: '7',
        label: '2012'
      }, {
        value: '8',
        label: '2011'
      }, {
        value: '9',
        label: '2010'
      }, {
        value: '10',
        label: '2009'
      }, {
        value: '11',
        label: '2008'
      }, {
        value: '12',
        label: '2007'
      }, {
        value: '13',
        label: '2006'
      }, {
        value: '14',
        label: '2005'
      }, {
        value: '15',
        label: '2004'
      }],
      value: '',
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
        },
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
        },
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
        },
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
          value: 100,
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
      ],
      color: {0: '#E4523B',
        1: '#0A454D',
        2: '#3DB296',
        3: '#ECC417',
        4: '#E8931E',
        5: '#E97778',
        6: '#7998C9',
        7: '#FFD57E',
        8: '#89C7B6',
        9: '#AD84C7',
        10: '#375A28',
        11: '#0093C6',
        12: '#192C47',
        13: '#7F4354',
        14: '#FF88A8',
        15: '#BBB06E',
        16: '#7B683D',
        17: '#CC2F5A',
        18: '#FF3B70',
        19: '#7F1D38'},
      result: null,
      api: 'http://43.240.98.137/test2.php',
      request: {
        method: 'visual'
      }
    }
  },
  mounted: function () {
    this.$http.post(this.api, this.request)
            .then((response) => {
      console.log(response)
      this.result = response.data
      this.data = this.result.all
      this.showSvg()
  })
  },
  methods: {
    changeBubble (year) {
      console.log(this.value)
      var filter = this.value
      this.data = this.result[filter]
      d3.selectAll('g').remove()
    },
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
      var root = d3.hierarchy({children: that.data})
        .sum(d => d.value)
      // we use pack() to automatically calculate radius conveniently only
      // and get only the leaves
      var nodes = pack(root).leaves().map(node => {
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
      svg.style('background-color', '#fff')
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
        .style('fill', function (d) {
          return that.color[d.index]
        })
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
        .classed('node-icon', true)
        .attr('clip-path', d => `url(#clip-${d.id})`)
        .text(function (d) {
          return d.name
        })
        .style('stroke', '#fff')
        .style('font-size', '20px')
        .style('fill', '#fff')
      node.append('title')
        .text(function (d) {
          return d.name
        })
        .style('stroke', '#fff')
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
        .style('color', '#fff')
      infoBox.append('p')
        .classed('circle-overlay__body', true)
        .html(d => d.desc)
        .style('color', '#fff')
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
        d3.selectAll('.node-icon').classed('node-icon--faded', false).style('opacity', '1')
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
            $currentGroup.select('.node-icon')
              .classed('node-icon--faded', true).style('opacity', '0')
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
          d3.selectAll('.node-icon').classed('node-icon--faded', false).style('opacity', '1')
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
  h1 {
    text-align: center;
  }
  svg {
    margin:auto;
    display:block;
  }
  .circle-overlay {
    font-size: 16px;
    border-radius: 50%;
    position: absolute;
    overflow: hidden;
    /*it's buggy with the foreignObject background right now*/
    /*background-color: rgba(255,255,255,0.5);*/
  }
  .circle-overlay__inner {
    text-align: center;
    width: 100%;
    height: 100%;
  }
  .hidden {
    display: none;
  }
  .node-icon node-icon--faded {
    opacity: 0;
  }
  .legend-size circle {
    fill: rgb(31, 119, 180);
  }
</style>
