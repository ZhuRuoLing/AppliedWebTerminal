---
navigation:
  title: Web Terminal
  icon: appwebterminal:web_terminal
  position: 100
item_ids:
- appwebterminal:web_terminal
---

# The ME Web Terminal

<ItemImage id="appwebterminal:web_terminal" scale="4" />

While <ItemLink id="ae2:terminal"/>s are the ways an AE2 network interacts with you, the <ItemLink id="appwebterminal:web_terminal"/> allows you to interact with an AE2 network just like a <ItemLink id="ae2:terminal"/> but via browser.

Using the functions that an <ItemLink id="appwebterminal:web_terminal"/> provides does not require you join a minecraft world, but it requires the minecraft server running, not paused, and you should know the password of a <ItemLink id="appwebterminal:web_terminal"/>.

Configure the terminal requires you join the world, and interact with the <ItemLink id="appwebterminal:web_terminal"/> block you placed.

To make the <ItemLink id="appwebterminal:web_terminal"/> work, it requires a connection to an AE2 network, it also requires a channel and power for 3 AE/t.

## Example Setup

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="structures/web_terminal.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>


## Recipe

<RecipeFor id="appwebterminal:web_terminal" />