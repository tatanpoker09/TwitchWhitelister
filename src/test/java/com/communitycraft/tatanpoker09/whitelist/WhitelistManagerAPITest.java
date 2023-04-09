package com.communitycraft.tatanpoker09.whitelist;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.communitycraft.tatanpoker09.plugin.TwitchBotPlugin;
import com.destroystokyo.paper.utils.PaperPluginLogger;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhitelistManagerAPITest {
    private WhitelistManagerAPI whitelistManagerAPI;
    private static final String PLAYER_NAME = "tatanpoker09";

    @BeforeEach
    public void setUp() {
        ServerMock serverMock = MockBukkit.mock();
        TwitchBotPlugin mockPlugin = mock(TwitchBotPlugin.class);
        when(mockPlugin.getLogger()).thenReturn(PaperPluginLogger.getLogger("TwitchBotPlugin"));
        whitelistManagerAPI = new WhitelistManagerAPI(mockPlugin);
        serverMock.addPlayer(PLAYER_NAME);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testAddUsernameToWhitelist() {
        whitelistManagerAPI.addUsernameToWhitelist(PLAYER_NAME);
        assert MockBukkit.getMock() != null;
        OfflinePlayer offlinePlayer = MockBukkit.getMock().getOfflinePlayer(PLAYER_NAME);
        Assertions.assertNotNull(offlinePlayer);
        Assertions.assertTrue(offlinePlayer.isWhitelisted());
    }
}
