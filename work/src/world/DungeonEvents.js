(() => {
  const originalStep = DungeonRun.prototype.step;
  DungeonRun.prototype.step = function(...args) {
    const totalAreas = typeof this.areaCount === 'function' ? this.areaCount() : Number(this.dungeon?.areaCount || this.dungeon?.floors || 0);
    if (this.active && this.floor > 0 && this.rng.int(1,100) <= 2) {
      for (const c of this.party || []) {
        if (!c) continue;
        c.hp = Math.min(c.maxHp, c.hp + Math.ceil(c.maxHp * 0.30));
        if (Number.isFinite(c.maxMp)) c.mp = Math.min(c.maxMp, (Number(c.mp)||0) + Math.ceil(c.maxMp * 0.30));
      }
      this.emit('HEALING_SPRING', {
        hpPercent:30, mpPercent:30,
        characters:(this.party||[]).map(c=>({id:c.id,name:c.name,hp:c.hp,maxHp:c.maxHp,mp:c.mp,maxMp:c.maxMp}))
      });
      if (this.floor === totalAreas) {
        this.complete=true; this.active=false; this.emit('RUN_COMPLETE',{floor:this.floor,areaCount:totalAreas});
      } else this.nextFloor();
      return {done:this.complete,battle:null};
    }
    return originalStep.apply(this,args);
  };
})();
