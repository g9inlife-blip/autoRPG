(() => {
  const roots=['dungeon','battle','loot','log'];
  const replaceFloorText=()=>roots.map(id=>document.getElementById(id)).filter(Boolean).forEach(root=>{const html=root.innerHTML.replace(/층/g,'구역');if(html!==root.innerHTML)root.innerHTML=html;});
  const wrap=(name)=>{const original=window[name];if(typeof original!=='function'||original._areaTerminologyPatch)return;const wrapped=function(...args){const result=original.apply(this,args);replaceFloorText();return result;};wrapped._areaTerminologyPatch=true;window[name]=wrapped;};
  wrap('render');wrap('showLogDetail');
  replaceFloorText();
})();
