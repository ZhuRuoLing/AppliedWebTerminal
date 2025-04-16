import{_ as F,a as q,b as I,c as U,d as K}from"./components-ChjZFr03.js";import{d as C,c as f,o as c,a as e,r as m,B as j,i as G,k as P,b as _,e as l,g as b,t as x,l as M,f as B,F as A,h as D,X as O,m as W,j as X}from"./node-modules_@vue-spdbOBV8.js";import{E as Y,h as L,f as H,i as J,d as N,e as R,j as Q,g as Z,k as ee,l as te,b as ae}from"./node-modules_element-plus-DlUbQDO9.js";import{t as k,l as ne,b as oe,c as le}from"./core-DWdunkx_.js";import{a as V,c as se}from"./data-Dg-AU-9l.js";import{L as E,c as re}from"./utils-BS2FQLbZ.js";const ie={class:"flex flex-wrap items-center"},Ce=C({__name:"DevTest",setup(T){const a={translate:"block.ae2.sky_stone_block"},n=["Available: 800.0M"],u={what:{id:"ae2:sky_stone_tank",displayName:'{"translate": "block.ae2.sky_stone_tank"}',type:"ae2_i"},craftAmount:1e8},i={what:{id:"ae2:quartz_glass",displayName:'{"translate": "block.ae2.quartz_glass"}',type:"ae2_i"},missingAmount:99999900,storedAmount:100},t={what:{id:"ae2:sky_stone_block",displayName:'{"translate": "block.ae2.sky_stone_block"}',type:"ae2_i"},storedAmount:8e8},s={what:{id:"minecraft:waxed_weathered_cut_copper_stairs",displayName:'{"translate": "block.minecraft.waxed_weathered_cut_copper_stairs"}',type:"ae2_i"},storedAmount:8e8};return(p,g)=>{const v=F,r=q;return c(),f("div",ie,[e(v,{text:a,tooltips:n}),e(r,{entry:u}),e(r,{entry:i}),e(r,{entry:t}),e(r,{entry:s})])}}}),ce=C({__name:"DialogTerminalLogin",setup(T){const a=V(),n=se(),u=m(!1),i=m(!0),t=m(""),s=m(!1);j(()=>{const r=n.getPassword(a.currentTerminal.uuid);r&&(t.value=r,s.value=!0)});function p(){u.value||(a.currentTerminal=void 0)}function g(){const r=a.currentTerminal.uuid;s.value&&n.storePassword(r,t.value),u.value=!0,ne(r,t.value).then(o=>{typeof o=="string"?(a.webSocketService.setToken(o),a.inTerminalPage=!0,a.token=o):Q.alert(`Error on login request: ${o}`,"Login Failed",{type:"error",confirmButtonText:"OK",beforeClose:(d,w,y)=>{u.value=!1,y()}}).then(()=>{u.value=!1})})}const v=G(()=>{const r=document.getElementsByTagName("html")[0].clientWidth,y=re(.6*r,300,350);return E.debug(`dialog width: ${y}`),y});return(r,o)=>{const d=L,w=H,y=J,S=R,z=N,$=Y;return c(),P($,{modelValue:i.value,"onUpdate:modelValue":o[3]||(o[3]=h=>i.value=h),title:_(k)("main.terminals.login.title"),width:`${v.value}px`,"before-close":p},{footer:l(()=>[e(z,{justify:"end"},{default:l(()=>[e(S,{class:"w-100px",disabled:u.value,onClick:p},{default:l(()=>[b(x(_(k)("common.button.cancel")),1)]),_:1},8,["disabled"]),e(S,{type:"primary",class:"w-100px",disabled:u.value,onClick:g},{default:l(()=>[b(x(_(k)("main.terminals.login.button.login")),1)]),_:1},8,["disabled"])]),_:1})]),default:l(()=>[e(d,{type:"info",size:"small",class:"font-italic"},{default:l(()=>[b(x(_(k)("main.terminals.login.label.terminal_name")),1)]),_:1}),e(d,{size:"large",class:"mx-2 my-2 block b-s-2 b-#555 b-s-solid p-2 font-bold"},{default:l(()=>[b(x(_(a).currentTerminal.name),1)]),_:1}),e(w,{modelValue:t.value,"onUpdate:modelValue":o[0]||(o[0]=h=>t.value=h),type:"password","show-password":"",autocomplete:"off",placeholder:"Password",disabled:u.value,class:"my-2",onKeydown:o[1]||(o[1]=h=>{h.key==="Enter"&&g()})},null,8,["modelValue","disabled"]),e(y,{modelValue:s.value,"onUpdate:modelValue":o[2]||(o[2]=h=>s.value=h),label:_(k)("main.terminals.login.button.remember_password")},null,8,["modelValue","label"])]),_:1},8,["modelValue","title","width"])}}}),ue={key:0},de={class:"mx-a h-full max-w-2000px min-w-300px w-70%"},_e={key:0,class:"m-4 flex flex-wrap items-stretch justify-center gap-4"},me={key:1},pe={key:2},Se=C({__name:"MainPage",setup(T){const a=V(),n=m(!1);function u(){oe().then(t=>{a.terminals=t}).catch(t=>{E.error("Error while reloading terminals list:"),E.error(t),n.value=!0})}u();function i(t){a.currentTerminal=t,E.info("Selected Terminal: "),E.info(t)}return(t,s)=>{const p=L,g=R,v=N,r=Z,o=I;return c(),f(A,null,[_(a).currentTerminal?(c(),f("div",ue,[e(ce)])):M("",!0),B("div",de,[e(v,{class:"mx-4 my-2",justify:"space-between"},{default:l(()=>[e(p,{size:"large",class:"font-bold"},{default:l(()=>[b(x(_(k)("main.terminals.title")),1)]),_:1}),e(g,{circle:"",disabled:!_(a).terminals&&!n.value,onClick:u},{default:l(()=>s[0]||(s[0]=[B("span",{class:"material-symbols-outlined"}," refresh ",-1)])),_:1},8,["disabled"])]),_:1}),e(r,{class:"my-1"}),_(a).terminals?(c(),f("div",_e,[(c(!0),f(A,null,D(_(a).terminals,d=>(c(),f("div",{key:d.uuid},[e(o,{"terminal-info":d,"on-click":i},null,8,["terminal-info"])]))),128))])):n.value?(c(),f("div",me," Error while loading terminals list! ")):(c(),f("div",pe," loading... "))])],64)}}}),fe={};function ge(T,a){return" (crafting) "}const ve=U(fe,[["render",ge]]),ye=15*5,be=C({__name:"StoragePage",setup(T){const a=V(),n=m("BY_NAME"),u=m([]),i=m(0),t=m(void 0),s=m(!1),p=m(null);function g(){if(t.value!==void 0&&i.value>t.value.totalPages){E.info(`refused to load more because loadedPage(${i.value}) > pageMeta(${t.value.totalPages})`);return}s.value||(E.info(`load more store: page(${i.value})`),s.value=!0,le(i.value,ye,n.value,a.token).then(d=>{d&&(u.value.push(...d.data),t.value=d.meta,i.value+=1),s.value=!1}))}g();const v=m("");function r(d){o()}function o(){if(p.value){const d=p.value.getBoundingClientRect(),w=Math.floor(d.width/64),y=(d.width-w*64)/2;v.value=`padding-left: ${y}px;`}}return j(()=>{o(),window.addEventListener("resize",r)}),O(()=>{window.removeEventListener("resize",r)}),(d,w)=>{const y=K,S=L,z=ee;return c(),f("div",{ref_key:"containerRef",ref:p},[W((c(),f("div",{"infinite-scroll-distance":50,class:"flex flex-wrap items-center overflow-y-scroll h-80vh",style:X(v.value)},[(c(!0),f(A,null,D(u.value,$=>(c(),P(y,{key:$.what.id,stack:$},null,8,["stack"]))),128))],4)),[[z,g]]),s.value?(c(),P(S,{key:0,size:"large"},{default:l(()=>w[0]||(w[0]=[b(" Loading... ")])),_:1})):M("",!0)],512)}}}),we={class:"terminal_page m-4 mx-a max-w-1000px min-w-300px w-80% font-bold"},$e=C({__name:"TerminalPage",setup(T){const a=V(),n=m();return(u,i)=>{const t=L,s=N,p=R,g=te,v=ae;return c(),f("div",we,[e(v,{class:"bg-transparent backdrop-blur-lg z-500"},{default:l(()=>[e(s,{justify:"space-between"},{default:l(()=>[e(s,null,{default:l(()=>[e(t,{size:"large",type:"primary"},{default:l(()=>[b(x(_(a).currentTerminal.name),1)]),_:1})]),_:1}),e(s,{class:"float-right"},{default:l(()=>[e(g,null,{default:l(()=>[e(p,{type:n.value===0?"primary":"default",disabled:n.value===0,onClick:i[0]||(i[0]=()=>{n.value=0})},{default:l(()=>[b(x(_(k)("terminal.button.storage_page")),1)]),_:1},8,["type","disabled"]),e(p,{type:n.value===1?"primary":"default",disabled:n.value===1,onClick:i[1]||(i[1]=()=>{n.value=1})},{default:l(()=>[b(x(_(k)("terminal.button.crafting_page")),1)]),_:1},8,["type","disabled"])]),_:1})]),_:1})]),_:1})]),_:1}),B("div",null,[n.value===0?(c(),P(be,{key:0})):n.value===1?(c(),P(ve,{key:1})):M("",!0)])])}}});export{Ce as _,$e as a,Se as b};
