package me.davidml16.arewards.interfaces;

import me.davidml16.arewards.objects.rewards.Reward;

import java.util.Comparator;

public class RewardIDComparator implements Comparator<Reward> {

    @Override
    public int compare(Reward o1, Reward o2) {
        int o1_id = Integer.parseInt(o1.getId().replaceAll("reward_", ""));
        int o2_id = Integer.parseInt(o2.getId().replaceAll("reward_", ""));

        return o1_id - o2_id;
    }

}
