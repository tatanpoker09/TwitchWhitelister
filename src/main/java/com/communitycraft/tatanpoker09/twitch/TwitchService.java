package com.communitycraft.tatanpoker09.twitch;

import com.communitycraft.tatanpoker09.plugin.TwitchBotPlugin;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.netflix.hystrix.HystrixCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.List;

public class TwitchService {
    private TwitchClient client;
    private final TwitchBotPlugin plugin;
    private final TwitchServiceConfiguration configuration;

    public TwitchService(TwitchBotPlugin plugin, TwitchServiceConfiguration configuration){
        this.plugin = plugin;
        this.configuration = configuration;
    }

    public TwitchClient startBot(){
        boolean enableChat = true;
        boolean enablePubSub = true;
        OAuth2Credential credentials = this.configuration.credentials;

        this.client = TwitchClientBuilder.builder()
                                    .withEnablePubSub(enablePubSub)
                                    .withEnableChat(enableChat)
                                    .withChatAccount(credentials)
                                    .withEnableHelix(true)
                                    .withDefaultAuthToken(credentials)
                                    .build();

        return this.client;
    }

    public void registerEvents(){
        this.registerLiveEvents();
        this.registerTwitchEvents();
    }


    private void registerLiveEvents() {
        this.client.getClientHelper().enableStreamEventListener(this.configuration.channelNames);
        if (!"".equals(this.configuration.liveMessage))
            this.client.getEventManager().onEvent(ChannelGoLiveEvent.class, this::onChannelLive);
        if (!"".equals(this.configuration.offlineMessage))
            this.client.getEventManager().onEvent(ChannelGoOfflineEvent.class, this::onChannelOffline);
    }

    private void registerTwitchEvents(){
        registerPubSubListeners();
        this.client.getEventManager().onEvent(RewardRedeemedEvent.class, this::onRewardRedeemedEvent);
    }

    private void registerPubSubListeners() {
        OAuth2Credential credentials = this.configuration.credentials;
        List<String> channelIds = getChannelIds();

        for(String channelId : channelIds){
            client.getPubSub().listenForChannelPointsRedemptionEvents(credentials, channelId);
        }
    }

    public List<String> getChannelIds(){
        HystrixCommand<UserList> users =
                this.client.getHelix().getUsers(this.configuration.credentials.getAccessToken(),
                        null, this.configuration.channelNames);
        UserList userList = users.execute();
        return userList.getUsers().stream().map(User::getId).toList();
    }


    public void onChannelOffline(ChannelGoOfflineEvent event) {
        EventChannel channel = event.getChannel();
        String name = channel.getName();
        String offlineMessage = this.configuration.offlineMessage;
        offlineMessage = offlineMessage.replace("{name}", name);
        plugin.getLogger().info(offlineMessage);
        Bukkit.broadcast(Component.text(offlineMessage));
    }

    public void onChannelLive(ChannelGoLiveEvent event) {
        String gameName = event.getStream().getGameName();
        String title = event.getStream().getTitle();
        EventChannel channel = event.getChannel();
        String name = channel.getName();
        String liveMessage = this.configuration.liveMessage;
        liveMessage = liveMessage.replace("{name}", name).replace("{game}", gameName).replace("{title}", title);
        Bukkit.broadcast(Component.text(liveMessage));
    }

    public void onRewardRedeemedEvent(RewardRedeemedEvent e){
        ChannelPointsRedemption redemption = e.getRedemption();
        String rewardText = redemption.getUserInput();
        String rewardID = redemption.getReward().getId();
        long rewardCost = redemption.getReward().getCost();

        if (rewardText == null){
            return;
        }
        if (rewardText.equalsIgnoreCase("register whitelist")){
            if (redemption.getUser().getId().equals(redemption.getChannelId())) {

                this.configuration.allowedRewardIds.add(rewardID);
                plugin.getConfig().set("allowedRewardIds", this.configuration.allowedRewardIds);
                plugin.saveConfig();
                plugin.getLogger().info("Registered whitelist reward for user: " + redemption.getUser().getDisplayName());
            }
            return;
        }

        List<String> allowedRewards = this.configuration.allowedRewardIds;
        if (allowedRewards.contains(rewardID)) {
            plugin.getWhitelistManager().addUsernameToWhitelist(rewardText);
            plugin.getLogger().info("Reward! " + rewardID + " for " + rewardCost + " points" + " with text " + rewardText + " redeemed by " + redemption.getUser().getDisplayName());
        }
    }

    public void closeClient() {
        if(client != null){
            client.close();
        }
    }


    public static class TwitchServiceConfiguration {
        private final OAuth2Credential credentials;
        private final List<String> channelNames;
        private final List<String> allowedRewardIds;
        public final String liveMessage;
        private final String offlineMessage;

        public TwitchServiceConfiguration(OAuth2Credential credentials, List<String> channelNames, List<String> allowedRewardIds, String liveMessage, String offlineMessage) {
            this.credentials = credentials;
            this.channelNames = channelNames;
            this.allowedRewardIds = allowedRewardIds;
            this.liveMessage = liveMessage;
            this.offlineMessage = offlineMessage;
        }
    }
}
