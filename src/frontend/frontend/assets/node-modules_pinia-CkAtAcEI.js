import{ar as D,r as H,aq as M,A as $,z as G,$ as T,E as x,az as V,ae as tt,C as et,D as st,w as nt,x as ot,ag as ct,i as rt}from"./node-modules_@vue-spdbOBV8.js";/*!
 * pinia v3.0.2
 * (c) 2025 Eduardo San Martin Morote
 * @license MIT
 */let q;const E=t=>q=t,B=Symbol();function k(t){return t&&typeof t=="object"&&Object.prototype.toString.call(t)==="[object Object]"&&typeof t.toJSON!="function"}var w;(function(t){t.direct="direct",t.patchObject="patch object",t.patchFunction="patch function"})(w||(w={}));function yt(){const t=D(!0),c=t.run(()=>H({}));let s=[],e=[];const a=M({install(r){E(a),a._a=r,r.provide(B,a),r.config.globalProperties.$pinia=a,e.forEach(u=>s.push(u)),e=[]},use(r){return this._a?s.push(r):e.push(r),this},_p:s,_a:null,_e:t,_s:new Map,state:c});return a}const J=()=>{};function F(t,c,s,e=J){t.push(c);const a=()=>{const r=t.indexOf(c);r>-1&&(t.splice(r,1),e())};return!s&&et()&&st(a),a}function p(t,...c){t.slice().forEach(s=>{s(...c)})}const ut=t=>t(),z=Symbol(),I=Symbol();function L(t,c){t instanceof Map&&c instanceof Map?c.forEach((s,e)=>t.set(e,s)):t instanceof Set&&c instanceof Set&&c.forEach(t.add,t);for(const s in c){if(!c.hasOwnProperty(s))continue;const e=c[s],a=t[s];k(a)&&k(e)&&t.hasOwnProperty(s)&&!x(e)&&!V(e)?t[s]=L(a,e):t[s]=e}return t}const at=Symbol();function ft(t){return!k(t)||!Object.prototype.hasOwnProperty.call(t,at)}const{assign:h}=Object;function lt(t){return!!(x(t)&&t.effect)}function it(t,c,s,e){const{state:a,actions:r,getters:u}=c,C=s.state.value[t];let y;function b(){C||(s.state.value[t]=a?a():{});const S=ct(s.state.value[t]);return h(S,r,Object.keys(u||{}).reduce((v,_)=>(v[_]=M(rt(()=>{E(s);const m=s._s.get(t);return u[_].call(m,m)})),v),{}))}return y=K(t,b,c,s,e,!0),y}function K(t,c,s={},e,a,r){let u;const C=h({actions:{}},s),y={deep:!0};let b,S,v=[],_=[],m;const j=e.state.value[t];!r&&!j&&(e.state.value[t]={}),H({});let W;function N(o){let n;b=S=!1,typeof o=="function"?(o(e.state.value[t]),n={type:w.patchFunction,storeId:t,events:m}):(L(e.state.value[t],o),n={type:w.patchObject,payload:o,storeId:t,events:m});const f=W=Symbol();ot().then(()=>{W===f&&(b=!0)}),S=!0,p(v,n,e.state.value[t])}const Q=r?function(){const{state:n}=s,f=n?n():{};this.$patch(d=>{h(d,f)})}:J;function U(){u.stop(),v=[],_=[],e._s.delete(t)}const A=(o,n="")=>{if(z in o)return o[I]=n,o;const f=function(){E(e);const d=Array.from(arguments),P=[],R=[];function Y(l){P.push(l)}function Z(l){R.push(l)}p(_,{args:d,name:f[I],store:i,after:Y,onError:Z});let g;try{g=o.apply(this&&this.$id===t?this:i,d)}catch(l){throw p(R,l),l}return g instanceof Promise?g.then(l=>(p(P,l),l)).catch(l=>(p(R,l),Promise.reject(l))):(p(P,g),g)};return f[z]=!0,f[I]=n,f},X={_p:e,$id:t,$onAction:F.bind(null,_),$patch:N,$reset:Q,$subscribe(o,n={}){const f=F(v,o,n.detached,()=>d()),d=u.run(()=>nt(()=>e.state.value[t],P=>{(n.flush==="sync"?S:b)&&o({storeId:t,type:w.direct,events:m},P)},h({},y,n)));return f},$dispose:U},i=T(X);e._s.set(t,i);const O=(e._a&&e._a.runWithContext||ut)(()=>e._e.run(()=>(u=D()).run(()=>c({action:A}))));for(const o in O){const n=O[o];if(x(n)&&!lt(n)||V(n))r||(j&&ft(n)&&(x(n)?n.value=j[o]:L(n,j[o])),e.state.value[t][o]=n);else if(typeof n=="function"){const f=A(n,o);O[o]=f,C.actions[o]=n}}return h(i,O),h(tt(i),O),Object.defineProperty(i,"$state",{get:()=>e.state.value[t],set:o=>{N(n=>{h(n,o)})}}),e._p.forEach(o=>{h(i,u.run(()=>o({store:i,app:e._a,pinia:e,options:C})))}),j&&r&&s.hydrate&&s.hydrate(i.$state,j),b=!0,S=!0,i}/*! #__NO_SIDE_EFFECTS__ */function St(t,c,s){let e;const a=typeof c=="function";e=a?s:c;function r(u,C){const y=G();return u=u||(y?$(B,null):null),u&&E(u),u=q,u._s.has(t)||(a?K(t,c,e,u):it(t,e,u)),u._s.get(t)}return r.$id=t,r}export{yt as c,St as d};
