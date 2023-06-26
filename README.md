![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyEconomy?style=flat-square)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyEconomy/total?logo=GitHub&style=flat-square)
[![Downloads](https://img.shields.io/modrinth/dt/fancyeconomy?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/fancyeconomy/versions)

# Fancy Economy

Simple economy plugin that lets you add multiple currencies

**Only supported for 1.20.1** _(might work in other versions too tho)_<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_

Vault and PlaceholderAPI is supported.

## Get the plugin

You can download the latest versions at the following places:

- https://hangar.papermc.io/Oliver/FancyEconomy
- https://modrinth.com/plugin/fancyeconomy
- https://github.com/FancyMcPlugins/FancyEconomy/releases
- Build from source

## Commands

- /FancyEconomy - displays the commands for this Plugin.
- /FancyEconomy reload - plugin config reload
- /FancyEconomy version - checks for a new version of the plugin
- /FancyEconomy currencies - shows a list of all currencies
- /Balance - shows your balance (default currency)
- /Balance (target) - shows a players balance (default currency)
- /Pay (target) (amount) - pays a certain amount of (default currency) to a specified player
- /Withdraw (amount) - Withdraw a certain amount of money
- /Balancetop (page) - Shows the richest players

For each currency there will a command:

- /(currency) balance - Shows your balance
- /(currency) balance (player) - Shows a player's balance
- /(currency) pay (player) (amount) - Pays money to a certain player
- /(currency) withdraw (amount) - Withdraw a certain amount of money
- /(currency) balancetop (page) - Shows the richest players
- /(currency) set (player) (amount) - Sets the balance of a certain player
- /(currency) add (player) (amount) - Adds money to a certain player
- /(currency) remove (player) (amount) - Removes money to a certain player

## Permissions

`FancyEconomy.balance` - access to the /balance command<br>
`FancyEconomy.pay` - access to the /pay command<br>
`FancyEconomy.withdraw` - access to the /withdraw command<br>
`FancyEconomy.admin` - access to the /FancyEconomy command<br>
`FancyEconomy.<currency>` - access to the /(currency) balance|pay commands<br>
`FancyEconomy.<currency>.admin` - access to the /(currency) set|add|remove commands<br>

## Placeholders

`%FancyEconomy_balance%` - displays the player's balance (default currency)<br>
`%%FancyEconomy_balance_<currency>%%` - displays the player's balance for a certain currency

## Build from source

1. Clone this repo and run `gradlew shadowJar`
2. The jar file will be in `build/libs/FancyEconomy-<version>.jar`
