package rando.beasts.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class ToolSetJellyWood extends BeastsToolSet {
    public ToolSetJellyWood() {
        super(Item.ToolMaterial.WOOD, "jelly");
    }

    @Override
    boolean damageEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if(!target.isPotionActive(MobEffects.POISON)) {
            PotionEffect effect = new PotionEffect(MobEffects.POISON, 100);
            if (target.isPotionApplicable(effect)) target.addPotionEffect(effect);
            return true;
        }
        return super.damageEntity(stack, target, attacker);
    }
}
