package de.erethon.bedrock.misc;

/**
 * @since 1.1.0
 * @author Fyreum
 */
public record StringIgnoreCase(String string) {

    @Override
    public int hashCode() {
        return string().toLowerCase().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof StringIgnoreCase s) {
            return string().equalsIgnoreCase(s.string());
        }
        return false;
    }

    @Override
    public String toString() {
        return string;
    }

}
