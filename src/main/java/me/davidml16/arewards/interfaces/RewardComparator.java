package me.davidml16.arewards.interfaces;

import me.davidml16.arewards.objects.rewards.Reward;

import java.util.Comparator;

public class RewardComparator implements Comparator<Reward> {

    @Override
    public int compare(Reward o1, Reward o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
