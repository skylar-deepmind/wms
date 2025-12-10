// import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";

// 引入element plus
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

const app = createApp(App);
app.use(ElementPlus); // 全局注册element plus
app.mount("#app");
