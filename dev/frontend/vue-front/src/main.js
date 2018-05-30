// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueResource from 'vue-resource'
import Vs from 'd3-vs'
import Element from 'element-ui'
import 'element-theme-default'
import VModal from 'vue-js-modal'

Vue.use(VModal)
Vue.use(Element)
Vue.use(Vs)
Vue.use(VueResource)

Vue.config.productionTip = false
Vue.http.options.emulateJSON = true

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
