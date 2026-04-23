import { createRouter, createWebHistory } from 'vue-router'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: () => import('../pages/home/HomePage.vue') },
    { path: '/hot', name: 'hot', component: () => import('../pages/home/HotPage.vue') },
    {
      path: '/following',
      name: 'following',
      component: () => import('../pages/home/FollowingPage.vue'),
    },
    { path: '/upload', name: 'upload', component: () => import('../pages/upload/UploadPage.vue') },
    {
      path: '/video/:id',
      name: 'video-detail',
      component: () => import('../views/VideoDetailView.vue'),
      props: true,
    },
  ],
  scrollBehavior: () => ({ top: 0 }),
})
