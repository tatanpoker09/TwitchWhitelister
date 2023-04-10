package com.communitycraft.tatanpoker09.plugin;

import com.communitycraft.tatanpoker09.commands.WhitelistChangeCommand;
import com.communitycraft.tatanpoker09.twitch.TwitchService;
import com.communitycraft.tatanpoker09.whitelist.WhitelistManagerAPI;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.List;

public class TwitchBotPlugin extends JavaPlugin {

    private static final String ALLOWED_REWARD_IDS_CONFIG_PATH = "allowedRewardIds";
    private WhitelistManagerAPI whitelistManagerAPI;

    private boolean testing;
    private TwitchService twitchService;


    public TwitchBotPlugin()
    {
        super();
    }

    protected TwitchBotPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    protected TwitchBotPlugin(Boolean testing){
        this.testing = testing;
    }
    protected TwitchBotPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file, Boolean testing)
    {
        this(loader, description, dataFolder, file);
        this.testing = testing;
    }


    @Override
    public void onLoad() {
        if(!testing) {
            CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));
            whitelistManagerAPI = new WhitelistManagerAPI(this);
        }
    }

    @Override
    public void onEnable()
    {
        String oauthToken = this.getConfig().getString("OAUTH_TOKEN");
        if (oauthToken == null || oauthToken.isEmpty()) {
            throw new RuntimeException("OAUTH_TOKEN is not set in the config file!");
        }
        OAuth2Credential credentials = new OAuth2Credential("twitch", oauthToken);

        if (!testing) {
            CommandAPI.onEnable(this);
        }
        this.saveDefaultConfig();
        getLogger().info("Initializing TwitchBot");
        new WhitelistChangeCommand();

        List<String> channelNames = getConfig().getStringList("channelNames");
        List<String> allowedRewardIds = getConfig().getStringList(ALLOWED_REWARD_IDS_CONFIG_PATH);
        String liveMessage = getConfig().getString("liveMessage");
        String offlineMessage = getConfig().getString("offlineMessage");

        twitchService = new TwitchService(this,
                new TwitchService.TwitchServiceConfiguration(credentials, channelNames, allowedRewardIds,liveMessage, offlineMessage));
        twitchService.registerEvents();
        getLogger().info("TwitchBot initialized");
    }
    @Override
    public void onDisable()
    {
        if(!testing) {
            CommandAPI.onDisable();
        }
        twitchService.closeClient();
        getLogger().info("TwitchBot disabled");
    }

    public WhitelistManagerAPI getWhitelistManager() {
        return whitelistManagerAPI;
    }
}

