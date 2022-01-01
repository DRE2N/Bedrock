package de.erethon.bedrock.compatibility;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This enumeration represents package versions.
 *
 * @author Daniel Saukel, Fyreum
 */
public enum Internals {

    /**
     * Represents upcoming CraftBukkit versions.
     * <br>
     * toString() returns the actual internals version instead of "NEW"
     */
    NEW(true),
    v1_18_R1(true),
    v1_17_R1(true),
    v1_16_R3(true),
    v1_16_R2(true),
    v1_16_R1(true),
    /**
     * Represents internals that are older than Minecraft 1.16.
     */
    OUTDATED(true),
    /**
     * Represents an implementation other than CraftBukkit.
     */
    UNKNOWN(false);

    private final boolean craftBukkitInternals;

    Internals(boolean craftBukkitInternals) {
        this.craftBukkitInternals = craftBukkitInternals;
    }

    /**
     * Returns if the server uses CraftBukkit internals
     *
     * @return true if the server uses CraftBukkit internals
     */
    public boolean useCraftBukkitInternals() {
        return craftBukkitInternals;
    }

    @Override
    public String toString() {
        if (this == NEW) {
            return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } else {
            return name();
        }
    }

    /**
     * Returns a Set of all internals that are equal to or higher than the environment version
     *
     * @return a Set of all internals that are equal to or higher than the environment version
     */
    public Set<Internals> andHigher() {
        return andHigher(this);
    }

    /**
     * Returns if the environment version is equal to or higher than the provided internals version
     *
     * @return if the environment version is equal to or higher than the provided internals version
     */
    public boolean isAtLeast() {
        return isAtLeast(this);
    }

    /**
     * Returns if the environment version is equal to or lower than the provided internals version
     *
     * @return if the environment version is equal to or lower than the internals version
     */
    public boolean isAtMost() {
        return isAtMost(this);
    }

    /* Statics */

    /**
     * Contains all values of this enumeration.
     */
    public static final Set<Internals> INDEPENDENT = new HashSet<>(Arrays.asList(Internals.values()));

    /**
     * Returns a Set of all internals that are equal to or higher than the environment version
     *
     * @param internals the oldest internals in the Set
     * @return a Set of all internals that are equal to or higher than the environment version
     */
    public static Set<Internals> andHigher(Internals internals) {
        Internals[] values = values();
        return new HashSet<>(Arrays.asList(values).subList(0, internals.ordinal() + 1));
    }

    /**
     * Returns if the environment version is equal to or higher than the provided internals version
     *
     * @param internals the minimum internals version to check
     * @return if the environment version is equal to or higher than the provided internals version
     */
    public static boolean isAtLeast(Internals internals) {
        return andHigher(internals).contains(CompatibilityHandler.getInstance().getInternals());
    }

    /**
     * Returns if the environment version is equal to or lower than the provided internals version
     *
     * @param internals the maximum internals to check
     * @return if the environment version is equal to or lower than the internals version
     */
    public static boolean isAtMost(Internals internals) {
        return internals == CompatibilityHandler.getInstance().getInternals() || !isAtLeast(internals);
    }

}
