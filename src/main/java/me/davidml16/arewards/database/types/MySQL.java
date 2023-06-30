package me.davidml16.arewards.database.types;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.objects.RewardCollected;
import me.davidml16.arewards.objects.rewards.Reward;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQL implements Database {

    private HikariDataSource hikari;

    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private final int port;

    private final Main main;

    public MySQL(Main main) {
        this.main = main;
        this.host = main.getConfig().getString("MySQL.Host");
        this.user = main.getConfig().getString("MySQL.User");
        this.password = main.getConfig().getString("MySQL.Password");
        this.database = main.getConfig().getString("MySQL.Database");
        this.port = main.getConfig().getInt("MySQL.Port");
    }

    @Override
    public void close() {
        if(hikari != null) {
            hikari.close();
        }
    }

    @Override
    public void open() {
        if (hikari != null)  return;

        try {
            HikariConfig config = new HikariConfig();
            config.setPoolName("    ARewards Pool");
            config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
            config.setUsername(this.user);
            config.setPassword(this.password);
            config.setMaximumPoolSize(75);
            config.setMinimumIdle(4);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikari = new HikariDataSource(config);

            hikari.getConnection();

            Main.log.sendMessage(Utils.translate("    &aMySQL has been enabled!"));
        } catch (HikariPool.PoolInitializationException | SQLException e) {
            Main.log.sendMessage(Utils.translate("    &cMySQL has an error on the conection! Now trying with SQLite..."));
            main.getDatabase().changeToSQLite();
        }
    }

    public void loadTables() {
        runStatement("CREATE TABLE IF NOT EXISTS ar_rewards (`UUID` varchar(40) NOT NULL, `rewardID` varchar(40) NOT NULL, `expire` bigint NOT NULL DEFAULT 0, `oneTime` BOOLEAN NOT NULL DEFAULT false, PRIMARY KEY (`UUID`, `rewardID`));");
        runStatement("ALTER TABLE ar_rewards ADD `oneTime` BOOLEAN NOT NULL DEFAULT false");
        runStatement("CREATE TABLE IF NOT EXISTS ar_players (`UUID` varchar(40) NOT NULL, `NAME` varchar(40), PRIMARY KEY (`UUID`));");
    }

    private void runStatement(String query) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = hikari.getConnection();
            statement = connection.prepareStatement(query);
            statement.execute();
        } catch (SQLException ignored) {
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    public boolean hasName(String name) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = hikari.getConnection();
            ps = connection.prepareStatement("SELECT * FROM ar_players WHERE NAME = '" + name + "';");
            rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(ps != null) ps.close();
            if(rs != null) rs.close();
            if(connection != null) connection.close();
        }

        return false;
    }

    public void createPlayerData(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                ps = connection.prepareStatement("INSERT INTO ar_players (UUID,NAME) VALUES(?,?)");
                ps.setString(1, p.getUniqueId().toString());
                ps.setString(2, p.getName());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    public void updatePlayerName(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                ps = connection.prepareStatement("UPDATE ar_players SET `NAME` = ? WHERE `UUID` = ?");
                ps.setString(1, p.getName());
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    public String getPlayerUUID(String name) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = hikari.getConnection();
            ps = connection.prepareStatement("SELECT * FROM ar_players WHERE NAME = '" + name + "';");
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("UUID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(ps != null) ps.close();
            if(rs != null) rs.close();
            if(connection != null) connection.close();
        }

        return "";
    }

    @Override
    public void addRewardCollected(UUID uuid, String rewardID, Long expireCooldown, boolean oneTime) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                ps = connection.prepareStatement("INSERT INTO ar_rewards (UUID,rewardID,expire,oneTime) VALUES(?,?,?,?)");
                ps.setString(1, uuid.toString());
                ps.setString(2, rewardID);
                ps.setLong(3, expireCooldown);
                ps.setBoolean(4, oneTime);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void removeRewardCollected(UUID uuid, String rewardID) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                ps = connection.prepareStatement("DELETE FROM ar_rewards WHERE UUID = '" + uuid + "' AND rewardID = '" + rewardID + "';");
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void removeRewardsCollected(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                ps = connection.prepareStatement("DELETE FROM ar_rewards WHERE UUID = '" + uuid + "';");
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void removeRewardsCollected(String rewardID) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                ps = connection.prepareStatement("DELETE FROM ar_rewards WHERE rewardID = '" + rewardID + "';");
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void removeExpiredRewards(UUID uuid) {
        long actualTime = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                ps = connection.prepareStatement("DELETE FROM ar_rewards WHERE UUID = '" + uuid + "' AND oneTime = '0' AND expire < '" + actualTime + "';");
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<List<RewardCollected>> getRewardCollected(UUID uuid, boolean oneTime) {
        CompletableFuture<List<RewardCollected>> result = new CompletableFuture<>();

        long actualTime = System.currentTimeMillis();

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            List<RewardCollected> rewards = new ArrayList<>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            Connection connection = null;
            try {
                connection = hikari.getConnection();
                if(!oneTime)
                    ps = connection.prepareStatement("SELECT * FROM ar_rewards WHERE UUID = '" + uuid.toString() + "' AND oneTime = '0' AND expire > '" + actualTime + "';");
                else
                    ps = connection.prepareStatement("SELECT * FROM ar_rewards WHERE UUID = '" + uuid.toString() + "' AND oneTime = '1';");

                rs = ps.executeQuery();
                while (rs.next()) {
                    rewards.add(new RewardCollected(UUID.fromString(rs.getString("UUID")), rs.getString("rewardID"), rs.getLong("expire"), rs.getBoolean("oneTime")));
                }

                result.complete(rewards);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
        return result;
    }

}