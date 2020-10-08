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

public class EditCommandRewardMenu implements ConversationAbandonedListener, CommonPrompts {

    private Main main;
    public EditCommandRewardMenu(Main main) {
        this.main = main;
    }

    public Conversation getConversation(Player paramPlayer, RewardType rewardType, Reward reward) {
        Conversation conversation = (new ConversationFactory(main)).withModality(true).withLocalEcho(false).withFirstPrompt(new RewardMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("rewardType", rewardType);

        conversation.getContext().setSessionData("rewardID", reward.getId());
        conversation.getContext().setSessionData("rewardName", reward.getName());
        conversation.getContext().setSessionData("rewardCommand", ((CommandReward) reward).getCommands().get(0));

        main.getGuiHandler().addConversation(paramPlayer);

        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null, null); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class RewardMenuOptions extends FixedSetPrompt {
        RewardMenuOptions() { super("1", "2", "3", "4"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            RewardType rewardType = (RewardType) param1ConversationContext.getSessionData("rewardType");
            switch (param1String) {
                case "1":
                    return new UncoloredStringPrompt(main, this, true, ChatColor.YELLOW + "  Edit reward name, \"cancel\" to return.\n\n ", "rewardName");
                case "2":
                    return new CommonStringPrompt(main,this, true,ChatColor.YELLOW + "  Edit reward command, \"cancel\" to return.\n  Available variables: %player%\n\n ", "rewardCommand");
                case "3":
                    if(param1ConversationContext.getSessionData("rewardName") != null
                            && param1ConversationContext.getSessionData("rewardCommand") != null) {
                        if (rewardsIdExist(rewardType, (String) param1ConversationContext.getSessionData("rewardID"))) {
                            String rewardID = (String) param1ConversationContext.getSessionData("rewardID");
                            String rewardName = (String) param1ConversationContext.getSessionData("rewardName");
                            String rewardCommand = (String) param1ConversationContext.getSessionData("rewardCommand");

                            Reward commandReward = rewardType.getReward(rewardID);
                            commandReward.setName(rewardName);
                            ((CommandReward) commandReward).setCommands(Arrays.asList(rewardCommand));

                            rewardType.save();

                            main.getRewardHandler().loadReward(rewardType);

                            param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                                    + " &aYou edited reward &e" + commandReward.getId() + " &afrom reward type &e" + rewardType.getId()));

                            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                            main.getEditRewardsGUI().reloadGUI(rewardType.getId());
                            main.getEditRewardsGUI().open((Player) param1ConversationContext.getSessionData("player"), rewardType.getId());
                            main.getGuiHandler().removeConversation((Player) param1ConversationContext.getSessionData("player"));
                            return Prompt.END_OF_CONVERSATION;
                        } else {
                            main.getGuiHandler().removeConversation((Player) param1ConversationContext.getSessionData("player"));
                            return Prompt.END_OF_CONVERSATION;
                        }
                    } else {
                        return new ErrorPrompt(main, this, "\n" + ChatColor.RED + "  You need to setup ID, NAME, RARITY, COMMAND and ICON to save reward!\n  Write anything to continue\n ");
                    }
                case "4":
                    return new ConfirmExitPrompt(main, this);
            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  REWARD EDITOR MENU\n";
            cadena += ChatColor.GREEN + " \n";
            if (param1ConversationContext.getSessionData("rewardName") == null) {
                cadena += ChatColor.RED + "    1 " + ChatColor.GRAY + "- Edit reward name (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Edit reward name (" + ChatColor.YELLOW + param1ConversationContext.getSessionData("rewardName") + ChatColor.GRAY + ")\n";
            }

            if (param1ConversationContext.getSessionData("rewardCommand") == null) {
                cadena += ChatColor.RED + "    2 " + ChatColor.GRAY + "- Edit reward command (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Edit reward command (" + ChatColor.YELLOW + param1ConversationContext.getSessionData("rewardCommand") + ChatColor.GRAY + ")\n";
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