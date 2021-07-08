package de.timeout.libs.skin;

import com.mojang.authlib.GameProfile;
import de.timeout.libs.reflect.Reflections;
import org.apache.commons.lang.reflect.FieldUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkinBuilder implements Future<Skin> {

    private static final Class<?> tileentityskullClass = Reflections.getNMSClass("TileEntitySkull");

    private static LoadingCache<String, GameProfile> yggdrasilSessionCache;


    static {
        try {
            yggdrasilSessionCache = (LoadingCache<String, GameProfile>)
                    FieldUtils.readDeclaredStaticField(tileentityskullClass, "skinCache", true);
        } catch (IllegalAccessException e) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot get SkinCache, initializing new one");
        }
    }

    public SkinBuilder() {

    }
}
