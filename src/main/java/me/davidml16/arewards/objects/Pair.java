package me.davidml16.arewards.objects;

public class Pair {

    private String rewardType;
    private int page;

    public Pair(String rewardType, int page) {
        this.rewardType = rewardType;
        this.page = page;
    }

    public String getRewardType() {
        return rewardType;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "cubeletType='" + rewardType + '\'' +
                ", page=" + page +
                '}';
    }
}
