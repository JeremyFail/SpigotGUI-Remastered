# SpigotGUI Remastered
SpigotGUI Remastered is a remastered edition of [SpigotGUI](https://spigotmc.org/resources/spigotgui.55266/) by JusticePro, which itself was a recreation of [BukkitGUI](https://bukkit.org/threads/tool-windows-mac-linux-gui-bukkitgui-multiplatform-user-interface-for-your-server.242058). This is a desktop-based GUI for running Minecraft servers.

This particular version is a fork of the original [SpigotGUI Remastered](https://www.spigotmc.org/resources/spigotgui-remastered.62417/) by JusticePro. I made some modifications for my own use, but may continue to improve this further with time.

## Download
You can download builds of this fork from the [Releases](https://github.com/JeremyFail/SpigotGUI-Remastered/releases) page.

You can also download the source and compile it your self using Apache Maven. Just run `mvn clean package` to compile the source, and the JAR will be in the target directory.

## How do I use SpigotGUI?

### Getting started

1. **Install the application**: to install SpigotGUI, simply place the `SpigotGUI Remastered` JAR file in the root directory of your Minecraft server (where your `server.properties` and server JAR files are typically located).
2. **Run the application**: Double‑click the JAR to run it
3. **Set your server JAR**: Open the **Settings** tab, click **Set Server File**, and choose your Minecraft server JAR (e.g. `paper.jar`, `spigot.jar`, etc). SpigotGUI will use this when you start the server.
4. **Optional: adjust RAM**: In Settings, set **Min Ram** and **Max Ram** (in MB). The default is 1024 MB.

### Tabs

- **Console**: Start and stop the server, watch live output, and send commands. Type in the text field and press Enter to run a command. Use the **Exit Timer** dropdown to choose immediate shutdown or a 1‑minute countdown when you click Stop Server.
- **Players**: View players (name, last IP). Right‑click a player for **Op**, **De-Op**, **Kick**, or **Ban**.
- **Settings**: Manage settings for the server and for SpigotGUI.
    - **Custom Arguments** and **Custom Switches** (arguments passed to the JVM/server when starting up)
    - **Theme** (sets the look and feel of SpigotGUI - requires a restart of the app)
    - **Font Size** (sets the font size for the console)
    - **Console dark mode** (enables/disables a dark background on the console)
    - **Disable console colors** (enables/disables colored text in the console)
    - **Open files in system default application** (when using the files tab, either opens the file in the system default application or the built-in basic text editor)
    - **Set Server File** (sets the server JAR file to run the server with)
    - **Edit Server.Properties** (opens `server.properties` in the working directory).
- **Files**: Browse the server directory (same folder as the JAR by default). Double‑click files to open them in the built‑in editor; use the list’s context menu for other actions.
- **Module List**: Lists loaded modules/plugins. Right‑click a module for any custom actions it provides.
- **Remote Admin**: **Connect to Server** opens a login window to connect to a remote SpigotGUI admin server. **Host Server** starts the built‑in admin server so others can connect and manage the server remotely (users and permissions are configured when hosting).
- **About/Help**: See credits and help information about how to use SpigotGUI.

### Tips

- Your choices (theme, RAM, server file path, console options, etc.) are saved and restored when you reopen SpigotGUI.
- Place the SpigotGUI JAR in your server folder (where `server.properties` and the server JAR live) so paths and the file browser work as expected.
