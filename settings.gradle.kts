rootProject.name = "FancyCoins"

include("fancy-plugin")
include("fancy-plugin:core-plugin")
findProject(":fancy-plugin:core-plugin")?.name = "core-plugin"
