package me.yamakaja.devathon2016.util.gson;

import com.google.gson.*;
import me.yamakaja.devathon2016.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class ItemStackTypeAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonStack = jsonElement.getAsJsonObject();
        ItemBuilder builder = new ItemBuilder(Material.valueOf(jsonStack.get("type").getAsString()), jsonStack
                .get("amount").getAsInt(),jsonStack.get("damage").getAsShort());
        if(jsonStack.has("meta")) {
            JsonObject meta = jsonStack.get("meta").getAsJsonObject();
            if(meta.has("displayName")) {
                builder.setDisplayName(meta.get("displayName").getAsString());
            }
            if(meta.has("lore")) {
                List<String> lore = new ArrayList<>();
                meta.get("lore").getAsJsonArray().forEach(element -> lore.add(element.getAsString()));
                builder.setLore(lore);
            }
        }

        return builder;
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonStack = new JsonObject();
        jsonStack.add("type", new JsonPrimitive(itemStack.getType().toString()));
        jsonStack.add("amount", new JsonPrimitive(itemStack.getAmount()));
        jsonStack.add("damage", new JsonPrimitive(itemStack.getDurability()));
        if(itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            JsonObject jsonMeta = new JsonObject();
            if(itemMeta.hasDisplayName())
                jsonMeta.add("displayName", new JsonPrimitive(itemMeta.getDisplayName()));
            if(itemMeta.hasLore()) {
                JsonArray jsonLoreArray = new JsonArray();
                List<String> lore = itemMeta.getLore();
                lore.forEach(line -> jsonLoreArray.add(new JsonPrimitive(line)));
                jsonMeta.add("lore", jsonLoreArray);
            }
            jsonStack.add("meta", jsonMeta);
        }
        return jsonStack;
    }

}
