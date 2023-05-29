![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyEconomy?style=flat-square)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyEconomy/total?logo=GitHub&style=flat-square)

# Fancy Economy
A simple plugin that adds an economy to your server.

**Only supported for 1.19.4** _(might work in other version too tho)_<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_

## Get the plugin

You can download the latest versions at the following places:

- TODO: hangar
- TODO: modrinth
- TODO: spigot
- https://github.com/FancyMcPlugins/FancyEconomy/releases
- Build from source

## Commands

- /FancyEconomy - displays the commands for this Plugin.
- /FancyEconomy reload - plugin config reload
- /FancyEconomy version - checks for a new version of the plugin
- /balance - shows your balance (default currency)
- /balance (target) - shows a players balance (default currency)
- /pay (target) (amount) - pays a certain amount of (default currency) to a specified player
- /withdraw (amount) - Withdraw a certain amount of money

For each currency there will a command:
- /(currency) balance - Shows your balance
- /(currency) balance (player) - Shows a player's balance
- /(currency) pay (player) (amount) - Pays money to a certain player
- /(currency) withdraw (amount) - Withdraw a certain amount of money
- /(currency) add (player) (amount) - Adds money to a certain player
- /(currency) remove (player) (amount) - Removes money to a certain player

## Permissions

`FancyEconomy.balance` - access to the /balance command<br>
`FancyEconomy.pay` - access to the /pay command<br>
`FancyEconomy.withdraw` - access to the /withdraw command<br>
`FancyEconomy.admin` - access to the /FancyEconomy command<br>
`FancyEconomy.admin` - access to the /FancyEconomy command<br>
`FancyEconomy.<currency>` - access to the /(currency) balance|pay  commands<br>
`FancyEconomy.<currency>.admin` - access to the /(currency) set|add|remove  commands<br>

## Build from source
1. Clone this repo and run `gradlew shadowJar`
2. The jar file will be in `build/libs/FancyEconomy-<version>.jar`
