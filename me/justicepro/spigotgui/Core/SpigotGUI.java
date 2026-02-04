package me.justicepro.spigotgui.Core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
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
import javax.swing.text.DefaultCaret;
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
	private JTextField inputTxt;

	private JComboBox<String> exitTimer;

	private JTextPane consoleTextPane;
	private ConsoleStyleHelper consoleStyleHelper;
	private JLabel status;

	private JSpinner maxRam;
	private JSpinner minRam;

	private JLabel lblStatus;

	private JComboBox<String> themeBox;
	/** Theme at app startup; used to decide if we can apply a new theme without restart (same family only). */
	private static Theme initialThemeForSession;
	/** Fixed size for theme label so it never changes when toggling text (avoids layout shift). */
	private static int themeLabelWidth = 0;
	private static int themeLabelHeight = 0;
	private JLabel lblTheme;

	public static Server server = null;

	public ImageIcon imgactive = loadIcon("/Active Small.png");
	public ImageIcon imgnotactive = loadIcon("/Not-Active Small.png");
	private JTable table;

	/** Load an image from classpath; returns null if missing so the app still starts. */
	private static ImageIcon loadIcon(String path) {
		try {
			java.io.InputStream in = SpigotGUI.class.getResourceAsStream(path);
			if (in != null) {
				return new ImageIcon(ImageIO.read(in));
			}
		} catch (Exception e) {
			// ignore — icon optional
		}
		return null;
	}

	public static SpigotGUI instance;

	public static ArrayList<Player> players = new ArrayList<>();

	private static Module module;

	private boolean restart = false;
	private JTextField customArgsTxt;
	private JTextField customSwitchesTxt;
	private JSpinner fontSpinner;
	
	private JCheckBox chckbxConsoleForsay;
	private JCheckBox consoleDarkModeCheckBox;
	private JCheckBox disableConsoleColorsCheckBox;
	private JCheckBox openFilesInSystemDefaultCheckBox;
	private JComboBox<String> fileEditorThemeBox;
	private FileModel fileModel;

	public static File jarFile;

	public static ServerHandler serverHandler = new ServerHandler();

	public static final String versionTag = "1.1.1";

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
				
				Settings s = new Settings(new ServerSettings(minRam.getValue(), maxRam.getValue(), customArgsTxt.getText(), customSwitchesTxt.getText(), jarFile), settings.getTheme(), fontSpinner.getValue(), consoleDarkModeCheckBox.isSelected(), !disableConsoleColorsCheckBox.isSelected(), openFilesInSystemDefaultCheckBox.isSelected(), getFileEditorThemeFromBox());
				
				for (Theme t : Theme.values()) {

					if (t.getName().equalsIgnoreCase(theme)) {
						s.setTheme(t);
					}

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
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Console", null, panel, null);

		JScrollPane scrollPane = new JScrollPane();

		int fontSize = (int) settings.getFontSize();
		Font consoleFont = getConsoleMonospaceFont(fontSize);
		consoleTextPane = new JTextPane();
		consoleTextPane.setFont(consoleFont);
		consoleTextPane.setEditable(false);
		consoleTextPane.setMargin(new java.awt.Insets(4, 4, 4, 4));
		consoleStyleHelper = new ConsoleStyleHelper(consoleTextPane, consoleFont, settings.isConsoleDarkMode(), 500_000);
		consoleStyleHelper.setColorsEnabled(settings.isConsoleColorsEnabled());
		DefaultCaret caret = (DefaultCaret) consoleTextPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setViewportView(consoleTextPane);

		inputTxt = new JTextField();
		inputTxt.setFont(consoleFont);
		inputTxt.setMargin(new Insets(4, 6, 4, 6));
		inputTxt.setPreferredSize(new Dimension(inputTxt.getPreferredSize().width, 26));
		inputTxt.setMinimumSize(new Dimension(60, 26));
		inputTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {

					if (server.isRunning()) {

						try {
							
							if (chckbxConsoleForsay.isSelected()) {
								server.sendCommand("say " + inputTxt.getText());
							}else {
								server.sendCommand(inputTxt.getText());
							}
							
						} catch (ProcessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						inputTxt.setText("");

					}

				}

			}
		});
		inputTxt.setColumns(10);

		JButton btnStartServer = new JButton("Start Server");
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

		JButton btnStopServer = new JButton("Stop Server");
		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {

					if (server.isRunning()) {

						try {
							module.sendCommand("say Server Shutdown!");

							if ( (exitTimer.getSelectedItem() + "").equalsIgnoreCase("No Exit Timer") ) {

								try {
									stopServer();
								} catch (ProcessException ex) {
									// TODO Auto-generated catch block
									ex.printStackTrace();
								}

							}

							if ( (exitTimer.getSelectedItem() + "").equalsIgnoreCase("1 Minute") ) {

								new Thread(new Runnable() {
									public void run() {
										try {

											module.sendCommand("say Stopping in 1 minute. Get to a safe spot.");

											Thread.sleep(1000 * 30);

											module.sendCommand("say Stopping in 30 seconds.");

											Thread.sleep(1000 * 20);

											for (int i = 10; i > 0; i--) {
												module.sendCommand("say Stopping in " + i + " seconds.");
												Thread.sleep(1000);
											}

										} catch (InterruptedException | ProcessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										try {
											stopServer();
										} catch (ProcessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								}).start();

							}

						} catch (ProcessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}else {
						JOptionPane.showMessageDialog(null, "There are no servers running.");
					}
				}

			}
		});

		status = new JLabel("");

		JLabel lblNewLabel = new JLabel("IP: " + Inet4Address.getLocalHost().getHostAddress());

		JButton btnRestartServer = new JButton("Restart Server");
		btnRestartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {

					if (server.isRunning()) {

						try {
							module.sendCommand("say Server Restart!");

							if ( (exitTimer.getSelectedItem() + "").equalsIgnoreCase("No Exit Timer") ) {

								try {
									stopServer();
									restart = true;
								} catch (ProcessException ex) {
									// TODO Auto-generated catch block
									ex.printStackTrace();
								}

							}

							if ( (exitTimer.getSelectedItem() + "").equalsIgnoreCase("1 Minute") ) {

								new Thread(new Runnable() {
									public void run() {
										try {

											module.sendCommand("say Restarting in 1 minute. Get to a safe spot.");

											Thread.sleep(1000 * 30);

											module.sendCommand("say Restarting in 30 seconds.");

											Thread.sleep(1000 * 20);

											for (int i = 10; i > 0; i--) {
												module.sendCommand("say Restarting in " + i + " seconds.");
												Thread.sleep(1000);
											}

										} catch (InterruptedException | ProcessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										try {
											stopServer();
											restart = true;
										} catch (ProcessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								}).start();

							}

						} catch (ProcessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}else {
						JOptionPane.showMessageDialog(null, "There are no servers running.");
					}
				}

			}
		});

		exitTimer = new JComboBox<String>();
		exitTimer.setModel(new DefaultComboBoxModel<String>(new String[] {"No Exit Timer", "1 Minute"}));

		lblStatus = new JLabel("Status: Offline");
		
		chckbxConsoleForsay = new JCheckBox("Console for /say");
		chckbxConsoleForsay.setToolTipText("When checked, your console input is sent as \"say <text>\", so it appears as a server message in game chat. When unchecked, input is sent as a raw command.");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(chckbxConsoleForsay)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(btnStartServer, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
							.addGap(11)
							.addComponent(btnStopServer, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnRestartServer, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(exitTimer, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
					.addComponent(status, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addComponent(inputTxt, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(inputTxt, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnStartServer)
								.addComponent(btnStopServer)
								.addComponent(btnRestartServer)
								.addComponent(exitTimer))
							.addGap(56))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(chckbxConsoleForsay)
								.addComponent(lblStatus))
							.addGap(3))
						.addComponent(status, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
		);
		panel.setLayout(gl_panel);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Players", null, panel_1, null);

		JScrollPane scrollPane_1 = new JScrollPane();

		table = new JTable();

		table.setModel(new DefaultTableModel(
				new Object[][] {
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
				},
				new String[] {
						"Username", "Running IP"
				}
				));
		scrollPane_1.setViewportView(table);

		setActive(false);

		setTableAsList(table, players);

		JMenuItem mntmPlayerName = new JMenuItem("Player Name");

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent arg0) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {}
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				int row = table.getSelectedRow();
				if (row < 0) return;
				Object val = table.getModel().getValueAt(row, 0);
				String player = (val == null) ? "" : val.toString().trim();
				mntmPlayerName.setText(player.isEmpty() ? "(no player)" : player);
			}
		});
		// Only show context menu when right-clicking on a row that has a player name (not empty/null)
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) showPlayersPopupIfRowValid(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) showPlayersPopupIfRowValid(e);
			}
			private void showPlayersPopupIfRowValid(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if (row < 0) return;
				Object val = table.getModel().getValueAt(row, 0);
				String player = (val == null) ? "" : val.toString().trim();
				if (player.isEmpty()) return;
				table.setRowSelectionInterval(row, row);
				popupMenu.show(table, e.getX(), e.getY());
			}
		});

		JMenuItem mntmOp = new JMenuItem("Op");
		mntmOp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("op " + player);
				} catch (ProcessException e) {
					// TODO Auto-generated catch block
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
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("deop " + player);
				} catch (ProcessException ex) {
					// TODO Auto-generated catch block
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
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {
					if (JOptionPane.showConfirmDialog(null, "Are you sure you want to kick " + player, "Kick", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						module.sendCommand("kick " + player + " " + JOptionPane.showInputDialog(null, "Reason?"));
					}
				} catch (ProcessException ex) {
					// TODO Auto-generated catch block
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
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {

					if (JOptionPane.showConfirmDialog(null, "Are you sure you want to ban " + player, "Ban", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						module.sendCommand("ban " + player + " " + JOptionPane.showInputDialog(null, "Reason?"));
					}

				} catch (ProcessException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

			}
		});
		popupMenu.add(mntmBan);

		JButton btnPardon = new JButton("Pardon a Player");
		btnPardon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String player = JOptionPane.showInputDialog(null, "Player Name");
				try {

					if (JOptionPane.showConfirmDialog(null, "Are you sure you want to pardon " + player, "Pardon", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						module.sendCommand("pardon " + player);
					}

				} catch (ProcessException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});

		JButton btnKick = new JButton("Kick");
		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {
					if (JOptionPane.showConfirmDialog(null, "Are you sure you want to kick " + player, "Kick", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						module.sendCommand("kick " + player + " " + JOptionPane.showInputDialog(null, "Reason?"));
					}
				} catch (ProcessException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

			}
		});

		JButton btnBan = new JButton("Ban");
		btnBan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {

					if (JOptionPane.showConfirmDialog(null, "Are you sure you want to ban " + player, "Ban", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						module.sendCommand("ban " + player + " " + JOptionPane.showInputDialog(null, "Reason?"));
					}

				} catch (ProcessException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

			}
		});

		JButton btnOp = new JButton("Op");
		btnOp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("op " + player);
				} catch (ProcessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		JButton btnDeop = new JButton("De-Op");
		btnDeop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (server != null) {

					if (!server.isRunning()) {
						JOptionPane.showMessageDialog(null, "There is no server running");
						return;
					}

				}else {
					JOptionPane.showMessageDialog(null, "There is no server running");
					return;
				}

				String player = table.getModel().getValueAt(table.getSelectedRow(), 0) + "";
				try {
					module.sendCommand("deop " + player);
				} catch (ProcessException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
										.addComponent(btnPardon, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnKick)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnBan)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnOp)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnDeop))
								.addGroup(gl_panel_1.createSequentialGroup()
										.addGap(12)
										.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)))
						.addContainerGap())
				);
		gl_panel_1.setVerticalGroup(
				gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGap(18)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnPardon)
								.addComponent(btnKick)
								.addComponent(btnBan)
								.addComponent(btnOp)
								.addComponent(btnDeop)))
				);
		panel_1.setLayout(gl_panel_1);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Settings", null, panel_2, null);

		minRam = new JSpinner();
		minRam.setModel(new SpinnerNumberModel(Integer.valueOf(1024), null, null, Integer.valueOf(1)));

		maxRam = new JSpinner();
		maxRam.setModel(new SpinnerNumberModel(Integer.valueOf(1024), null, null, Integer.valueOf(1)));
		
		JLabel lblMinRam = new JLabel("Min Ram");
		lblMinRam.setHorizontalAlignment(SwingConstants.LEFT);

		JLabel lblMaxRam = new JLabel("Max Ram");
		lblMaxRam.setHorizontalAlignment(SwingConstants.LEFT);

		customArgsTxt = new JTextField();
		
		customArgsTxt.setColumns(10);

		customSwitchesTxt = new JTextField();
		customSwitchesTxt.setColumns(10);
		
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
		customArgsTxt.setText(serverSettings.getCustomArgs());
		customSwitchesTxt.setText(serverSettings.getCustomSwitches());
		
		JLabel lblCustomArgs = new JLabel("Custom Arguments");
		JLabel lblCustomSwitches = new JLabel("Custom Switches");
		
		JLabel lblJarFile = new JLabel("Server File: server.jar");
		lblJarFile.setHorizontalAlignment(SwingConstants.LEFT);

		JButton btnSetJarFile = new JButton("Set Server File");
		btnSetJarFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File jarDir = getDefaultDirectory();
				JFileChooser fileChooser = new JFileChooser();

				fileChooser.setCurrentDirectory(jarDir);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar file (*.jar)", "jar");

				fileChooser.setFileFilter(filter);

				int result = fileChooser.showOpenDialog(SpigotGUI.this);

				if (result==JFileChooser.APPROVE_OPTION) {
					jarFile = fileChooser.getSelectedFile();
					lblJarFile.setText("Server File: " + jarFile.getAbsolutePath());
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
		disableConsoleColorsCheckBox.setSelected(!settings.isConsoleColorsEnabled());
		disableConsoleColorsCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (consoleStyleHelper != null) {
					consoleStyleHelper.setColorsEnabled(!disableConsoleColorsCheckBox.isSelected());
				}
			}
		});

		openFilesInSystemDefaultCheckBox = new JCheckBox("Open files in system default application");
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
		btnEditServerproperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileEditor fileEditor = new FileEditor();
				fileEditor.setLocationRelativeTo(SpigotGUI.this);
				try {
					fileEditor.openFile(new File("server.properties"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileEditor.setVisible(true);
			}
		});
		
		// --- Settings layout: scrollable sectioned panels (content does not stretch vertically) ---
		int pad = 6;
		JPanel settingsContentInner = new JPanel();
		settingsContentInner.setLayout(new BoxLayout(settingsContentInner, BoxLayout.Y_AXIS));
		settingsContentInner.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Section: Server (path + two buttons same width, left-aligned)
		JPanel serverSection = new JPanel();
		serverSection.setBorder(new TitledBorder(null, "Server", TitledBorder.LEADING, TitledBorder.TOP));
		serverSection.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(pad, pad, pad, pad);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = 0; c.weightx = 1; serverSection.add(lblJarFile, c);
		btnEditServerproperties.setPreferredSize(new Dimension(220, btnEditServerproperties.getPreferredSize().height));
		c.gridy = 1; c.weightx = 0; serverSection.add(btnSetJarFile, c);
		c.gridx = 1; serverSection.add(btnEditServerproperties, c);
		settingsContentInner.add(serverSection);

		// Section: JVM / Run options
		JPanel jvmSection = new JPanel();
		jvmSection.setBorder(new TitledBorder(null, "JVM / Run options", TitledBorder.LEADING, TitledBorder.TOP));
		jvmSection.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.insets = new Insets(pad, pad, pad, pad);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; c.gridy = 0; c.weightx = 0; jvmSection.add(lblMinRam, c);
		c.gridx = 1; c.weightx = 1; jvmSection.add(minRam, c);
		c.gridy = 1; c.gridx = 0; c.weightx = 0; jvmSection.add(lblMaxRam, c);
		c.gridx = 1; c.weightx = 1; jvmSection.add(maxRam, c);
		c.gridy = 2; c.gridx = 0; c.weightx = 0; jvmSection.add(lblCustomArgs, c);
		c.gridx = 1; c.weightx = 1; jvmSection.add(customArgsTxt, c);
		c.gridy = 3; c.gridx = 0; c.weightx = 0; jvmSection.add(lblCustomSwitches, c);
		c.gridx = 1; c.weightx = 1; jvmSection.add(customSwitchesTxt, c);
		customArgsTxt.setMinimumSize(new Dimension(60, 20));
		customSwitchesTxt.setMinimumSize(new Dimension(60, 20));
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
		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.gridwidth = 1; appearanceSection.add(lblTheme, c);
		c.gridx = 1; c.weightx = 1; appearanceSection.add(themeBox, c);
		JLabel lblFileEditorTheme = new JLabel("File editor theme");
		fileEditorThemeBox = new JComboBox<>(new String[] { "default", "default-alt", "dark", "druid", "eclipse", "idea", "monokai", "vs" });
		fileEditorThemeBox.setSelectedItem(settings.getFileEditorTheme());
		fileEditorThemeBox.addActionListener(e -> me.justicepro.spigotgui.FileExplorer.FileEditor.setDefaultThemeName(getFileEditorThemeFromBox()));
		c.gridy = 1; c.gridx = 0; c.weightx = 0; appearanceSection.add(lblFileEditorTheme, c);
		c.gridx = 1; c.weightx = 1; appearanceSection.add(fileEditorThemeBox, c);
		c.gridy = 2; c.gridx = 0; c.weightx = 0; appearanceSection.add(lblFontSize, c);
		c.gridx = 1; c.weightx = 1; appearanceSection.add(fontSpinner, c);
		c.gridy = 3; c.gridx = 0; c.gridwidth = 2; appearanceSection.add(consoleDarkModeCheckBox, c);
		c.gridwidth = 1;
		c.gridy = 4; c.gridx = 0; c.gridwidth = 2; appearanceSection.add(disableConsoleColorsCheckBox, c);
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

		fileList.addMouseListener(fileModel.createMouseListener());
		fileList.addKeyListener(fileModel.createKeyListener());
		fileModel.loadDirectory(jarDir);

		scrollPane_2.setViewportView(fileList);

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
						.addComponent(btnFileEditor, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				);
		gl_panel_3.setVerticalGroup(
				gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
						.addGap(6)
						.addComponent(btnFileEditor)
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

		JPanel panel_5 = new JPanel();
		tabbedPane.addTab("About/Help", null, panel_5, null);

		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					InstructionWindow window = new InstructionWindow();
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

	public void setTableAsList(JTable table, List<Player> players) {

		for (int i = 0; i < table.getModel().getRowCount(); i++) {
			table.getModel().setValueAt(null, i, 0);
			table.getModel().setValueAt(null, i, 1);
		}

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);

			table.getModel().setValueAt(player.username, i, 0);
			table.getModel().setValueAt(player.lastIP, i, 1);
			/*table.getModel().setValueAt(player.whitelisted, i, 2);
			table.getModel().setValueAt(player.opped, i, 3);*/

		}

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

		instance.setTableAsList(instance.table, players);
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
			instance.setTableAsList(instance.table, players);
		} catch (Exception e) {
			Dialogs.showError("An error occurred while updating player data.");
		}
	}

	public void startServer() throws IOException {
		startServer("nogui " + customArgsTxt.getText(), Server.makeMemory(minRam.getValue() + "M", maxRam.getValue() + "M") + " " + customSwitchesTxt.getText());

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
			status.setIcon(imgactive);
			lblStatus.setText("Status: Online");
		}else {
			status.setIcon(imgnotactive);
			lblStatus.setText("Status: Offline");
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
		if (consoleStyleHelper != null) {
			consoleStyleHelper.appendLine(message);
		}
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

			if (getVersion() != null) {
				setTitle("SpigotGUI Remastered (" + versionTag + ") - " + getVersion().getName() + " API [" + getServerType().getName() + "]");
			}else {
				setTitle("SpigotGUI Remastered (" + versionTag + ") - [" + getServerType().getName() + "]");
			}

		}

		@Override
		public void onVersionDetected(String version) {
			super.onVersionDetected(version);

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
}