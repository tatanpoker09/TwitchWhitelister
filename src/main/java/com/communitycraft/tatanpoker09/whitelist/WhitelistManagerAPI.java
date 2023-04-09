package com.communitycraft.tatanpoker09.whitelist;

import com.communitycraft.tatanpoker09.plugin.TwitchBotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class WhitelistManagerAPI {
    private final TwitchBotPlugin plugin;

    public WhitelistManagerAPI(TwitchBotPlugin plugin) {
        this.plugin = plugin;
    }


    public void addUsernameToWhitelist(String username){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        if (offlinePlayer.isWhitelisted()) {
            plugin.getLogger().info(String.format("Player %s is already whitelisted!", offlinePlayer.getName()));
            return;
        }
        offlinePlayer.setWhitelisted(true);
        plugin.getLogger().info("Added to whitelist: " + username);
    }
}