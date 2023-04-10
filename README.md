# Twitch Channel Whitelist Plugin

[![Version](https://img.shields.io/badge/version-1.3.3-green)](https://github.com/tatanpoker09/TwitchWhitelister/releases/tag/1.3.3)
[![License](https://img.shields.io/badge/license-Creative_Commons_Attribution--NonCommercial--ShareAlike_4.0_International-blue)](https://creativecommons.org/licenses/by-nc-sa/4.0/)


The Twitch Channel Whitelist Plugin is a Minecraft plugin that allows you to connect to your Twitch channel and configure a specific reward that will let users be added automatically to a whitelist. Additionally, the plugin broadcasts throughout the server when a streamer goes live or offline, further promoting Twitch engagement. The broadcasted messages are configurable.

## Features

- Connect your Minecraft server to your Twitch channel
- Configure a specific Twitch reward that will add users to a whitelist in Minecraft
- Automatically add users to the whitelist when they redeem the configured Twitch reward
- Broadcast messages throughout the server when a streamer goes live or offline
- Configurable broadcast messages

## Installation

1. Download the latest version of the plugin from [here](https://github.com/Community-Craft/TwitchBot/releases).
2. Copy the plugin JAR file to the `plugins` directory of your Minecraft server.
3. Restart the server to load the plugin.

## Configuration

- Edit the `config.yml` file to configure the plugin with your Twitch channel details, reward ID, and broadcast messages in the following format (a similar config should be autogenerated by the plugin):
```yaml
OAUTH_TOKEN: OAUTH_TOKEN
allowedRewardIds:
- REWARD_ID_1
- REWARD_ID_2
channelNames:
- CHANNEL_ID_1
- CHANNEL_ID_2
liveMessage: "Streamer {name} has started streaming {game} with title {title}"
offlineMessage: "Streamer {name} has stopped streaming"
```
- OAUTH_TOKEN: The OAuth token for your Twitch application. You can generate this token by creating a Twitch application.
- allowedRewardIds: A list of reward IDs that will add users to the whitelist when redeemed. To obtain a reward ID, start the server with the plugin, then have the owner of the Twitch channel redeem the wanted reward with the text "register whitelist". The console will display a confirmation message if the reward was linked correctly.
- channelNames: A list of Twitch channel names that the plugin will listen for live/offline events on.
- liveMessage: The message that will be broadcasted throughout the server when a streamer goes live. Supports {name}, {game}, and {title} placeholders.
- offlineMessage: The message that will be broadcasted throughout the server when a streamer goes offline. Supports {name} placeholder.

## Usage

1. Configure the plugin with your Twitch channel details and reward ID using the `config.yml` file.
2. To get an OAUTH_TOKEN you can go to https://twitchtokengenerator.com/. Press "Custom Scope Token", then just go to the bottom and press Generate Token! You will need to log in with your twitch account and then an access token will appear at the top of the website.
3. Start your Minecraft server.
4. You can configure rewardIds in the channels specified in the config.yml file by redeeming the rewards and typing "register whitelist" in the chat.
5. Afterwards, users who redeem the configured Twitch reward will be added to the whitelist automatically.
6. The plugin will broadcast messages throughout the server when a streamer goes live or offline. You can configure these messages in the `config.yml` file.

## Issues

- There are no known issues with the plugin.
- If you find one, open an issue within the repository.

## Contributing

- If you have an idea for a feature, write it and open a pull request or write a request issue about it, and we'll take a look.
-  If you find a bug or an issue, open an issue within the repository.
-  Currently, we have no specific guidelines for contributing to this plugin, but we welcome any contributions that will make the plugin better.

## License
   This plugin is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. You are free to modify and share this plugin, as long as it is not for commercial purposes. If you make any modifications, you must release them under the same license.

## Credits
This plugin was made possible with the help of the following libraries:

- [MockBukkit](https://github.com/MockBukkit/MockBukkit/)
- [CommandAPI](https://github.com/JorelAli/CommandAPI/)
- [Twitch4j](https://github.com/twitch4j/twitch4j)
