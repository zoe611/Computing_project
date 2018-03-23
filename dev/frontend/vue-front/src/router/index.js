import Vue from 'vue'
import Router from 'vue-router'

import Home from '@/components/Home/Home'
import Search from '@/components/Search/Search'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/search',
      name: 'Search',
      component: Search
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile
    }
  ]
})
