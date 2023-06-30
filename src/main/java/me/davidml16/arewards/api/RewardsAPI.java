package me.davidml16.arewards.api;

import me.davidml16.arewards.Main;

public class RewardsAPI {

    private static Main main;

    public RewardsAPI(Main main) {
        RewardsAPI.main = main;
    }

    public static void reloadHologramAnimation() {
        if(main.getHologramHandler().getImplementation() == null) return;
        main.getHologramHandler().getColorAnimation().reset();
        main.getHologramHandler().getImplementation().reloadHolograms();
    }

}
