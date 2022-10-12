package de.rytrox.spicy.skin.mineskin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MineskinTest {

    @Test
    public void shouldConvertJsonToObject() throws IOException {
        JsonObject object = JsonParser.parseString(Files.readString(Paths.get("src", "test", "resources", "mineskin", "skin.json"))).getAsJsonObject();
        Mineskin mineskin = new Mineskin(object);

        GameProfile targetGameProfile = new GameProfile(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), "test");
        targetGameProfile.getProperties().put("textures", new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTYwNzcxNTY2MDI2OSwKICAicHJvZmlsZUlkIiA6ICI5NGRkMjM0YTIzMjA0ZjE3OWY0MGExY2M4MGFmYWIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJEZXJUaW1lb3V0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgxNDc4NDg2YjY4ZmQwYzkyNTViZDRhYzdmZjI4ZDFhYjhkYjZkODFlZGEwMWVlY2IzMTkwMjA5ODVmZWNhOWYiCiAgICB9CiAgfQp9",
                "DFii9Dqfq6/QrGWJ0rOn9qrlOY00/M+bFkU/73PJxFybAlOUVzeq59PnHlKisaoCLZJ27yzwxfn7SRQ0XyFdI3SMaKS6zEL1LJSPCuYN1/Tz8tjYo3dCS+cYL3g2Z1cmKT2HxwHTtg85jRk+ShYQt6QX/lXDG9J06RrTsbQJVuvtVUXipKzIyYn0Q3NroGib2uqkONet11OtDk5RrxzehhISQedjCUhCGzkneYIM/i4wVZwDV/SCBy+98Jk1W+xj1Dv3xNBwvxmgKi597r40P6/dikCbGhe0jMbnUFc/rhUoEw8mPFgeRw61EqfdR6uzbfC03YMN6wjrUaqTneAIDlDUl4Ik2e2xXAk7kmE9nAzcPa1Z99armmUhOXpb/j+WUjd8O8LTzBub8L0E8zB8g4guL45z6VTp1QYp1Cv930xVRkLK5lu6+H155Tne3+wV7+AtyC0G3I6xJ/jzpBT26K7Lfng7JwfWgPZEJnbv7maN9DjmxA1URCjPSwz00LdNFsKXGQ6m7+Cwj/0YRP3a6y1AHp31wZzI4JAx+8cGIJBPQzEGGAgyx0uJXwA+aLOo1BoScbjzzWAgDgSALpQSx/sn2R6fv69kpLCYESgf+Jms9UYq+HOxfG6z/4xxnYoaZbcQhttwtbR7Z05bbi//SUPGLbyzMyqKz2fsWdQs16M="));

        assertEquals(101, mineskin.getID());
        assertEquals(53, mineskin.getAccountId());
        assertEquals(1002, mineskin.getDuration());
        assertEquals(0, mineskin.getViews());
        assertEquals(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"), mineskin.getUniqueID());
        assertEquals("test", mineskin.getName());
        assertEquals(1625947177503L, mineskin.getTimestamp());
        assertTrue(mineskin.isPrivateSkin());
        assertEquals(targetGameProfile, mineskin.getData());
    }

    @Test
    public void shouldNotConvertIllegalJson() throws IOException {
        JsonObject object = JsonParser.parseString(Files.readString(Paths.get("src", "test", "resources", "mineskin", "skin.json"))).getAsJsonObject();
        object.addProperty("uuid", "Hello World");

        assertThrows(IllegalArgumentException.class, () -> new Mineskin(object));
    }
}
