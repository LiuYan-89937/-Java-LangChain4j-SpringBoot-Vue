import { createApp } from 'vue'

import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElIcons from '@element-plus/icons-vue' // 引入 Element Plus 图标库
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import router from './router'

const app = createApp(App)
for (let iconName in ElIcons) {
  app.component(iconName, ElIcons[iconName])
}
app.use(router)

app.use(ElementPlus,{locale: zhCn})
app.mount('#app')
