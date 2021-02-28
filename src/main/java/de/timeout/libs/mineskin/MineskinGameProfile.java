package de.timeout.libs.mineskin;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class MineskinGameProfile extends GameProfile {

    private int id;

    public MineskinGameProfile(UUID id, String name) {
        super(id, name);
    }
}
