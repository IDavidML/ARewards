package me.davidml16.arewards.conversation.rewards;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.conversation.CommonPrompts;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.objects.rewards.CommandReward;
import me.davidml16.arewards.objects.rewards.Reward;
import me.davidml16.arewards.utils.Sounds;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CommandRewardMenu implements ConversationAbandonedListener, CommonPrompts {

    private Main main;
    public CommandRewardMenu(Main main) {
        this.main = main;
    }

    public Conversation getConversation(Player paramPlayer, RewardType rewardType) {
        Conversation conversation = (new ConversationFactory(main)).withModality(true).withLocalEcho(false).withFirstPrompt(new RewardMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("rewardType", rewardType);

        main.getGuiHandler().addConversation(paramPlayer);

        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class RewardMenuOptions extends FixedSetPrompt {
        RewardMenuOptions() { super("1", "2", "3", "4"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            RewardType rewardType = (RewardType) param1ConversationContext.getSessionData("rewardType");
            switch (param1String) {
                case "1":
                    return new CommonPrompts.UncoloredStringPrompt(main, this, true, ChatColor.YELLOW + "  Enter reward name, \"cancel\" to return.\n\n ", "rewardName");
                case "2":
                    return new CommonStringPrompt(main,this, true,ChatColor.YELLOW + "  Enter reward command, \"cancel\" to return.\n  Available variables: %player%\n\n ", "rewardCommand");
                case "3":
                    if(param1ConversationContext.getSessionData("rewardName") != null
                            && param1ConversationContext.getSessionData("rewardCommand") != null) {
                        if (!rewardsIdExist(rewardType, (String) param1ConversationContext.getSessionData("rewardID"))) {
                            String rewardID = "reward_" + rewardType.getRewards().size();
                            String rewardName = (String) param1ConversationContext.getSessionData("rewardName");
                            String rewardCommand = (String) param1ConversationContext.getSessionData("rewardCommand");

                            Reward commandReward = new CommandReward(rewardID, rewardName, Arrays.asList(rewardCommand), rewardType);
                            rewardType.getRewards().add(commandReward);
                            rewardType.save();

                            param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                                    + " &aYou added reward &e" + commandReward.getId() + " &ato reward type &e" + rewardType.getId()));

                            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                            main.getEditRewardsGUI().reloadGUI(rewardType.getId());
                            main.getEditRewardsGUI().open((Player) param1ConversationContext.getSessionData("player"), rewardType.getId());
                            main.getGuiHandler().removeConversation((Player) param1ConversationContext.getSessionData("player"));
                            return Prompt.END_OF_CONVERSATION;
                        } else {
                            return new CommonPrompts.ErrorPrompt(main, this, "\n" + ChatColor.RED + "  There is already a reward with that ID, please change it and try again\n  Write anything to continue\n ");
                        }
                    } else {
                        return new CommonPrompts.ErrorPrompt(main, this, "\n" + ChatColor.RED + "  You need to setup ID, NAME, RARITY, COMMAND and ICON to save reward!\n  Write anything to continue\n ");
                    }
                case "4":
                    return new CommonPrompts.ConfirmExitPrompt(main, this);
            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  REWARD CREATION MENU\n";
            cadena += ChatColor.GREEN + " \n";
            if (param1ConversationContext.getSessionData("rewardName") == null) {
                cadena += ChatColor.RED + "    1 " + ChatColor.GRAY + "- Set reward name (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Set reward name (" + ChatColor.YELLOW + param1ConversationContext.getSessionData("rewardName") + ChatColor.GRAY + ")\n";
            }

            if (param1ConversationContext.getSessionData("rewardCommand") == null) {
                cadena += ChatColor.RED + "    2 " + ChatColor.GRAY + "- Set reward command (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Set reward command (" + ChatColor.YELLOW + param1ConversationContext.getSessionData("rewardCommand") + ChatColor.GRAY + ")\n";
            }

            cadena += ChatColor.GREEN + "    3 " + ChatColor.GRAY + "- Save\n";
            cadena += ChatColor.GREEN + "    4 " + ChatColor.GRAY + "- Exit and discard\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GOLD + "" + ChatColor.YELLOW + "  Choose the option: \n";
            cadena += ChatColor.GREEN + " \n";
            return cadena;
        }
    }

    private boolean rewardsIdExist(RewardType rewardType, String rewardID) {
        for(Reward reward : rewardType.getRewards()) {
            if(reward.getId().equalsIgnoreCase(rewardID)) return true;
        }
        return false;
    }
}