package me.davidml16.arewards.handlers;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.objects.RewardCollected;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.objects.rewards.Reward;
import me.davidml16.arewards.utils.MessageUtils;
import me.davidml16.arewards.utils.TimeAPI.TimeUtils;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionHandler {

    private Main main;

    public TransactionHandler(Main main) {
        this.main = main;
    }

    public void claimReward(Player player, RewardType rewardType, boolean isVote) {

        RewardCollected rewardCollected = new RewardCollected(player.getUniqueId(), rewardType.getId(), rewardType.isOneTime());
        try {
            main.getDatabaseHandler().addRewardCollected(rewardCollected.getUuid(), rewardCollected.getRewardID(), rewardCollected.getExpire(), rewardCollected.isOneTime());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        main.getPlayerDataHandler().getData(player).getRewards().add(rewardCollected);

        main.getRewardHandler().giveReward(player, main.getRewardTypeHandler().getTypeBydId(rewardType.getId()));

        if(!isVote)
            sendClaimedMessage(player, rewardType);
        else
            sendVotedMessage(player, rewardType);

    }

    public void cooldownReward(Player player, RewardType rewardType) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {

            Optional<RewardCollected> rewards = main.getPlayerDataHandler().getData(player).getRewards()
                    .stream().filter(rc -> rc.getRewardID().equalsIgnoreCase(rewardType.getId())).findFirst();

            if (rewards.isPresent()) {

                RewardCollected rewardCollected1 = rewards.get();

                String cooldownMessage = main.getLanguageHandler().getMessage("Rewards.Cooldown");
                cooldownMessage = cooldownMessage.replaceAll("%cooldown%",
                        TimeUtils.millisToLongDHMS(rewardCollected1.getExpire() - System.currentTimeMillis()));

                String finalCooldownMessage = cooldownMessage;
                Bukkit.getScheduler().runTask(main, () -> {
                    player.sendMessage(Utils.translate(finalCooldownMessage));
                });

            }

        });

    }

    public void noPermissionReward(Player player, RewardType rewardType) {

        for(String line : rewardType.getNoPermissionMessage()) {
            player.sendMessage(Utils.translate(line));
        }

    }

    public void claimVoteRewards(Player player) {

        Profile profile = main.getPlayerDataHandler().getData(player.getUniqueId());
        List<RewardCollected> rewardCollecteds = new ArrayList<>(profile.getRewards());

        for(RewardType rewardType : main.getRewardTypeHandler().getTypes().values()) {

            if(!rewardType.isNeedVote())
                continue;

            if(rewardType.isRequirePermission())
                if(!main.getRewardTypeHandler().haveRewardPermission(player, rewardType))
                    continue;

            Optional<RewardCollected> rewardCollected = rewardCollecteds.stream().filter(rc -> rc.getRewardID().equalsIgnoreCase(rewardType.getId())).findFirst();

            if(rewardCollected.isPresent())
                continue;

            claimReward(player, rewardType, true);

        }

    }

    public void sendClaimedMessage(Player player, RewardType rewardType) {

        List<String> rewardsLore = new ArrayList<>();
        for(Reward reward : rewardType.getRewards()) {
            rewardsLore.add(Utils.translate(main.getLanguageHandler().getMessage("Rewards.Reward").replaceAll("%reward_name%", reward.getName())));
        }

        List<String> lore = new ArrayList<>();
        for (String line : main.getLanguageHandler().getMessageList("Rewards.Claimed")) {
            if(!line.contains("%rewards%"))
                lore.add(Utils.translate(line.replaceAll("%center%", "")));
            else
                lore.addAll(rewardsLore);
        }

        for (String line : lore)
            if(!line.contains("%center%"))
                player.sendMessage(Utils.translate(line));
            else
                player.sendMessage(Utils.translate(MessageUtils.centeredMessage(line)));

    }

    public void sendVotedMessage(Player player, RewardType rewardType) {

        List<String> rewardsLore = new ArrayList<>();
        for(Reward reward : rewardType.getRewards()) {
            rewardsLore.add(Utils.translate(main.getLanguageHandler().getMessage("Rewards.Reward").replaceAll("%reward_name%", reward.getName())));
        }

        List<String> lore = new ArrayList<>();
        for (String line : main.getLanguageHandler().getMessageList("Rewards.Voted")) {
            if(!line.contains("%rewards%"))
                lore.add(Utils.translate(line.replaceAll("%center%", "")));
            else
                lore.addAll(rewardsLore);
        }

        for (String line : lore)
            if(!line.contains("%center%"))
                player.sendMessage(Utils.translate(line));
            else
                player.sendMessage(Utils.translate(MessageUtils.centeredMessage(line)));

    }

    public void sendVoteLinks(Player player) {

        for (String line : main.getLanguageHandler().getMessageList("VoteLinks"))
            if(!line.contains("%center%"))
                player.sendMessage(Utils.translate(line));
            else
                player.sendMessage(Utils.translate(MessageUtils.centeredMessage(line.replaceAll("%center%", ""))));

    }

    public void claimedReward(Player player) {
        player.sendMessage(Utils.translate(main.getLanguageHandler().getMessage("Rewards.OneTimeClaimed")));
    }

}
