package com.communitycraft.tatanpoker09.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WhitelistChangeCommandTest {

    private WhitelistChangeCommand whitelistChangeCommand;
    private ServerMock serverMock;
    @BeforeEach
    public void setUp() {
        this.whitelistChangeCommand = mock(WhitelistChangeCommand.class);

        serverMock = MockBukkit.mock();
        doNothing().when(whitelistChangeCommand).registerCommand();
        when(whitelistChangeCommand.changeWhitelist(anyString(), anyString())).thenCallRealMethod();
        serverMock.addPlayer("tatanpoker09");
        serverMock.addPlayer("username_tester");
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void test_changeWhitelist() {
        OfflinePlayer offlinePlayer = MockBukkit.getMock().getOfflinePlayer("tatanpoker09");
        offlinePlayer.setWhitelisted(true);
        Assertions.assertTrue(offlinePlayer.isWhitelisted());
        this.whitelistChangeCommand.changeWhitelist("tatanpoker09", "username_tester");
        Assertions.assertFalse(offlinePlayer.isWhitelisted());
        Assertions.assertTrue(serverMock.getOfflinePlayer("username_tester").isWhitelisted());
    }
}
