# Applied Web Terminal

<div style="display: flex; flex-wrap: wrap">
<a href="https://www.curseforge.com/minecraft/mc-mods/applied-web-terminal">
  <img alt="curseforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg">
</a>
<a href="https://modrinth.com/mod/applied-web-terminal">
  <img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">
</a>
<img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg">
<img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/fabric_vector.svg">
</div>

Move your AE terminal to the web!

## Dependencies

- [AE2](https://modrinth.com/mod/ae2)
- [Kotlin For Forge](https://modrinth.com/mod/kotlin-for-forge)
- [Configuration](https://modrinth.com/mod/configuration)

## Usage

- Download the latest version of this mod from [Releases](https://github.com/ZhuRuoLing/AppliedWebTerminal/releases) and install the dependencies
- If you are playing in single-player mode:
    - Enter your single-player world
    - Run the command `/appwebterminal resources render` to generate frontend resources (only needs to be run once)
- If you are playing on a server:
    - Create a new single-player world
    - Run the command `/appwebterminal resources render` to generate frontend resources (only needs to be run once)
    - Upload the `aeKeyResources` folder from your game root directory to your server
- Connect the `ME Web Terminal` to the AE network
- Right-click to open the GUI and configure the name and password
- Open your web browser, access the network terminal address, and log in with the password
- All done!

## Images
### Storage Page

<img src="/images/storage.png" style="width: 250px" alt="Storage Page">

### Crafting Status Page
<img src="/images/crafting.png" style="width: 250px" alt="Crafting Page">

### In-Game GUI
<img src="/images/ui.png" style="width: 250px" alt="In-Game GUI">
