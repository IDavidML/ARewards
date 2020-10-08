package me.davidml16.arewards.conversation;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.utils.Sounds;
import me.davidml16.arewards.utils.TimeAPI.TimeAPI;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class CooldownMenu implements ConversationAbandonedListener, CommonPrompts {

    private Main main;
    public CooldownMenu(Main main) {
        this.main = main;
    }

    public Conversation getConversation(Player paramPlayer, RewardType type) {
        Conversation conversation = (new ConversationFactory(main)).withModality(true).withLocalEcho(false).withFirstPrompt(new RenameMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("type", type);
        conversation.getContext().setSessionData("cooldown", type.getCooldownString());

        main.getGuiHandler().addConversation(paramPlayer);

        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class RenameMenuOptions extends FixedSetPrompt {
        RenameMenuOptions() { super("1", "2"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            RewardType type = (RewardType) param1ConversationContext.getSessionData("type");
            Player player = (Player) param1ConversationContext.getSessionData("player");
            switch (param1String) {
                case "1":
                    return new UncoloredStringPrompt(main, this, true, ChatColor.YELLOW + "  Enter reward type cooldown, \"cancel\" to return." + ChatColor.GREEN + "\n \n    y (years), mo (months), w (weeks), d (days), \n    h (hours), m (minutes), s (seconds) \n\n ", "cooldown");
                case "2":
                    String cooldown = (String) param1ConversationContext.getSessionData("cooldown");
                    type.setCooldownString(cooldown);
                    type.setCooldown(new TimeAPI(cooldown).getMilliseconds());
                    type.save();
                    param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                            + " &aSaved cooldown of reward type &e" + type.getId() + " &awithout errors!"));
                    Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);
                    main.getSetupGUI().reloadGUI(type.getId());
                    main.getSetupGUI().open(player, type.getId());
                    main.getGuiHandler().removeConversation(player);
                    return Prompt.END_OF_CONVERSATION;
            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  REWARD TYPE COOLDOWN MENU\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Edit reward cooldown (" + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', (String)param1ConversationContext.getSessionData("cooldown")) + ChatColor.GRAY + ")\n";
            cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Save and exit\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GOLD + "" + ChatColor.YELLOW + "  Choose the option: \n";
            cadena += ChatColor.GREEN + " \n";
            return cadena;
        }
    }

}