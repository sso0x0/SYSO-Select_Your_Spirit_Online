package frame;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import DTO.MyData;
import user.UserMap;

// 로그인 화면
public class LoginPage extends JPanel {

	private Color bgColor = new Color(255, 255, 255); // 기본 흰 배경 (현재 미사용)
	private Color borderColor = new Color(0, 0, 0); // 버튼 테두리 색 (현재 투명 처리)

	private static final Color PAGE_BG = new Color(225, 215, 200); // 전체 페이지 배경 (베이지)
	private static final Color CARD_BG = new Color(255, 252, 247); // 카드 기본 배경
	private static final Color CARD_HOVER_BG = new Color(245, 238, 228); // 카드 호버 배경
	private static final Color TOPBAR_BG = new Color(210, 198, 182); // 상단바 배경
	private static final Color ACCENT1 = new Color(85, 65, 55); // 로그인 버튼 기본 색 (진한 갈색)
	private static final Color ACCENT2 = new Color(120, 80, 60); // 보조 강조색
	private static final Color ACCENT_HV1 = new Color(60, 45, 38); // 로그인 버튼 호버 색 (더 진한 갈색)
	private static final Color ACCENT_HV2 = new Color(95, 60, 45); // 보조 강조 호버색
	private static final Color TEXT_PRIMARY = new Color(85, 65, 55); // 주요 텍스트 색
	private static final Color TEXT_SECONDARY = new Color(130, 110, 95); // 보조 텍스트 색
	private static final Color DIVIDER = new Color(255, 252, 247); // 구분선 기본 배경 (투명 처리)
	private static final Color BORDER_COLOR = new Color(210, 190, 180); // 입력 필드 테두리 색

