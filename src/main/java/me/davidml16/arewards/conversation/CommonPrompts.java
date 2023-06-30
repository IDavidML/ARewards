package me.davidml16.arewards.conversation;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.utils.Sounds;
import me.davidml16.arewards.utils.Utils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public interface CommonPrompts  {

    public static class CommonStringPrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private boolean allowSpaces;
        private Main main;

        public CommonStringPrompt(Main main, Prompt param1Prompt, boolean param1Boolean, String param1String1, String param1String2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.allowSpaces = param1Boolean;
            this.text = param1String1;
            this.storeValue = param1String2;
        }

        public CommonStringPrompt(Prompt param1Prompt, String param1String1, String param1String2) {
            this(null, param1Prompt, true, param1String1, param1String2);
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            return this.text;
        }

        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.trim().equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }
            if (!this.allowSpaces && param1String.contains(" ")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Spaces are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, param1String);
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return this.parentPrompt;
        }
    }

    public static class DuplicateRangeStringPrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private boolean allowSpaces;
        private Main main;

        public DuplicateRangeStringPrompt(Main main, Prompt param1Prompt, boolean param1Boolean, String param1String1, String param1String2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.allowSpaces = param1Boolean;
            this.text = param1String1;
            this.storeValue = param1String2;
        }

        public DuplicateRangeStringPrompt(Prompt param1Prompt, String param1String1, String param1String2) {
            this(null, param1Prompt, true, param1String1, param1String2);
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            return this.text;
        }

        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.trim().equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }
            if (!this.allowSpaces && param1String.contains(" ")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Spaces are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }
            if (!param1String.contains("-") || param1String.split("-").length != 2) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Format: min-max\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, param1String);
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return this.parentPrompt;
        }
    }

    public static class UncoloredStringPrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private boolean allowSpaces;
        private Main main;

        public UncoloredStringPrompt(Main main, Prompt param1Prompt, boolean param1Boolean, String param1String1, String param1String2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.allowSpaces = param1Boolean;
            this.text = param1String1;
            this.storeValue = param1String2;
        }

        public UncoloredStringPrompt(Prompt param1Prompt, String param1String1, String param1String2) {
            this(null, param1Prompt, true, param1String1, param1String2);
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            return this.text;
        }

        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.trim().equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }
            if (!this.allowSpaces && param1String.contains(" ")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Spaces are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }
            if (param1String.contains("&") || param1String.contains("§")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Color codes are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, param1String);
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return this.parentPrompt;
        }
    }

    public static class SkullStringPrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private boolean allowSpaces;
        private Main main;

        public SkullStringPrompt(Main main, Prompt param1Prompt, boolean param1Boolean, String param1String1, String param1String2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.allowSpaces = param1Boolean;
            this.text = param1String1;
            this.storeValue = param1String2;
        }

        public SkullStringPrompt(Prompt param1Prompt, String param1String1, String param1String2) {
            this(null, param1Prompt, true, param1String1, param1String2);
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            return this.text;
        }

        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.trim().equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }
            if (!this.allowSpaces && param1String.contains(" ")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Spaces are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, param1String);
            param1ConversationContext.getForWhom().sendRawMessage(
                    ChatColor.GREEN + "  Succesfully setup skull texture with method " +
                        ChatColor.YELLOW + param1ConversationContext.getSessionData("method"));
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return this.parentPrompt;
        }
    }

    public static class MineSkinStringPrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private boolean allowSpaces;
        private Main main;

        public MineSkinStringPrompt(Main main, Prompt param1Prompt, boolean param1Boolean, String param1String1, String param1String2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.allowSpaces = param1Boolean;
            this.text = param1String1;
            this.storeValue = param1String2;
        }

        public MineSkinStringPrompt(Prompt param1Prompt, String param1String1, String param1String2) {
            this(null, param1Prompt, true, param1String1, param1String2);
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            return this.text;
        }

        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.trim().equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }
            if (!this.allowSpaces && param1String.contains(" ")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Spaces are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            if (!param1String.contains("minesk.in")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Invalid Mineskin direct link!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, param1String);
            param1ConversationContext.getForWhom().sendRawMessage(
                    ChatColor.GREEN + "  Succesfully setup skull texture with method " +
                            ChatColor.YELLOW + param1ConversationContext.getSessionData("method"));
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return this.parentPrompt;
        }
    }

    public static class NumericRangePrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private double minValue;
        private double maxValue;
        private boolean hasRange;
        private Main main;

        public NumericRangePrompt(Main main, Prompt param1Prompt, String param1String1, String param1String2, double param1Float1, double param1Float2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.text = param1String1;
            this.storeValue = param1String2;
            this.minValue = param1Float1;
            this.maxValue = param1Float2;
            this.hasRange = true;
        }

        public NumericRangePrompt(Prompt param1Prompt, String param1String1, String param1String2) {
            this.parentPrompt = param1Prompt;
            this.text = param1String1;
            this.storeValue = param1String2;
            this.hasRange = false;
        }

        public String getPromptText(ConversationContext param1ConversationContext) { return this.text; }


        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }
            if (!NumberUtils.isNumber(param1String)) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  That's not a valid number!\n ");
                return this;
            }

            double f = Double.parseDouble(param1String);

            if (this.hasRange && (f < this.minValue || f > this.maxValue)) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  The value must be between " + this.minValue + " and " + this.maxValue + "!\n ");
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, f);
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return this.parentPrompt;
        }
    }

    public static class NumericIntegerRangePrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private int minValue;
        private int maxValue;
        private boolean hasRange;
        private Main main;

        public NumericIntegerRangePrompt(Main main, Prompt param1Prompt, String param1String1, String param1String2, int param1Float1, int param1Float2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.text = param1String1;
            this.storeValue = param1String2;
            this.minValue = param1Float1;
            this.maxValue = param1Float2;
            this.hasRange = true;
        }

        public NumericIntegerRangePrompt(Prompt param1Prompt, String param1String1, String param1String2) {
            this.parentPrompt = param1Prompt;
            this.text = param1String1;
            this.storeValue = param1String2;
            this.hasRange = false;
        }

        public String getPromptText(ConversationContext param1ConversationContext) { return this.text; }


        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }
            if (!NumberUtils.isNumber(param1String)) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  That's not a valid number!\n ");
                return this;
            }

            int f = Integer.parseInt(param1String);

            if (this.hasRange && (f < this.minValue || f > this.maxValue)) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  The value must be between " + this.minValue + " and " + this.maxValue + "!\n ");
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, f);
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return this.parentPrompt;
        }
    }

    public static class BooleanPrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private Main main;

        public BooleanPrompt(Main main, Prompt param1Prompt, String param1String1, String param1String2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.text = param1String1;
            this.storeValue = param1String2;
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            return this.text;
        }

        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.trim().equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }

            if (param1String.contains(" ")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Spaces are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            if (param1String.equalsIgnoreCase("true") || param1String.equalsIgnoreCase("false")) {
                param1ConversationContext.setSessionData(this.storeValue, param1String.toLowerCase());
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
                return parentPrompt;
            }

            param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  That's not a valid option!\n ");
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
            return this;
        }
    }

    public static class IntegerPrompt extends StringPrompt {
        private Prompt parentPrompt;
        private String text;
        private String storeValue;
        private Main main;

        public IntegerPrompt(Main main, Prompt param1Prompt, String param1String1, String param1String2) {
            this.main = main;
            this.parentPrompt = param1Prompt;
            this.text = param1String1;
            this.storeValue = param1String2;
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            return this.text;
        }

        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.trim().equalsIgnoreCase("cancel")) {
                return this.parentPrompt;
            }

            if (param1String.contains(" ")) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Spaces are not allowed!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            int integer = 0;

            try {
                integer = Integer.parseInt(param1String);
            } catch(NumberFormatException e) {
                param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  Invalid number!\n ");
                Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                        ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                return this;
            }

            param1ConversationContext.setSessionData(this.storeValue, integer);
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.CLICK, 10, 2);
            return parentPrompt;
        }
    }

    public static class ConfirmExitPrompt extends StringPrompt {
        private Prompt parent;
        private Main main;

        public ConfirmExitPrompt(Main main, Prompt param1Prompt) {
            this.main = main;
            this.parent = param1Prompt;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String str = ChatColor.GREEN + "  1 " + ChatColor.GRAY + "- Yes\n" + ChatColor.RED + "  2 " + ChatColor.GRAY + "- No\n ";

            return ChatColor.YELLOW + "\n Are you sure you want to exit without saving?\n \n" + str;
        }


        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            if (param1String.equals("1") || param1String.equalsIgnoreCase("Yes")) {

                param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                        + " &cYou leave type setup menu!"));

                Main.get().getGuiHandler().removeConversation((Player) param1ConversationContext.getSessionData("player"));
                return Prompt.END_OF_CONVERSATION;
            }
            if (param1String.equals("2") || param1String.equalsIgnoreCase("No")) {
                return this.parent;
            }
            param1ConversationContext.getForWhom().sendRawMessage(ChatColor.RED + "  That's not a valid option!\n");
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
            return this;
        }
    }

    public static class ErrorPrompt extends StringPrompt {
        private Prompt parent;
        private String text;
        private Main main;

        public ErrorPrompt(Main main, Prompt param1Prompt, String param1String1) {
            this.main = main;
            this.parent = param1Prompt;
            this.text = param1String1;
        }

        public String getPromptText(ConversationContext param1ConversationContext) {
            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
            return text;
        }


        public Prompt acceptInput(ConversationContext param1ConversationContext, String param1String) {
            param1ConversationContext.getForWhom().sendRawMessage(text);
            return parent;
        }
    }

}