package fr.kyra.hawking.objects;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Optional;

public class CustomPlayer {
    private ArrayList<AuthorizedBlock> authorizedBlocks = new ArrayList<>();

    public CustomPlayer() {}

    public CustomPlayer(ArrayList<AuthorizedBlock> authorizedBlocks) {
        this.authorizedBlocks = authorizedBlocks;
    }

    public ArrayList<AuthorizedBlock> getAuthorizedBlocks() {
        return authorizedBlocks;
    }

    public void setAuthorizedBlocks(ArrayList<AuthorizedBlock> authorizedBlocks) {
        this.authorizedBlocks = authorizedBlocks;
    }

    public void incrementAuthorizedBlock(Material material, int n) {
        Optional<AuthorizedBlock> optionalAuthorizedBlock = this.authorizedBlocks.stream().filter(o -> o.getMaterial() == material).findFirst();

        if (optionalAuthorizedBlock.isPresent()) {
            AuthorizedBlock newAuthorizedBlock = optionalAuthorizedBlock.get();
            newAuthorizedBlock.addMined(n);

            this.authorizedBlocks.set(this.authorizedBlocks.indexOf(optionalAuthorizedBlock.get()), newAuthorizedBlock);
        } else {
            AuthorizedBlock newAuthorizedBlock = new AuthorizedBlock(material, 1);
            this.authorizedBlocks.add(newAuthorizedBlock);
        }
    }
}