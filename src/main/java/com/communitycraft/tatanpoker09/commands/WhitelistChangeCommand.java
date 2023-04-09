package com.communitycraft.tatanpoker09.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class WhitelistChangeCommand {
    private static final String COMMAND = "whitelistchange";
    private static final String ALIAS = "wc";
    private static final String PERMISSION = "whitelist.change";

    public WhitelistChangeCommand() {
        registerCommand();
    }

    void registerCommand() {
        new CommandAPICommand(COMMAND)
                .withAliases(ALIAS)
                .withPermission(PERMISSION)
                .withArguments(new StringArgument("currentUsername"),
                        new StringArgument("newUsername"))
                .executes(this::executeCommand).register();
    }

    private void executeCommand(CommandSender sender, Object[] args) {
        String currentUsername = (String) args[0];
        String newUsername = (String) args[1];

        String message = changeWhitelist(currentUsername, newUsername);
        sender.sendRichMessage(message);
    }

    String changeWhitelist(String oldUsername, String newUsername){
        OfflinePlayer oldPlayer = Bukkit.getOfflinePlayer(oldUsername);
        OfflinePlayer newPlayer = Bukkit.getOfflinePlayer(newUsername);

        if (!oldPlayer.isWhitelisted()){
            return formatNotWhitelistedMessage(oldUsername);
        }

        if(newPlayer.isWhitelisted()) {
            return formatAlreadyWhitelistedMessage(newUsername);
        }

        oldPlayer.setWhitelisted(false);
        newPlayer.setWhitelisted(true);

        return formatSuccessMessage(oldUsername, newUsername);
    }

    private String formatNotWhitelistedMessage(String oldUsername) {
        TextComponent textComponent =
                Component.text("The specified current user '")
                        .color(TextColor.color(0x00FF00))
                        .append(Component.text(oldUsername)
                                .color(TextColor.color(0xFFD700)))
                        .append(Component.text("' is not whitelisted!")
                                .color(TextColor.color(0x00FF00)));
        return textComponent.toString();
    }

    private String formatAlreadyWhitelistedMessage(String newUsername) {
        TextComponent textComponent =
                Component.text()
                        .color(TextColor.color(0x00FF00))
                        .append(Component.text("Player ")
                                .color(TextColor.color(0x00FF00)))
                        .append(Component.text(newUsername)
                                .color(TextColor.color(0xFFD700)))
                        .append(Component.text(" is already whitelisted!")
                                .color(TextColor.color(0x00FF00))).build();
        return textComponent.toString();
    }

    private String formatSuccessMessage(String oldUsername, String newUsername) {
        TextComponent textComponent =
                Component.text("Whitelisted user has been changed from '")
                        .color(TextColor.color(0x00FF00))
                        .append(Component.text(oldUsername)
                                .color(TextColor.color(0xFFD700)))
                        .append(Component.text("' to '")
                                .color(TextColor.color(0x00FF00)))
                        .append(Component.text(newUsername)
                                .color(TextColor.color(0xFFD700)))
                        .append(Component.text("'")
                                .color(TextColor.color(0x00FF00)));
        return textComponent.toString();
    }
}