	// 둥근 모서리 텍스트 입력 필드
	private JTextField createRoundTextField(String placeholder) {
		JTextField tf = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// 부모 배경색으로 전체를 채워 둥근 모서리 바깥 잔상 제거
				g2.setColor(getParent() != null ? getParent().getBackground() : PAGE_BG);
				g2.fillRect(0, 0, getWidth(), getHeight());

				// 실제 필드 영역을 흰색 둥근 사각형으로 그림
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();

				// 기본 paintComponent 호출 → 텍스트와 커서 렌더링
				super.paintComponent(g);

				// 입력값이 없고 포커스가 없을 때 회색 플레이스홀더 텍스트 그림
				if (getText().isEmpty() && !isFocusOwner()) {
					Graphics2D g3 = (Graphics2D) g.create();
					g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g3.setColor(new Color(150, 150, 150)); // 회색
					g3.setFont(getFont());
					g3.drawString(placeholder, 15, getHeight() / 2 + 4); // 수직 가운데 정렬
					g3.dispose();
				}
			}
		};
		tf.setOpaque(false); // 기본 배경 그리기 비활성화
		tf.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15)); // 내부 여백
		tf.setBackground(Color.WHITE);

		// 포커스 이동 시 플레이스홀더 갱신을 위해 repaint
		tf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				tf.repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				tf.repaint();
			}
		});
		return tf;
	}

	// 둥근 모서리 비밀번호 입력 필드
	private JPasswordField createRoundPasswordField(String placeholder) {
		JPasswordField pf = new JPasswordField() {
			@Override
			protected void paintComponent(Graphics g) {
				// 비밀번호 점과 커서는 기본 paintComponent가 처리
				super.paintComponent(g);

				// 입력값이 없고 포커스가 없을 때 플레이스홀더 표시
				if (getText().isEmpty() && !isFocusOwner()) {
					Graphics2D g3 = (Graphics2D) g.create();
					g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g3.setColor(new Color(150, 150, 150));
					g3.setFont(getFont());
					g3.drawString(placeholder, 15, getHeight() / 2 + 4);
					g3.dispose();
				}
			}
		};
		pf.setOpaque(false);
		pf.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
		pf.setBackground(new Color(0, 0, 0, 0)); // 완전 투명 배경 (pwPanel이 배경을 그림)

		// 포커스 이동 시 플레이스홀더 갱신
		pf.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				pf.repaint();
			}

			@Override
			public void focusGained(java.awt.event.FocusEvent e) {
				pf.repaint();
			}
		});
		return pf;
	}

	// 둥근 모서리 버튼, 호버 시 배경색 변경
	private JButton createRoundButton(String text, Color commonBg, Color pressedBg) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// 버튼 배경을 둥근 사각형으로 그림
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.setColor(borderColor);
				g2.dispose();
				super.paintComponent(g); // 텍스트 렌더링
			}
		};
		btn.setContentAreaFilled(false); // 기본 배경 그리기 비활성화
		btn.setBorderPainted(false); // 기본 테두리 비활성화
		btn.setFocusPainted(false); // 포커스 링 비활성화
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 포인터를 손 모양으로
		btn.setBackground(commonBg);

		// 마우스 진입/이탈 시 배경색 전환으로 hover 효과 구현
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(pressedBg);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(commonBg);
			}
		});
		return btn;
	}

	// 로그인 화면 전체 UI 생성자
	public LoginPage() {
		setBackground(new Color(225, 215, 200)); // 페이지 배경 베이지색
		setLayout(null); // 절대 좌표 레이아웃 사용
		setSize(600, 800);

		final int frameWidth = 600; // 패널 너비 (컴포넌트 가운데 정렬 기준)

		// 로고 이미지
		JLabel titleLogo = new JLabel();
		titleLogo.setBounds((frameWidth - 320) / 2, 80, 320, 190); // 가운데 정렬, 상단 여백 80px
		try {
			ImageIcon icon = new ImageIcon("ui_img/SYSOLogo.png");
			Image img = icon.getImage().getScaledInstance(320, 190, Image.SCALE_SMOOTH);
			titleLogo.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			titleLogo.setText("NO IMG"); // 이미지 로드 실패 시 텍스트 표시
		}
		add(titleLogo);

		// 레이아웃 상수
		final int X = (frameWidth - 400) / 2; // 입력 필드 시작 X (가운데 정렬)
		final int W = 400; // 입력 필드 너비
		final int WARN_H = 18; // 경고 메시지 라벨 높이
		final int GAP = 12; // 필드 간 세로 간격

		// 경고 메시지가 없을 때의 기준 Y 좌표 (경고 표시 시 동적으로 아래 컴포넌트가 밀림)
		final int Y_ID = 300; // 아이디 입력 필드 Y
		final int Y_WARN_ID = Y_ID + 47; // 아이디 경고 라벨 Y (필드 바로 아래)
		final int Y_PW = Y_WARN_ID + GAP; // 비밀번호 필드 Y
		final int Y_WARN_PW = Y_PW + 47; // 비밀번호 경고 라벨 Y
		final int Y_BTN = Y_WARN_PW + GAP + 15; // 로그인 버튼 Y
		final int Y_DIV = Y_BTN + 50 + 30; // 구분선 Y
		final int Y_REQ = Y_DIV + 10; // 회원가입 안내 문구 Y
		final int Y_SI = Y_REQ + 40; // 회원가입 버튼 Y

		// 아이디 입력 필드
		JTextField tfId = createRoundTextField("아이디를 입력하세요.");
		tfId.setBounds(X, Y_ID, W, 47);
		tfId.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

		// 아이디 경고 라벨 (초기 높이 0 = 숨김 상태)
		JLabel warningId = new JLabel("", JLabel.LEFT);
		warningId.setBounds(X + 8, Y_WARN_ID, W, 0);
		warningId.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
		warningId.setForeground(new Color(200, 60, 60)); // 빨간색 계열 경고 문구

		// 비밀번호 입력 필드
		JPasswordField pfPw = createRoundPasswordField("비밀번호를 입력하세요.");
		pfPw.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

		// 비밀번호 표시/숨김 토글 버튼 (눈 아이콘)
		final boolean[] pwVisible = { false }; // 현재 비밀번호가 보이는 상태인지 여부

		JButton btnEye = new JButton();
		btnEye.setContentAreaFilled(false);
		btnEye.setBorderPainted(false);
		btnEye.setFocusPainted(false);
		btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnEye.setOpaque(false);

		// 눈 아이콘 이미지 로드 (닫힌 눈 = 비밀번호 숨김 상태, 열린 눈 = 표시 상태)
		ImageIcon eyeClosedIcon, eyeOpenIcon;
		try {
			Image imgClosed = new ImageIcon("ui_img/eye_crossed.png").getImage().getScaledInstance(20, 20,
					Image.SCALE_SMOOTH);
			Image imgOpen = new ImageIcon("ui_img/eye.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			eyeClosedIcon = new ImageIcon(imgClosed);
			eyeOpenIcon = new ImageIcon(imgOpen);
		} catch (Exception ex) {
			// 이미지 로드 실패 시 빈 아이콘으로 대체
			eyeClosedIcon = new ImageIcon();
			eyeOpenIcon = new ImageIcon();
		}
		btnEye.setIcon(eyeClosedIcon); // 초기 상태: 비밀번호 숨김(닫힌 눈)

		final ImageIcon finalEyeOpen = eyeOpenIcon;
		final ImageIcon finalEyeClosed = eyeClosedIcon;

		// 눈 아이콘 클릭 시 비밀번호 표시 여부 토글
		btnEye.addActionListener(ev -> {
			pwVisible[0] = !pwVisible[0];
			if (pwVisible[0]) {
				pfPw.setEchoChar((char) 0); // echoChar를 0으로 설정 > 평문 표시
				btnEye.setIcon(finalEyeOpen); // 열린 눈 아이콘으로 변경
			} else {
				pfPw.setEchoChar('\u2022'); // 문자로 복원 > 숨김
				btnEye.setIcon(finalEyeClosed); // 닫힌 눈 아이콘으로 복원
			}
		});

		// 비밀번호 필드 + 눈 아이콘을 하나의 패널로 묶음
		// pwPanel이 직접 흰색 둥근 사각형 배경을 그려 필드와 버튼을 하나의 입력창처럼 보이게 함
		JPanel pwPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// 부모 배경으로 채워 모서리 잔상 제거
				g2.setColor(getParent() != null ? getParent().getBackground() : PAGE_BG);
				g2.fillRect(0, 0, getWidth(), getHeight());
				// 흰색 둥근 배경 (입력창 전체 영역)
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
			}
		};
		pwPanel.setOpaque(false);

		// 패널 내부 배치: 비밀번호 필드(오른쪽 47px 제외 전체) + 눈 버튼(오른쪽 44px)
		pfPw.setBounds(0, 0, W - 47, 47);
		btnEye.setBounds(W - 47, 0, 44, 47);
		pwPanel.add(pfPw);
		pwPanel.add(btnEye);

		// 비밀번호 경고 라벨 (초기 높이 0 = 숨김 상태)
		JLabel warningPw = new JLabel("", JLabel.LEFT);
		warningPw.setBounds(X + 8, Y_WARN_PW, W, 0);
		warningPw.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
		warningPw.setForeground(new Color(200, 60, 60));

		// 로그인 버튼
		JButton btnLo = createRoundButton("로그인", ACCENT1, ACCENT_HV1);
		tfId.addActionListener(e -> btnLo.doClick());
		pfPw.addActionListener(e -> btnLo.doClick());
		btnLo.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		btnLo.setBounds(X, Y_BTN, W, 50);
		btnLo.setForeground(Color.white);

		// 구분선 (그라디언트 수평선)
		// LinearGradientPaint으로 양 끝이 투명하게 페이드되는 가느다란 선을 그림
		JPanel divider = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				int w = getWidth(), h = getHeight();
				// 중앙이 진하고 양 끝이 투명한 그라디언트
				LinearGradientPaint paint = new LinearGradientPaint(0, 0, w, 0,
						new float[] { 0f, 0.15f, 0.5f, 0.85f, 1f },
						new Color[] { new Color(150, 150, 150, 30), new Color(100, 100, 100, 80),
								new Color(0, 0, 0, 100), new Color(100, 100, 100, 80), new Color(150, 150, 150, 30) });
				g2.setPaint(paint);
				g2.fillRect(0, 0, w, h);
				g2.dispose();
			}
		};
		divider.setBackground(DIVIDER);
		divider.setOpaque(false);
		divider.setBounds((frameWidth - 480) / 2, Y_DIV, 480, 1); // 높이 1px = 가느다란 선

		// 회원가입 안내 문구
		JLabel reqSignUp = new JLabel("회원이 아니신가요?", JLabel.CENTER);
		reqSignUp.setBounds(X, Y_REQ, W, 40);
		reqSignUp.setFont(new Font("맑은 고딕", Font.PLAIN, 11));

		// 회원가입 버튼
		JButton btnSi = createRoundButton("회원가입", new Color(160, 130, 110), new Color(140, 110, 90));
		btnSi.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		btnSi.setBounds(X, Y_SI, W, 44);
		btnSi.setForeground(Color.white);

		// 컴포넌트 패널에 추가
		add(tfId);
		add(warningId);
		add(pwPanel);
		pwPanel.setBounds(X, Y_PW, W, 47); // 비밀번호 패널 초기 위치
		add(warningPw);
		add(btnLo);
		add(divider);
		add(reqSignUp);
		add(btnSi);

		// 동적 레이아웃 상태 플래그
		// 경고가 표시될 때 해당 라벨의 높이가 생겨 아래 컴포넌트들이 밀려 내려감
		final boolean[] idWarnOn = { false }; // 아이디 경고 현재 표시 여부
		final boolean[] pwWarnOn = { false }; // 비밀번호 경고 현재 표시 여부

		// 경고 표시 여부에 따라 모든 컴포넌트의 Y 좌표를 재계산
		Runnable relayout = () -> {
			int idOff = idWarnOn[0] ? WARN_H : 0; // 아이디 경고로 인한 Y 오프셋
			int pwOff = pwWarnOn[0] ? WARN_H : 0; // 비밀번호 경고로 인한 Y 오프셋

			warningId.setBounds(X + 8, Y_WARN_ID, W, idWarnOn[0] ? WARN_H : 0); // 경고 있으면 높이 WARN_H, 없으면 0(숨김)

			pwPanel.setBounds(X, Y_PW + idOff, W, 47); // 아이디 경고만큼 아래로
			warningPw.setBounds(X + 8, Y_WARN_PW + idOff, W, pwWarnOn[0] ? WARN_H : 0);

			btnLo.setBounds(X, Y_BTN + idOff + pwOff, W, 50); // 두 경고 합산만큼 아래로
			divider.setBounds((frameWidth - 480) / 2, Y_DIV + idOff + pwOff, 480, 1);
			reqSignUp.setBounds(X, Y_REQ + idOff + pwOff, W, 40);
			btnSi.setBounds(X, Y_SI + idOff + pwOff, W, 44);
			repaint();
		};

		// 포커스 이동 시 경고 자동 제거
		// 아이디 필드에 다시 포커스가 생기면 아이디 경고를 즉시 숨김
		tfId.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (idWarnOn[0]) {
					idWarnOn[0] = false;
					warningId.setText("");
					relayout.run();
				}
			}
		});

		// 비밀번호 필드에 다시 포커스가 생기면 비밀번호 경고를 즉시 숨김
		pfPw.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (pwWarnOn[0]) {
					pwWarnOn[0] = false;
					warningPw.setText("");
					relayout.run();
				}
			}
		});

		// 패널 빈 영역 클릭 시 포커스를 패널 자체로 이동 (입력 필드 포커스 해제)
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
			}
		});

		// 로그인 버튼 클릭 이벤트
		btnLo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String id = tfId.getText().trim();
				String pw = new String(pfPw.getPassword());

				// 이전 경고 상태 초기화
				idWarnOn[0] = false;
				pwWarnOn[0] = false;
				warningId.setText("");
				warningPw.setText("");

				// 아이디 유효성 검사
				if (id.isEmpty()) {
					// 아이디 미입력
					idWarnOn[0] = true;
					warningId.setText("* 아이디를 입력해주세요.");
				} else if (UserMap.getUserInfoMap(id) == null) {
					// DB에 존재하지 않는 아이디
					idWarnOn[0] = true;
					warningId.setText("* 존재하지 않는 아이디입니다.");
				}

				// 비밀번호 유효성 검사
				if (pw.isEmpty()) {
					// 비밀번호 미입력
					pwWarnOn[0] = true;
					warningPw.setText("* 비밀번호를 입력해주세요.");
				} else if (!idWarnOn[0]) {
					// 아이디가 유효한 경우에만 비밀번호 일치 여부 검사
					if (!UserMap.getUserInfoMap(id).getPw().equals(pw)) {
						pwWarnOn[0] = true;
						warningPw.setText("* 비밀번호가 올바르지 않습니다.");
					}
				} else {
					// 아이디가 잘못됐고 비밀번호도 입력된 경우 > 비밀번호도 확인 요청
					pwWarnOn[0] = true;
					warningPw.setText("* 비밀번호가 올바르지 않습니다.");
				}

				// 검증 실패 시 경고 표시 후 로그인 중단
				if (idWarnOn[0] || pwWarnOn[0]) {
					relayout.run();
					return;
				}

				// 모든 검증 통과 > 메인 화면으로 전환
				FrameBase.getInstance(new FrameMain(UserMap.getUserInfoMap(id)));
			}
		});

		// 회원가입 버튼 클릭 이벤트
		btnSi.addActionListener(e -> FrameBase.getInstance(new SignUpPage()));
	}

}