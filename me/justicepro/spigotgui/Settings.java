package me.justicepro.spigotgui;

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
		this.serverSettings = serverSettings;
		this.theme = theme;
		this.fontSize = fontSize;
		this.consoleDarkMode = consoleDarkMode;
		this.consoleColorsEnabled = consoleColorsEnabled;
		this.openFilesInSystemDefault = openFilesInSystemDefault;
		this.fileEditorTheme = fileEditorTheme != null ? fileEditorTheme : "default";
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
}