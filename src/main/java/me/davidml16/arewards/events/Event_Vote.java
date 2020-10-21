package me.davidml16.arewards.events;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import me.davidml16.arewards.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Event_Vote implements Listener {

    private Main main;

    public Event_Vote(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onVote(VotifierEvent event) {

        Vote vote = event.getVote();

        String username = vote.getUsername();
        Player player = Bukkit.getPlayer(username);

        if(player != null)
            main.getTransactionHandler().claimVoteRewards(player);

    }

}
