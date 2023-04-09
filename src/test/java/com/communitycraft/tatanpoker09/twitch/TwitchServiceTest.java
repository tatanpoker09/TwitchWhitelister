package com.communitycraft.tatanpoker09.twitch;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.communitycraft.tatanpoker09.plugin.TwitchBotPlugin;
import com.communitycraft.tatanpoker09.whitelist.WhitelistManagerAPI;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class TwitchServiceTest {
    private TwitchService twitchService;
    private TwitchBotPlugin mockPlugin;
    private WhitelistManagerAPI mockWhitelistManager;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        OAuth2Credential credential = new OAuth2Credential("twitch", System.getenv("OAUTH_TEST_TOKEN"));
        System.out.println("OAUTH_TEST_TOKEN: " + System.getenv("OAUTH_TEST_TOKEN"));
        // create arraylist with default values
        List<String> channelNames = new ArrayList<>(){{
            add("Tamara1001");
            add("tatanpoker09");
        }};
        List<String> allowedRewardsIds = new ArrayList<>(){{
            add("reward1");
            add("reward2");
        }};
        String liveMessage = "Streamer {name} has started streaming {game} with title {title}";
        String offlineMessage = "Streamer {name} has stopped streaming";

        TwitchService.TwitchServiceConfiguration configuration = new TwitchService.TwitchServiceConfiguration(credential, channelNames, allowedRewardsIds, liveMessage, offlineMessage);

        mockPlugin = mock(TwitchBotPlugin.class);
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);
        when(mockPlugin.getConfig()).thenReturn(fileConfiguration);
        when(mockPlugin.getLogger()).thenReturn(Logger.getLogger("MockTwitchBotPlugin"));
        mockWhitelistManager = mock(WhitelistManagerAPI.class);
        when(mockPlugin.getWhitelistManager()).thenReturn(mockWhitelistManager);
        doNothing().when(mockWhitelistManager).addUsernameToWhitelist(anyString());

        twitchService = new TwitchService(mockPlugin, configuration);
    }
    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        twitchService.closeClient();
    }

    @Test
    public void test_canStartBot(){
        twitchService.startBot();
    }

    @Test
    public void test_canRegisterPubSubRewardListener(){
        TwitchClient twitchClient = twitchService.startBot();
        twitchService.registerEvents();
        EventManager eventManager = twitchClient.getPubSub().getEventManager();
        int size = eventManager.getEventHandlers().size();
        Assertions.assertEquals(1, size);
        List<IEventSubscription> activeSubscriptions = eventManager.getActiveSubscriptions()
                .stream()
                .filter(subscription -> subscription.getEventType().equals(RewardRedeemedEvent.class)).toList();

        // This will get the pubsub listening to channel points redemptions.
        Assertions.assertEquals(1, activeSubscriptions.size());
    }

    @Test
    public void test_channelGoOfflineEventIsRegistered(){
        TwitchClient twitchClient = twitchService.startBot();
        twitchService.registerEvents();
        EventManager eventManager = twitchClient.getEventManager();
        int size = eventManager.getEventHandlers().size();

        Assertions.assertEquals(1, size);

        List<IEventSubscription> activeSubscriptions = eventManager.getActiveSubscriptions()
                .stream()
                .filter(subscription -> subscription.getEventType().equals(ChannelGoOfflineEvent.class)).toList();

        // we check if channelgoofflineevent is registered
        Assertions.assertEquals(1, activeSubscriptions.size());
    }


    @Test
    public void test_channelGoLiveEventIsRegistered(){
        TwitchClient twitchClient = twitchService.startBot();
        twitchService.registerEvents();
        EventManager eventManager = twitchClient.getEventManager();
        int size = eventManager.getEventHandlers().size();

        Assertions.assertEquals(1, size);

        List<IEventSubscription> activeSubscriptions = eventManager.getActiveSubscriptions()
                .stream()
                .filter(subscription -> subscription.getEventType().equals(ChannelGoLiveEvent.class)).toList();

        // we check if channelgoofflineevent is registered
        Assertions.assertEquals(1, activeSubscriptions.size());
    }

    @Test
    public void test_channelGoLiveEventIsCalled(){
        TwitchService spyTwitchService = spy(twitchService);
        TwitchClient twitchClient = spyTwitchService.startBot();
        spyTwitchService.registerEvents();

        // we dispatch the event
        EventChannel eventChannel = new EventChannel("channel1_owner", "channel1");
        // create mock stream object
        Stream stream = Mockito.mock(Stream.class);
        // set the private gameName field to "example game"

        when(stream.getGameName()).thenReturn("Minecraft");
        when(stream.getTitle()).thenReturn("Playtesting");

        ChannelGoLiveEvent event = new ChannelGoLiveEvent(eventChannel, stream);
        twitchClient.getEventManager().publish(event);

        Mockito.verify(spyTwitchService, Mockito.times(1)).onChannelLive(event);
    }

    @Test
    public void test_channelGoOfflineEventIsCalled(){
        TwitchService spyTwitchService = spy(twitchService);
        TwitchClient twitchClient = spyTwitchService.startBot();
        spyTwitchService.registerEvents();

        // we dispatch the event
        EventChannel eventChannel = new EventChannel("channel1", "channel1");
        ChannelGoOfflineEvent event = new ChannelGoOfflineEvent(eventChannel);
        twitchClient.getEventManager().publish(event);

        Mockito.verify(spyTwitchService, Mockito.times(1)).onChannelOffline(event);
    }

    @Test
    public void test_rewardRedeemedEventIsCalled(){
        TwitchService spyTwitchService = spy(twitchService);
        TwitchClient twitchClient = spyTwitchService.startBot();
        spyTwitchService.registerEvents();

        // we dispatch the event
        ChannelPointsRedemption channelPointsRedemption = new ChannelPointsRedemption();
        ChannelPointsReward channelPointsReward = new ChannelPointsReward();
        channelPointsReward.setId("reward_id");
        channelPointsRedemption.setReward(channelPointsReward);
        RewardRedeemedEvent event = new RewardRedeemedEvent(Instant.now(), channelPointsRedemption);
        twitchClient.getEventManager().publish(event);

        verify(spyTwitchService, times(1)).onRewardRedeemedEvent(event);
    }

    @Test
    public void test_rewardRedeemedOwnerCanRegisterRewardID(){
        TwitchService spyTwitchService = spy(twitchService);
        TwitchClient twitchClient = spyTwitchService.startBot();
        spyTwitchService.registerEvents();

        // we dispatch the event
        ChannelPointsRedemption channelPointsRedemption = new ChannelPointsRedemption();
        ChannelPointsReward channelPointsReward = new ChannelPointsReward();
        channelPointsReward.setId("reward_id");
        channelPointsRedemption.setReward(channelPointsReward);
        channelPointsRedemption.setUserInput("register whitelist");
        ChannelPointsUser channelPointsUser = new ChannelPointsUser();
        channelPointsUser.setId("channel1_owner");
        channelPointsRedemption.setUser(channelPointsUser);
        channelPointsRedemption.setChannelId("channel1_owner");
        RewardRedeemedEvent event = new RewardRedeemedEvent(Instant.now(), channelPointsRedemption);
        twitchClient.getEventManager().publish(event);

        verify(spyTwitchService, times(1)).onRewardRedeemedEvent(event);

        verify(mockPlugin, times(1)).saveConfig();
    }

    @Test
    public void test_rewardRedeemedNonOwnerCannotRegisterRewardID(){
        TwitchService spyTwitchService = spy(twitchService);
        TwitchClient twitchClient = spyTwitchService.startBot();
        spyTwitchService.registerEvents();

        // we dispatch the event
        ChannelPointsRedemption channelPointsRedemption = new ChannelPointsRedemption();
        ChannelPointsReward channelPointsReward = new ChannelPointsReward();
        channelPointsReward.setId("reward_id");
        channelPointsRedemption.setReward(channelPointsReward);
        channelPointsRedemption.setUserInput("register whitelist");
        ChannelPointsUser channelPointsUser = new ChannelPointsUser();
        channelPointsUser.setId("channel1_owner");
        channelPointsRedemption.setUser(channelPointsUser);
        channelPointsRedemption.setChannelId("channel2_owner");
        RewardRedeemedEvent event = new RewardRedeemedEvent(Instant.now(), channelPointsRedemption);
        twitchClient.getEventManager().publish(event);

        verify(spyTwitchService, times(1)).onRewardRedeemedEvent(event);

        verify(mockPlugin, times(0)).saveConfig();
    }

    @Test
    public void test_rewardRedeemedCorrectRewardIsRegistered(){
        TwitchService spyTwitchService = spy(twitchService);
        TwitchClient twitchClient = spyTwitchService.startBot();
        spyTwitchService.registerEvents();

        // we dispatch the event
        ChannelPointsRedemption channelPointsRedemption = new ChannelPointsRedemption();
        ChannelPointsReward channelPointsReward = new ChannelPointsReward();
        channelPointsReward.setId("reward1");
        channelPointsRedemption.setReward(channelPointsReward);
        channelPointsRedemption.setUserInput("tatanpoker09");
        ChannelPointsUser channelPointsUser = new ChannelPointsUser();
        channelPointsUser.setId("channel1_owner");
        channelPointsRedemption.setUser(channelPointsUser);
        channelPointsRedemption.setChannelId("channel2_owner");
        RewardRedeemedEvent event = new RewardRedeemedEvent(Instant.now(), channelPointsRedemption);
        twitchClient.getEventManager().publish(event);

        verify(spyTwitchService, times(1)).onRewardRedeemedEvent(event);

        verify(mockWhitelistManager, times(1)).addUsernameToWhitelist("tatanpoker09");
    }

    @Test
    public void test_notAllowedRewardRedeemedNoRewardIsRegistered(){
        TwitchService spyTwitchService = spy(twitchService);
        TwitchClient twitchClient = spyTwitchService.startBot();
        spyTwitchService.registerEvents();

        // we dispatch the event
        ChannelPointsRedemption channelPointsRedemption = new ChannelPointsRedemption();
        ChannelPointsReward channelPointsReward = new ChannelPointsReward();
        channelPointsReward.setId("reward3");
        channelPointsRedemption.setReward(channelPointsReward);
        channelPointsRedemption.setUserInput("tatanpoker09");
        ChannelPointsUser channelPointsUser = new ChannelPointsUser();
        channelPointsUser.setId("channel1_owner");
        channelPointsRedemption.setUser(channelPointsUser);
        channelPointsRedemption.setChannelId("channel2_owner");
        RewardRedeemedEvent event = new RewardRedeemedEvent(Instant.now(), channelPointsRedemption);
        twitchClient.getEventManager().publish(event);

        verify(spyTwitchService, times(1)).onRewardRedeemedEvent(event);

        verify(mockWhitelistManager, times(0)).addUsernameToWhitelist(anyString());
    }

    @Test
    public void test_channelIdsRetrievedCorrectly(){
        twitchService.startBot();
        List<String> channelIds = twitchService.getChannelIds();
        Assertions.assertEquals(2, channelIds.size());
        Assertions.assertTrue(channelIds.contains("120935302"));
        Assertions.assertTrue(channelIds.contains("10896528"));
    }
}
