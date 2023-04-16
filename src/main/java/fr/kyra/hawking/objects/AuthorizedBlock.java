package fr.kyra.hawking.objects;


import org.bukkit.Material;

import java.util.Objects;

public class AuthorizedBlock {

    private final Material material;
    private int mined;

    public AuthorizedBlock(Material material) {
        this.material = material;
        this.mined = 0;
    }

    public AuthorizedBlock(Material material, int mined) {
        this.material = material;
        this.mined = mined;
    }

    public int getMined() {
        return this.mined;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMined(int n) {
        this.mined = n;
    }

    public void addMined(int n) {
        this.mined += n;
    }

    public void subMined(int n) {
        this.mined -= n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizedBlock authorizedBlock = (AuthorizedBlock) o;
        return material == authorizedBlock.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(material);
    }

    @Override
    public String toString() {
        return "AuthorizedBlock{" +
                "material=" + material +
                ", mined=" + mined +
                '}';
    }
}
