# SpigotGUI Remastered
SpigotGUI Remastered is a remastered edition of [SpigotGUI](https://spigotmc.org/resources/spigotgui.55266/) by JusticePro, which itself was a recreation of [BukkitGUI](https://bukkit.org/threads/tool-windows-mac-linux-gui-bukkitgui-multiplatform-user-interface-for-your-server.242058). This is a desktop-based GUI for running Minecraft servers.

This particular version is a fork of the original [SpigotGUI Remastered](https://www.spigotmc.org/resources/spigotgui-remastered.62417/) by JusticePro. I made some modifications for my own use, but may continue to improve this further with time.

## Download
You can download builds of this fork from the [Releases](https://github.com/JeremyFail/SpigotGUI-Remastered/releases) page.

You can also download the source and compile it yourself using Apache Maven. Just run `mvn clean package` to compile the source, and the JAR will be in the target directory.

## How do I use SpigotGUI?

### Getting started

1. **Install the application**: to install SpigotGUI, simply place the `SpigotGUI Remastered` JAR file in the root directory of your Minecraft server (where your `server.properties` and server JAR files are typically located).
2. **Run the application**: Double‑click the JAR to run it
3. **Set your server JAR**: Open the **Settings** tab, click **Set Server File**, and choose your Minecraft server JAR (e.g. `paper.jar`, `spigot.jar`, etc). SpigotGUI will use this when you start the server.
4. **Optional: adjust RAM**: In Settings, set **Min Ram** and **Max Ram** (in MB). The default is 1024 MB.

### Main Controls

- Start, stop, and restart the server using the buttons at the top.
- Navigate to various control panels using the tabbed interface. See details below for more information.

### Tabs

- **Console**: Watch live output, and send commands. Type in the text field and press Enter to run a command.
- **Players**: View players (name, last IP). Right‑click a player for **Op**, **De-Op**, **Kick**, or **Ban**. Pardon players who have been banned previously.
- **Resources**: View live CPU and RAM usage while the server is running
- **Files**: Browse the server directory (same folder as the JAR by default). Double‑click files to open them.
- **Module List**: Lists loaded modules/plugins. Right‑click a module for any custom actions it provides.
- **Remote Admin**: **Connect to Server** opens a login window to connect to a remote SpigotGUI admin server. **Host Server** starts the built‑in admin server so others can connect and manage the server remotely (users and permissions are configured when hosting).
- **Settings**: Manage settings for the server and for SpigotGUI. Hover over settings to learn more about what each one does.
    - **Server Settings** (set the server JAR file, edit the server.properties, set the server shutdown timer duration)
    - **JVM/Run Options** (set values passed to the JVM/server when starting up such as RAM, arguments, and switches)
    - **File Settings** (configure how files are opened from the files tab)
    - **Appearance Settings** (set the overall application theme, file editor theme, console appearance settings)
- **About/Help**: See credits and help information about how to use SpigotGUI.

### Tips

- Your choices (theme, RAM, server file path, console options, etc.) are saved and restored when you reopen SpigotGUI.
- Place the SpigotGUI JAR in your server folder (where `server.properties` and the server JAR live) so paths and the file browser work as expected.
