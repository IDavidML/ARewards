package me.davidml16.arewards.objects;

import me.davidml16.arewards.Main;

import java.util.UUID;

public class RewardCollected {

    private UUID uuid;
    private String rewardID;
    private Long expire;

    public RewardCollected(UUID uuid, String rewardID, Long expire) {
        this.uuid = uuid;
        this.rewardID = rewardID;
        this.expire = expire;
    }

    public RewardCollected(UUID uuid, String rewardID) {
        this.uuid = uuid;
        this.rewardID = rewardID;

        RewardType rewardType = Main.get().getRewardTypeHandler().getTypeBydId(rewardID);
        if(rewardType != null)
            this.expire = System.currentTimeMillis() + rewardType.getCooldown();
        else
            this.expire = System.currentTimeMillis();
    }

    public RewardCollected(UUID uuid, RewardType rewardType) {
        this.uuid = uuid;
        this.rewardID = rewardType.getId();
        this.expire = System.currentTimeMillis() + rewardType.getCooldown();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getRewardID() {
        return rewardID;
    }

    public void setRewardID(String rewardID) {
        this.rewardID = rewardID;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "RewardCollected{" +
                "uuid=" + uuid +
                ", rewardID='" + rewardID + '\'' +
                ", expire=" + expire +
                '}';
    }

}
