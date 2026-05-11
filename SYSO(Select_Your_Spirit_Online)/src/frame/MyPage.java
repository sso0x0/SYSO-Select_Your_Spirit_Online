package frame;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import DTO.MyData;
import shoppingCart.Cart;
import shoppingCart.CartItem;
import shoppingCart.Order;
import user.SignUpValidator;
import user.UserMap;

/**
 * 마이페이지 화면 패널. 로그인된 사용자의 개인 정보(이름·아이디·생년월일·전화번호·이메일)와 주문 내역을 날짜 순으로 보여준다. 전체
 * 내용을 단일 JScrollPane으로 감싸 내용 길이에 관계없이 스크롤로 접근할 수 있다.
 */
public class MyPage extends JPanel {

	private static final long serialVersionUID = 1L;

	// ── 색상 상수 ──────────────────────────────────────────────────
	/** 전체 페이지 배경색 */
	private static final Color BG = new Color(225, 215, 200);
	/** 히어로 카드 그라디언트 시작색 (어두운 브라운) */
	private static final Color HERO_DARK = new Color(85, 65, 55);
	/** 히어로 카드 그라디언트 끝색 (밝은 브라운) */
	private static final Color HERO_LIGHT = new Color(120, 80, 60);
	/** 상단 바 배경색 */
	private static final Color TOPBAR_BG = new Color(210, 198, 182);
	/** 카드 배경색 */
	private static final Color CARD_BG = new Color(255, 252, 247);
	/** 카드 테두리 색상 */
	private static final Color CARD_BORDER = new Color(210, 190, 180);
	/** 카드 아이콘 배경색 */
	private static final Color CARD_ICON_BG = new Color(255, 252, 247);
	/** 주요 액센트 색상 (버튼, 강조 텍스트 등) */
	private static final Color ACCENT = new Color(85, 65, 55);
	/** 보조 액센트 색상 */
	private static final Color ACCENT2 = new Color(120, 80, 60);
	/** 액센트 호버 색상 */
	private static final Color ACCENT_HOV = new Color(60, 45, 38);
	/** 진한 텍스트 색상 */
	private static final Color TEXT_DARK = new Color(85, 65, 55);
	/** 중간 텍스트 색상 */
	private static final Color TEXT_MID = new Color(85, 65, 55);
	/** 연한 보조 텍스트 색상 (라벨 키, 힌트 등) */
	private static final Color TEXT_LIGHT = new Color(130, 110, 95);
	/** 구분선 색상 */
	private static final Color DIVIDER = new Color(196, 180, 165);
	/** 날짜 칩·섹션 헤더 배경 태그 색상 */
	private static final Color TAG_BG = new Color(245, 238, 228);
	/** 주문 카드 헤더·푸터 배경색 */
	private static final Color ORDER_HDR = new Color(245, 238, 228);
	/** 본문 기본 텍스트 색상 */
	private static final Color TEXT_PRIMARY = new Color(85, 65, 55);

	// ── 폰트 상수 ──────────────────────────────────────────────────
	/** 굵은 13px 폰트 */
	private static final Font SANS_BOLD = new Font("맑은 고딕", Font.BOLD, 13);
	/** 보통 12px 폰트 */
	private static final Font SANS_MED = new Font("맑은 고딕", Font.PLAIN, 12);
	/** 보통 11px 폰트 */
	private static final Font SANS_SM = new Font("맑은 고딕", Font.PLAIN, 11);
	/** 굵은 10px 폰트 */
	private static final Font SANS_XS = new Font("맑은 고딕", Font.BOLD, 10);

	// ── 레이아웃 치수 상수 ────────────────────────────────────────
	/** 화면 전체 너비 */
	private static final int W = 600;
	/** 화면 전체 높이 */
	private static final int H = 800;
	/** 좌우 패딩(12px)을 제외한 콘텐츠 유효 너비 */
	private static final int WRAP_W = W - 24;

	/**
	 * 주문 카드 내부 컴포넌트 너비. 전체 스크롤 구조이므로 스크롤바(7px) + 테두리(2px) = 9px를 뺀다.
	 */
	private static final int OW = WRAP_W - 9;

	// ── 개인정보 카드 고정 높이 ────────────────────────────────────
	/** 일반 정보(이름·아이디 등) 카드 한 줄 높이 */
	private static final int INFO_H = 50;
	/** 이메일 카드 높이 (변경 버튼 포함) */
	private static final int EMAIL_H = 50;
	/** 로그아웃 버튼 행 높이 */
	private static final int LOGOUT_H = 44;

	// ── 주문 카드 고정 높이 ────────────────────────────────────────
	/** 주문 카드 헤더(주문 번호·시간·결제 수단) 높이 */
	private static final int ORDER_HDR_H = 36;
	/** 상품 한 줄의 높이 */
	private static final int ITEM_ROW_H = 64;
	/** 주문 카드 푸터(총 결제 금액) 높이 */
	private static final int ORDER_FTR_H = 34;

	/** 현재 로그인된 사용자 데이터 */
	private final MyData loginUser;
	/** 이메일 변경 후 화면을 즉시 갱신하기 위한 참조 */
	private JLabel emailLabel;

