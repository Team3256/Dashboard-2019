import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  mode: 'hash',
  routes: [
    {
      path: '/main',
      name: 'landing-page',
      component: require('@/components/LandingPage').default
    },
    {
      path: '/console',
      name: 'console',
      component: require('@/windows/InteractiveConsole').default
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
});
