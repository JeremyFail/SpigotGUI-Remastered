package me.justicepro.spigotgui.Core;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;

import me.justicepro.spigotgui.Utils.ConsoleStyleHelper;

/**
 * Builds the Console tab: scrollable log, command input field, and options
 * (Console for /say, Console scroll sticky). Callbacks to the main frame
 * for sending commands and scroll/sticky behavior.
 */
public class ConsolePanel extends JPanel {

	private final JTextPane consoleTextPane;
	private final JScrollPane consoleScrollPane;
	private final JTextField consoleCommandInput;
	private final JCheckBox chkConsoleInputAsSay;
	private final JCheckBox chkConsoleScrollSticky;
	private final ConsoleStyleHelper consoleStyleHelper;

	/**
	 * @param gui Main frame; must implement console callbacks (send command, scroll, sticky).
	 * @param consoleFont Monospace font for the log and input.
	 * @param consoleDarkMode Dark background for the log.
	 * @param colorsEnabled Whether to apply ANSI/§ colors.
	 * @param wrapWordBreakOnly Wrap only at word boundaries.
	 * @param manualStickyMode Whether the sticky checkbox is visible and controls sticky.
	 * @param initialStickToBottom Initial value for scroll sticky.
	 */
	public ConsolePanel(SpigotGUI gui, Font consoleFont, boolean consoleDarkMode, boolean colorsEnabled,
			boolean wrapWordBreakOnly, boolean manualStickyMode, boolean initialStickToBottom) {
		ConsoleStyleHelper.setConsoleWrapWordBreakOnly(wrapWordBreakOnly);
		consoleTextPane = new JTextPane();
		consoleTextPane.setEditorKit(new me.justicepro.spigotgui.Utils.WrapEditorKit());
		consoleTextPane.setFont(consoleFont);
		consoleTextPane.setEditable(false);
		consoleTextPane.setMargin(new Insets(4, 4, 4, 4));
		consoleStyleHelper = new ConsoleStyleHelper(consoleTextPane, consoleFont, consoleDarkMode, 500_000);
		consoleStyleHelper.setColorsEnabled(colorsEnabled);
		DefaultCaret caret = (DefaultCaret) consoleTextPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(consoleTextPane);
		consoleScrollPane = scrollPane;
		scrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				if (consoleTextPane != null) consoleTextPane.revalidate();
			}
		});
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				gui.notifyConsoleScrollBarAdjustment(e);
			}
		});

		// Clickable links in console
		consoleTextPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) return;
				String url = getLinkUrlAt(consoleTextPane, e.getPoint());
				if (url != null) {
					try {
						java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
					} catch (Exception ex) { /* ignore */ }
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

		chkConsoleInputAsSay = new JCheckBox("Console for /say");
		chkConsoleInputAsSay.setToolTipText("When checked, your console input is sent as \"say <text>\", so it appears as a server message in game chat. When unchecked, input is sent as a raw command.");

		chkConsoleScrollSticky = new JCheckBox("Console scroll sticky");
		chkConsoleScrollSticky.setSelected(initialStickToBottom);
		chkConsoleScrollSticky.setToolTipText("When checked, the console will auto-scroll to the bottom when new lines arrive. When unchecked, it stays at the current position.");
		chkConsoleScrollSticky.setVisible(manualStickyMode);
		chkConsoleScrollSticky.setEnabled(manualStickyMode);
		chkConsoleScrollSticky.addActionListener(ev -> {
			if (manualStickyMode) {
				gui.setConsoleStickToBottom(chkConsoleScrollSticky.isSelected());
				gui.updateScrollStickyDebugCheckbox();
			}
		});

		consoleCommandInput = new JTextField();
		consoleCommandInput.setFont(consoleFont);
		consoleCommandInput.setMargin(new Insets(4, 6, 4, 6));
		consoleCommandInput.setPreferredSize(new java.awt.Dimension(consoleCommandInput.getPreferredSize().width, 26));
		consoleCommandInput.setMinimumSize(new java.awt.Dimension(60, 26));
		consoleCommandInput.addActionListener(e -> {
			String text = consoleCommandInput.getText().trim();
			if (text.isEmpty()) return;
			gui.sendConsoleCommand(text, chkConsoleInputAsSay.isSelected());
			consoleCommandInput.setText("");
			gui.scheduleScrollAfterCommand();
		});
		consoleCommandInput.setColumns(10);

		GroupLayout gl = new GroupLayout(this);
		setLayout(gl);
		gl.setHorizontalGroup(
			gl.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
				.addGroup(gl.createSequentialGroup()
					.addGap(10)
					.addComponent(consoleCommandInput, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
					.addGap(10))
				.addGroup(gl.createSequentialGroup()
					.addContainerGap()
					.addComponent(chkConsoleInputAsSay)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkConsoleScrollSticky)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl.setVerticalGroup(
			gl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(consoleCommandInput, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl.createParallelGroup(Alignment.BASELINE)
						.addComponent(chkConsoleInputAsSay)
						.addComponent(chkConsoleScrollSticky))
					.addGap(3))
		);
	}

	@SuppressWarnings("deprecation")
	private static String getLinkUrlAt(JTextPane textPane, Point p) {
		int offs = textPane.viewToModel(p);
		if (offs < 0) return null;
		StyledDocument doc = (StyledDocument) textPane.getDocument();
		javax.swing.text.Element el = doc.getCharacterElement(offs);
		Object url = el.getAttributes().getAttribute(ConsoleStyleHelper.LINK_URL);
		return (url instanceof String) ? (String) url : null;
	}

	public JTextPane getConsoleTextPane() { return consoleTextPane; }
	public JScrollPane getConsoleScrollPane() { return consoleScrollPane; }
	public JTextField getConsoleCommandInput() { return consoleCommandInput; }
	public JCheckBox getChkConsoleInputAsSay() { return chkConsoleInputAsSay; }
	public JCheckBox getChkConsoleScrollSticky() { return chkConsoleScrollSticky; }
	public ConsoleStyleHelper getConsoleStyleHelper() { return consoleStyleHelper; }
}
