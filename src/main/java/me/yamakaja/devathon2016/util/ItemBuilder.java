package me.yamakaja.devathon2016.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Yamakaja on 05.11.16.
 */
@SuppressWarnings("WeakerAccess")
public class ItemBuilder extends ItemStack {

    public ItemBuilder(ItemStack stack) {
        super(stack);
    }

    public ItemBuilder(Material type) {
        super(type);
    }

    public ItemBuilder(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    public ItemBuilder(Material type, int amount) {
        super(type, amount);
    }

    public ItemBuilder(String skullOwner) {
        super(Material.SKULL_ITEM, 1, (short) 3);
        this.modifyMeta(meta -> ((SkullMeta) meta).setOwner(skullOwner));
    }

    public ItemBuilder(String title, String author, List<String> pages) {
        super(Material.WRITTEN_BOOK);
        this.modifyMeta(meta -> {
            ((BookMeta) meta).setAuthor(author);
            ((BookMeta) meta).setTitle(title);
            ((BookMeta) meta).setPages(pages);
        });
    }

    public ItemBuilder(PotionData basePotionData, Map<PotionEffect, Boolean> customEffects, boolean splash) {
        super(splash ? Material.SPLASH_POTION : Material.POTION);
        this.modifyMeta(meta -> {
            PotionMeta pMeta = (PotionMeta) meta;
            pMeta.setBasePotionData(basePotionData);
            if (customEffects == null) return;
            customEffects.forEach(pMeta::addCustomEffect);
        });
    }

    public ItemBuilder setLore(List<String> lore) {
        this.modifyMeta(meta -> meta.setLore(lore));
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        this.modifyMeta(meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.addAll(Arrays.asList(lines));
            meta.setLore(lore);
        });
        return this;
    }

    public String getLore(int line) throws IndexOutOfBoundsException {
        return this.getItemMeta().getLore().get(line);
    }

    public ItemBuilder clearMeta() {
        this.setItemMeta(Bukkit.getItemFactory().getItemMeta(this.getType()));
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        this.modifyMeta(meta -> meta.setDisplayName(name));
        return this;
    }

    public ItemBuilder clearDisplayName() {
        setDisplayName("");
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean unsafe) {
        if (unsafe) this.addUnsafeEnchantment(enchantment, level);
        else super.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchs, boolean unsafe) {
        if (unsafe) this.addUnsafeEnchantments(enchs);
        else this.addEnchantments(enchs);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        this.getEnchantments().forEach((ench, level) -> this.removeEnchantment(ench));
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        this.modifyMeta(meta -> meta.addItemFlags(flags));
        return this;
    }

    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        this.modifyMeta(meta -> meta.removeItemFlags(flags));
        return this;
    }

    public ItemBuilder modifyMeta(Consumer<ItemMeta> metaConsumer) {
        ItemMeta meta = this.hasItemMeta() ? this.getItemMeta() : Bukkit.getItemFactory().getItemMeta(this.getType());
        metaConsumer.accept(meta);
        this.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setCount(int amount) {
        this.setAmount(amount);
        return this;
    }
}
