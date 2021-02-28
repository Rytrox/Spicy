package de.timeout.libs.config;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class VectorExample {

    public void drawArray(Player player, int verticalOffset, Color[][] image, double pixelGap, double size) {
        // First of all, we need a player or a looking direction and his position.
        // Our goal is to build a straight like g: x = s_Vector + r * direction_Vector

        // This is our start vector. We need this vector later
        Vector position = player.getLocation().toVector();

        // Let's start with the looking direction itself...
        Vector lookingDirection = player.getTargetBlock((HashSet<Material>) null, 100) // get the block where the player is looking at
                .getLocation() // get its location
                .toVector() // changes it to vector
                .subtract(position); // and subtract the player's position, so it's the looking direction
        // Now, let's move on the projection of the looking direction (shadow)
        Vector projection = new Vector(lookingDirection.getX(), 0D, lookingDirection.getZ());

        // The cross product returns a perpendicular vector of two vectors. That's our searched direction...
        // We also need to normalize it, so it has a length of 1
        Vector direction = lookingDirection.crossProduct(projection).normalize();


        // And now we have our straight. Starting at out player's position we can draw our line
        // The rest is up to you!
    }
}
