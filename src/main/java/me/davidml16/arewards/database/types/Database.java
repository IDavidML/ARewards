package me.davidml16.arewards.database.types;

import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.objects.RewardCollected;
import me.davidml16.arewards.objects.rewards.Reward;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    void close();

    void open();

    void loadTables();

    boolean hasName(String name) throws SQLException;

    void createPlayerData(Player p);

    void updatePlayerName(Player p);

    String getPlayerUUID(String name) throws SQLException;

    void addRewardCollected(UUID uuid, String rewardID, Long expireCooldown, boolean oneTime) throws SQLException;

    void removeRewardCollected(UUID uuid, String rewardID) throws SQLException;

    void removeRewardsCollected(UUID uuid) throws SQLException;

    void removeRewardsCollected(String rewardID) throws SQLException;

    void removeExpiredRewards(UUID uuid) throws SQLException;

    CompletableFuture<List<RewardCollected>> getRewardCollected(UUID uuid, boolean oneTime);

}
