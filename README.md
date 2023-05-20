![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyCoins?style=flat-square)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyCoins/total?logo=GitHub&style=flat-square)

# Fancy Coins
A simple plugin that adds an economy to your server (coins).

**Only supported for 1.19.4** _(might work in other version too tho)_<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_

## Get the plugin

You can download the latest versions at the following places:

- TODO: hangar
- TODO: modrinth
- TODO: spigot
- https://github.com/FancyMcPlugins/FancyCoins/releases
- Build from source

## Commands

- /fancycoins - displays the commands for this Plugin.
- /fancycoins reload - plugin config reload
- /fancycoins version - checks for a new version of the plugin
- /balance - shows your balance (default currency)
- /balance (target) - shows a players balance (default currency)
- /pay (target) (amount) - pays a certain amount of (default currency) to a specified player

For each currency there will a command:
- /(currency) balance - Shows your balance
- /(currency) balance (player) - Shows a player's balance
- /(currency) pay (player) (amount) - Pays money to a certain player
- /(currency) add (player) (amount) - Adds money to a certain player
- /(currency) remove (player) (amount) - Removes money to a certain player

## Permissions

TODO


## Build from source
1. Clone this repo and run `gradlew shadowJar`
2. The jar file will be in `build/libs/FancyCoins-<version>.jar`
