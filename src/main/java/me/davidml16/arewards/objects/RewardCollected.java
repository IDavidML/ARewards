package me.davidml16.arewards.objects;

import me.davidml16.arewards.Main;

import java.util.UUID;

public class RewardCollected {

    private UUID uuid;
    private String rewardID;
    private Long expire;
    private boolean oneTime;

    public RewardCollected(UUID uuid, String rewardID, Long expire, boolean oneTime) {
        this.uuid = uuid;
        this.rewardID = rewardID;
        this.expire = expire;
        this.oneTime = oneTime;
    }

    public RewardCollected(UUID uuid, String rewardID, boolean oneTime) {
        this.uuid = uuid;
        this.rewardID = rewardID;
        this.oneTime = oneTime;

        RewardType rewardType = Main.get().getRewardTypeHandler().getTypeBydId(rewardID);
        if(rewardType != null)
            this.expire = System.currentTimeMillis() + rewardType.getCooldown();
        else
            this.expire = System.currentTimeMillis();
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

    public boolean isOneTime() { return oneTime; }

    public void setOneTime(boolean oneTime) { this.oneTime = oneTime; }

    @Override
    public String toString() {
        return "RewardCollected{" +
                "uuid=" + uuid +
                ", rewardID='" + rewardID + '\'' +
                ", expire=" + expire +
                '}';
    }

}
