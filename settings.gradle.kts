rootProject.name = "FancyCoins"

include("fancy-plugin")
include("fancy-plugin:core-plugin")
findProject(":fancy-plugin:core-plugin")?.name = "core-plugin"
include("fancy-plugin:core-folia")
findProject(":fancy-plugin:core-folia")?.name = "core-folia"
include("fancy-api")
