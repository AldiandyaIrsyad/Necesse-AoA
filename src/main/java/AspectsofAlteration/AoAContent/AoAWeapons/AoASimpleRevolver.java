package AspectsofAlteration.AoAContent.AoAWeapons;

import necesse.engine.Screen;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.gfx.GameResources;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;

public class AoASimpleRevolver extends GunProjectileToolItem {
    public AoASimpleRevolver() {
        super(NORMAL_AMMO_TYPES, 500);
        this.rarity = Rarity.COMMON;
        this.animSpeed = 400;
        this.attackDamage = new GameDamage(DamageTypeRegistry.RANGED, 23.0F);
        this.attackXOffset = 7;
        this.attackYOffset = 5;
        this.attackRange = 800;
        this.velocity = 350;
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    public void playFireSound(AttackAnimMob mob) {
        Screen.playSound(GameResources.handgun, SoundEffect.effect(mob));
    }
}
