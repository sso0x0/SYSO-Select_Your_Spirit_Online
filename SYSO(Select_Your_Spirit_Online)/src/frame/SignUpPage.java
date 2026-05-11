package frame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.JTextComponent;

import DTO.MyData;
import user.SignUpValidator;
import user.UserMap;

// 회원가입 화면 페이지
public class SignUpPage extends JPanel {

	// 배경 및 버튼에 사용하는 색상 정의
	private static final Color PAGE_BG = new Color(225, 215, 200);
	private static final Color ACCENT1 = new Color(85, 65, 55);
	private static final Color ACCENT2 = new Color(120, 80, 60);
	private static final Color ACCENT_HV = new Color(60, 45, 38);
	private static final Color COLOR_ERR = new Color(196, 69, 61);

	// 입력 필드의 위치와 크기 기준값
	private static final int FRAME_W = 592;
	private static final int FIELD_X = (FRAME_W - 400) / 2;
	private static final int FIELD_W = 400;
	private static final int FIELD_H = 47;
	private static final int WARN_H = 18;
	private static final int GAP = 12;

	// 각 입력 필드의 기준 Y 좌표
	private static final int Y_ID = 230;
	private static final int Y_WARN_ID = Y_ID + FIELD_H;
	private static final int Y_PW1 = Y_WARN_ID + GAP;
	private static final int Y_WARN_PW1 = Y_PW1 + FIELD_H;
	private static final int Y_PW = Y_WARN_PW1 + GAP;
	private static final int Y_WARN_PW = Y_PW + FIELD_H;
	private static final int Y_NAME = Y_WARN_PW + GAP;
	private static final int Y_WARN_NAME = Y_NAME + FIELD_H;
	private static final int Y_MAIL = Y_WARN_NAME + GAP;
	private static final int Y_WARN_MAIL = Y_MAIL + FIELD_H;
	private static final int Y_BIR = Y_WARN_MAIL + GAP;
	private static final int Y_WARN_BIR = Y_BIR + FIELD_H;
	private static final int Y_PON = Y_WARN_BIR + GAP;
	private static final int Y_WARN_PON = Y_PON + FIELD_H;
	private static final int Y_BTN = Y_WARN_PON + GAP + 10;
	private static final int Y_LINK = Y_BTN + 50 + 30;

	// 오류 표시 여부 플래그 (순서: 아이디, 비밀번호, 비밀번호확인, 이름, 이메일, 생년월일, 전화번호)
	private final boolean[] warnFlags = new boolean[7];

	// 입력 필드
	private final JTextField tfId;
	private final JPasswordField pfPw1;
	private final JPasswordField pfPw;
	private final JTextField tfName;
	private final JTextField tfMail;
	private final JTextField tfBir;
	private final JTextField tfPon;

	// 오류 메시지 레이블
	private final JLabel warnId, warnPw1, warnPw, warnName, warnMail, warnBir, warnPon;

	// 레이아웃 구성 컴포넌트
	private final JPanel contentPanel;
	private final JPanel pw1Panel;
	private final JPanel pwPanel;
	private final JButton btnUp;

