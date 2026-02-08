package me.justicepro.spigotgui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Settings implements Serializable {

	/** Pin UID so old settings files (without consoleDarkMode) still deserialize. */
	private static final long serialVersionUID = -2270671006336242076L;

	private ServerSettings serverSettings;
	private Theme theme;
	private Object fontSize;
	/** Console dark mode (dark background); if false, light background. Persisted like theme. Default off (light). */
	private boolean consoleDarkMode = false;
	/** Console colors enabled; if false, display all text in default fg (black/white). Persisted like theme. */
	private boolean consoleColorsEnabled = true;
	/** When true, double-clicking a file in the Files tab opens it in the system default app instead of the built-in editor. */
	private boolean openFilesInSystemDefault = false;
	/** File editor theme name (e.g. "default", "dark", "eclipse"). Used for RSyntaxTextArea built-in themes. */
	private String fileEditorTheme = "default";
	/** When true, show the manual "Console scroll sticky" checkbox on the Console tab and do not auto-update sticky on scroll. */
	private boolean manualConsoleScrollSticky = false;
	/** When true, server control buttons (Start/Stop/Restart) show text; when false, they show icons only. */
	private boolean serverButtonsUseText = false;
	/** Seconds to wait before shutdown/restart when using countdown; 0 = immediate. Replaces old "exit timer" dropdown. */
	private int shutdownCountdownSeconds = 0;
	/** When true, console text wraps only at word boundaries (spaces); when false (default), wraps at any character. */
	private boolean consoleWrapWordBreakOnly = false;

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize) {
		this.serverSettings = serverSettings;
		this.theme = theme;
		this.fontSize = fontSize;
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode) {
		this.serverSettings = serverSettings;
		this.theme = theme;
		this.fontSize = fontSize;
		this.consoleDarkMode = consoleDarkMode;
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode, boolean consoleColorsEnabled) {
		this.serverSettings = serverSettings;
		this.theme = theme;
		this.fontSize = fontSize;
		this.consoleDarkMode = consoleDarkMode;
		this.consoleColorsEnabled = consoleColorsEnabled;
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode, boolean consoleColorsEnabled, boolean openFilesInSystemDefault) {
		this.serverSettings = serverSettings;
		this.theme = theme;
		this.fontSize = fontSize;
		this.consoleDarkMode = consoleDarkMode;
		this.consoleColorsEnabled = consoleColorsEnabled;
		this.openFilesInSystemDefault = openFilesInSystemDefault;
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode, boolean consoleColorsEnabled, boolean openFilesInSystemDefault, String fileEditorTheme) {
		this(serverSettings, theme, fontSize, consoleDarkMode, consoleColorsEnabled, openFilesInSystemDefault, fileEditorTheme, false);
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode, boolean consoleColorsEnabled, boolean openFilesInSystemDefault, String fileEditorTheme, boolean manualConsoleScrollSticky) {
		this(serverSettings, theme, fontSize, consoleDarkMode, consoleColorsEnabled, openFilesInSystemDefault, fileEditorTheme, manualConsoleScrollSticky, false);
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode, boolean consoleColorsEnabled, boolean openFilesInSystemDefault, String fileEditorTheme, boolean manualConsoleScrollSticky, boolean serverButtonsUseText) {
		this(serverSettings, theme, fontSize, consoleDarkMode, consoleColorsEnabled, openFilesInSystemDefault, fileEditorTheme, manualConsoleScrollSticky, serverButtonsUseText, 0);
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode, boolean consoleColorsEnabled, boolean openFilesInSystemDefault, String fileEditorTheme, boolean manualConsoleScrollSticky, boolean serverButtonsUseText, int shutdownCountdownSeconds) {
		this(serverSettings, theme, fontSize, consoleDarkMode, consoleColorsEnabled, openFilesInSystemDefault, fileEditorTheme, manualConsoleScrollSticky, serverButtonsUseText, shutdownCountdownSeconds, false);
	}

	public Settings(ServerSettings serverSettings, Theme theme, Object fontSize, boolean consoleDarkMode, boolean consoleColorsEnabled, boolean openFilesInSystemDefault, String fileEditorTheme, boolean manualConsoleScrollSticky, boolean serverButtonsUseText, int shutdownCountdownSeconds, boolean consoleWrapWordBreakOnly) {
		this.serverSettings = serverSettings;
		this.theme = theme;
		this.fontSize = fontSize;
		this.consoleDarkMode = consoleDarkMode;
		this.consoleColorsEnabled = consoleColorsEnabled;
		this.openFilesInSystemDefault = openFilesInSystemDefault;
		this.fileEditorTheme = fileEditorTheme != null ? fileEditorTheme : "default";
		this.manualConsoleScrollSticky = manualConsoleScrollSticky;
		this.serverButtonsUseText = serverButtonsUseText;
		this.shutdownCountdownSeconds = Math.max(0, shutdownCountdownSeconds);
		this.consoleWrapWordBreakOnly = consoleWrapWordBreakOnly;
	}

	public ServerSettings getServerSettings() {
		return serverSettings;
	}
	
	public Object getFontSize() {
		return fontSize;
	}
	
	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public boolean isConsoleDarkMode() {
		return consoleDarkMode;
	}

	public void setConsoleDarkMode(boolean consoleDarkMode) {
		this.consoleDarkMode = consoleDarkMode;
	}

	public boolean isConsoleColorsEnabled() {
		return consoleColorsEnabled;
	}

	public void setConsoleColorsEnabled(boolean consoleColorsEnabled) {
		this.consoleColorsEnabled = consoleColorsEnabled;
	}

	public boolean isOpenFilesInSystemDefault() {
		return openFilesInSystemDefault;
	}

	public void setOpenFilesInSystemDefault(boolean openFilesInSystemDefault) {
		this.openFilesInSystemDefault = openFilesInSystemDefault;
	}

	public String getFileEditorTheme() {
		return (fileEditorTheme != null && !fileEditorTheme.isEmpty()) ? fileEditorTheme : "default";
	}

	public void setFileEditorTheme(String fileEditorTheme) {
		this.fileEditorTheme = fileEditorTheme != null ? fileEditorTheme : "default";
	}

	public boolean isManualConsoleScrollSticky() {
		return manualConsoleScrollSticky;
	}

	public void setManualConsoleScrollSticky(boolean manualConsoleScrollSticky) {
		this.manualConsoleScrollSticky = manualConsoleScrollSticky;
	}

	public boolean isServerButtonsUseText() {
		return serverButtonsUseText;
	}

	public void setServerButtonsUseText(boolean serverButtonsUseText) {
		this.serverButtonsUseText = serverButtonsUseText;
	}

	public int getShutdownCountdownSeconds() {
		return shutdownCountdownSeconds;
	}

	public void setShutdownCountdownSeconds(int shutdownCountdownSeconds) {
		this.shutdownCountdownSeconds = Math.max(0, shutdownCountdownSeconds);
	}

	public boolean isConsoleWrapWordBreakOnly() {
		return consoleWrapWordBreakOnly;
	}

	public void setConsoleWrapWordBreakOnly(boolean consoleWrapWordBreakOnly) {
		this.consoleWrapWordBreakOnly = consoleWrapWordBreakOnly;
	}

	/** Backward compatibility: old settings files have no shutdownCountdownSeconds (default 0). Normalize after deserialize. */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (shutdownCountdownSeconds < 0) shutdownCountdownSeconds = 0;
	}
}