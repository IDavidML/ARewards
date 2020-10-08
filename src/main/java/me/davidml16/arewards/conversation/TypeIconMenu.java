package me.davidml16.arewards.conversation;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.utils.SkullCreator;
import me.davidml16.arewards.utils.Sounds;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class TypeIconMenu implements ConversationAbandonedListener, CommonPrompts {

    private Main main;
    public TypeIconMenu(Main main) {
        this.main = main;
    }

    public Conversation getConversation(Player paramPlayer, RewardType type, String status) {
        Conversation conversation = (new ConversationFactory(main)).withModality(true).withLocalEcho(false).withFirstPrompt(new RenameMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("type", type);
        conversation.getContext().setSessionData("status", status);
        conversation.getContext().setSessionData("texture", main.getRewardTypeHandler().getConfig(type.getId()).get("type.icon." + status + ".item"));

        main.getGuiHandler().addConversation(paramPlayer);

        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null, "available"); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class RenameMenuOptions extends FixedSetPrompt {
        RenameMenuOptions() { super("1", "2", "3", "4", "5", "6"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            RewardType rewardType = (RewardType) param1ConversationContext.getSessionData("type");
            Player player = (Player) param1ConversationContext.getSessionData("player");
            switch (param1String) {
                case "1":
                    param1ConversationContext.setSessionData("method", "base64");
                    return new SkullStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter base64 texture string, \"cancel\" to return.\n\n ", "texture");
                case "2":
                    param1ConversationContext.setSessionData("method", "uuid");
                    return new SkullStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter player uuid, \"cancel\" to return.\n\n ", "texture");
                case "3":
                    param1ConversationContext.setSessionData("method", "name");
                    return new SkullStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter player name, \"cancel\" to return.\n\n ", "texture");
                case "4":
                    param1ConversationContext.setSessionData("method", "url");
                    return new MineSkinStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter mineskin direct link, \"cancel\" to return.\n\n ", "texture");
                case "5":
                    final String method = (String) param1ConversationContext.getSessionData("method");
                    final String texture = (String) param1ConversationContext.getSessionData("texture");
                    final String status = (String) param1ConversationContext.getSessionData("status");

                    if(!method.equalsIgnoreCase("url")) {
                        main.getRewardTypeHandler().getConfig(rewardType.getId()).set("type.icon." + status + ".item", method + ":" + texture);

                        switch(method) {
                            case "base64":
                                if(status.equalsIgnoreCase("available"))
                                    rewardType.setAvailableIcon(SkullCreator.itemFromBase64(texture));
                                else if(status.equalsIgnoreCase("cooldown"))
                                    rewardType.setCooldownIcon(SkullCreator.itemFromBase64(texture));
                                break;
                            case "uuid":
                                if(status.equalsIgnoreCase("available"))
                                    rewardType.setAvailableIcon(SkullCreator.itemFromUuid(UUID.fromString(texture)));
                                else if(status.equalsIgnoreCase("cooldown"))
                                    rewardType.setCooldownIcon(SkullCreator.itemFromUuid(UUID.fromString(texture)));
                                break;
                            case "name":
                                if(status.equalsIgnoreCase("available"))
                                    rewardType.setAvailableIcon(SkullCreator.itemFromName(texture));
                                else if(status.equalsIgnoreCase("cooldown"))
                                    rewardType.setCooldownIcon(SkullCreator.itemFromName(texture));
                                break;
                        }

                        main.getRewardTypeHandler().saveConfig(rewardType.getId());
                        param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                                + " &aSaved " + status + " skull texture of reward type &e" + rewardType.getId() + " &awithout errors!"));

                        Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);
                        main.getSetupGUI().reloadGUI(rewardType.getId());
                        main.getSetupGUI().open(player, rewardType.getId());
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                            DataOutputStream out = null;
                            BufferedReader reader = null;
                            try {
                                URL target = new URL("https://api.mineskin.org/generate/url");
                                HttpURLConnection con = (HttpURLConnection) target.openConnection();
                                con.setRequestMethod("POST");
                                con.setDoOutput(true);
                                con.setConnectTimeout(1000);
                                con.setReadTimeout(30000);
                                out = new DataOutputStream(con.getOutputStream());
                                out.writeBytes("url=" + URLEncoder.encode(texture, "UTF-8"));
                                out.close();
                                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                JSONObject output = (JSONObject) new JSONParser().parse(reader);
                                JSONObject data = (JSONObject) output.get("data");
                                JSONObject texture1 = (JSONObject) data.get("texture");
                                String textureEncoded = (String) texture1.get("value");
                                con.disconnect();
                                Bukkit.getScheduler().runTask(main, () -> {
                                    main.getRewardTypeHandler().getConfig(rewardType.getId()).set("type.icon." + status + ".item", "base64:" + textureEncoded);

                                    if(status.equalsIgnoreCase("available"))
                                        rewardType.setAvailableIcon(SkullCreator.itemFromBase64(textureEncoded));
                                    else if(status.equalsIgnoreCase("cooldown"))
                                        rewardType.setCooldownIcon(SkullCreator.itemFromBase64(textureEncoded));

                                    main.getRewardTypeHandler().saveConfig(rewardType.getId());
                                    param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                                            + " &aSaved " + status + " skull texture of reward type &e" + rewardType.getId() + " &awithout errors!"));

                                    Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);
                                    main.getSetupGUI().reloadGUI(rewardType.getId());
                                    main.getSetupGUI().open(player, rewardType.getId());
                                });
                            } catch (Throwable t) {
                                t.printStackTrace();
                            } finally {
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                    }
                                }
                                if (reader != null) {
                                    try {
                                        reader.close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                        });
                    }
                    main.getGuiHandler().removeConversation(player);
                    return Prompt.END_OF_CONVERSATION;
                case "6":
                    return new CommonPrompts.ConfirmExitPrompt(main, this);
            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  REWARD TYPE ICON MENU\n ";
            cadena += ChatColor.GREEN + " \n ";
            cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Base64 String\n ";
            cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Player UUID\n ";
            cadena += ChatColor.GREEN + "    3 " + ChatColor.GRAY + "- Player Name\n ";
            cadena += ChatColor.GREEN + "    4 " + ChatColor.GRAY + "- Mineskin Direct Link\n ";
            cadena += ChatColor.GREEN + " \n ";
            cadena += ChatColor.GOLD + "    5 " + ChatColor.GRAY + "- Save and exit\n ";
            cadena += ChatColor.GOLD + "    6 " + ChatColor.GRAY + "- Exit and discard\n ";
            cadena += ChatColor.GREEN + "\n ";
            cadena += ChatColor.GOLD + "" + ChatColor.YELLOW + "  Choose the option: \n ";
            return cadena;
        }
    }

}