	public SignUpPage() {
		setLayout(null);
		setBackground(PAGE_BG);
		setSize(600, 800);

		contentPanel = new JPanel(null) {
			@Override
			public Dimension getPreferredSize() {
				int maxY = 0;
				for (Component c : getComponents())
					maxY = Math.max(maxY, c.getY() + c.getHeight());
				return new Dimension(FRAME_W, maxY + 40);
			}
		};
		contentPanel.setBackground(PAGE_BG);

		JScrollPane scrollPane = buildScrollPane(contentPanel);
		scrollPane.setBounds(0, 0, 600, 800);
		add(scrollPane);

		tfId = createRoundTextField("아이디를 입력하세요.");
		pfPw1 = createRoundPasswordField("비밀번호를 입력하세요.");
		pfPw = createRoundPasswordField("비밀번호를 한 번 더 입력하세요.");
		tfName = createRoundTextField("이름을 입력하세요.");
		tfMail = createRoundTextField("이메일을 입력하세요.");
		tfBir = createRoundTextField("생년월일 8자리를 입력하세요. (예: 19980122)");
		tfPon = createRoundTextField("전화번호를 입력하세요. (예: 01012345678)");

		warnId = createWarnLabel();
		warnPw1 = createWarnLabel();
		warnPw = createWarnLabel();
		warnName = createWarnLabel();
		warnMail = createWarnLabel();
		warnBir = createWarnLabel();
		warnPon = createWarnLabel();

		ImageIcon[] eyeIcons = loadEyeIcons();
		pw1Panel = buildPwPanel(pfPw1, eyeIcons);
		pwPanel = buildPwPanel(pfPw, eyeIcons);

		btnUp = makeActionButton("가입하기", ACCENT1, ACCENT_HV);
		JLabel btnNo = buildLoginLink();

		addLogoAndTitle();

		for (Component c : new Component[] { tfId, warnId, pw1Panel, warnPw1, pwPanel, warnPw, tfName, warnName, tfMail,
				warnMail, tfBir, warnBir, tfPon, warnPon, btnUp, btnNo })
			contentPanel.add(c);

		relayout();
		bindFocusClear();
		bindValidators();
		bindSignUpAction();

		btnNo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FrameBase.getInstance(new LoginPage());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				btnNo.setFont(new Font("맑은 고딕", Font.BOLD, 12));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnNo.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
			}
		});
	}

	// ────────────────────────── 레이아웃 ──────────────────────────

	private void relayout() {
		int o0 = warnH(0), o1 = warnH(1), o2 = warnH(2), o3 = warnH(3), o4 = warnH(4), o5 = warnH(5), o6 = warnH(6);

		place(tfId, FIELD_X, Y_ID, FIELD_W, FIELD_H);
		place(warnId, FIELD_X + 8, Y_WARN_ID, FIELD_W, o0);

		int c1 = o0;
		place(pw1Panel, FIELD_X, Y_PW1 + c1, FIELD_W, FIELD_H);
		place(warnPw1, FIELD_X + 8, Y_WARN_PW1 + c1, FIELD_W, o1);

		int c2 = c1 + o1;
		place(pwPanel, FIELD_X, Y_PW + c2, FIELD_W, FIELD_H);
		place(warnPw, FIELD_X + 8, Y_WARN_PW + c2, FIELD_W, o2);

		int c3 = c2 + o2;
		place(tfName, FIELD_X, Y_NAME + c3, FIELD_W, FIELD_H);
		place(warnName, FIELD_X + 8, Y_WARN_NAME + c3, FIELD_W, o3);

		int c4 = c3 + o3;
		place(tfMail, FIELD_X, Y_MAIL + c4, FIELD_W, FIELD_H);
		place(warnMail, FIELD_X + 8, Y_WARN_MAIL + c4, FIELD_W, o4);

		int c5 = c4 + o4;
		place(tfBir, FIELD_X, Y_BIR + c5, FIELD_W, FIELD_H);
		place(warnBir, FIELD_X + 8, Y_WARN_BIR + c5, FIELD_W, o5);

		int c6 = c5 + o5;
		place(tfPon, FIELD_X, Y_PON + c6, FIELD_W, FIELD_H);
		place(warnPon, FIELD_X + 8, Y_WARN_PON + c6, FIELD_W, o6);

		int total = o0 + o1 + o2 + o3 + o4 + o5 + o6;
		place(btnUp, FIELD_X, Y_BTN + total, FIELD_W, 50);

		for (Component c : contentPanel.getComponents()) {
			if (c instanceof JLabel && "로그인 페이지로 이동".equals(((JLabel) c).getText()))
				place(c, (FRAME_W - 160) / 2, Y_LINK + total, 160, 18);
		}

		contentPanel.repaint();
		contentPanel.revalidate();
	}

	private int warnH(int i) {
		return warnFlags[i] ? WARN_H : 0;
	}

	private static void place(Component c, int x, int y, int w, int h) {
		c.setBounds(x, y, w, h);
	}

	// ────────────────────────── 포커스 / 유효성 ──────────────────────────

	private void bindFocusClear() {
		clearOnFocus(tfId, 0, warnId);
		clearOnFocus(pfPw1, 1, warnPw1);
		clearOnFocus(pfPw, 2, warnPw);
		clearOnFocus(tfName, 3, warnName);
		clearOnFocus(tfMail, 4, warnMail);
		clearOnFocus(tfBir, 5, warnBir);
		clearOnFocus(tfPon, 6, warnPon);
	}

	private void clearOnFocus(JComponent field, int idx, JLabel warn) {
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (warnFlags[idx]) {
					warnFlags[idx] = false;
					warn.setText("");
					relayout();
				}
			}
		});
	}

	private void bindValidators() {
		watchField(tfId, () -> {
			String id = tfId.getText();
			if (!id.isEmpty() && UserMap.umap.containsKey(id))
				setWarnMsg(warnId, "* 이미 존재하는 아이디입니다.");
			else
				clearWarn(warnId);
		});

		watchField(pfPw1, () -> {
			String pw = new String(pfPw1.getPassword());
			if (!pw.isEmpty() && !SignUpValidator.isValidPassword(pw))
				setWarnMsg(warnPw1, "* 영문 대소문자, 숫자, 특수기호 포함 9자 이상이어야 합니다.");
			else
				clearWarn(warnPw1);
			validatePwMatch();
		});

		watchField(pfPw, this::validatePwMatch);

		watchField(tfMail, () -> {
			String mail = tfMail.getText();
			if (!mail.isEmpty() && !SignUpValidator.isValidEmail(mail))
				setWarnMsg(warnMail, "* 이메일 형식이 맞지 않습니다.");
			else
				clearWarn(warnMail);
		});

		watchField(tfBir, () -> {
			String bir = tfBir.getText();
			if (bir.isEmpty()) {
				clearWarn(warnBir);
				return;
			}
			String msg = SignUpValidator.validateBirthday(bir);
			if (msg != null)
				setWarnMsg(warnBir, msg);
			else
				clearWarn(warnBir);
		});

		watchField(tfPon, () -> {
			String pon = tfPon.getText();
			if (!pon.isEmpty() && !SignUpValidator.isValidPhone(pon))
				setWarnMsg(warnPon, "* 01012345678 형식으로 입력하세요.");
			else
				clearWarn(warnPon);
		});
	}

	private void validatePwMatch() {
		String p1 = new String(pfPw1.getPassword());
		String p2 = new String(pfPw.getPassword());
		if (!p2.isEmpty() && !p1.equals(p2))
			setWarnMsg(warnPw, "* 비밀번호가 일치하지 않습니다.");
		else
			clearWarn(warnPw);
	}

	// ────────────────────────── 가입 처리 ──────────────────────────

	private void bindSignUpAction() {
		btnUp.addActionListener(e -> {
			String id = tfId.getText().trim();
			String pw1s = new String(pfPw1.getPassword());
			String pws = new String(pfPw.getPassword());
			String name = tfName.getText().trim();
			String mail = tfMail.getText().trim();
			String bir = tfBir.getText().trim();
			String pon = tfPon.getText().trim();

			resetAllWarnings();
			boolean ok = true;

			if (id.isEmpty())
				ok = setError(0, warnId, "* 필수 입력입니다.");
			else if (UserMap.umap.containsKey(id))
				ok = setError(0, warnId, "* 이미 존재하는 아이디입니다.");

			if (pw1s.isEmpty())
				ok = setError(1, warnPw1, "* 필수 입력입니다.");
			else if (!SignUpValidator.isValidPassword(pw1s))
				ok = setError(1, warnPw1, "* 영문 대소문자, 숫자, 특수기호 포함 9자 이상이어야 합니다.");

			if (pws.isEmpty())
				ok = setError(2, warnPw, "* 필수 입력입니다.");
			else if (!pw1s.equals(pws))
				ok = setError(2, warnPw, "* 비밀번호가 일치하지 않습니다.");

			if (name.isEmpty())
				ok = setError(3, warnName, "* 필수 입력입니다.");

			if (mail.isEmpty())
				ok = setError(4, warnMail, "* 필수 입력입니다.");
			else if (!SignUpValidator.isValidEmail(mail))
				ok = setError(4, warnMail, "* 이메일 형식이 맞지 않습니다.");

			if (bir.isEmpty()) {
				ok = setError(5, warnBir, "* 필수 입력입니다.");
			} else {
				String birMsg = SignUpValidator.validateBirthday(bir);
				if (birMsg != null)
					ok = setError(5, warnBir, birMsg);
			}

			if (pon.isEmpty())
				ok = setError(6, warnPon, "* 필수 입력입니다.");
			else if (!SignUpValidator.isValidPhone(pon))
				ok = setError(6, warnPon, "* 01012345678 형식으로 입력하세요.");

			relayout();
			if (!ok)
				return;

			try {
				String birFmt = bir.substring(0, 4) + "." + bir.substring(4, 6) + "." + bir.substring(6, 8);
				String ponFmt = pon.substring(0, 3) + "-" + pon.substring(3, 7) + "-" + pon.substring(7, 11);
				JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다.");
				new UserMap(id, new MyData(id, pw1s, name, mail, birFmt, ponFmt));
				FrameBase.getInstance(new LoginPage());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	// ────────────────────────── 오류 메시지 헬퍼 ──────────────────────────

	private void setWarnMsg(JLabel lbl, String msg) {
		lbl.setText(msg);
		lbl.setForeground(COLOR_ERR);
	}

	private static void clearWarn(JLabel lbl) {
		lbl.setText("");
	}

	private boolean setError(int idx, JLabel lbl, String msg) {
		warnFlags[idx] = true;
		setWarnMsg(lbl, msg);
		return false;
	}

	private void resetAllWarnings() {
		Arrays.fill(warnFlags, false);
		for (JLabel l : new JLabel[] { warnId, warnPw1, warnPw, warnName, warnMail, warnBir, warnPon })
			l.setText("");
	}

	// ────────────────────────── DocumentListener 유틸 ──────────────────────────

	private static void watchField(JTextField tf, Runnable r) {
		tf.getDocument().addDocumentListener(simpleDocListener(r));
	}

	private static void watchField(JPasswordField pf, Runnable r) {
		pf.getDocument().addDocumentListener(simpleDocListener(r));
	}

	private static DocumentListener simpleDocListener(Runnable r) {
		return new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				r.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				r.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				r.run();
			}
		};
	}

	// ────────────────────────── UI 컴포넌트 빌더 ──────────────────────────

	private static JLabel createWarnLabel() {
		JLabel lbl = new JLabel("", JLabel.LEFT);
		lbl.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
		lbl.setForeground(COLOR_ERR);
		return lbl;
	}

	private JTextField createRoundTextField(String placeholder) {
		JTextField tf = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getParent() != null ? getParent().getBackground() : PAGE_BG);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
				super.paintComponent(g);
				if (getText().isEmpty() && !isFocusOwner())
					drawPlaceholder(g, placeholder);
			}
		};
		styleInputField(tf);
		return tf;
	}

	private JPasswordField createRoundPasswordField(String placeholder) {
		JPasswordField pf = new JPasswordField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (getPassword().length == 0 && !isFocusOwner())
					drawPlaceholder(g, placeholder);
			}
		};
		styleInputField(pf);
		return pf;
	}

	private static void styleInputField(JTextComponent tc) {
		tc.setOpaque(false);
		tc.setBackground(Color.WHITE);
		tc.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
		tc.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		tc.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				tc.repaint();
			}

			@Override
			public void focusLost(FocusEvent e) {
				tc.repaint();
			}
		});
	}

	private static void drawPlaceholder(Graphics g, String text) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(new Color(150, 150, 150));
		g2.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		FontMetrics fm = g2.getFontMetrics();
		Rectangle clip = g2.getClipBounds();
		int h = (clip != null) ? clip.height : FIELD_H;
		g2.drawString(text, 15, (h + fm.getAscent() - fm.getDescent()) / 2);
		g2.dispose();
	}

	private JPanel buildPwPanel(JPasswordField pf, ImageIcon[] eyeIcons) {
		JPanel panel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getParent() != null ? getParent().getBackground() : PAGE_BG);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
			}
		};
		panel.setOpaque(false);

		boolean[] visible = { false };
		JButton eye = new JButton(eyeIcons[0]);
		eye.setContentAreaFilled(false);
		eye.setBorderPainted(false);
		eye.setFocusPainted(false);
		eye.setOpaque(false);
		eye.setCursor(new Cursor(Cursor.HAND_CURSOR));
		eye.addActionListener(ev -> {
			visible[0] = !visible[0];
			pf.setEchoChar(visible[0] ? (char) 0 : '\u2022');
			eye.setIcon(visible[0] ? eyeIcons[1] : eyeIcons[0]);
		});

		pf.setBounds(0, 0, FIELD_W - 47, FIELD_H);
		eye.setBounds(FIELD_W - 47, 0, 44, FIELD_H);
		panel.add(pf);
		panel.add(eye);
		return panel;
	}

	private static ImageIcon[] loadEyeIcons() {
		try {
			Image closed = new ImageIcon("ui_img/eye_crossed.png").getImage().getScaledInstance(20, 20,
					Image.SCALE_SMOOTH);
			Image open = new ImageIcon("ui_img/eye.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			return new ImageIcon[] { new ImageIcon(closed), new ImageIcon(open) };
		} catch (Exception e) {
			return new ImageIcon[] { new ImageIcon(), new ImageIcon() };
		}
	}

	private static JButton makeActionButton(String text, Color base, Color hover) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
		btn.setForeground(Color.WHITE);
		btn.setBackground(base);
		btn.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(base);
			}
		});
		return btn;
	}

	private JLabel buildLoginLink() {
		JLabel lbl = new JLabel("로그인 페이지로 이동", JLabel.CENTER);
		lbl.setForeground(ACCENT2);
		lbl.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return lbl;
	}

	private void addLogoAndTitle() {
		JLabel logo = new JLabel();
		logo.setBounds((FRAME_W - 300) / 2, 30, 300, 150);
		try {
			Image img = new ImageIcon("ui_img/SYSOLogo.png").getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
			logo.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			logo.setText("LOGO");
		}
		contentPanel.add(logo);

		JLabel title = new JLabel("회원가입", JLabel.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		title.setForeground(ACCENT1);
		title.setBounds(0, 185, FRAME_W, 30);
		contentPanel.add(title);
	}

	private static JScrollPane buildScrollPane(JPanel content) {
		JScrollPane sp = new JScrollPane(content, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getViewport().setBackground(PAGE_BG);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
		sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				thumbColor = new Color(180, 170, 160);
				trackColor = PAGE_BG;
			}

			@Override
			protected JButton createDecreaseButton(int o) {
				return zeroBtn();
			}

			@Override
			protected JButton createIncreaseButton(int o) {
				return zeroBtn();
			}

			private JButton zeroBtn() {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(8, 0));
				return b;
			}
		});
		return sp;
	}
}