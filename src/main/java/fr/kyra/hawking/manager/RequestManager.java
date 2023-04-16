package fr.kyra.hawking.manager;

import fr.kyra.hawking.HawKing;
import fr.kyra.hawking.objects.CustomPlayer;
import fr.kyra.hawking.objects.AuthorizedBlock;
import org.bukkit.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class RequestManager {
    private static final HawKing hawKing = HawKing.getInstance();
    private static final Connection connection = hawKing.getDatabaseManager().getConnection();

    public static void registerPlayer(UUID uuid, String username) {
        try {
            boolean playerExist = checkPlayer(uuid, username);

            if (!playerExist) {
                String insertQuery = "INSERT INTO players (uuid, username) VALUES (?, ?)";

                PreparedStatement insertPreparedStatement = connection.prepareStatement(insertQuery);
                insertPreparedStatement.setString(1, uuid.toString());
                insertPreparedStatement.setString(2, username);

                insertPreparedStatement.execute();
                insertPreparedStatement.close();
            }

            CustomPlayer customPlayer = new CustomPlayer();
            ArrayList<AuthorizedBlock> authorizedBlocks = getPlayerAuthorizedBlocks(uuid);

            customPlayer.setAuthorizedBlocks(authorizedBlocks);

            hawKing.getCustomPlayers().put(uuid, customPlayer);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean checkPlayer(UUID uuid, String username) throws SQLException {
        boolean playerExist = false;

        String selectQuery = "SELECT username FROM players WHERE uuid=?";

        PreparedStatement selectPreparedStatement = connection.prepareStatement(selectQuery);
        selectPreparedStatement.setString(1, uuid.toString());

        ResultSet selectResultSet = selectPreparedStatement.executeQuery();

        if (selectResultSet.first()) {
            String updateQuery = "UPDATE players SET username = ? WHERE uuid=?";

            PreparedStatement updatePreparedStatement = connection.prepareStatement(updateQuery);
            updatePreparedStatement.setString(1, username);
            updatePreparedStatement.setString(2, uuid.toString());

            updatePreparedStatement.executeUpdate();
            updatePreparedStatement.close();

            playerExist = true;
        }

        selectResultSet.close();
        selectPreparedStatement.close();

        return playerExist;
    }

    public static int getPlayerIdByUuid(UUID uuid) {
        try {
            PreparedStatement playerIdQuery = connection.prepareStatement("SELECT id FROM players WHERE uuid = ?");
            playerIdQuery.setString(1, uuid.toString());

            ResultSet playerIdResult = playerIdQuery.executeQuery();
            playerIdResult.next();

            return playerIdResult.getInt("id");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return -1;
    }

    public static ArrayList<AuthorizedBlock> getPlayerAuthorizedBlocks(UUID uuid) {
        ArrayList<AuthorizedBlock> playerAuthorizedBlocks = new ArrayList<>();

        try {
            int playerId = getPlayerIdByUuid(uuid);

            if (playerId == -1)
                throw new SQLException();
            // Sélectionner les enregistrements correspondants dans la table "players_has_blocks"
            PreparedStatement blocksQuery = connection.prepareStatement("" +
                    "SELECT authorized_blocks.identifier, players_has_authorized_blocks.count " +
                    "FROM players_has_authorized_blocks " +
                    "JOIN authorized_blocks ON authorized_blocks.id = players_has_authorized_blocks.block_id " +
                    "WHERE players_has_authorized_blocks.player_id = ?"
            );
            blocksQuery.setInt(1, playerId);
            ResultSet blocksResult = blocksQuery.executeQuery();

            // Parcourir les résultats sur les blocks minés par le joueur
            while (blocksResult.next()) {
                String blockIdentifier = blocksResult.getString("identifier");
                int blockCount = blocksResult.getInt("count");

                Material material = Material.matchMaterial(blockIdentifier);
                AuthorizedBlock authorizedBlock = new AuthorizedBlock(material, blockCount);

                playerAuthorizedBlocks.add(authorizedBlock);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return playerAuthorizedBlocks;
    }

    public static void updatePlayer(UUID uuid, CustomPlayer customPlayer) {
        int playerId = getPlayerIdByUuid(uuid);
        customPlayer.getAuthorizedBlocks().forEach(authorizedBlock -> {
            System.out.println(authorizedBlock);
            try {
                PreparedStatement selectBlockIdQuery = connection.prepareStatement("" +
                        "SELECT id FROM authorized_blocks WHERE identifier = ?"
                );
                selectBlockIdQuery.setString(1, authorizedBlock.getMaterial().name());
                ResultSet resultSet = selectBlockIdQuery.executeQuery();
                resultSet.next();

                int blockId = resultSet.getInt("id");

                PreparedStatement updatePlayerBlockQuery = connection.prepareStatement("" +
                        "UPDATE players_has_authorized_blocks\n" +
                        "SET count = ?\n" +
                        "WHERE player_id = ?\n" +
                        "AND block_id = ?"
                );

                updatePlayerBlockQuery.setInt(1, authorizedBlock.getMined());
                updatePlayerBlockQuery.setInt(2, playerId);
                updatePlayerBlockQuery.setInt(3, blockId);

                updatePlayerBlockQuery.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static ArrayList<Material> getAuthorizedBlocks() {
        String query = "SELECT identifier FROM authorized_blocks";
        ArrayList<Material> authorizedBlocks = new ArrayList<>();

        try {
            PreparedStatement selectQuery = connection.prepareStatement(query);
            ResultSet resultSet = selectQuery.executeQuery();

            while(resultSet.next()) {
                String identifier = resultSet.getString("identifier");
                Material material = Material.matchMaterial(identifier);

                authorizedBlocks.add(material);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorizedBlocks;
    }
}
