import{h as l,i as f}from"./node-modules_element-plus-CM9U3G2a.js";import{b as m}from"./components-1e0OC7d9.js";import{u,f as d,a as g}from"./data-CyNSjlnJ.js";import{_,a as h}from"./pages-MosX8vdl.js";import{d as y,p as C,e as n,o as c,w as v,b as w,u as O,av as P}from"./node-modules_@vue-BE5T86hj.js";/* empty css               */import{c as b}from"./node-modules_pinia-DRyLmZMT.js";import"./node-modules_lodash-es-S0Y0Up6J.js";import"./node-modules_@element-plus-DCQN2kKc.js";import"./node-modules_@popperjs-D_chPuIy.js";import"./node-modules_@ctrl-r5W6hzzQ.js";import"./node-modules_dayjs-BC8lxUvW.js";import"./node-modules_async-validator-9PlIezaS.js";import"./node-modules_memoize-one-BdPwpGay.js";import"./node-modules_normalize-wheel-es-BQoi3Ox2.js";import"./node-modules_@floating-ui-DwceP2Gb.js";import"./composables-D9siU0Xv.js";import"./node-modules_@vueuse-DHBqWc5D.js";import"./node-modules_sprintf-js-D7DtBTRn.js";import"./core-QeicWyxs.js";import"./utils-DPPkojbW.js";(function(){const t=document.createElement("link").relList;if(t&&t.supports&&t.supports("modulepreload"))return;for(const e of document.querySelectorAll('link[rel="modulepreload"]'))r(e);new MutationObserver(e=>{for(const o of e)if(o.type==="childList")for(const i of o.addedNodes)i.tagName==="LINK"&&i.rel==="modulepreload"&&r(i)}).observe(document,{childList:!0,subtree:!0});function s(e){const o={};return e.integrity&&(o.integrity=e.integrity),e.referrerPolicy&&(o.referrerPolicy=e.referrerPolicy),e.crossOrigin==="use-credentials"?o.credentials="include":e.crossOrigin==="anonymous"?o.credentials="omit":o.credentials="same-origin",o}function r(e){if(e.ep)return;e.ep=!0;const o=s(e);fetch(e.href,o)}})();const L=y({__name:"App",setup(p){const t=u();d(r=>{t.serverConfig=r,document.title=t.serverConfig.title});const s=g();return C(t.localConfig,()=>{s.localConfigStoreManager.set(t.localConfig)}),(r,e)=>{const o=m,i=l;return c(),n(i,{class:"h-full min-h-100vh w-full flex flex-col items-stretch"},{default:v(()=>[w(o),O(s).inTerminalPage?(c(),n(_,{key:0,class:"grow"})):(c(),n(h,{key:1,class:"grow"}))]),_:1})}}}),a=P(L),S=b();a.use(f);a.use(S);a.mount("#app");
