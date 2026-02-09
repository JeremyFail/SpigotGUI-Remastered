package me.justicepro.spigotgui.Core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.awt.Desktop;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;

import me.justicepro.spigotgui.JModulePanel;
import me.justicepro.spigotgui.Module;
import me.justicepro.spigotgui.ModuleManager;
import me.justicepro.spigotgui.ProcessException;
import me.justicepro.spigotgui.ReporterWindow;
import me.justicepro.spigotgui.Server;
import me.justicepro.spigotgui.ServerSettings;
import me.justicepro.spigotgui.Settings;
import me.justicepro.spigotgui.Theme;
import me.justicepro.spigotgui.FileExplorer.FileEditor;
import me.justicepro.spigotgui.FileExplorer.FileModel;
import me.justicepro.spigotgui.Instructions.InstructionWindow;
import me.justicepro.spigotgui.RemoteAdmin.CorePermissions;
import me.justicepro.spigotgui.RemoteAdmin.LoginWindow;
import me.justicepro.spigotgui.RemoteAdmin.Permission;
import me.justicepro.spigotgui.RemoteAdmin.ServerWindow;
import me.justicepro.spigotgui.RemoteAdmin.PacketHandlers.ServerHandler;
import me.justicepro.spigotgui.RemoteAdmin.Server.RServer;
import me.justicepro.spigotgui.Utils.ConsoleStyleHelper;
import me.justicepro.spigotgui.Utils.Dialogs;
import me.justicepro.spigotgui.Utils.Player;

public class SpigotGUI extends JFrame {

	private static final int MIN_SIZE_BASE_WIDTH = 650;
	private static final int MIN_SIZE_BASE_HEIGHT = 400;

	private JPanel contentPane;
	private JTextField consoleCommandInput;

	private JSpinner shutdownCountdownSpinner;

	private JTextPane consoleTextPane;
	private ConsoleStyleHelper consoleStyleHelper;
	/** Scroll pane wrapping the console text pane; used for stick-to-bottom behavior. */
	private JScrollPane consoleScrollPane;
	/** When true, new console output scrolls to the bottom of the console view. */
	private volatile boolean consoleStickToBottom = true;
	/** Ignore scroll bar listener until this time (ms); avoids treating programmatic scroll or append-induced scroll as user scroll. */
	private volatile long ignoreScrollBarUntil = 0;
	/** True while we're scrolling from the command timer so we don't re-enable sticky. */
	private volatile boolean scrollingFromCommand = false;
	/** Drawn green/red circle for server status; replaces previous image-based icon. */
	private StatusCirclePanel serverStatusCircle;

	private JSpinner maxRam;
	private JSpinner minRam;

	private JLabel lblServerStatusText;

	private JComboBox<String> themeBox;
	/** Theme at app startup; used to decide if we can apply a new theme without restart (same family only). */
	private static Theme initialThemeForSession;
	/** Fixed size for theme label so it never changes when toggling text (avoids layout shift). */
	private static int themeLabelWidth = 0;
	private static int themeLabelHeight = 0;
	private JLabel lblTheme;

	public static Server server = null;

	private ResourcesPanel resourcesPanel;

	private JButton btnStartServer;
	private JButton btnStopServer;
	private JButton btnRestartServer;
	private JTable playersTable;
	private JLabel lblPlayersOnlineCount;

	public static SpigotGUI instance;

	public static ArrayList<Player> players = new ArrayList<>();

	private static Module module;

	private boolean restart = false;
	private JTextField customJvmArgsField;
	private JTextField customJvmSwitchesField;
	private JSpinner fontSpinner;
	
	private JCheckBox chkConsoleInputAsSay;
	/** Manual control for console scroll sticky; visible only when Settings "Manual console scroll sticky" is on. */
	private JCheckBox chkConsoleScrollSticky;
	/** When true, sticky is controlled only by the manual checkbox; scroll bar does not update it. */
	private boolean manualConsoleScrollStickyMode = false;
	private JCheckBox consoleDarkModeCheckBox;
	private JCheckBox manualConsoleScrollStickyCheckBox;
	private JCheckBox serverButtonsUseTextCheckBox;
	private JCheckBox disableConsoleColorsCheckBox;
	private JCheckBox consoleWrapWordBreakOnlyCheckBox;
	private JCheckBox openFilesInSystemDefaultCheckBox;
	private JComboBox<String> fileEditorThemeBox;
	private FileModel fileModel;
	/** Hovered index in the Files tab list for highlight; -1 when none. */
	private int fileListHoverIndex = -1;

	public static File jarFile;

	public static ServerHandler serverHandler = new ServerHandler();

	public static final String versionTag = getVersionTag();

	/** 
	 * Get the version tag from the pom.xml file.
	 * @return The version tag.
	 */
	private static String getVersionTag() {
		String v = SpigotGUI.class.getPackage().getImplementationVersion();
		return (v != null && !v.isEmpty()) ? v : "dev";
	}

	//public static ServerSettings serverSettings;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings settings = loadSettings();
					UIManager.setLookAndFeel(settings.getTheme().getLookAndFeel());

