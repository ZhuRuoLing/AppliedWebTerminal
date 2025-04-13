var c=Object.defineProperty;var a=(s,e,t)=>e in s?c(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var o=(s,e,t)=>a(s,typeof e!="symbol"?e+"":e,t);import{u as h}from"./data-CyNSjlnJ.js";import{L as r}from"./utils-DPPkojbW.js";class f{constructor(){o(this,"_token");o(this,"_webSocket");o(this,"_onOpenListeners",new Map);o(this,"_onMessageListeners",new Map);o(this,"_onErrorListeners",new Map);o(this,"_onCloseListeners",new Map)}_onOpen(e){r.info("WebSocket Service doing onOpen"),this._onOpenListeners.forEach((t,n)=>{t(e)})}_onMessage(e){this._onOpenListeners.forEach((t,n)=>{t(e)})}_onError(e){this._onErrorListeners.forEach((t,n)=>{t(e)})}_onClose(e){this._onCloseListeners.forEach((t,n)=>{t(e)})}addOnOpenListener(e,t){return this._onOpenListeners.set(e,t),this}removeOnOpenListener(e){return this._onOpenListeners.delete(e),this}addOnMessageListener(e,t){return this._onMessageListeners.set(e,t),this}removeOnMessageListener(e){return this._onMessageListeners.delete(e),this}addOnCloseListener(e,t){return this._onCloseListeners.set(e,t),this}removeOnCloseListener(e){return this._onCloseListeners.delete(e),this}addOnErrorListener(e,t){return this._onErrorListeners.set(e,t),this}removeOnErrorListener(e){return this._onErrorListeners.delete(e),this}setToken(e){return this._token=e,this}connect(e=this._token){r.info(`connecting to WebSocket with token : ${e}`),this._token||(this._token=e),this._webSocket&&this.close();const i=h().serverConfig.webSocketUrl||`ws://${location.host}/ws`;this._webSocket=new WebSocket(`${i}?token=${e}`),this._webSocket.addEventListener("open",this._onOpen.bind(this)),this._webSocket.addEventListener("message",this._onMessage.bind(this)),this._webSocket.addEventListener("close",this._onClose.bind(this)),this._webSocket.addEventListener("error",this._onError.bind(this))}send(e){var t;(t=this._webSocket)==null||t.send(e)}get readyState(){var e;return(e=this._webSocket)==null?void 0:e.readyState}get url(){var e;return(e=this._webSocket)==null?void 0:e.url}close(){var e;r.info("Closing WebSocket"),(e=this._webSocket)==null||e.close(),this._webSocket=void 0}}async function L(){r.info("Fetching Terminal list...");const s=await fetch("/list",{cache:"no-cache"});if(s.ok){const e=await s.json();return r.info("Fetch Terminal list successfully."),r.info(e),e}return[]}async function k(s,e){const n=await fetch("/login",{cache:"no-cache",method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify({uuid:s,password:e})});if(n.ok)try{const i=await n.json();return i.success&&i.payload?i.payload:0}catch{return 1}return 1}export{f as W,L as f,k as l};
