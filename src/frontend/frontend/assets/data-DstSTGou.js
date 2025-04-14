import{d as i}from"./node-modules_pinia-CDiED6eR.js";import{W as g}from"./core-ClhDkQYQ.js";import{L as e}from"./utils-DPPkojbW.js";function l(){return{title:"Applied Web Terminal"}}const c="/settings";function C(r,o=()=>{}){e.info("Fetching server config..."),fetch(c,{method:"GET",mode:"cors",cache:"no-cache",headers:{"Content-Type":"application/json"}}).then(a=>{a.ok?a.json().then(n=>{e.info("Fetch ServerConfig.yaml Success :"),e.info(n),r(n)}):(e.warn("Fetch ServerConfig.yaml Error :"),e.warn(a.toString()),o(a))})}function f(){return{localConfig:s().localConfigStoreManager.get(),serverConfig:l()}}const y=i("appConfig",{state:()=>f()});function u(){return{language:"en_us",translateCache:new Map}}class S{constructor(o,t,a=window.localStorage){this.defaultValue=o,this.localStorageKey=t,this.store=a}get(){e.info(`LocalStorageManager getting ${this.localStorageKey}`);const o=this.store.getItem(this.localStorageKey);let t;return o?t=JSON.parse(o):(e.warn(`LocalStorageManager get ${this.localStorageKey} is empty, setting default.`),t=this.defaultValue(),this.set(t)),e.info(`LocalStorageManager got ${this.localStorageKey}:`),e.debug(t),t}set(o){e.info(`LocalStorageManager setting ${this.localStorageKey}:`),e.debug(o);const t=JSON.stringify(o);this.store.setItem(this.localStorageKey,t)}}function s(){return{localConfigStoreManager:new S(u,"LocalStorage"),webSocketService:new g,inTerminalPage:!1}}const v=i("appStorage",{state:()=>s()});export{v as a,C as f,y as u};
