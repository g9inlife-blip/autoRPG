class MonsterScaling {
  static CONFIG={maxLevel:50,curves:{hp:1.5,attack:1.2,exp:1.6},base:{hp:20,attack:5,defense:2,exp:10},multipliers:{normal:{normal:{hp:1,attack:1,defense:1,exp:1},enhanced:{hp:1.5,attack:1.3,defense:1.2,exp:1.6}},rare:{normal:{hp:3,attack:1.8,defense:1.5,exp:3.5},enhanced:{hp:4.5,attack:2.2,defense:1.8,exp:5.5}},boss:{normal:{hp:12,attack:3.5,defense:2.5,exp:15},enhanced:{hp:20,attack:4.5,defense:3.2,exp:30}}}};
  static clampLevel(level){return Math.max(1,Math.min(this.CONFIG.maxLevel,Number(level)||1));}
  static calculate(level,type='normal',form='normal',themeMultiplier={}){const l=this.clampLevel(level),c=this.CONFIG.curves,b=this.CONFIG.base,m=this.CONFIG.multipliers[type]?.[form]||this.CONFIG.multipliers.normal.normal,t=themeMultiplier||{};return {level:l,hp:Math.round(b.hp*Math.pow(l,c.hp)*m.hp*(t.hp||1)),attack:Math.round(b.attack*Math.pow(l,c.attack)*m.attack*(t.attack||1)),defense:Math.round(b.defense*l*m.defense*(t.defense||1)),exp:Math.round(b.exp*Math.pow(l,c.exp)*m.exp*(t.exp||1))};}
  static create(spec={}){const s=this.calculate(spec.level,spec.type,spec.form,spec.multiplier);return Object.assign({},spec,s);}
}
window.MonsterScaling=MonsterScaling;
