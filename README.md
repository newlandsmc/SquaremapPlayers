# SquaremapPlayers

___
Plugin designed for [SemiVanilla-MC](https://github.com/SemiVanilla-MC/SemiVanilla-MC).
This plugin is to be used with the default player rendering disabled and replaces this by adding circular markers where the player has a random position within this marker.

## **Downloads**
Downloads can be obtained on the [github actions page.](https://github.com/SemiVanilla-MC/SquaremapPlayers/actions)

## **Building**

#### Initial setup
Clone the repo using `git clone https://github.com/SemiVanilla-MC/SquaremapPlayers.git`.

#### Compiling
se the command `./gradlew build --stacktrace` in the project root directory.
The compiled jar will be placed in directory `/build/libs/`.

## **Commands**

| Command             | Description                 | Permission                  |
|---------------------|-----------------------------|-----------------------------|

## **Permissions**

| Permission                  | Description                                    |
|-----------------------------|------------------------------------------------|

## **Configuration**

```yaml
# Magic value used to determine auto configuration updates, do not change this value
config-version: 1
# Time in ticks to check if a player's marker needs to be updated
update-interval: 300
# Radius for the circle
radius: 250
# Settings related to normal player markers and how they are displayed on the webmap
marker:
  color: ffff
  weight: 1
  opacity: 1.0
  fill-color: ffff
  fill-opacity: 0.2
  hover-tooltip: ''
  click-tooltip: ''
# Settings related to bounty markers and how they are displayed on the webmap
bounty:
  color: ff0000
  weight: 1
  opacity: 1.0
  fill-color: ffc800
  fill-opacity: 0.2
  hover-tooltip: ''
  click-tooltip: ''
# Per world settings
world-settings:
  # Applies to all worlds that don't have this configured
  default:
    # Enable marker displays in this world 
    enabled: true
    layer:
      # Layer label in the selection menu
      label: Players
      controls:
        # Can this setting be toggled
        enabled: true
        # Hide the setting
        hide-by-default: false
    player:
      # Hide vanished players
      hide-vanished: false
      # Stop updating player locations if vanished.
      persist-vanished: true
      # Amount in blocks that the player needs to move to update the marker.
      update-radius: 4
```

## **Support**

## **License**
[LICENSE](LICENSE)