	/**
	 * 마이페이지를 초기화한다.
	 *
	 * @param cart  현재 세션의 장바구니 (현재 화면에서 직접 사용하지 않지만 향후 확장을 위해 수신)
	 * @param order 주문 내역을 가져오는 데 사용하는 주문 객체
	 * @param d     현재 로그인된 사용자 데이터
	 */
	public MyPage(Cart cart, Order order, MyData d) {
		this.loginUser = d;
		setBackground(BG);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(W, H));

		add(buildTopBar(), BorderLayout.NORTH);
		add(buildBody(order), BorderLayout.CENTER);
	}

	// ══════════════════════════════════════════════════════════════
	// 상단 바
	// ══════════════════════════════════════════════════════════════

	/**
	 * 화면 상단 내비게이션 바를 생성한다. 중앙에 페이지 제목, 우측에 홈 버튼, 좌측에 뒤로가기 버튼을 배치한다.
	 *
	 * @return 완성된 상단 바 패널
	 */
	private JPanel buildTopBar() {
		JPanel bar = new JPanel(null);
		bar.setPreferredSize(new Dimension(W, 80));
		bar.setBackground(TOPBAR_BG);
		bar.setBorder(new MatteBorder(0, 0, 1, 0, DIVIDER));

		// 페이지 제목
		JLabel lblTitle = new JLabel("마이페이지", SwingConstants.CENTER);
		lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		lblTitle.setForeground(TEXT_PRIMARY);
		lblTitle.setBounds(200, 26, 200, 28);
		bar.add(lblTitle);

		// 홈 버튼: 클릭 시 메인 화면으로 이동
		JButton btnHome = new JButton(scaledIcon("ui_img/home.png", 30, 30));
		btnHome.setPressedIcon(scaledIcon("ui_img/home_pressed.png", 30, 30));
		btnHome.setOpaque(false);
		btnHome.setContentAreaFilled(false);
		btnHome.setBorderPainted(false);
		btnHome.setFocusPainted(false);
		btnHome.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnHome.setBounds(550, 25, 30, 30);
		btnHome.addActionListener(e -> FrameBase.getInstance(new FrameMain(loginUser)));
		bar.add(btnHome);

		// 뒤로가기 버튼: 클릭 시 FrameBase 히스토리 스택의 이전 화면으로 이동
		ImageIcon backIcon = new ImageIcon(
				new ImageIcon("ui_img/back.png").getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		ImageIcon backPressedIcon = new ImageIcon(
				new ImageIcon("ui_img/back_pressed.PNG").getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		JButton btnBack = new JButton(backIcon);
		btnBack.setPressedIcon(backPressedIcon);
		btnBack.setLayout(null);
		btnBack.setBounds(5, 18, 65, 50);
		btnBack.setContentAreaFilled(false);
		btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnBack.setBorderPainted(false);
		btnBack.setOpaque(false);
		btnBack.setFocusPainted(false);
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameBase.goBack();
			}
		});
		add(btnBack);

		return bar;
	}

	// ══════════════════════════════════════════════════════════════
	// 본문 (전체 스크롤 영역)
	// ══════════════════════════════════════════════════════════════

	/**
	 * 마이페이지 본문 전체를 단일 JScrollPane으로 감싸서 반환한다. 내부에는 개인 정보 섹션과 주문 내역 섹션이 위에서 아래로 쌓인다.
	 * 내용이 화면 높이를 초과하면 세로 스크롤로 접근할 수 있다.
	 *
	 * @param order 주문 내역 데이터
	 * @return 스크롤 가능한 본문 패널
	 */
	private JScrollPane buildBody(Order order) {
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.setBackground(BG);
		body.setBorder(new EmptyBorder(12, 12, 16, 12));

		// BoxLayout Y축에서 왼쪽 정렬을 유지하려면 각 자식에 LEFT_ALIGNMENT를 지정해야 한다.
		addLeft(body, buildProfileSection());
		body.add(vGap(10));
		addLeft(body, buildOrderSection(order));

		JScrollPane scroll = new JScrollPane(body);
		scroll.setBorder(null);
		scroll.setBackground(BG);
		scroll.getViewport().setBackground(BG);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(18);
		styleScrollBar(scroll);
		return scroll;
	}

	/**
	 * BoxLayout Y축 컨테이너에 자식 패널을 왼쪽 정렬로 추가한다.
	 *
	 * @param parent 추가 대상 부모 패널
	 * @param child  추가할 자식 패널
	 */
	private void addLeft(JPanel parent, JPanel child) {
		child.setAlignmentX(Component.LEFT_ALIGNMENT);
		parent.add(child);
	}

	// ══════════════════════════════════════════════════════════════
	// 히어로 카드 (프로필 요약)
	// ══════════════════════════════════════════════════════════════

	/**
	 * 사용자 이름·아이디·배지·아이디·전화번호를 담은 히어로 카드를 생성한다. 배경은 HERO_DARK → HERO_LIGHT 방향의
	 * 그라디언트로 칠하고, 반투명 원형 장식으로 깊이감을 더한다.
	 *
	 * @return 히어로 카드 패널
	 */
	private JPanel buildHeroCard() {
		JPanel hero = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// 대각선 방향 그라디언트 배경
				g2.setPaint(new GradientPaint(0, 0, HERO_DARK, getWidth(), getHeight(), HERO_LIGHT));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
				// 우상단·좌하단 반투명 원형 장식
				g2.setColor(new Color(255, 255, 255, 14));
				g2.fillOval(getWidth() - 110, -50, 180, 180);
				g2.setColor(new Color(255, 255, 255, 7));
				g2.fillOval(-50, getHeight() - 70, 150, 150);
				g2.dispose();
			}
		};
		hero.setOpaque(false);
		final int HH = 124;
		hero.setPreferredSize(new Dimension(WRAP_W, HH));
		hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, HH));

		// 커피 이모지 아바타: 반투명 흰 원 위에 그려진다.
		JLabel avatar = new JLabel("☕", SwingConstants.CENTER) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(255, 255, 255, 35));
				g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
				g2.setColor(new Color(255, 252, 247));
				g2.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		avatar.setFont(new Font("맑은 고딕", Font.PLAIN, 22));
		avatar.setOpaque(false);
		avatar.setBounds(18, 16, 54, 54);
		hero.add(avatar);

		// 사용자 이름
		JLabel nameLabel = new JLabel(UserMap.umap.get(loginUser.getId()).getName());
		nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		nameLabel.setForeground(new Color(255, 252, 247));
		nameLabel.setBounds(84, 18, 260, 22);
		hero.add(nameLabel);

		// "@아이디" 형태의 보조 텍스트
		JLabel idTag = new JLabel("@" + UserMap.umap.get(loginUser.getId()).getId());
		idTag.setFont(SANS_SM);
		idTag.setForeground(new Color(245, 230, 208, 155));
		idTag.setBounds(84, 42, 260, 16);
		hero.add(idTag);

		// "MEMBER" 뱃지: 반투명 둥근 테두리 배경을 직접 그려 표현
		JLabel badge = new JLabel("MEMBER") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(255, 255, 255, 28));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
				g2.setColor(new Color(255, 255, 255, 46));
				g2.setStroke(new BasicStroke(1f));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		badge.setFont(new Font("맑은 고딕", Font.BOLD, 9));
		badge.setForeground(new Color(245, 230, 208, 190));
		badge.setHorizontalAlignment(SwingConstants.CENTER);
		badge.setOpaque(false);
		badge.setBounds(84, 62, 60, 17);
		hero.add(badge);

		// 아바타·이름 영역과 통계 셀 사이의 반투명 수평 구분선
		JSeparator sep = new JSeparator() {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(255, 255, 255, 32));
				g.drawLine(0, 0, getWidth(), 0);
			}
		};
		sep.setOpaque(false);
		sep.setBounds(12, 86, WRAP_W - 24, 1);
		hero.add(sep);

		// 하단 통계 2셀: 아이디 / 전화번호
		String[][] stats = { { "아이디", UserMap.umap.get(loginUser.getId()).getId() },
				{ "전화번호", UserMap.umap.get(loginUser.getId()).getPon() }, };
		int sw = WRAP_W / 2;
		for (int i = 0; i < stats.length; i++) {
			// 첫 번째 셀(i=0)에만 오른쪽 구분선을 그린다.
			JPanel cell = buildStatCell(stats[i][0], stats[i][1], i == 0);
			cell.setBounds(i * sw, 90, sw, 30);
			hero.add(cell);
		}
		return hero;
	}

	/**
	 * 히어로 카드 하단 통계 셀 하나를 생성한다. 값(굵게)과 키(작은 글씨)가 위아래로 배치된다.
	 *
	 * @param label     키 텍스트 (예: "아이디")
	 * @param value     값 텍스트 (예: "hong123")
	 * @param hasBorder true이면 셀 오른쪽에 반투명 구분선을 그린다.
	 * @return 통계 셀 패널
	 */
	private JPanel buildStatCell(String label, String value, boolean hasBorder) {
		JPanel cell = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (hasBorder) {
					g.setColor(new Color(255, 255, 255, 28));
					g.drawLine(getWidth() - 1, 4, getWidth() - 1, getHeight() - 4);
				}
			}
		};
		cell.setOpaque(false);

		JLabel valLbl = new JLabel(value, SwingConstants.CENTER);
		valLbl.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		valLbl.setForeground(new Color(255, 252, 247));

		JLabel keyLbl = new JLabel(label, SwingConstants.CENTER);
		keyLbl.setFont(new Font("맑은 고딕", Font.PLAIN, 9));
		keyLbl.setForeground(new Color(245, 230, 208, 135));

		cell.add(valLbl);
		cell.add(keyLbl);

		// 셀 크기가 바뀔 때마다 내부 라벨 위치를 재계산
		cell.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int w = cell.getWidth();
				valLbl.setBounds(0, 1, w, 16);
				keyLbl.setBounds(0, 17, w, 12);
			}
		});
		return cell;
	}

	// ══════════════════════════════════════════════════════════════
	// 개인 정보 섹션
	// ══════════════════════════════════════════════════════════════

	/**
	 * 개인 정보 섹션을 생성한다. 섹션 헤더 → 이름·아이디·생년월일·전화번호 카드 4개 → 이메일 카드 → 로그아웃 버튼 순서로 배치된다.
	 * 높이는 포함된 컴포넌트의 합산 높이로 고정한다.
	 *
	 * @return 개인 정보 섹션 패널
	 */
	private JPanel buildProfileSection() {
		JPanel sec = new JPanel();
		sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
		sec.setBackground(BG);

		// 섹션 전체 고정 높이 계산:
		// 헤더(26) + gap(6) + 정보 카드 4개(50×4 + gap 6×4) + 이메일(50) + gap(10) + 로그아웃(44)
		int profileH = 26 + 6 + (INFO_H * 4 + 6 * 4) + EMAIL_H + 10 + LOGOUT_H;
		sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, profileH));
		sec.setPreferredSize(new Dimension(WRAP_W, profileH));
		sec.setMinimumSize(new Dimension(WRAP_W, profileH));

		addLeft(sec, buildSectionHeader("개인 정보"));
		sec.add(vGap(6));

		// 표시할 정보 필드 목록 (라벨명, 값)
		String[][] fields = { { "이름", UserMap.umap.get(loginUser.getId()).getName() },
				{ "아이디", UserMap.umap.get(loginUser.getId()).getId() },
				{ "생년월일", UserMap.umap.get(loginUser.getId()).getBir() },
				{ "전화번호", UserMap.umap.get(loginUser.getId()).getPon() }, };
		for (String[] f : fields) {
			addLeft(sec, buildInfoCard(f[0], f[1]));
			sec.add(vGap(6));
		}

		addLeft(sec, buildEmailCard());
		sec.add(vGap(10));
		addLeft(sec, buildActionRow());
		return sec;
	}

	/**
	 * 섹션 구분 헤더를 생성한다. 텍스트 왼쪽에 두께 3px 세로 강조 바를 함께 그린다.
	 *
	 * @param title 헤더에 표시할 제목 텍스트
	 * @return 섹션 헤더 패널
	 */
	private JPanel buildSectionHeader(String title) {
		JPanel p = new JPanel(null);
		p.setBackground(BG);
		p.setPreferredSize(new Dimension(WRAP_W, 26));
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
		p.setMinimumSize(new Dimension(100, 26));

		JLabel lbl = new JLabel("  " + title);
		lbl.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		lbl.setForeground(ACCENT);
		lbl.setBounds(0, 0, 300, 26);
		p.add(lbl);

		// 텍스트 좌측에 그려지는 3px 세로 강조 바
		JPanel bar = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(ACCENT2);
				g2.setStroke(new BasicStroke(3f));
				g2.drawLine(0, 4, 0, getHeight() - 4);
				g2.dispose();
			}
		};
		bar.setOpaque(false);
		bar.setBounds(0, 0, 4, 26);
		p.add(bar);
		return p;
	}

	/**
	 * 단일 정보 필드(이름·아이디 등)를 보여주는 둥근 카드를 생성한다. 카드 상단에 작은 키 라벨, 하단에 값 라벨을 배치한다.
	 *
	 * @param labelTxt 키 텍스트 (예: "이름")
	 * @param value    표시할 값 (예: "홍길동")
	 * @return 정보 카드 패널
	 */
	private JPanel buildInfoCard(String labelTxt, String value) {
		RoundPanel card = new RoundPanel(12, CARD_BG, CARD_BORDER);
		card.setLayout(null);
		card.setPreferredSize(new Dimension(WRAP_W, INFO_H));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, INFO_H));
		card.setMinimumSize(new Dimension(WRAP_W, INFO_H));

		// 키: 대문자·소형 폰트로 표시
		JLabel key = new JLabel(labelTxt.toUpperCase());
		key.setFont(new Font("맑은 고딕", Font.BOLD, 8));
		key.setForeground(TEXT_LIGHT);
		key.setBounds(14, 10, 200, 11);
		card.add(key);

		// 값: 굵은 폰트로 표시
		JLabel val = new JLabel(value);
		val.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		val.setForeground(TEXT_DARK);
		val.setBounds(14, 24, WRAP_W - 30, 18);
		card.add(val);

		// 카드 너비가 바뀔 때 값 라벨 너비를 재조정
		card.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				val.setBounds(14, 24, card.getWidth() - 30, 18);
			}
		});
		return card;
	}

	/**
	 * 이메일 정보와 변경 버튼을 포함한 카드를 생성한다. 변경 버튼 클릭 시 입력 다이얼로그를 띄워 유효성 검증 후 이메일을 갱신한다.
	 *
	 * @return 이메일 카드 패널
	 */
	private JPanel buildEmailCard() {
		RoundPanel card = new RoundPanel(12, CARD_BG, CARD_BORDER);
		card.setLayout(null);
		card.setPreferredSize(new Dimension(WRAP_W, EMAIL_H));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, EMAIL_H));
		card.setMinimumSize(new Dimension(WRAP_W, EMAIL_H));

		JLabel key = new JLabel("이메일");
		key.setFont(new Font("맑은 고딕", Font.BOLD, 8));
		key.setForeground(TEXT_LIGHT);
		key.setBounds(14, 10, 180, 11);
		card.add(key);

		// 현재 이메일 값 (변경 후 setText로 갱신됨)
		emailLabel = new JLabel(UserMap.umap.get(loginUser.getId()).getEmail());
		emailLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		emailLabel.setForeground(TEXT_DARK);
		emailLabel.setBounds(14, 24, WRAP_W - 110, 18);
		card.add(emailLabel);

		// 이메일 변경 버튼 (알약 모양 버튼)
		JButton btnChange = makePillButton("변경", ACCENT2, ACCENT, 64, 26);
		btnChange.setBounds(WRAP_W - 80, (EMAIL_H - 26) / 2, 64, 26);
		card.add(btnChange);

		btnChange.addActionListener(e -> {
			JTextField tf = new JTextField(UserMap.umap.get(loginUser.getId()).getEmail(), 24);
			tf.setFont(SANS_MED);
			int res = JOptionPane.showConfirmDialog(null, new Object[] { "새 이메일을 입력하세요:", tf }, "이메일 변경",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (res == JOptionPane.OK_OPTION) {
				String newEmail = tf.getText().trim();
				// 이메일 형식 유효성 검사 (SignUpValidator 활용)
				if (!SignUpValidator.isValidEmail(newEmail)) {
					JOptionPane.showMessageDialog(null, "올바른 이메일 형식이 아닙니다.", "오류", JOptionPane.WARNING_MESSAGE);
					return;
				}
				UserMap.umap.get(loginUser.getId()).setEmail(newEmail);
				if (emailLabel != null)
					emailLabel.setText(newEmail);
				JOptionPane.showMessageDialog(null, "이메일이 변경되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// 카드 크기 변경 시 버튼·라벨 위치 재조정
		card.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				btnChange.setBounds(card.getWidth() - 80, (EMAIL_H - 26) / 2, 64, 26);
				emailLabel.setBounds(14, 24, card.getWidth() - 110, 18);
			}
		});
		return card;
	}

	/**
	 * 로그아웃 버튼 행을 생성한다. 클릭 시 확인 다이얼로그를 표시하고, 승인하면 StartPage로 이동한다. 버튼은 둥근 모서리의 커스텀
	 * paintComponent로 그린다.
	 *
	 * @return 로그아웃 버튼 행 패널
	 */
	private JPanel buildActionRow() {
		JPanel row = new JPanel(new GridLayout(1, 1));
		row.setBackground(BG);
		row.setPreferredSize(new Dimension(WRAP_W, LOGOUT_H));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, LOGOUT_H));
		row.setMinimumSize(new Dimension(WRAP_W, LOGOUT_H));

		JButton btn = new JButton("로그아웃") {
			@Override
			protected void paintComponent(Graphics g) {
				// 배경을 둥근 사각형으로 직접 채워 ContentAreaFilled 없이도 색상이 보이게 한다.
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
				g2.dispose();
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				// 둥근 테두리를 직접 그린다.
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(ACCENT_HOV);
				g2.setStroke(new BasicStroke(1f));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
				g2.dispose();
			}
		};
		btn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		btn.setBackground(ACCENT);
		btn.setForeground(new Color(255, 252, 247));
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 마우스 호버 시 색상 변경
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(ACCENT_HOV);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(ACCENT);
			}
		});

		btn.addActionListener(e -> {
			int res = JOptionPane.showConfirmDialog(null,
					UserMap.umap.get(loginUser.getId()).getName() + "님, 로그아웃 하시겠습니까?", "로그아웃",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (res == JOptionPane.YES_OPTION)
				FrameBase.getInstance(new StartPage());
		});
		row.add(btn);
		return row;
	}

	// ══════════════════════════════════════════════════════════════
	// 주문 내역 섹션
	// ══════════════════════════════════════════════════════════════

	/**
	 * 주문 내역 섹션을 생성한다. 날짜 내림차순으로 정렬된 주문 그룹을 날짜 칩 → 주문 카드 순으로 배치한다. 주문이 없으면 안내 메시지를
	 * 표시한다. 내용을 그대로 쌓는 방식이며, 스크롤은 상위 JScrollPane이 담당한다.
	 *
	 * @param order 주문 내역 데이터
	 * @return 주문 내역 섹션 패널
	 */
	private JPanel buildOrderSection(Order order) {
		JPanel wrap = new JPanel();
		wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
		wrap.setBackground(BG);
		wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		wrap.setMinimumSize(new Dimension(100, 40));

		addLeft(wrap, buildSectionHeader("주문 내역"));
		wrap.add(vGap(8));

		Map<String, List<Order.OrderRecord>> byDate = order.getOrderHistoryByDate();

		if (byDate.isEmpty()) {
			// 주문 없음 안내 카드
			JPanel empty = new JPanel();
			empty.setBackground(CARD_BG);
			empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
			empty.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
			empty.setPreferredSize(new Dimension(WRAP_W, 80));
			empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
			JLabel lbl = new JLabel("아직 주문 내역이 없습니다.", SwingConstants.CENTER);
			lbl.setFont(SANS_MED);
			lbl.setForeground(TEXT_LIGHT);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			lbl.setBorder(new EmptyBorder(28, 0, 28, 0));
			empty.add(lbl);
			addLeft(wrap, empty);
		} else {
			// 날짜 내림차순 정렬 후 날짜 칩 → 해당 날짜의 주문 카드 순으로 추가
			byDate.entrySet().stream().sorted(Map.Entry.<String, List<Order.OrderRecord>>comparingByKey().reversed())
					.forEach(entry -> {
						addLeft(wrap, buildDateChip(entry.getKey()));
						wrap.add(vGap(6));
						for (Order.OrderRecord rec : entry.getValue()) {
							addLeft(wrap, buildOrderCard(rec));
							wrap.add(vGap(10));
						}
					});
		}
		wrap.add(vGap(10));
		return wrap;
	}

	/**
	 * 날짜를 표시하는 둥근 칩 패널을 생성한다. "yyyy-MM-dd" 형식의 문자열을 "yyyy년 MM월 dd일"로 변환하여 표시한다.
	 *
	 * @param dateStr "yyyy-MM-dd" 형식의 날짜 문자열
	 * @return 날짜 칩 패널
	 */
	private JPanel buildDateChip(String dateStr) {
		JPanel p = new JPanel(null);
		p.setBackground(BG);
		p.setOpaque(true);
		final int CH = 26;
		p.setPreferredSize(new Dimension(WRAP_W, CH));
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, CH));
		p.setMinimumSize(new Dimension(100, CH));

		// 배경·테두리를 직접 그리는 둥근 칩 라벨
		JLabel chip = new JLabel(formatDate(dateStr), SwingConstants.CENTER) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(TAG_BG);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
				g2.setColor(DIVIDER);
				g2.setStroke(new BasicStroke(1f));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		chip.setFont(new Font("맑은 고딕", Font.BOLD, 10));
		chip.setForeground(TEXT_MID);
		chip.setOpaque(false);
		chip.setBounds((WRAP_W - 190) / 2, 3, 190, CH - 6);
		p.add(chip);

		// 패널 너비가 바뀌면 칩을 다시 가운데 정렬
		p.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int w = p.getWidth();
				chip.setBounds((w - 190) / 2, 3, 190, CH - 6);
			}
		});
		return p;
	}

	/**
	 * 단일 주문 레코드를 보여주는 카드를 생성한다. 카드는 헤더(주문 번호·시간·결제 수단) + 상품 행 N개 + 푸터(총 금액) 구조이다.
	 * 높이는 ORDER_HDR_H + 상품 수 × (ITEM_ROW_H + 1) + ORDER_FTR_H 로 계산한다.
	 *
	 * @param record 표시할 주문 레코드
	 * @return 주문 카드 패널
	 */
	private JPanel buildOrderCard(Order.OrderRecord record) {
		int itemCount = record.getCart().size();
		// 상품 행마다 1px 구분선이 추가되므로 ITEM_ROW_H + 1 을 곱한다.
		int totalH = ORDER_HDR_H + itemCount * (ITEM_ROW_H + 1) + ORDER_FTR_H;

		RoundPanel card = new RoundPanel(12, CARD_BG, CARD_BORDER);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(WRAP_W, totalH));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, totalH));
		card.setMinimumSize(new Dimension(100, totalH));

		// ── 헤더 ────────────────────────────────────────────────
		JPanel header = new JPanel(null);
		header.setBackground(ORDER_HDR);
		header.setPreferredSize(new Dimension(WRAP_W, ORDER_HDR_H));
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, ORDER_HDR_H));
		header.setMinimumSize(new Dimension(100, ORDER_HDR_H));
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setBorder(new MatteBorder(0, 0, 1, 0, DIVIDER));

		// 주문 번호
		JLabel orderId = new JLabel("주문 #" + record.getOrderId());
		orderId.setFont(new Font("맑은 고딕", Font.BOLD, 11));
		orderId.setForeground(ACCENT);
		orderId.setBounds(10, (ORDER_HDR_H - 16) / 2, 90, 16);
		header.add(orderId);

		// 주문 시각
		JLabel timeLabel = new JLabel(record.getOrderTime());
		timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 9));
		timeLabel.setForeground(TEXT_LIGHT);
		timeLabel.setBounds(106, (ORDER_HDR_H - 13) / 2, 160, 13);
		header.add(timeLabel);

		// 결제 수단 (우측 정렬)
		JLabel payLabel = new JLabel(record.getPayment().getLabel());
		payLabel.setFont(new Font("맑은 고딕", Font.BOLD, 9));
		payLabel.setForeground(new Color(50, 100, 150));
		payLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		payLabel.setBounds(OW - 110, (ORDER_HDR_H - 13) / 2, 100, 13);
		header.add(payLabel);

		// 헤더 너비 변경 시 결제 수단·시각 라벨 위치 재조정
		header.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				payLabel.setBounds(header.getWidth() - 120, (ORDER_HDR_H - 13) / 2, 110, 13);
				timeLabel.setBounds(106, (ORDER_HDR_H - 13) / 2, header.getWidth() - 240, 13);
			}
		});
		card.add(header);

		// ── 상품 행 ─────────────────────────────────────────────
		// 각 CartItem마다 행을 만들고, 행 아래에 1px 구분선을 추가한다.
		for (CartItem ci : record.getCart().values()) {
			JPanel row = buildItemRow(ci);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			card.add(row);

			JSeparator sep = new JSeparator();
			sep.setForeground(new Color(225, 215, 200));
			sep.setPreferredSize(new Dimension(WRAP_W, 1));
			sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
			sep.setMinimumSize(new Dimension(100, 1));
			sep.setAlignmentX(Component.LEFT_ALIGNMENT);
			card.add(sep);
		}

		// ── 푸터 ─────────────────────────────────────────────────
		JPanel footer = new JPanel(null);
		footer.setBackground(ORDER_HDR);
		footer.setPreferredSize(new Dimension(WRAP_W, ORDER_FTR_H));
		footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, ORDER_FTR_H));
		footer.setMinimumSize(new Dimension(100, ORDER_FTR_H));
		footer.setAlignmentX(Component.LEFT_ALIGNMENT);
		footer.setBorder(new MatteBorder(1, 0, 0, 0, DIVIDER));

		JLabel totalTxt = new JLabel("총 결제 금액");
		totalTxt.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
		totalTxt.setForeground(TEXT_MID);
		totalTxt.setBounds(12, (ORDER_FTR_H - 15) / 2, 88, 15);
		footer.add(totalTxt);

		// 총 결제 금액 (우측 정렬)
		JLabel totalVal = new JLabel(fmtPrice(record.getTotalPrice()));
		totalVal.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		totalVal.setForeground(ACCENT);
		totalVal.setHorizontalAlignment(SwingConstants.RIGHT);
		totalVal.setBounds(OW - 170, (ORDER_FTR_H - 18) / 2, 156, 18);
		footer.add(totalVal);

		// 푸터 너비 변경 시 총액 라벨 위치 재조정
		footer.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				totalVal.setBounds(footer.getWidth() - 180, (ORDER_FTR_H - 18) / 2, 166, 18);
			}
		});
		card.add(footer);
		return card;
	}

	/**
	 * 주문 카드 내에서 상품 한 줄을 생성한다. 좌측에 상품 이미지, 우측에 이름·종류·수량, 맨 오른쪽에 소계 금액을 배치한다. 이미지 로드
	 * 실패 시 커피 이모지로 대체한다.
	 *
	 * @param ci 표시할 장바구니 아이템
	 * @return 상품 한 줄 패널 (고정 높이 ITEM_ROW_H)
	 */
	private JPanel buildItemRow(CartItem ci) {
		JPanel row = new JPanel(null);
		row.setBackground(CARD_BG);
		row.setPreferredSize(new Dimension(WRAP_W, ITEM_ROW_H));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_ROW_H));
		row.setMinimumSize(new Dimension(100, ITEM_ROW_H));

		// 상품 썸네일 이미지 (44×44, 실패 시 이모지 대체)
		JLabel imgLbl = new JLabel("", SwingConstants.CENTER);
		final int IMG = 44;
		imgLbl.setBounds(10, (ITEM_ROW_H - IMG) / 2, IMG, IMG);
		imgLbl.setOpaque(true);
		imgLbl.setBackground(CARD_ICON_BG);
		imgLbl.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
		String imgPath = ci.getItem().getMainImg();
		if (imgPath != null && !imgPath.isBlank()) {
			try {
				Image img = new ImageIcon(imgPath).getImage().getScaledInstance(IMG - 4, IMG - 4, Image.SCALE_SMOOTH);
				imgLbl.setIcon(new ImageIcon(img));
			} catch (Exception ignored) {
				imgLbl.setText("☕");
				imgLbl.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
			}
		} else {
			imgLbl.setText("☕");
			imgLbl.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		}
		row.add(imgLbl);

		// 상품명
		JLabel name = new JLabel(ci.getItem().getName());
		name.setFont(new Font("맑은 고딕", Font.BOLD, 11));
		name.setForeground(TEXT_DARK);
		name.setBounds(64, 8, 200, 16);
		row.add(name);

		// 상품 종류 (null-safe 처리)
		String kind = "";
		try {
			kind = ci.getItem().getKind() != null ? ci.getItem().getKind() : "";
		} catch (Exception ignored) {
		}
		JLabel kindLbl = new JLabel(kind);
		kindLbl.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
		kindLbl.setForeground(TEXT_LIGHT);
		kindLbl.setBounds(64, 27, 160, 13);
		row.add(kindLbl);

		// 구매 수량
		JLabel qtyLbl = new JLabel(ci.getQuantity() + "개");
		qtyLbl.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
		qtyLbl.setForeground(TEXT_LIGHT);
		qtyLbl.setBounds(64, 43, 60, 13);
		row.add(qtyLbl);

		// 소계 금액 (우측 정렬)
		JLabel subTotal = new JLabel(fmtPrice(ci.getTotal()));
		subTotal.setFont(new Font("맑은 고딕", Font.BOLD, 11));
		subTotal.setForeground(ACCENT2);
		subTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		subTotal.setBounds(OW - 150, (ITEM_ROW_H - 16) / 2, 136, 16);
		row.add(subTotal);

		// 행 너비 변경 시 이름·소계 위치 재조정
		row.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				name.setBounds(64, 8, row.getWidth() - 180, 16);
				subTotal.setBounds(row.getWidth() - 154, (ITEM_ROW_H - 16) / 2, 140, 16);
			}
		});
		return row;
	}

	// ══════════════════════════════════════════════════════════════
	// 유틸리티 메서드
	// ══════════════════════════════════════════════════════════════

	/**
	 * 지정된 높이의 수직 여백 컴포넌트를 반환한다.
	 *
	 * @param h 여백 높이(px)
	 * @return BoxLayout용 수직 Strut
	 */
	private Component vGap(int h) {
		return Box.createVerticalStrut(h);
	}

	/**
	 * 정수 가격을 "1,000원" 형식의 한국 로케일 문자열로 변환한다.
	 *
	 * @param price 원화 단위 정수 가격
	 * @return 천 단위 콤마와 "원" 접미사가 붙은 문자열
	 */
	private String fmtPrice(int price) {
		return NumberFormat.getNumberInstance(Locale.KOREA).format(price) + "원";
	}

	/**
	 * "yyyy-MM-dd" 형식의 날짜 문자열을 "yyyy년 MM월 dd일" 형식으로 변환한다. 파싱에 실패하면 원본 문자열을 그대로
	 * 반환한다.
	 *
	 * @param d 변환할 날짜 문자열
	 * @return 한국어 날짜 표현 문자열
	 */
	private String formatDate(String d) {
		try {
			String[] p = d.split("-");
			return p[0] + "년 " + p[1] + "월 " + p[2] + "일";
		} catch (Exception e) {
			return d;
		}
	}

	/**
	 * 둥근 알약 모양의 버튼을 생성한다. 마우스 호버 시 배경색이 hover 색상으로 바뀐다.
	 *
	 * @param text  버튼 텍스트
	 * @param bg    기본 배경색
	 * @param hover 호버 배경색
	 * @param w     버튼 너비(px)
	 * @param h     버튼 높이(px)
	 * @return 알약 모양 JButton
	 */
	private JButton makePillButton(String text, Color bg, Color hover, int w, int h) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
				g2.dispose();
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				// 기본 테두리를 그리지 않아 배경 색상만 보이게 한다.
			}
		};
		btn.setFont(new Font("맑은 고딕", Font.BOLD, 11));
		btn.setBackground(bg);
		btn.setForeground(new Color(255, 252, 247));
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(w, h));
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(bg);
			}
		});
		return btn;
	}

	/**
	 * JScrollPane의 세로 스크롤바를 커스텀 스타일로 변경한다. 위아래 화살표 버튼을 제거하고, 스크롤바 두께를 7px로 설정한다.
	 *
	 * @param scroll 스타일을 적용할 JScrollPane
	 */
	private void styleScrollBar(JScrollPane scroll) {
		scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				thumbColor = new Color(185, 162, 135); // 스크롤 핸들 색상
				trackColor = BG; // 트랙 배경색
			}

			/** 위쪽 화살표 버튼을 크기 0 버튼으로 교체하여 숨긴다. */
			@Override
			protected JButton createDecreaseButton(int o) {
				return zeroBtn();
			}

			/** 아래쪽 화살표 버튼을 크기 0 버튼으로 교체하여 숨긴다. */
			@Override
			protected JButton createIncreaseButton(int o) {
				return zeroBtn();
			}

			/** 화면에 보이지 않는 크기 0짜리 빈 버튼을 생성한다. */
			private JButton zeroBtn() {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(0, 0));
				return b;
			}
		});
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(7, Integer.MAX_VALUE));
	}

	/**
	 * 이미지 파일을 지정된 크기로 축소한 ImageIcon을 반환한다.
	 *
	 * @param path 이미지 파일 경로
	 * @param w    목표 너비(px)
	 * @param h    목표 높이(px)
	 * @return 크기 조정된 ImageIcon
	 */
	private ImageIcon scaledIcon(String path, int w, int h) {
		return new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	// ══════════════════════════════════════════════════════════════
	// 내부 클래스: 커스텀 컴포넌트
	// ══════════════════════════════════════════════════════════════

	/**
	 * 둥근 모서리 배경과 테두리를 직접 그리는 JPanel. Swing 기본 직각 패널 대신 부드러운 카드 형태를 표현할 때 사용한다.
	 */
	private static class RoundPanel extends JPanel {

		/** 모서리 반지름(px) */
		private final int radius;
		/** 배경 채우기 색상 */
		private final Color bg;
		/** 테두리 선 색상 */
		private final Color border;

		/**
		 * @param radius 모서리 반지름
		 * @param bg     배경 색상
		 * @param border 테두리 색상
		 */
		RoundPanel(int radius, Color bg, Color border) {
			this.radius = radius;
			this.bg = bg;
			this.border = border;
			setOpaque(false); // 부모 배경이 비치도록 불투명 해제
		}

		/** 둥근 사각형으로 배경을 채운다. */
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(bg);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
			g2.dispose();
		}

		/** 둥근 사각형 테두리를 그린다. */
		@Override
		protected void paintBorder(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(border);
			g2.setStroke(new BasicStroke(1.2f));
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
			g2.dispose();
		}
	}

	/**
	 * 이모지를 아이콘으로 표시하는 둥근 라벨. 배경을 직접 그려 카드 형태의 아이콘 영역처럼 보이게 한다.
	 */
	private static class RoundIconLabel extends JLabel {

		/**
		 * @param emoji 표시할 이모지 문자
		 * @param size  라벨의 너비/높이(px), 폰트 크기는 size/2로 설정된다.
		 */
		RoundIconLabel(String emoji, int size) {
			super(emoji, SwingConstants.CENTER);
			setFont(new Font("맑은 고딕", Font.PLAIN, size / 2));
			setOpaque(false);
			setPreferredSize(new Dimension(size, size));
		}

		/** 둥근 배경과 테두리를 그린 뒤 이모지 텍스트를 렌더링한다. */
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(CARD_ICON_BG);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
			g2.setColor(new Color(210, 190, 168));
			g2.setStroke(new BasicStroke(1f));
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
			g2.dispose();
			super.paintComponent(g);
		}
	}
}