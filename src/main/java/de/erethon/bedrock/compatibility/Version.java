package de.erethon.bedrock.compatibility;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static de.erethon.bedrock.compatibility.Internals.*;

/**
 * This enumeration represents the Minecraft version and is supposed not to be implementation specific.
 *
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public enum Version {

    /**
     * Represents upcoming versions.
     * <p>
     * getCraftBukkitInternals() might return a known package version or NEW.
     */
    NEW(true, true, true, Internals.NEW),
    MC1_20_4(true, true, true, v1_20_R3),
    MC1_20_3(true, true, true, v1_20_R3),
    MC1_20_2(true, true, true, v1_20_R2),
    MC1_20_1(true, true, true, v1_20_R1),
    MC1_20(true, true, true, v1_20_R1),
    MC1_19_4(true, true, true, v1_19_R3),
    MC1_19_3(true, true, true, v1_19_R2),
    MC1_19_2(true, true, true, v1_19_R1),
    MC1_19_1(true, true, true, v1_19_R1),
    MC1_19(true, true, true, v1_19_R1),
    MC1_18_2(true, true, true, v1_18_R2),
    MC1_18_1(true, true, true, OUTDATED),
    MC1_18(true, true, true, OUTDATED),
    MC1_17_1(true, true, true, OUTDATED),
    MC1_17(true, true, true, OUTDATED),
    MC1_16_5(true, true, true, OUTDATED),
    MC1_16_4(true, true, true, OUTDATED),
    MC1_16_3(true, true, true, OUTDATED),
    MC1_16_2(true, true, true, OUTDATED),
    MC1_16_1(true, true, true, OUTDATED),
    MC1_16(true, true, true, OUTDATED),
    MC1_15_2(true, true, true, OUTDATED),
    MC1_15_1(true, true, true, OUTDATED),
    MC1_15(true, true, true, OUTDATED),
    MC1_14_4(true, true, true, OUTDATED),
    MC1_14_3(true, true, true, OUTDATED),
    MC1_14_2(true, true, true, OUTDATED),
    MC1_14_1(true, true, true, OUTDATED),
    MC1_14(true, true, true, OUTDATED),
    MC1_13_2(true, true, true, OUTDATED),
    MC1_13_1(true, true, true, OUTDATED),
    MC1_13(true, true, true, OUTDATED),
    MC1_12_2(true, true, false, OUTDATED),
    MC1_12_1(true, true, false, OUTDATED),
    MC1_12(true, true, false, OUTDATED),
    MC1_11_2(true, true, false, OUTDATED),
    MC1_11_1(true, true, false, OUTDATED),
    MC1_11(true, true, false, OUTDATED),
    MC1_10_2(true, false, false, OUTDATED),
    MC1_10_1(true, false, false, OUTDATED),
    MC1_10(true, false, false, OUTDATED),
    MC1_9_4(true, false, false, OUTDATED),
    MC1_9_2(true, false, false, OUTDATED),
    MC1_9(true, false, false, OUTDATED),
    MC1_8_9(true, false, false, OUTDATED),
    MC1_8_8(true, false, false, OUTDATED),
    MC1_8_7(true, false, false, OUTDATED),
    MC1_8_6(true, false, false, OUTDATED),
    MC1_8_5(true, false, false, OUTDATED),
    MC1_8_4(true, false, false, OUTDATED),
    MC1_8_3(true, false, false, OUTDATED),
    MC1_8_1(true, false, false, OUTDATED),
    MC1_8(true, false, false, OUTDATED),
    MC1_7_10(true, false, false, OUTDATED),
    MC1_7_9(true, false, false, OUTDATED),
    MC1_7_8(true, false, false, OUTDATED),
    MC1_7_7(true, false, false, OUTDATED),
    MC1_7_6(true, false, false, OUTDATED),
    MC1_7_5(false, false, false, OUTDATED),
    MC1_7_4(false, false, false, OUTDATED),
    MC1_7_2(false, false, false, OUTDATED),
    MC1_6_4(false, false, false, OUTDATED),
    MC1_6_2(false, false, false, OUTDATED),
    MC1_6_1(false, false, false, OUTDATED),
    MC1_5_2(false, false, false, OUTDATED),
    MC1_5_1(false, false, false, OUTDATED),
    MC1_5(false, false, false, OUTDATED),
    MC1_4_7(false, false, false, OUTDATED),
    MC1_4_6(false, false, false, OUTDATED),
    MC1_4_5(false, false, false, OUTDATED),
    MC1_4_4(false, false, false, OUTDATED),
    MC1_4_2(false, false, false, OUTDATED);

    private final boolean uuids;
    private final boolean newMobNames;
    private final boolean newMaterials;
    private final Internals craftBukkitInternals;

    Version(boolean uuids, boolean newMobNames, boolean newMaterials, Internals craftBukkitInternals) {
        this.uuids = uuids;
        this.newMobNames = newMobNames;
        this.newMaterials = newMaterials;
        this.craftBukkitInternals = craftBukkitInternals;
    }

    /**
     * Returns if this version supports UUIDs
     *
     * @return if this version supports UUIDs
     */
    public boolean useUUIDs() {
        return uuids;
    }

    /**
     * Returns if this version uses the mob String IDs introduced in Minecraft 1.11
     *
     * @return if this version uses the mob String IDs introduced in Minecraft 1.11
     */
    public boolean useNewMobNames() {
        return newMobNames;
    }

    /**
     * Returns if this version uses the material String IDs introduced in Minecraft 1.13
     *
     * @return if this version uses the material String IDs introduced in Minecraft 1.13
     */
    public boolean useNewMaterials() {
        return newMaterials;
    }

    /**
     * Returns the package version that CraftBukkit uses for this Minecraft version
     *
     * @return the package version that CraftBukkit uses for this Minecraft version
     */
    public Internals getCraftBukkitInternals() {
        if (this == NEW) {
            try {
                return Internals.valueOf(Internals.NEW.toString());
            } catch (IllegalArgumentException exception) {
                return Internals.NEW;
            }
        } else {
            return craftBukkitInternals;
        }
    }

    @Override
    public String toString() {
        String[] string = super.toString().replace("_", ".").split("MC");

        if (string.length == 2) {
            return string[1];
        } else {
            return string[0];
        }
    }

    /* Statics */

    public static final Set<Version> INDEPENDENT = new HashSet<>(Arrays.asList(Version.values()));

    /**
     * Returns the version String taken directly from the server
     *
     * @return the version string taken directly from the server
     */
    public static Version getByServer() {
        try {
            if (Package.getPackage("org.bukkit.craftbukkit") != null) {
                String versionString = Bukkit.getServer().getVersion().split("\\(MC: ")[1].split("\\)")[0];
                for (Version version : Version.values()) {
                    if (version.toString().equals(versionString)) {
                        return version;
                    }
                }
            } else if (Package.getPackage("net.glowstone") != null) {
                String versionString = Bukkit.getServer().getVersion().split("-")[2];
                for (Version version : Version.values()) {
                    if (version.name().replaceAll("_", ".").equals(versionString)) {
                        return version;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return NEW;
    }

    /**
     * Returns a Set of all versions that are equal to or higher than the provided version
     *
     * @param version the oldest version in the Set
     * @return a Set of all versions that are equal to or higher than the provided version
     */
    public static Set<Version> andHigher(Version version) {
        Version[] values = values();
        return new HashSet<>(Arrays.asList(values).subList(0, version.ordinal() + 1));
    }

    /**
     * Returns if the environment version is equal to or higher than the provided version
     *
     * @param version the minimum version to check
     * @return if the environment version is equal to or higher than the provided version
     */
    public static boolean isAtLeast(Version version) {
        return andHigher(version).contains(CompatibilityHandler.getInstance().getVersion());
    }

    /**
     * Returns if the environment version is equal to or lower than the provided version
     *
     * @param version the maximum version to check
     * @return if the environment version is equal to or lower than the provided version
     */
    public static boolean isAtMost(Version version) {
        return version == CompatibilityHandler.getInstance().getVersion() || !isAtLeast(version);
    }

}
