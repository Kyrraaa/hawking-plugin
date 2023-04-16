package fr.kyra.hawking.manager;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

public class DatabaseManager {

    private final Connection connection;
    public DatabaseManager(FileConfiguration configuration) {
        String host = configuration.getString("database.host");
        String database = configuration.getString("database.database");
        String username = configuration.getString("database.username");
        String password = configuration.getString("database.password");

        boolean useSSL = configuration.getBoolean("database.useSSL", false);
        this.connection = this.init(host, database, username, password, useSSL);
    }

    private Connection init(String host, String database, String username, String password, boolean useSSL) {
        String url = String.format("jdbc:mysql://%s/%s?allowPublicKeyRetrieval=true&useSSL=%b", host, database, useSSL);

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement createTableStatement = connection.createStatement();

            createTableStatement.execute(this.createPlayersSchema());
            createTableStatement.execute(this.createBlocksSchema());
            createTableStatement.execute(this.createPlayersHasBlocksSchema());

            createTableStatement.execute(this.createTriggerAddBlockToPlayers());
            createTableStatement.execute(this.createTriggerAddPlayerToBlocks());

            return connection;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }

    private String createPlayersSchema() {
        return "CREATE TABLE IF NOT EXISTS players (\n" +
                "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    uuid VARCHAR(36) NOT NULL UNIQUE,\n" +
                "    username VARCHAR(16) NOT NULL,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    last_login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP\n" +
                ");";
    }

    private String createBlocksSchema() {
        return "CREATE TABLE IF NOT EXISTS authorized_blocks (\n" +
                "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    identifier VARCHAR(255) NOT NULL,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP\n" +
                ");\n";
    }

    private String createPlayersHasBlocksSchema() {
        return "CREATE TABLE IF NOT EXISTS players_has_authorized_blocks (\n" +
                "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    player_id INT,\n" +
                "    block_id INT,\n" +
                "    count INT DEFAULT 0,\n" +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY (block_id) REFERENCES authorized_blocks(id) ON DELETE CASCADE\n" +
                ");\n";
    }

    private String createTriggerAddBlockToPlayers() {
        return "CREATE TRIGGER IF NOT EXISTS add_authorized_blocks_to_players AFTER INSERT ON authorized_blocks\n" +
                "FOR EACH ROW\n" +
                "BEGIN\n" +
                "    INSERT INTO players_has_authorized_blocks (player_id, block_id, count)\n" +
                "    SELECT id, NEW.id, 0 FROM players;\n" +
                "END;\n";
    }

    private String createTriggerAddPlayerToBlocks() {
        return "CREATE TRIGGER IF NOT EXISTS add_player_authorized_blocks AFTER INSERT ON players\n" +
                "FOR EACH ROW\n" +
                "BEGIN\n" +
                "  INSERT INTO players_has_authorized_blocks (player_id, block_id, count)\n" +
                "    SELECT NEW.id, id, 0 FROM authorized_blocks;\n" +
                "END;";
    }
}