					instance = new SpigotGUI(settings);
					instance.setVisible(true);
				} catch (Exception e) {
					ReporterWindow reporter = new ReporterWindow(e);
					reporter.setVisible(true);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param settings 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public SpigotGUI(Settings settings) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		ServerSettings serverSettings = settings.getServerSettings();
		if (serverSettings.getJarFile() != null) {
			jarFile = serverSettings.getJarFile();
		}
		if (initialThemeForSession == null) {
			initialThemeForSession = settings.getTheme();
		}
		//setIconImage(ImageIO.read(getClass().getResourceAsStream("/spigotgui.png")));
		setTitle("SpigotGUI Remastered (" + versionTag + ")");
		module = new ModuleCore();
		module.init();
		ModuleManager.registerModule(module);

		ModuleManager.registerModule(serverHandler);
		serverHandler.init();

		ModuleManager.init();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {

				boolean close = true;

				if (server!=null) {
					if (server.isRunning()) {
						JOptionPane.showMessageDialog(null, "A Server is running, you can't close this program unless you stop it.");
						close = false;
					}
				}

				String theme = themeBox.getItemAt(themeBox.getSelectedIndex());
				
				Settings s = new Settings(new ServerSettings(minRam.getValue(), maxRam.getValue(), customJvmArgsField.getText(), customJvmSwitchesField.getText(), jarFile), settings.getTheme(), fontSpinner.getValue(), consoleDarkModeCheckBox.isSelected(), !disableConsoleColorsCheckBox.isSelected(), openFilesInSystemDefaultCheckBox.isSelected(), getFileEditorThemeFromBox(), manualConsoleScrollStickyCheckBox != null && manualConsoleScrollStickyCheckBox.isSelected(), serverButtonsUseTextCheckBox != null && serverButtonsUseTextCheckBox.isSelected(), getShutdownCountdownSeconds(), consoleWrapWordBreakOnlyCheckBox != null ? consoleWrapWordBreakOnlyCheckBox.isSelected() : settings.isConsoleWrapWordBreakOnly());
				
				for (Theme t : Theme.values()) {

					if (t.getName().equalsIgnoreCase(theme)) {
						s.setTheme(t);
					}

				}
				
				if (resourcesPanel != null) {
					resourcesPanel.stopPolling();
				}
				try {
					saveSettings(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (close) {
					System.exit(0);
				}

			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 700, 600);
		setMinimumSize(new Dimension(MIN_SIZE_BASE_WIDTH, MIN_SIZE_BASE_HEIGHT));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		ToolTipManager.sharedInstance().setDismissDelay(15000);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Console", null, panel, null);

		JScrollPane scrollPane = new JScrollPane();

		int fontSize = (int) settings.getFontSize();
		Font consoleFont = getConsoleMonospaceFont(fontSize);
		ConsoleStyleHelper.setConsoleWrapWordBreakOnly(settings.isConsoleWrapWordBreakOnly());
		consoleTextPane = new JTextPane();
		consoleTextPane.setEditorKit(new me.justicepro.spigotgui.Utils.WrapEditorKit());
		consoleTextPane.setFont(consoleFont);
		consoleTextPane.setEditable(false);
		consoleTextPane.setMargin(new java.awt.Insets(4, 4, 4, 4));
		consoleStyleHelper = new ConsoleStyleHelper(consoleTextPane, consoleFont, settings.isConsoleDarkMode(), 500_000);
		consoleStyleHelper.setColorsEnabled(settings.isConsoleColorsEnabled());
		DefaultCaret caret = (DefaultCaret) consoleTextPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setViewportView(consoleTextPane);
		consoleScrollPane = scrollPane;
		scrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				if (consoleTextPane != null) {
					consoleTextPane.revalidate();
				}
			}
		});
		// Viewport ChangeListener can miss user scrolls; scroll bar AdjustmentListener fires when user scrolls (wheel or drag).
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				updateConsoleStickToBottomFromScrollBar(e);
			}
		});
		// Clickable links: open http(s) URLs in the default browser when clicked.
		consoleTextPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) return;
				String url = getLinkUrlAt(consoleTextPane, e.getPoint());
				if (url != null) {
					try {
						Desktop.getDesktop().browse(URI.create(url));
					} catch (Exception ex) {
						// ignore (e.g. no browser, invalid URL)
					}
				}
			}
		});
		consoleTextPane.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				String url = getLinkUrlAt(consoleTextPane, e.getPoint());
				consoleTextPane.setCursor(url != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
			}
		});

		consoleCommandInput = new JTextField();
		consoleCommandInput.setFont(consoleFont);
		consoleCommandInput.setMargin(new Insets(4, 6, 4, 6));
		consoleCommandInput.setPreferredSize(new Dimension(consoleCommandInput.getPreferredSize().width, 26));
		consoleCommandInput.setMinimumSize(new Dimension(60, 26));
		consoleCommandInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {

					if (server.isRunning()) {

						try {
							
							if (chkConsoleInputAsSay.isSelected()) {
								server.sendCommand("say " + consoleCommandInput.getText());
							}else {
								server.sendCommand(consoleCommandInput.getText());
							}
							
						} catch (ProcessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						consoleCommandInput.setText("");
						// Scroll to bottom after a short delay so the server's echo of the command has time to appear.
						Timer scrollAfterCommand = new Timer(150, e -> {
							scrollingFromCommand = true;
							ignoreScrollBarUntil = System.currentTimeMillis() + 250;
							scrollConsoleToBottomOnly();
							SwingUtilities.invokeLater(() -> scrollingFromCommand = false);
						});
						scrollAfterCommand.setRepeats(false);
						scrollAfterCommand.start();

					}

				}

			}
		});
		consoleCommandInput.setColumns(10);

		btnStartServer = new JButton("Start Server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {

					if (server.isRunning()) {
						JOptionPane.showMessageDialog(null, "A Server is already running.");
					}else {
						try {
							startServer();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}else {
					try {
						startServer();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});

		btnStopServer = new JButton("Stop Server");
		btnStopServer.setEnabled(false);
		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {
					if (server.isRunning()) {
						runShutdownOrRestartCountdown(getShutdownCountdownSeconds(), false);
					} else {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There are no servers running.");
					}
				}
			}
		});

		serverStatusCircle = new StatusCirclePanel();

		String serverIP;
		try {
			serverIP = Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {
			serverIP = "—";
		}
		JLabel lblServerIP = new JLabel("Server IP: " + serverIP);
		lblServerIP.setToolTipText("Local address for this machine. Players can use this to connect (e.g. for LAN).");
		lblServerIP.setMinimumSize(new Dimension(200, 20));
		lblServerIP.setPreferredSize(new Dimension(200, 20));

		btnRestartServer = new JButton("Restart Server");
		btnRestartServer.setEnabled(false);
		btnRestartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {
					if (server.isRunning()) {
						runShutdownOrRestartCountdown(getShutdownCountdownSeconds(), true);
					} else {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There are no servers running.");
					}
				}
			}
		});

		lblServerStatusText = new JLabel("Status: Offline");
		lblServerStatusText.setHorizontalAlignment(SwingConstants.RIGHT);
		lblServerStatusText.setMinimumSize(new Dimension(130, 20));
		lblServerStatusText.setPreferredSize(new Dimension(130, 20));

		chkConsoleScrollSticky = new JCheckBox("Console scroll sticky");
		chkConsoleScrollSticky.setSelected(consoleStickToBottom);
		chkConsoleScrollSticky.setToolTipText("When checked, the console will auto-scroll to the bottom when new lines arrive. When unchecked, it stays at the current position.");
		manualConsoleScrollStickyMode = settings.isManualConsoleScrollSticky();
		chkConsoleScrollSticky.setVisible(manualConsoleScrollStickyMode);
		chkConsoleScrollSticky.setEnabled(manualConsoleScrollStickyMode);
		chkConsoleScrollSticky.addActionListener(e -> {
			if (manualConsoleScrollStickyMode) {
				consoleStickToBottom = chkConsoleScrollSticky.isSelected();
				updateScrollStickyDebugCheckbox();
			}
		});

		chkConsoleInputAsSay = new JCheckBox("Console for /say");
		chkConsoleInputAsSay.setToolTipText("When checked, your console input is sent as \"say <text>\", so it appears as a server message in game chat. When unchecked, input is sent as a raw command.");

		// Top bar: server controls (left), server IP (center), status (right), visible in all tabs
		JPanel topBar = new JPanel(new BorderLayout(0, 0));
		JPanel topBarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
		topBarLeft.add(btnStartServer);
		topBarLeft.add(btnStopServer);
		topBarLeft.add(btnRestartServer);
		topBarLeft.add(Box.createHorizontalStrut(6));
		topBarLeft.add(lblServerIP);
		JPanel topBarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
		topBarRight.add(lblServerStatusText);
		serverStatusCircle.setPreferredSize(new Dimension(26, 26));
		topBarRight.add(serverStatusCircle);
		applyServerButtonStyle(!settings.isServerButtonsUseText());
		topBar.add(topBarLeft, BorderLayout.WEST);
		topBar.add(topBarRight, BorderLayout.EAST);
		contentPane.add(topBar, BorderLayout.NORTH);
		tabbedPane.setBorder(new EmptyBorder(8, 0, 0, 0));
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addComponent(consoleCommandInput, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
					.addGap(10))
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chkConsoleInputAsSay)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkConsoleScrollSticky)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(consoleCommandInput, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chkConsoleInputAsSay)
						.addComponent(chkConsoleScrollSticky))
					.addGap(3))
		);
		panel.setLayout(gl_panel);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Players", null, panel_1, null);

		JScrollPane scrollPane_1 = new JScrollPane();

		playersTable = new JTable();

		playersTable.setModel(new DefaultTableModel(
				new String[] { "Username", "Running IP" },
				0
				));
		lblPlayersOnlineCount = new JLabel("Players online: 0");
		scrollPane_1.setViewportView(playersTable);

		setActive(false);
		updateServerButtonStates(false);

		setTableAsList(playersTable, players);

		JMenuItem mntmPlayerName = new JMenuItem("Player Name");

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent arg0) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {}
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				int row = playersTable.getSelectedRow();
				if (row < 0) return;
				Object val = playersTable.getModel().getValueAt(row, 0);
				String player = (val == null) ? "" : val.toString().trim();
				mntmPlayerName.setText(player.isEmpty() ? "(no player)" : player);
			}
		});
		// Only show context menu when right-clicking on a row that has a player name (not empty/null)
		playersTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) showPlayersPopupIfRowValid(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) showPlayersPopupIfRowValid(e);
			}
			private void showPlayersPopupIfRowValid(MouseEvent e) {
				int row = playersTable.rowAtPoint(e.getPoint());
				if (row < 0) return;
				Object val = playersTable.getModel().getValueAt(row, 0);
				String player = (val == null) ? "" : val.toString().trim();
				if (player.isEmpty() || "(No players online)".equals(player)) return;
				playersTable.setRowSelectionInterval(row, row);
				popupMenu.show(playersTable, e.getX(), e.getY());
			}
		});

		JMenuItem mntmOp = new JMenuItem("Op");
		mntmOp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {
					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("op " + player);
				} catch (ProcessException e) {
					e.printStackTrace();
				}
			}
		});

		popupMenu.add(mntmPlayerName);

		JMenuItem menuItem_1 = new JMenuItem(" ");
		popupMenu.add(menuItem_1);
		popupMenu.add(mntmOp);

		JMenuItem mntmDeop = new JMenuItem("De-Op");
		mntmDeop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {
					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("deop " + player);
				} catch (ProcessException ex) {
					ex.printStackTrace();
				}
			}
		});
		popupMenu.add(mntmDeop);

		JMenuItem mntmKick = new JMenuItem("Kick");
		mntmKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {
					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					if (JOptionPane.showConfirmDialog(SpigotGUI.this, "Are you sure you want to kick " + player + "?", "Kick", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						String reason = JOptionPane.showInputDialog(SpigotGUI.this, "Reason?");
						module.sendCommand("kick " + player + (reason != null ? " " + reason : ""));
					}
				} catch (ProcessException ex) {
					ex.printStackTrace();
				}
			}
		});
		popupMenu.add(mntmKick);

		JMenuItem mntmBan = new JMenuItem("Ban");
		mntmBan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {
					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					if (JOptionPane.showConfirmDialog(SpigotGUI.this, "Are you sure you want to ban " + player + "?", "Ban", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						String reason = JOptionPane.showInputDialog(SpigotGUI.this, "Reason?");
						module.sendCommand("ban " + player + (reason != null ? " " + reason : ""));
					}
				} catch (ProcessException ex) {
					ex.printStackTrace();
				}
			}
		});
		popupMenu.add(mntmBan);

		JButton btnPardon = new JButton("Pardon a Player");
		btnPardon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String player = JOptionPane.showInputDialog(SpigotGUI.this, "Player Name");
				if (player == null || player.trim().isEmpty()) return;
				player = player.trim();
				try {
					if (JOptionPane.showConfirmDialog(SpigotGUI.this, "Are you sure you want to pardon " + player + "?", "Pardon", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						module.sendCommand("pardon " + player);
					}
				} catch (ProcessException ex) {
					ex.printStackTrace();
				}
			}
		});

		JButton btnKick = new JButton("Kick");
		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					if (JOptionPane.showConfirmDialog(SpigotGUI.this, "Are you sure you want to kick " + player + "?", "Kick", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						String reason = JOptionPane.showInputDialog(SpigotGUI.this, "Reason?");
						module.sendCommand("kick " + player + (reason != null ? " " + reason : ""));
					}
				} catch (ProcessException ex) {
					ex.printStackTrace();
				}
			}
		});

		JButton btnBan = new JButton("Ban");
		btnBan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					if (JOptionPane.showConfirmDialog(SpigotGUI.this, "Are you sure you want to ban " + player + "?", "Ban", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						String reason = JOptionPane.showInputDialog(SpigotGUI.this, "Reason?");
						module.sendCommand("ban " + player + (reason != null ? " " + reason : ""));
					}
				} catch (ProcessException ex) {
					ex.printStackTrace();
				}
			}
		});

		JButton btnOp = new JButton("Op");
		btnOp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("op " + player);
				} catch (ProcessException e) {
					e.printStackTrace();
				}
			}
		});

		JButton btnDeop = new JButton("De-Op");
		btnDeop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (server != null) {
					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(SpigotGUI.this, "There is no server running");
					return;
				}
				String player = playersTable.getModel().getValueAt(playersTable.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("deop " + player);
				} catch (ProcessException ex) {
					ex.printStackTrace();
				}
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(12)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
										.addComponent(btnKick)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnBan)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnOp)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnDeop)
										.addPreferredGap(ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
										.addComponent(btnPardon, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
										.addComponent(lblPlayersOnlineCount)
										.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)))
						.addContainerGap())
				);
		gl_panel_1.setVerticalGroup(
				gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblPlayersOnlineCount)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnKick)
								.addComponent(btnBan)
								.addComponent(btnOp)
								.addComponent(btnDeop)
								.addComponent(btnPardon))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGap(6)
						.addContainerGap())
				);
		panel_1.setLayout(gl_panel_1);

		// Resources tab: memory/CPU graph and stats (after Players, before Settings)
		resourcesPanel = new ResourcesPanel(server, serverSettings);
		resourcesPanel.startPolling();
		tabbedPane.addTab("Resources", null, resourcesPanel, null);

		JPanel panel_2 = new JPanel();
		// Settings tab is added later (before About/Help)

		minRam = new JSpinner();
		minRam.setModel(new SpinnerNumberModel(Integer.valueOf(1024), null, null, Integer.valueOf(1)));

		maxRam = new JSpinner();
		maxRam.setModel(new SpinnerNumberModel(Integer.valueOf(1024), null, null, Integer.valueOf(1)));
		
		JLabel lblMinRam = new JLabel("Min RAM");
		lblMinRam.setHorizontalAlignment(SwingConstants.LEFT);

		JLabel lblMaxRam = new JLabel("Max RAM");
		lblMaxRam.setHorizontalAlignment(SwingConstants.LEFT);

		customJvmArgsField = new JTextField();
		
		customJvmArgsField.setColumns(10);

		customJvmSwitchesField = new JTextField();
		customJvmSwitchesField.setColumns(10);
		
		fontSpinner = new JSpinner();
		fontSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Font newFont = getConsoleMonospaceFont((int) fontSpinner.getValue());
				consoleTextPane.setFont(newFont);
				if (consoleStyleHelper != null) {
					consoleStyleHelper.setBaseFont(newFont);
				}
			}
		});
		
		fontSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(13), null, null, Integer.valueOf(1)));
		
		minRam.setValue(serverSettings.getMinRam());
		maxRam.setValue(serverSettings.getMaxRam());
		fontSpinner.setValue(settings.getFontSize());
		customJvmArgsField.setText(serverSettings.getCustomArgs());
		customJvmSwitchesField.setText(serverSettings.getCustomSwitches());
		
		JLabel lblCustomArgs = new JLabel("Custom Arguments");
		JLabel lblCustomSwitches = new JLabel("Custom Switches");
		
		JTextField serverFileField = new JTextField(30);
		serverFileField.setEditable(false);
		serverFileField.setToolTipText("Path to the server JAR file. Use \"Set Server File\" to change.");
		serverFileField.setText(jarFile != null ? jarFile.getAbsolutePath() : new File("server.jar").getAbsolutePath());

		JButton btnSetJarFile = new JButton("Set Server File");
		btnSetJarFile.setToolTipText("Choose the server JAR file to run (e.g. paper.jar, spigot.jar).");
		btnSetJarFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File jarDir = getDefaultDirectory();
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(jarDir);
				fileChooser.setFileFilter(new FileNameExtensionFilter("Jar file (*.jar)", "jar"));
				if (jarFile != null && jarFile.getParentFile() != null) fileChooser.setSelectedFile(jarFile);
				if (fileChooser.showOpenDialog(SpigotGUI.this) == JFileChooser.APPROVE_OPTION) {
					jarFile = fileChooser.getSelectedFile();
					serverFileField.setText(jarFile.getAbsolutePath());
				}
			}
		});
		
		JLabel lblFontSize = new JLabel("Console font size");

		lblTheme = new JLabel("Theme (may require restart)");
		themeBox = new JComboBox<String>();
		themeBox.setModel(new DefaultComboBoxModel<>(new String[] {"Aluminium", "Aero", "Acryl", "Bernstein", "Fast", "Graphite", "HiFi", "Luna", "McWin", "Metal", "Mint", "Motif", "Noire", "Smart", "Texture", "Windows"}));
		themeBox.setSelectedItem(settings.getTheme().getName());
		updateThemeLabelFor(settings.getTheme());
		themeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String themeName = themeBox.getSelectedItem() + "";
				if (themeName.isEmpty()) return;
				Theme theme = Theme.valueOf(themeName.replaceAll(" ", "_"));
				boolean sameFamily = (initialThemeForSession != null && initialThemeForSession.getFamily() == theme.getFamily());
				if (sameFamily) {
					try {
						UIManager.setLookAndFeel(theme.getLookAndFeel());
						SwingUtilities.updateComponentTreeUI(SpigotGUI.this);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| UnsupportedLookAndFeelException e) {
						e.printStackTrace();
					}
				}
				updateThemeLabelFor(theme);
			}
		});

		consoleDarkModeCheckBox = new JCheckBox("Console dark mode");
		consoleDarkModeCheckBox.setToolTipText("Use a dark background in the console tab. When unchecked, the console uses a light background.");
		consoleDarkModeCheckBox.setSelected(settings.isConsoleDarkMode());
		consoleDarkModeCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (consoleStyleHelper != null) {
					consoleStyleHelper.setDarkMode(consoleDarkModeCheckBox.isSelected());
				}
			}
		});

		disableConsoleColorsCheckBox = new JCheckBox("Disable console colors");
		disableConsoleColorsCheckBox.setToolTipText("When checked, console text is shown in the default color only (no ANSI or § colors).");
		disableConsoleColorsCheckBox.setSelected(!settings.isConsoleColorsEnabled());
		disableConsoleColorsCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (consoleStyleHelper != null) {
					consoleStyleHelper.setColorsEnabled(!disableConsoleColorsCheckBox.isSelected());
				}
			}
		});

		consoleWrapWordBreakOnlyCheckBox = new JCheckBox("Console text wrap on word-break only");
		consoleWrapWordBreakOnlyCheckBox.setToolTipText("When unchecked (default), long lines wrap at any character so everything stays visible without a horizontal scrollbar. When checked, lines wrap only at spaces, so very long words or tokens may extend off-screen.");
		consoleWrapWordBreakOnlyCheckBox.setSelected(settings.isConsoleWrapWordBreakOnly());
		consoleWrapWordBreakOnlyCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConsoleStyleHelper.setConsoleWrapWordBreakOnly(consoleWrapWordBreakOnlyCheckBox.isSelected());
				if (consoleTextPane != null) {
					consoleTextPane.revalidate();
					consoleTextPane.repaint();
				}
			}
		});

		openFilesInSystemDefaultCheckBox = new JCheckBox("Open files in system default application");
		openFilesInSystemDefaultCheckBox.setToolTipText("When checked, double-clicking a file in the Files tab opens it in your system's default application instead of the built-in editor.");
		openFilesInSystemDefaultCheckBox.setSelected(settings.isOpenFilesInSystemDefault());
		openFilesInSystemDefaultCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileModel != null) {
					fileModel.setOpenInSystemDefault(openFilesInSystemDefaultCheckBox.isSelected());
				}
			}
		});

		JButton btnEditServerproperties = new JButton("Edit Server.Properties");
		btnEditServerproperties.setToolTipText("Edit the server.properties file.");
		btnEditServerproperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File serverProps = new File("server.properties");
				if (openFilesInSystemDefaultCheckBox != null && openFilesInSystemDefaultCheckBox.isSelected()) {
					try {
						Desktop.getDesktop().open(serverProps);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(SpigotGUI.this, "Could not open: " + e.getMessage());
					}
				} else {
					FileEditor fileEditor = new FileEditor();
					fileEditor.setLocationRelativeTo(SpigotGUI.this);
					try {
						fileEditor.openFile(serverProps);
					} catch (IOException e) {
						e.printStackTrace();
					}
					fileEditor.setVisible(true);
				}
			}
		});
		
		// --- Settings layout: scrollable sectioned panels (content does not stretch vertically) ---
		int pad = 6;
		JPanel settingsContentInner = new JPanel();
		settingsContentInner.setLayout(new BoxLayout(settingsContentInner, BoxLayout.Y_AXIS));
		settingsContentInner.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Section: Server — each row has its own layout (columns do not align across rows)
		JPanel serverSection = new JPanel();
		serverSection.setBorder(new TitledBorder(null, "Server", TitledBorder.LEADING, TitledBorder.TOP));
		serverSection.setLayout(new GridBagLayout());
		GridBagConstraints rc = new GridBagConstraints();
		rc.fill = GridBagConstraints.HORIZONTAL;
		rc.weightx = 1;
		rc.gridwidth = 1;
		rc.anchor = GridBagConstraints.WEST;
		rc.insets = new Insets(pad, pad, pad, pad);
		int serverBtnW = 120;
		btnSetJarFile.setPreferredSize(new Dimension(serverBtnW, btnSetJarFile.getPreferredSize().height));
		// Row 1: flexible text field + fixed Set Server File button
		JPanel row1 = new JPanel(new BorderLayout(8, 0));
		row1.add(serverFileField, BorderLayout.CENTER);
		row1.add(btnSetJarFile, BorderLayout.EAST);
		rc.gridx = 0; rc.gridy = 0; serverSection.add(row1, rc);
		JLabel lblShutdownCountdown = new JLabel("Shutdown/restart countdown (seconds)");
		lblShutdownCountdown.setToolTipText("When you click Stop or Restart, wait this many seconds before actually stopping (announces in chat). 0 = immediate.");
		shutdownCountdownSpinner = new JSpinner(new SpinnerNumberModel(Math.max(0, settings.getShutdownCountdownSeconds()), 0, 86400, 1));
		shutdownCountdownSpinner.setToolTipText(lblShutdownCountdown.getToolTipText());
		shutdownCountdownSpinner.setPreferredSize(new Dimension(90, shutdownCountdownSpinner.getPreferredSize().height));
		// Rows 2 & 3: same 3-column layout so Edit button matches first column width of countdown row
		JPanel countdownEditPanel = new JPanel(new GridBagLayout());
		GridBagConstraints ce = new GridBagConstraints();
		ce.insets = new Insets(0, 0, 0, 0);
		ce.anchor = GridBagConstraints.WEST;
		ce.fill = GridBagConstraints.NONE;
		ce.gridx = 0; ce.gridy = 0; ce.weightx = 0; countdownEditPanel.add(lblShutdownCountdown, ce);
		ce.insets = new Insets(0, 8, 0, 0);
		ce.gridx = 1; ce.weightx = 0; countdownEditPanel.add(shutdownCountdownSpinner, ce);
		ce.insets = new Insets(0, 0, 0, 0);
		ce.gridx = 2; ce.weightx = 1; ce.fill = GridBagConstraints.HORIZONTAL; countdownEditPanel.add(new JPanel(), ce);
		// Spacer row so there is visible gap between countdown row and Edit button row (match other section row spacing = 2*pad)
		JPanel spacerRow = new JPanel();
		int rowGap = pad * 2;
		spacerRow.setPreferredSize(new Dimension(0, rowGap));
		spacerRow.setMinimumSize(new Dimension(0, rowGap));
		ce.gridy = 1; ce.gridx = 0; ce.gridwidth = 3; ce.weightx = 1; ce.fill = GridBagConstraints.HORIZONTAL; countdownEditPanel.add(spacerRow, ce);
		ce.gridwidth = 1;
		ce.gridy = 2; ce.gridx = 0; ce.weightx = 0; ce.fill = GridBagConstraints.HORIZONTAL; countdownEditPanel.add(btnEditServerproperties, ce);
		ce.gridx = 1; ce.weightx = 0; ce.fill = GridBagConstraints.NONE; countdownEditPanel.add(new JPanel(), ce);
		ce.gridx = 2; ce.weightx = 1; ce.fill = GridBagConstraints.HORIZONTAL; countdownEditPanel.add(new JPanel(), ce);
		rc.gridy = 1; rc.gridheight = 2; serverSection.add(countdownEditPanel, rc);
		rc.gridheight = 1;
		settingsContentInner.add(serverSection);

		// Section: JVM / Run options — custom args/switches first, then min/max RAM (fixed-width spinners)
		JPanel jvmSection = new JPanel();
		jvmSection.setBorder(new TitledBorder(null, "JVM / Run options", TitledBorder.LEADING, TitledBorder.TOP));
		jvmSection.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(pad, pad, pad, pad);
		c.anchor = GridBagConstraints.WEST;
		lblCustomArgs.setToolTipText("<html><b>Custom arguments</b> — Passed to the JVM as program arguments (e.g. <code>-Dproperty=value</code> for system properties).<br>These appear after the class/jar and before any application arguments. Use for JVM tuning and -D flags. Switches (above) are for launcher options.</html>");
		customJvmArgsField.setToolTipText(lblCustomArgs.getToolTipText());
		lblCustomSwitches.setToolTipText("<html><b>Custom switches</b> — Passed to the java launcher as command-line switches (e.g. <code>-Xmx2G</code>, <code>-XX:+UseG1GC</code>).<br>These appear before the class/jar. Use for memory, GC, and other JVM options. Arguments (below) are for -D and app-level; switches are for launcher options.</html>");
		customJvmSwitchesField.setToolTipText(lblCustomSwitches.getToolTipText());
		JScrollPane argsScroll = new JScrollPane(customJvmArgsField);
		argsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		argsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		argsScroll.setBorder(null);
		customJvmArgsField.addMouseWheelListener(e -> {
			Component p = customJvmArgsField.getParent();
			if (p instanceof JViewport) {
				JScrollPane sp = (JScrollPane) ((JViewport) p).getParent();
				javax.swing.JScrollBar bar = sp.getHorizontalScrollBar();
				if (bar != null && bar.isEnabled()) {
					int step = e.getUnitsToScroll() * 24;
					bar.setValue(Math.max(bar.getMinimum(), Math.min(bar.getMaximum() - bar.getVisibleAmount(), bar.getValue() + step)));
				}
			}
		});
		JScrollPane switchesScroll = new JScrollPane(customJvmSwitchesField);
		switchesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		switchesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		switchesScroll.setBorder(null);
		customJvmSwitchesField.addMouseWheelListener(e -> {
			Component p = customJvmSwitchesField.getParent();
			if (p instanceof JViewport) {
				JScrollPane sp = (JScrollPane) ((JViewport) p).getParent();
				javax.swing.JScrollBar bar = sp.getHorizontalScrollBar();
				if (bar != null && bar.isEnabled()) {
					int step = e.getUnitsToScroll() * 24;
					bar.setValue(Math.max(bar.getMinimum(), Math.min(bar.getMaximum() - bar.getVisibleAmount(), bar.getValue() + step)));
				}
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; c.gridy = 0; c.weightx = 0; jvmSection.add(lblCustomSwitches, c);
		c.gridx = 1; c.gridwidth = 2; c.weightx = 1; jvmSection.add(switchesScroll, c);
		c.gridwidth = 1;
		c.gridy = 1; c.gridx = 0; c.weightx = 0; jvmSection.add(lblCustomArgs, c);
		c.gridx = 1; c.gridwidth = 2; c.weightx = 1; jvmSection.add(argsScroll, c);
		c.gridwidth = 1;
		int spinnerW = 90;
		minRam.setPreferredSize(new Dimension(spinnerW, minRam.getPreferredSize().height));
		maxRam.setPreferredSize(new Dimension(spinnerW, maxRam.getPreferredSize().height));
		lblMinRam.setToolTipText("Minimum heap size in MB allocated to the server JVM.");
		minRam.setToolTipText(lblMinRam.getToolTipText());
		lblMaxRam.setToolTipText("Maximum heap size in MB allocated to the server JVM.");
		maxRam.setToolTipText(lblMaxRam.getToolTipText());
		c.fill = GridBagConstraints.NONE;
		c.gridy = 2; c.gridx = 0; c.weightx = 0; jvmSection.add(lblMinRam, c);
		c.gridx = 1; c.weightx = 0; jvmSection.add(minRam, c);
		c.gridx = 2; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; jvmSection.add(new JPanel(), c);
		c.fill = GridBagConstraints.NONE;
		c.gridy = 3; c.gridx = 0; c.weightx = 0; jvmSection.add(lblMaxRam, c);
		c.gridx = 1; c.weightx = 0; jvmSection.add(maxRam, c);
		c.gridx = 2; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; jvmSection.add(new JPanel(), c);
		c.gridwidth = 1;
		customJvmArgsField.setMinimumSize(new Dimension(60, 20));
		customJvmSwitchesField.setMinimumSize(new Dimension(60, 20));
		settingsContentInner.add(jvmSection);

		// Section: Files (full width like other sections; checkbox left-aligned)
		JPanel filesSection = new JPanel();
		filesSection.setBorder(new TitledBorder(null, "Files", TitledBorder.LEADING, TitledBorder.TOP));
		filesSection.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.insets = new Insets(pad, pad, pad, pad);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = 0; c.weightx = 0; filesSection.add(openFilesInSystemDefaultCheckBox, c);
		c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; filesSection.add(new JPanel(), c);
		settingsContentInner.add(filesSection);

		// Section: Appearance (theme, file editor theme, console font size, console options)
		JPanel appearanceSection = new JPanel();
		appearanceSection.setBorder(new TitledBorder(null, "Appearance", TitledBorder.LEADING, TitledBorder.TOP));
		appearanceSection.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.insets = new Insets(pad, pad, pad, pad);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		lblTheme.setToolTipText("Look and feel for the application. May require an application restart to take effect.");
		themeBox.setToolTipText(lblTheme.getToolTipText());
		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.gridwidth = 1; appearanceSection.add(lblTheme, c);
		c.gridx = 1; c.weightx = 1; appearanceSection.add(themeBox, c);
		JLabel lblFileEditorTheme = new JLabel("File editor theme");
		lblFileEditorTheme.setToolTipText("Syntax highlighting theme used in the built-in file editor.");
		fileEditorThemeBox = new JComboBox<>(new String[] { "default", "default-alt", "dark", "druid", "eclipse", "idea", "monokai", "vs" });
		fileEditorThemeBox.setSelectedItem(settings.getFileEditorTheme());
		fileEditorThemeBox.setToolTipText(lblFileEditorTheme.getToolTipText());
		fileEditorThemeBox.addActionListener(e -> me.justicepro.spigotgui.FileExplorer.FileEditor.setDefaultThemeName(getFileEditorThemeFromBox()));
		c.gridy = 1; c.gridx = 0; c.weightx = 0; appearanceSection.add(lblFileEditorTheme, c);
		c.gridx = 1; c.weightx = 1; appearanceSection.add(fileEditorThemeBox, c);
		lblFontSize.setToolTipText("Font size (in points) for the console text.");
		fontSpinner.setPreferredSize(new Dimension(90, fontSpinner.getPreferredSize().height));
		fontSpinner.setToolTipText(lblFontSize.getToolTipText());
		c.gridy = 2; c.gridx = 0; c.weightx = 0; c.fill = GridBagConstraints.NONE; appearanceSection.add(lblFontSize, c);
		c.gridx = 1; c.weightx = 0; c.fill = GridBagConstraints.NONE; appearanceSection.add(fontSpinner, c);
		c.gridx = 2; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; appearanceSection.add(new JPanel(), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 3; c.gridx = 0; c.gridwidth = 2; appearanceSection.add(consoleDarkModeCheckBox, c);
		c.gridwidth = 1;
		c.gridy = 4; c.gridx = 0; c.gridwidth = 2; appearanceSection.add(disableConsoleColorsCheckBox, c);
		c.gridy = 5; c.gridx = 0; c.gridwidth = 2; appearanceSection.add(consoleWrapWordBreakOnlyCheckBox, c);
		manualConsoleScrollStickyCheckBox = new JCheckBox("Manual console scroll sticky");
		manualConsoleScrollStickyCheckBox.setToolTipText("<html>When checked, a \"Console scroll sticky\" checkbox appears on the Console tab.<br>You control whether the console auto-scrolls to the bottom by toggling that checkbox.<br>When unchecked, sticky is automatic: scroll to bottom to stick, scroll up to unstick.</html>");
		manualConsoleScrollStickyCheckBox.setSelected(settings.isManualConsoleScrollSticky());
		manualConsoleScrollStickyCheckBox.addActionListener(e -> {
			manualConsoleScrollStickyMode = manualConsoleScrollStickyCheckBox.isSelected();
			if (chkConsoleScrollSticky != null) {
				chkConsoleScrollSticky.setVisible(manualConsoleScrollStickyMode);
				chkConsoleScrollSticky.setEnabled(manualConsoleScrollStickyMode);
				if (manualConsoleScrollStickyMode) chkConsoleScrollSticky.setSelected(consoleStickToBottom);
			}
		});
		c.gridy = 6; c.gridx = 0; c.gridwidth = 2; appearanceSection.add(manualConsoleScrollStickyCheckBox, c);
		serverButtonsUseTextCheckBox = new JCheckBox("Use text for server control buttons");
		serverButtonsUseTextCheckBox.setToolTipText("When checked, Start/Stop/Restart show text. When unchecked, they show only icons (play, stop, refresh) with tooltips.");
		serverButtonsUseTextCheckBox.setSelected(settings.isServerButtonsUseText());
		serverButtonsUseTextCheckBox.addActionListener(e -> applyServerButtonStyle(!serverButtonsUseTextCheckBox.isSelected()));
		c.gridy = 7; c.gridx = 0; c.gridwidth = 2; appearanceSection.add(serverButtonsUseTextCheckBox, c);
		minRam.setMinimumSize(new Dimension(50, minRam.getPreferredSize().height));
		maxRam.setMinimumSize(new Dimension(50, maxRam.getPreferredSize().height));
		fontSpinner.setMinimumSize(new Dimension(50, fontSpinner.getPreferredSize().height));
		themeBox.setMinimumSize(new Dimension(80, themeBox.getPreferredSize().height));
		fileEditorThemeBox.setMinimumSize(new Dimension(80, fileEditorThemeBox.getPreferredSize().height));
		settingsContentInner.add(appearanceSection);
		me.justicepro.spigotgui.FileExplorer.FileEditor.setDefaultThemeName(settings.getFileEditorTheme());

		// Wrap in Scrollable panel so content does not stretch vertically when window is tall
		JPanel settingsContent = new JPanel(new BorderLayout()) {
			@Override
			public java.awt.Dimension getPreferredSize() {
				return settingsContentInner.getPreferredSize();
			}
		};
		settingsContent.add(settingsContentInner, BorderLayout.NORTH);

		JScrollPane settingsScroll = new JScrollPane(settingsContent);
		settingsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		settingsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		settingsScroll.getVerticalScrollBar().setUnitIncrement(24);
		settingsScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		panel_2.setLayout(new BorderLayout());
		panel_2.add(settingsScroll, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Files", null, panel_3, null);

		JScrollPane scrollPane_2 = new JScrollPane();

		File jarDir = getDefaultDirectory();

		JList<String> fileList = new JList<String>();
		fileModel = new FileModel(fileList);
		fileModel.setParentFrame(this);
		fileModel.setOpenInSystemDefault(settings.isOpenFilesInSystemDefault());
		fileList.setModel(fileModel);
		fileList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				// Hover highlight (like Windows Explorer) when not selected
				if (!isSelected && index == fileListHoverIndex) {
					java.awt.Color bg = getBackground();
					setBackground(bg != null ? bg.darker() : java.awt.Color.LIGHT_GRAY);
				}
				String s = value == null ? "" : value.toString();
				Icon dirIcon = UIManager.getIcon("FileView.directoryIcon");
				Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
				if ("..".equals(s)) {
					setIcon(dirIcon != null ? dirIcon : UIManager.getIcon("FileView.upFolderIcon"));
					setText(".. (up one level)");
				} else if (s.startsWith("/")) {
					setIcon(dirIcon);
					setText(s.substring(1));
				} else {
					setIcon(fileIcon);
					setText(s);
				}
				return this;
			}
		});
		java.awt.event.MouseMotionListener motionListener = new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int i = fileList.locationToIndex(e.getPoint());
				if (i != fileListHoverIndex) {
					fileListHoverIndex = i;
					fileList.repaint();
				}
			}
		};
		fileList.addMouseMotionListener(motionListener);
		fileList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (fileListHoverIndex != -1) {
					fileListHoverIndex = -1;
					fileList.repaint();
				}
			}
		});

		fileList.addMouseListener(fileModel.createMouseListener());
		fileList.addKeyListener(fileModel.createKeyListener());
		fileModel.loadDirectory(jarDir);

		scrollPane_2.setViewportView(fileList);

		JPopupMenu filesContextMenu = new JPopupMenu();
		JMenuItem mnuOpen = new JMenuItem("Open");
		mnuOpen.addActionListener(e -> fileModel.onFileRun());
		filesContextMenu.add(mnuOpen);
		filesContextMenu.addSeparator();
		JMenuItem mnuCut = new JMenuItem("Cut");
		mnuCut.addActionListener(e -> { if (fileModel.getSelectedFile() != null) fileModel.setClipboard(fileModel.getSelectedFile(), true); });
		filesContextMenu.add(mnuCut);
		JMenuItem mnuCopy = new JMenuItem("Copy");
		mnuCopy.addActionListener(e -> { if (fileModel.getSelectedFile() != null) fileModel.setClipboard(fileModel.getSelectedFile(), false); });
		filesContextMenu.add(mnuCopy);
		JMenuItem mnuPaste = new JMenuItem("Paste");
		mnuPaste.addActionListener(e -> fileModel.pasteFromClipboard());
		filesContextMenu.add(mnuPaste);
		filesContextMenu.addSeparator();
		JMenuItem mnuDelete = new JMenuItem("Delete");
		mnuDelete.addActionListener(e -> {
			File sel = fileModel.getSelectedFile();
			if (sel != null && JOptionPane.showConfirmDialog(SpigotGUI.this, "Delete \"" + sel.getName() + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				fileModel.deleteSelected();
		});
		filesContextMenu.add(mnuDelete);
		JMenuItem mnuRename = new JMenuItem("Rename");
		mnuRename.addActionListener(e -> {
			File sel = fileModel.getSelectedFile();
			if (sel != null) {
				String name = JOptionPane.showInputDialog(SpigotGUI.this, "New name:", sel.getName());
				if (name != null) fileModel.renameSelected(name);
			}
		});
		filesContextMenu.add(mnuRename);
		filesContextMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override public void popupMenuCanceled(PopupMenuEvent e) {}
			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				boolean hasSel = fileModel.getSelectedFile() != null;
				mnuOpen.setEnabled(hasSel);
				mnuCut.setEnabled(hasSel);
				mnuCopy.setEnabled(hasSel);
				mnuDelete.setEnabled(hasSel);
				mnuRename.setEnabled(hasSel);
				mnuPaste.setEnabled(FileModel.getClipboardFile() != null);
			}
		});
		// Right-click: select row under cursor then show context menu
		fileList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int i = fileList.locationToIndex(e.getPoint());
					if (i >= 0) {
						fileList.setSelectedIndex(i);
						fileList.requestFocusInWindow();
					}
					filesContextMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int i = fileList.locationToIndex(e.getPoint());
					if (i >= 0) {
						fileList.setSelectedIndex(i);
						fileList.requestFocusInWindow();
					}
					filesContextMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		JButton btnNew = new JButton("New...");
		JPopupMenu newMenu = new JPopupMenu();
		JMenuItem mnuNewFile = new JMenuItem("New file");
		mnuNewFile.addActionListener(e -> {
			String name = JOptionPane.showInputDialog(SpigotGUI.this, "File name:", "new file.txt");
			if (name != null) fileModel.createNewFile(name);
		});
		newMenu.add(mnuNewFile);
		JMenuItem mnuNewFolder = new JMenuItem("New folder");
		mnuNewFolder.addActionListener(e -> {
			String name = JOptionPane.showInputDialog(SpigotGUI.this, "Folder name:", "new folder");
			if (name != null) fileModel.createNewFolder(name);
		});
		newMenu.add(mnuNewFolder);
		btnNew.addActionListener(e -> newMenu.show(btnNew, 0, btnNew.getHeight()));

		JButton btnFileEditor = new JButton("File Editor");
		btnFileEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileEditor editor = new FileEditor();
				editor.setLocationRelativeTo(SpigotGUI.this);
				editor.setVisible(true);
			}
		});
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
				gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
				.addGroup(gl_panel_3.createSequentialGroup()
						.addComponent(btnNew)
						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnFileEditor, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				);
		gl_panel_3.setVerticalGroup(
				gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
						.addGap(6)
						.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnNew)
								.addComponent(btnFileEditor))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE))
				);
		panel_3.setLayout(gl_panel_3);

		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Module List", null, panel_4, null);

		JScrollPane scrollPane_3 = new JScrollPane();

		JList<String> moduleList = new JList<String>();
		DefaultListModel<String> moduleListModel = new DefaultListModel<>();

		moduleList.setModel(moduleListModel);

		for (Module module : ModuleManager.modules) {
			moduleListModel.addElement(module.getName());
		}

		scrollPane_3.setViewportView(moduleList);

		JPopupMenu popupMenu_1 = new JPopupMenu();
		popupMenu_1.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {

			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				popupMenu_1.removeAll();

				if (moduleList.getSelectedIndex() != -1) {

					JMenuItem mntmModuleName = new JMenuItem(moduleList.getSelectedValue());
					popupMenu_1.add(mntmModuleName);

					JMenuItem menuItem_2 = new JMenuItem(" ");
					popupMenu_1.add(menuItem_2);
					System.out.println(moduleList.getSelectedValue());
					Module module = ModuleManager.getModule(moduleList.getSelectedValue());

					if (module != null) {

						if (module.getMenuItems() != null) {

							for (JMenuItem item : module.getMenuItems()) {
								popupMenu_1.add(item);
							}

						}else {
							JMenuItem noModuleMenuItem = new JMenuItem("No Module Items");
							popupMenu_1.add(noModuleMenuItem);
						}

					}

				}

			}
		});
		addPopup(moduleList, popupMenu_1);
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
				gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
				);
		gl_panel_4.setVerticalGroup(
				gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
				);
		panel_4.setLayout(gl_panel_4);

		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Remote Admin", null, panel_6, null);

		JButton btnConnectToServer = new JButton("Connect to Server");
		btnConnectToServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LoginWindow login = new LoginWindow();
				login.setVisible(true);

			}
		});

		JButton btnCreateServer = new JButton("Host Server");
		btnCreateServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerWindow serverWindow = new ServerWindow();
				serverWindow.setVisible(true);
			}
		});
		GroupLayout gl_panel_6 = new GroupLayout(panel_6);
		gl_panel_6.setHorizontalGroup(
				gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_6.createSequentialGroup()
						.addComponent(btnConnectToServer, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
						.addComponent(btnCreateServer, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))
				);
		gl_panel_6.setVerticalGroup(
				gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_6.createSequentialGroup()
						.addContainerGap(535, Short.MAX_VALUE)
						.addGroup(gl_panel_6.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnConnectToServer, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnCreateServer, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)))
				);
		panel_6.setLayout(gl_panel_6);

		tabbedPane.addTab("Settings", null, panel_2, null);

		JPanel panel_5 = new JPanel();
		tabbedPane.addTab("About/Help", null, panel_5, null);

		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					InstructionWindow window = new InstructionWindow();
					window.setLocationRelativeTo(SpigotGUI.this);
					window.setVisible(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		JLabel lblCreatedByJusticepro = new JLabel("By JusticePro, Ymerejliaf");
		JLabel lblThemesByJtatoo = new JLabel("Themes by JTatoo");

		GroupLayout gl_panel_5 = new GroupLayout(panel_5);
		gl_panel_5.setHorizontalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnHelp, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblCreatedByJusticepro)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblThemesByJtatoo)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_5.setVerticalGroup(
			gl_panel_5.createSequentialGroup()
				.addContainerGap()
				.addComponent(btnHelp)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblCreatedByJusticepro)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(lblThemesByJtatoo)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		panel_5.setLayout(gl_panel_5);

		for (Module module : ModuleManager.modules) {

			if (module.getPage() != null) {
				JPanel panel1 = new JPanel();
				JModulePanel p = module.getPage();

				JScrollPane sp = new JScrollPane();
				sp.add(p);

				panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

				panel1.add(p);

				tabbedPane.addTab(p.getTitle(), null, panel1, null);
			}

		}


	}

	public static void saveSettings(Settings settings) throws IOException {
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File("spigotgui.settings")));
		output.writeObject(settings);
		output.flush();
		output.close();
	}

	public static Settings loadSettings() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		File file = new File("spigotgui.settings");
		File backupFile = new File("spigotgui.settings.old");

		if (!file.exists()) {
			// No main file — try backup (e.g. from a previous failed load)
			if (backupFile.exists()) {
				Settings fromBackup = tryLoadSettingsFrom(backupFile);
				if (fromBackup != null) {
					saveSettings(fromBackup); // migrate to new format on main file
					return fromBackup;
				}
			}
			Theme defaultTheme = System.getProperty("os.name", "").toLowerCase().contains("win") ? Theme.Windows : Theme.Graphite;
			Settings defaults = new Settings(ServerSettings.getDefault(), defaultTheme, 13, false, true, false, "default");
			saveSettings(defaults);
			return defaults;
		}

		// Try main file first
		Settings settings = tryLoadSettingsFrom(file);
		if (settings != null) {
			return settings;
		}

		// Main file failed (e.g. old/incompatible format) — try backup if it exists
		if (backupFile.exists()) {
			settings = tryLoadSettingsFrom(backupFile);
			if (settings != null) {
				saveSettings(settings); // migrate to new format
				return settings;
			}
		}

		// Both failed — backup main file so we don't lose it, then use defaults
		file.renameTo(backupFile);
		Theme defaultTheme = System.getProperty("os.name", "").toLowerCase().contains("win") ? Theme.Windows : Theme.Graphite;
		Settings defaults = new Settings(ServerSettings.getDefault(), defaultTheme, 13, false, true, false, "default");
		saveSettings(defaults);
		return defaults;
	}

	/** Try to deserialize Settings from a file; returns null on any error. */
	private static Settings tryLoadSettingsFrom(File file) {
		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
			return (Settings) input.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	/** Add nodes from under "dir" into curTop. Highly recursive. */
	DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
		String curPath = dir.getPath();
		DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
		if (curTop != null) { // should only be null at root
			curTop.add(curDir);
		}
		Vector<String> ol = new Vector<>();
		String[] tmp = dir.list();
		for (int i = 0; i < tmp.length; i++)
			ol.addElement(tmp[i]);
		Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
		File f;
		Vector<String> files = new Vector<>();
		// Make two passes, one for Dirs and one for Files. This is #1.
		for (int i = 0; i < ol.size(); i++) {
			String thisObject = ol.elementAt(i);
			String newPath;
			if (curPath.equals("."))
				newPath = thisObject;
			else
				newPath = curPath + File.separator + thisObject;
			if ((f = new File(newPath)).isDirectory())
				addNodes(curDir, f);
			else
				files.addElement(thisObject);
		}
		// Pass two: for files.
		for (int fnum = 0; fnum < files.size(); fnum++)
			curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
		return curDir;
	}

	public void setTableAsList(JTable playersTable, List<Player> players) {
		DefaultTableModel model = (DefaultTableModel) playersTable.getModel();
		model.setRowCount(0);
		for (Player p : players) {
			model.addRow(new Object[] { p.username, p.lastIP });
		}
		if (players.isEmpty()) {
			model.addRow(new Object[] { "(No players online)", "" });
		}
		updatePlayersOnlineCountLabel(players.size());
	}

	private void updatePlayersOnlineCountLabel(int count) {
		if (lblPlayersOnlineCount == null) return;
		String maxStr = getServerMaxPlayers();
		lblPlayersOnlineCount.setText(maxStr != null
				? "Players online: " + count + " / " + maxStr
				: "Players online: " + count);
	}

	/** Reads max-players from server.properties if present; returns null if not found or not started. */
	private String getServerMaxPlayers() {
		return getServerMaxPlayersStatic();
	}

	/** Static version for use from ResourcesPanel etc. Reads max-players from server.properties. */
	public static String getServerMaxPlayersStatic() {
		try {
			File f = new File("server.properties");
			if (!f.canRead()) return null;
			for (String line : Files.readAllLines(f.toPath())) {
				line = line.trim();
				if (line.startsWith("max-players=") && !line.startsWith("#")) {
					return line.substring("max-players=".length()).trim();
				}
			}
		} catch (Exception ignored) { }
		return null;
	}

	public static void updatePlayerData(Player player) {

		boolean contains = false;

		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);

			if (p.username.equalsIgnoreCase(player.username)) {
				contains = true;
				p.lastIP = player.lastIP;
			}

		}

		if (!contains) {
			players.add(player);
		}

		instance.setTableAsList(instance.playersTable, players);
	}

	public static void removePlayerData(String player) {
		try {
			ArrayList<Player> plys = new ArrayList<>(players);
			for (int i = 0; i < plys.size(); i++) {
				Player p = plys.get(i);
				if (p.username.equalsIgnoreCase(player)) {
					players.remove(i);
					System.out.println("Player Found");
					break;
				}
			}
			instance.setTableAsList(instance.playersTable, players);
		} catch (Exception e) {
			Dialogs.showError("An error occurred while updating player data.");
		}
	}

	public void startServer() throws IOException {
		startServer("nogui " + customJvmArgsField.getText(), Server.makeMemory(minRam.getValue() + "M", maxRam.getValue() + "M") + " " + customJvmSwitchesField.getText());

	}

	public void startServer(String args, String switches) throws IOException {
		try {
			javax.swing.text.Document doc = consoleTextPane.getDocument();
			doc.remove(0, doc.getLength());
		} catch (javax.swing.text.BadLocationException e) {
			// ignore
		}
		File eula = new File("eula.txt");

		if (!eula.exists()) {

			int result = JOptionPane.showOptionDialog(null,
					"Do you agree to the Minecraft Eula? (https://account.mojang.com/documents/minecraft_eula)", "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

			if (result==JOptionPane.YES_OPTION) {
				Files.copy(getClass().getResourceAsStream("/eula.txt"), eula.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}else {
				JOptionPane.showMessageDialog(null, "You must agree to the eula to run a server.");
				return;
			}

		}

		if (jarFile==null) {

			File file = new File("server.jar");

			if (file.exists()) {
				jarFile = file;
			}else {
				JOptionPane.showMessageDialog(null, "There is no selected jar file. Look at Server Settings.");
				return;
			}

		}

		if (!jarFile.exists()) {
			JOptionPane.showMessageDialog(null, "The selected jar file does not exist.");
			return;
		}

		if (server != null) {

			if (!server.isRunning()) {
				server = new Server(jarFile, "nogui " + args, switches);
				try {
					server.start();
				} catch (IOException | ProcessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setActive(true);
			}else {
				System.out.println("Server is Running");
			}

		}else {
			server = new Server(jarFile, "nogui " + args, switches);
			try {
				server.start();
			} catch (IOException | ProcessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setActive(true);
		}

	}

	public void setActive(boolean active) throws IOException {

		if (active==true) {
			serverStatusCircle.setOnline(true);
			lblServerStatusText.setText("Status: Online");
		}else {
			serverStatusCircle.setOnline(false);
			lblServerStatusText.setText("Status: Offline");
		}
		updateServerButtonStates(active);
	}

	/** Enable/disable Start, Stop, Restart based on whether the server is running. */
	private void updateServerButtonStates(boolean serverRunning) {
		Color enabledFg = UIManager.getColor("Button.foreground");
		if (enabledFg == null) enabledFg = Color.BLACK;
		Color disabledFg = UIManager.getColor("Button.disabledText");
		if (disabledFg == null) disabledFg = Color.GRAY;
		for (JButton btn : new JButton[] { btnStartServer, btnStopServer, btnRestartServer }) {
			if (btn == null) continue;
			boolean enable = (btn == btnStartServer) ? !serverRunning : serverRunning;
			btn.setEnabled(enable);
			btn.setForeground(enable ? enabledFg : disabledFg);
			btn.repaint();
		}
	}

	/** Create play/stop/restart icons with a fixed color so we can set both normal and disabled icon. */
	private static Icon createPlayIcon(int size, final Color color) {
		return new Icon() {
			@Override public int getIconWidth() { return size; }
			@Override public int getIconHeight() { return size; }
			@Override public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(color);
				GeneralPath play = new GeneralPath();
				play.moveTo(x + 5, y + 3);
				play.lineTo(x + 5, y + size - 3);
				play.lineTo(x + size - 4, y + size / 2);
				play.closePath();
				g2.fill(play);
				g2.dispose();
			}
		};
	}
	private static Icon createStopIcon(int size, final Color color) {
		return new Icon() {
			@Override public int getIconWidth() { return size; }
			@Override public int getIconHeight() { return size; }
			@Override public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(color);
				g2.fillRect(x + 5, y + 5, size - 10, size - 10);
				g2.dispose();
			}
		};
	}
	private static Icon createRestartIcon(int size, final Color color) {
		return new Icon() {
			@Override public int getIconWidth() { return size; }
			@Override public int getIconHeight() { return size; }
			@Override public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(color);
				int cx = x + size / 2, cy = y + size / 2;
				int rad = size / 2 - 2;
				if (rad < 4) rad = 4;
				// Arc like Edge: starts upper-right (35°), runs CCW to top-right (315°) — gap at top
				Arc2D arc = new Arc2D.Float(cx - rad, cy - rad, rad * 2, rad * 2, 35, 280, Arc2D.OPEN);
				java.awt.Stroke old = g2.getStroke();
				g2.setStroke(new java.awt.BasicStroke(2f));
				g2.draw(arc);
				g2.setStroke(old);
				// Arrow at end of arc (315° = top-right), tip pointing down-right (tangent direction)
				double endRad = Math.toRadians(315);
				double cos = Math.cos(endRad), sin = Math.sin(endRad);
				double baseX = cx + rad * cos;
				double baseY = cy + rad * sin;
				double tx = -sin, ty = cos;  // tangent for CCW at 315° → down-right in Java
				double tipLen = 5.5, halfBase = 3.5;
				double tipX = baseX + tipLen * tx;
				double tipY = baseY + tipLen * ty;
				double px = cos * halfBase, py = sin * halfBase;  // perpendicular for base
				GeneralPath arrow = new GeneralPath();
				arrow.moveTo((float) tipX, (float) tipY);
				arrow.lineTo((float) (baseX - px), (float) (baseY - py));
				arrow.lineTo((float) (baseX + px), (float) (baseY + py));
				arrow.closePath();
				g2.fill(arrow);
				g2.dispose();
			}
		};
	}

	/** Show icons (when useIcons true) or text (when false) on Start/Stop/Restart. Use setDisabledIcon so disabled state shows gray. */
	private void applyServerButtonStyle(boolean useIcons) {
		if (btnStartServer == null) return;
		int size = 18;
		Color enabledFg = UIManager.getColor("Button.foreground");
		if (enabledFg == null) enabledFg = Color.BLACK;
		Color disabledFg = UIManager.getColor("Button.disabledText");
		if (disabledFg == null) disabledFg = Color.GRAY;
		if (useIcons) {
			btnStartServer.setText("");
			btnStartServer.setIcon(createPlayIcon(size, enabledFg));
			btnStartServer.setDisabledIcon(createPlayIcon(size, disabledFg));
			btnStartServer.setToolTipText("Start Server");
			btnStopServer.setText("");
			btnStopServer.setIcon(createStopIcon(size, enabledFg));
			btnStopServer.setDisabledIcon(createStopIcon(size, disabledFg));
			btnStopServer.setToolTipText("Stop Server");
			btnRestartServer.setText("");
			btnRestartServer.setIcon(createRestartIcon(size, enabledFg));
			btnRestartServer.setDisabledIcon(createRestartIcon(size, disabledFg));
			btnRestartServer.setToolTipText("Restart Server");
		} else {
			btnStartServer.setIcon(null);
			btnStartServer.setDisabledIcon(null);
			btnStartServer.setText("Start Server");
			btnStartServer.setToolTipText(null);
			btnStopServer.setIcon(null);
			btnStopServer.setDisabledIcon(null);
			btnStopServer.setText("Stop Server");
			btnStopServer.setToolTipText(null);
			btnRestartServer.setIcon(null);
			btnRestartServer.setDisabledIcon(null);
			btnRestartServer.setText("Restart Server");
			btnRestartServer.setToolTipText(null);
		}
	}

	/** Current shutdown/restart countdown in seconds (from Settings tab spinner if built, else from settings). */
	private int getShutdownCountdownSeconds() {
		if (shutdownCountdownSpinner != null) {
			Object v = shutdownCountdownSpinner.getValue();
			if (v instanceof Number) return Math.max(0, ((Number) v).intValue());
		}
		return 0;
	}

	/** Runs countdown then stops (and sets restart flag if isRestart). Called from EDT. */
	private void runShutdownOrRestartCountdown(int seconds, final boolean isRestart) {
		String verb = isRestart ? "Restart" : "Shutdown";
		try {
			if (seconds <= 0) {
				module.sendCommand("say Server " + verb + "!");
				stopServer();
				if (isRestart) restart = true;
				return;
			}
			module.sendCommand("say Server " + verb + "!");
			final int secs = seconds;
			new Thread(() -> {
				try {
					for (int i = secs; i > 0; i--) {
						boolean announce = (i == secs) || (i <= 10) || (i == 30) || (i == 60) || (i < secs && i % 60 == 0);
						if (announce)
							module.sendCommand("say " + verb + "ing in " + i + " second" + (i == 1 ? "" : "s") + (i == secs && secs >= 60 ? ". Get to a safe spot." : "."));
						Thread.sleep(1000);
					}
				} catch (InterruptedException | ProcessException e1) {
					Thread.currentThread().interrupt();
					e1.printStackTrace();
				}
				try {
					stopServer();
					if (isRestart) restart = true;
				} catch (ProcessException e) {
					e.printStackTrace();
				}
			}).start();
		} catch (ProcessException e1) {
			e1.printStackTrace();
		}
	}

	public void stopServer() throws ProcessException {

		if (server != null) {
			if (server.isRunning()) {
				server.sendCommand("stop");
			}
		}

	}

	public void addToConsole(String message) {
		if (consoleStyleHelper == null) return;
		// Run append and scroll on EDT so we can control whether the view scrolls at all.
		SwingUtilities.invokeLater(() -> {
			ignoreScrollBarUntil = System.currentTimeMillis() + 250;
			boolean wasStick = consoleStickToBottom;
			JViewport vp = consoleScrollPane != null ? consoleScrollPane.getViewport() : null;
			Point savedPos = (vp != null && !wasStick) ? vp.getViewPosition() : null;

			if (!wasStick && consoleTextPane != null) {
				// Prevent the view from scrolling when we append: caret won't scroll to show new content.
				DefaultCaret caret = (DefaultCaret) consoleTextPane.getCaret();
				caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
				try {
					consoleStyleHelper.appendLine(message);
					if (vp != null && savedPos != null) vp.setViewPosition(savedPos);
				} finally {
					caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
				}
			} else {
				consoleStyleHelper.appendLine(message);
				if (wasStick) scrollConsoleToBottom();
			}
			updateScrollStickyDebugCheckbox();
		});
	}

	/** Updates {@link #consoleStickToBottom}: true if scroll bar is at bottom, false if user has scrolled up. */
	private void updateConsoleStickToBottomFromScrollBar(AdjustmentEvent e) {
		// When manual mode is on, sticky is controlled only by the checkbox.
		if (manualConsoleScrollStickyMode) return;
		// Never update sticky when we're scrolling from the command (one-time jump without re-sticking).
		if (scrollingFromCommand) return;
		// User-initiated scroll (drag thumb, wheel, page up/down) always updates sticky so we re-stick when they scroll to bottom even during heavy append.
		// Programmatic scroll (our scroll or viewport restore) is ignored during ignoreScrollBarUntil.
		boolean userInitiated = (e != null && isUserScrollAdjustment(e.getAdjustmentType()));
		if (!userInitiated && System.currentTimeMillis() < ignoreScrollBarUntil) return;
		if (consoleScrollPane == null) return;
		javax.swing.JScrollBar bar = consoleScrollPane.getVerticalScrollBar();
		if (bar == null || !bar.isVisible()) return;
		int value = bar.getValue();
		int extent = bar.getModel().getExtent();
		int max = bar.getMaximum();
		// At bottom when visible range reaches the end (small tolerance)
		consoleStickToBottom = (value + extent >= max - 5);
		updateScrollStickyDebugCheckbox();
	}

	/** True if the adjustment type is from direct user input (drag, wheel, page), not programmatic value change. */
	private static boolean isUserScrollAdjustment(int adjustmentType) {
		return adjustmentType == AdjustmentEvent.TRACK
			|| adjustmentType == AdjustmentEvent.UNIT_INCREMENT
			|| adjustmentType == AdjustmentEvent.UNIT_DECREMENT
			|| adjustmentType == AdjustmentEvent.BLOCK_INCREMENT
			|| adjustmentType == AdjustmentEvent.BLOCK_DECREMENT;
	}

	/** Scrolls the console view so the latest output is visible; called when stick-to-bottom is on. */
	private void scrollConsoleToBottom() {
		scrollConsoleToBottomOnly();
		consoleStickToBottom = true; // we just scrolled to bottom, so stick until user scrolls up
		updateScrollStickyDebugCheckbox();
	}

	/** Scrolls the console to the bottom without changing the sticky state (e.g. after user enters a command). */
	@SuppressWarnings("deprecation")
	private void scrollConsoleToBottomOnly() {
		if (consoleTextPane == null) return;
		try {
			int len = consoleTextPane.getDocument().getLength();
			consoleTextPane.setCaretPosition(len);
			Rectangle r = consoleTextPane.modelToView(len);
			if (r != null) {
				consoleTextPane.scrollRectToVisible(r);
			}
		} catch (BadLocationException ignored) {}
	}

	/** Updates the debug checkbox to match {@link #consoleStickToBottom} (call on EDT). */
	private void updateScrollStickyDebugCheckbox() {
		if (chkConsoleScrollSticky != null) {
			chkConsoleScrollSticky.setSelected(consoleStickToBottom);
		}
	}

	/** If the given point in the text pane is over a link (http/https), returns the URL; otherwise null. */
	@SuppressWarnings("deprecation")
	private static String getLinkUrlAt(JTextPane textPane, Point p) {
		int offs = textPane.viewToModel(p);
		if (offs < 0) return null;
		StyledDocument doc = (StyledDocument) textPane.getDocument();
		javax.swing.text.Element el = doc.getCharacterElement(offs);
		Object url = el.getAttributes().getAttribute(ConsoleStyleHelper.LINK_URL);
		return (url instanceof String) ? (String) url : null;
	}

	/** Returns a Unicode-friendly monospace font for the console. */
	private static Font getConsoleMonospaceFont(int size) {
		String[] preferred = { "Consolas", "DejaVu Sans Mono", "Monospaced", "Courier New" };
		for (String name : preferred) {
			Font f = new Font(name, Font.PLAIN, size);
			if (name.equals(f.getFamily())) {
				return f;
			}
		}
		return new Font(Font.MONOSPACED, Font.PLAIN, size);
	}

	/** Default directory for file browser and choosers; works when run from IDE or from JAR. */
	private static File getDefaultDirectory() {
		java.net.URL url = ClassLoader.getSystemClassLoader().getResource(".");
		if (url != null) {
			return new File(url.getPath().replaceAll("%20", " "));
		}
		return new File(System.getProperty("user.dir", "."));
	}

	/** Updates the theme label to (may require restart) or (restart required) in red; keeps label at fixed size so layout never shifts. */
	private void updateThemeLabelFor(Theme selectedTheme) {
		if (lblTheme == null) return;
		boolean sameFamily = initialThemeForSession != null && initialThemeForSession.getFamily() == selectedTheme.getFamily();
		if (sameFamily) {
			lblTheme.setText("Theme (may require restart)");
			lblTheme.setForeground(null);
			if (themeLabelWidth <= 0) {
				int w = lblTheme.getPreferredSize().width;
				int minW = lblTheme.getFontMetrics(lblTheme.getFont()).stringWidth("Theme (may require restart)") + 8;
				themeLabelWidth = Math.max(w, minW);
				themeLabelHeight = lblTheme.getPreferredSize().height;
			}
		} else {
			lblTheme.setText("<html>Theme <font color='red'>(restart required)</font></html>");
			if (themeLabelWidth <= 0) {
				int w = lblTheme.getPreferredSize().width;
				int minW = lblTheme.getFontMetrics(lblTheme.getFont()).stringWidth("Theme (may require restart)") + 8;
				themeLabelWidth = Math.max(w, minW);
				themeLabelHeight = lblTheme.getPreferredSize().height;
			}
		}
		// Always apply fixed size so the label never changes size and the settings page doesn't shift
		lblTheme.setPreferredSize(new Dimension(themeLabelWidth, themeLabelHeight));
		lblTheme.setMinimumSize(new Dimension(themeLabelWidth, themeLabelHeight));
	}

	private String getFileEditorThemeFromBox() {
		Object sel = fileEditorThemeBox != null ? fileEditorThemeBox.getSelectedItem() : null;
		return (sel != null && sel.toString().length() > 0) ? sel.toString() : "default";
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	class ModuleCore extends Module implements ActionListener {

		public ModuleCore() {
			super("Core");
		}

		@Override
		public String getWebsite() {
			return "the Core";
		}

		@Override
		public void onConsolePrintRaw(String message) {
			super.onConsolePrintRaw(message);
			addToConsole(message);
		}

		@Override
		public void onPlayerJoin(String player, String ip) {
			updatePlayerData(new Player(player, ip));
		}

		@Override
		public void onPlayerLeave(String player, String reason) {
			removePlayerData(player);
		}

		@Override
		public void onServerClosed() {
			super.onServerClosed();

			if (restart) {
				try {
					startServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						setActive(false);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				restart = false;
			}else {
				setTitle("SpigotGUI Remastered (" + versionTag + ")");
				try {
					setActive(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void init() {
			super.init();
			RServer.serverPacketHandlers.add(serverHandler);

		}

		@Override
		public Permission[] getPermissions() {
			Permission[] permissions = {
					CorePermissions.CONSOLE_READ,
					CorePermissions.CONSOLE_SEND,
					CorePermissions.CHAT_READ,
					CorePermissions.CHAT_SEND,
					CorePermissions.ADMIN,
					CorePermissions.ALLOW_BOT,
			};

			return permissions;
		}

		@Override
		public void onBukkitVersionDetected(String version) {
			super.onBukkitVersionDetected(version);

			setVersionDisplay();
		}

		@Override
		public void onSpongeVersionDetected(String version) {
			super.onSpongeVersionDetected(version);

			setVersionDisplay();
		}

		public void setVersionDisplay() {
			setTitle("SpigotGUI Remastered (" + versionTag + ") - [" + getServerType().getName() + "]");
		}

		@Override
		public void onVersionDetected(String version) {
			setVersionDisplay();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

		}

	}

	public static boolean isRunning() {
		if (server == null) {
			return false;
		}
		return server.isRunning();
	}

	/** Panel that paints a green (online) or red (offline) circle for server status. */
	private static final class StatusCirclePanel extends JPanel {
		private boolean online = false;

		void setOnline(boolean online) {
			if (this.online != online) {
				this.online = online;
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth();
			int h = getHeight();
			int d = (int) (Math.min(w, h) * 0.6);
			if (d < 4) d = 4;
			int x = (w - d) / 2;
			int y = (h - d) / 2;
			g2.setColor(online ? new Color(0, 180, 0) : new Color(200, 0, 0));
			g2.fill(new Ellipse2D.Float(x, y, d, d));
			g2.setColor(online ? new Color(0, 220, 0) : new Color(255, 80, 80));
			g2.draw(new Ellipse2D.Float(x, y, d, d));
			g2.dispose();
		}
	}
}