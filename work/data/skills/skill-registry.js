const SKILL_REGISTRY = {
  slash: { id:'slash', name:'강타', classId:'warrior', cooldown:2, multiplier:1.35, dialogueAction:'skill', description:'강한 일격으로 큰 피해를 줍니다.' },
  fireball: { id:'fireball', name:'화염구', classId:'mage', cooldown:3, multiplier:1.55, dialogueAction:'skill', description:'마법 공격으로 높은 피해를 줍니다.' },
  'quick-strike': { id:'quick-strike', name:'연속 베기', classId:'rogue', cooldown:2, multiplier:1.2, extraHit:true, dialogueAction:'skill', description:'빠르게 두 번 공격합니다.' }
};
window.SKILL_REGISTRY = SKILL_REGISTRY;
