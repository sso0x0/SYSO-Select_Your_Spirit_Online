package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import DTO.MyData;
import DTO.Item;
import shoppingCart.Cart;
import shoppingCart.CartItem;
import shoppingCart.Order;
import shoppingCart.Order.OrderRecord;
import shoppingCart.Payment;

// 장바구니 화면 프레임 (상품 목록 수정 및 선택 삭제, 결제 수단 및 최종 결제)
public class CartFrame extends JPanel {

	private static final long serialVersionUID = 1L;

	// 색상 상수 정의
	private static final Color PAGE_BG = new Color(225, 215, 200); // 전체 배경색 (베이지 계열)
	private static final Color CARD_BG = new Color(255, 252, 247); // 상품 카드 기본 배경
	private static final Color CARD_HOVER_BG = new Color(245, 238, 228); // 상품 카드 마우스 오버 배경
	private static final Color TOPBAR_BG = new Color(210, 198, 182); // 상단 바 배경
	private static final Color ACCENT1 = new Color(85, 65, 55); // 주 강조색 (진한 갈색)
	private static final Color ACCENT2 = new Color(120, 80, 60); // 보조 강조색 (중간 갈색)
	private static final Color ACCENT_HV1 = new Color(60, 45, 38); // ACCENT1 호버 색
	private static final Color ACCENT_HV2 = new Color(95, 60, 45); // ACCENT2 호버 색
	private static final Color TEXT_PRIMARY = new Color(85, 65, 55); // 주 텍스트 색상
	private static final Color TEXT_SECONDARY = new Color(130, 110, 95); // 보조 텍스트 색상
	private static final Color DIVIDER = new Color(196, 180, 165); // 구분선 색상
	private static final Color BORDER_COLOR = new Color(210, 190, 180); // 테두리 색상

	// 선택 여부에 따른 카드 기본 배경색
	private static Color cardBg(boolean checked) {
		return checked ? new Color(255, 248, 238) : CARD_BG;
	}

	// 선택 여부에 따른 카드 호버 배경색
	private static Color cardHoverBg(boolean checked) {
		return checked ? new Color(250, 240, 225) : CARD_HOVER_BG;
	}

	private final Cart cart; // 장바구니 데이터 (상품 목록, 수량 등 관리)
	private final Order order; // 주문 처리 객체 (결제 실행, 주문 기록 생성)
	private final MyData loginUser; // 현재 로그인된 사용자 정보

	// 현재 선택된 결제 수단 (기본값: 신용카드)
	private Payment selectedPayment = Payment.CREDIT_CARD;

	// UI 컴포넌트
	private JPanel listPanel; // 상품 카드 목록이 그려지는 스크롤 내부 패널
	private JLabel lblGoodsTotal; // 상품 금액 표시 라벨
	private JLabel lblFinalTotal; // 최종 결제 금액 표시 라벨

	// 각 상품 체크 여부 매핑
	private final Map<Integer, Boolean> checkedMap = new HashMap<>();

	private JCheckBox chkAll; // 전체 선택 체크박스
	private JLabel lblSelCount; // N개 선택됨 표시 라벨

	// 생성자
	public CartFrame(Cart cart, Order order, MyData d) {
		this.cart = cart;
		this.order = order;
		this.loginUser = d;

		setBackground(PAGE_BG);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(600, 800));

		// 레이아웃: 상단 바 / 중앙 스크롤 목록 / 하단 결제 영역
		add(buildTopBar(), BorderLayout.NORTH);
		add(buildScrollArea(), BorderLayout.CENTER);
		add(buildBottomArea(), BorderLayout.SOUTH);

		// 장바구니 목록 최초 렌더링
		refreshList();
	}

	// 상단 바: 좌측 로고 이미지, 중앙 타이틀, 우측 홈버튼(클릭 시 메인으로 이동)
	private JPanel buildTopBar() {
		JPanel bar = new JPanel(null); // 절대 좌표 레이아웃
		bar.setPreferredSize(new Dimension(600, 80));
		bar.setBackground(TOPBAR_BG);
		bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER)); // 하단 구분선

		// 장바구니 타이틀 중앙 배치
		JLabel lblTitle = label("장바구니", 20, Font.BOLD, TEXT_PRIMARY);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(200, 26, 200, 28);
		bar.add(lblTitle);

		// 홈 버튼 우측 배치 - 클릭 시 메인 화면으로 전환
		JButton btnHome = new JButton(scaledIcon("ui_img/home.png", 30, 30));
		btnHome.setPressedIcon(scaledIcon("ui_img/home_pressed.png", 30, 30)); // 클릭 시 아이콘 변경
		btnHome.setOpaque(false);
		btnHome.setContentAreaFilled(false);
		btnHome.setBorderPainted(false);
		btnHome.setFocusPainted(false);
		btnHome.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnHome.setBounds(550, 25, 30, 30);
		btnHome.addActionListener(e -> FrameBase.getInstance(new FrameMain(loginUser)));
		bar.add(btnHome);

		// 뒤로가기 버튼 좌측 배치 - 클릭 시, 사용자가 이전에 있었던 화면으로 전환
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
				FrameBase.goBack(); // 사용자가 현재 페이지 이전에 있었던 페이지로 돌림

			}
		});
		add(btnBack);

		return bar;
	}

	// 중앙 스크롤 영역
	private JScrollPane buildScrollArea() {
		// 실제 카드들이 추가되는 영역
		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(PAGE_BG);
		listPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

		JScrollPane scroll = new JScrollPane(listPanel);
		scroll.setBorder(null);
		scroll.setBackground(PAGE_BG);
		scroll.getViewport().setBackground(PAGE_BG);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		// FrameMain과 동일한 모던 스크롤바 커스텀 적용
		scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				thumbColor = new Color(180, 170, 160); // 스크롤바 색상
				trackColor = PAGE_BG; // 트랙은 배경색과 통일
			}

			@Override
			protected JButton createDecreaseButton(int o) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int o) {
				return createZeroButton();
			}

			private JButton createZeroButton() {
				JButton btn = new JButton();
				btn.setPreferredSize(new Dimension(10, 0)); // 화살표 버튼 숨기기
				return btn;
			}
		});
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));

		return scroll;
	}

	// 하단 결제 영역: 금액 요약, 결제 수단, 버튼 세로 배치
	private JPanel buildBottomArea() {
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		bottom.setBackground(CARD_HOVER_BG);
		bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER)); // 상단 구분선

		bottom.add(buildSummaryPanel()); // 상품 금액 / 배송비 / 최종 금액
		bottom.add(buildPaymentPanel()); // 결제 수단 선택 버튼들
		bottom.add(buildButtonRow()); // 장바구니 비우기, 결제하기 버튼

		return bottom;
	}

	// 금액 요약 패널: 상품 금액, 배송비, 최종 결제 금액
	private JPanel buildSummaryPanel() {
		final int PAD = 20;
		final int INNER_W = 600 - PAD * 2;

		JPanel p = new JPanel(null);
		p.setPreferredSize(new Dimension(600, 100));
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		p.setBackground(CARD_HOVER_BG);

		// 상품 금액 라벨
		addRightAlignedRow(p, PAD, INNER_W, 8, label("상품 금액", 12, Font.PLAIN, TEXT_SECONDARY),
				lblGoodsTotal = label("0원", 12, Font.PLAIN, TEXT_SECONDARY));

		// 배송비 라벨 무료 처리
		addRightAlignedRow(p, PAD, INNER_W, 30, label("배송비", 12, Font.PLAIN, TEXT_SECONDARY),
				label("무료", 12, Font.PLAIN, TEXT_SECONDARY));

		// 구분선
		JSeparator sep = new JSeparator();
		sep.setForeground(DIVIDER);
		sep.setBounds(PAD, 58, INNER_W, 1);
		p.add(sep);

		// 최종 결제 금액
		addRightAlignedRow(p, PAD, INNER_W, 68, label("최종 결제금액", 20, Font.BOLD, TEXT_PRIMARY),
				lblFinalTotal = label("0원", 20, Font.BOLD, ACCENT1));

		return p;
	}

	// 좌측 라벨 + 우측 정렬 라벨 한 쌍을 패널에 추가하는 헬퍼
	private void addRightAlignedRow(JPanel p, int pad, int innerW, int y, JLabel left, JLabel right) {
		left.setBounds(pad, y, 200, 26);
		right.setBounds(pad, y, innerW, 26);
		right.setHorizontalAlignment(SwingConstants.RIGHT);
		p.add(left);
		p.add(right);
	}

	// 결제 수단 선택 패널
	private JPanel buildPaymentPanel() {
		final int PAD = 20;
		final int INNER_W = 600 - PAD * 2;

		JPanel p = new JPanel(null);
		p.setPreferredSize(new Dimension(600, 62));
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
		p.setBackground(CARD_HOVER_BG);

		JLabel lbl = label("결제 수단", 12, Font.PLAIN, TEXT_SECONDARY);
		lbl.setBounds(PAD, 2, 100, 18);
		p.add(lbl);

		// Payment enum 개수에 따라 버튼 너비를 균등 분할
		Payment[] methods = Payment.values();
		int gap = 8;
		int btnW = (INNER_W - gap * (methods.length - 1)) / methods.length;

		for (int i = 0; i < methods.length; i++) {
			final Payment pm = methods[i];
			JButton btn = new JButton(pm.getLabel());
			btn.setBounds(PAD + i * (btnW + gap), 22, btnW, 30);
			btn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			stylePayBtn(btn, pm == selectedPayment); // 초기 선택 상태 스타일 적용

			btn.addActionListener(e -> {
				selectedPayment = pm; // 선택된 결제 수단 갱신
				// 패널 내 모든 버튼 스타일 재적용 (선택/비선택 구분)
				for (Component c : p.getComponents()) {
					if (c instanceof JButton) {
						JButton pb = (JButton) c;
						boolean sel = Arrays.stream(Payment.values())
								.anyMatch(x -> x.getLabel().equals(pb.getText()) && x == selectedPayment);
						stylePayBtn(pb, sel);
					}
				}
			});
			p.add(btn);
		}
		return p;
	}

	// 결제 수단 버튼 선택/비선택 스타일 적용
	private void stylePayBtn(JButton btn, boolean selected) {
		if (selected) {
			// 선택됨: 강조 배경 + 주 색상 테두리
			btn.setBackground(new Color(240, 228, 215));
			btn.setForeground(ACCENT1);
			btn.setBorder(BorderFactory.createLineBorder(ACCENT1, 1));
		} else {
			// 미선택: 흰색 배경 + 연한 테두리
			btn.setBackground(CARD_BG);
			btn.setForeground(TEXT_SECONDARY);
			btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		}
	}

	// 하단 버튼 행: 장바구니 비우기, 결제하기 버튼 가로 배치
	private JPanel buildButtonRow() {
		JPanel p = new JPanel(null);
		p.setPreferredSize(new Dimension(600, 54));
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
		p.setBackground(CARD_HOVER_BG);

		// 장바구니 비우기 버튼 (왼쪽)
		JButton btnClear = makeActionButton("장바구니 비우기", ACCENT2, ACCENT_HV2);
		btnClear.setBounds(20, 2, 272, 40);
		btnClear.addActionListener(e -> {
			int res = JOptionPane.showConfirmDialog(this, "장바구니를 비우시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				cart.clearCart(); // 장바구니 전체 비우기
				checkedMap.clear(); // 체크 상태 초기화
				refreshList(); // 화면 갱신
			}
		});
		p.add(btnClear);

		// 결제하기 버튼 (오른쪽)
		JButton btnPay = makeActionButton("결제하기", ACCENT1, ACCENT_HV1);
		btnPay.setBounds(300, 2, 280, 40);
		btnPay.addActionListener(e -> handleCheckout());
		p.add(btnPay);

		return p;
	}

	// 전체 선택 툴바: 전체 선택 체크박스, 선택 삭제 버튼
	private JPanel buildSelectionToolbar() {
		JPanel bar = new JPanel(null);
		bar.setPreferredSize(new Dimension(560, 30));
		bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		bar.setBackground(PAGE_BG);
		bar.setBorder(BorderFactory.createEmptyBorder());

		// 전체 선택 체크박스 (왼쪽 끝)
		chkAll = new JCheckBox("전체 선택");
		chkAll.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
		chkAll.setForeground(TEXT_SECONDARY);
		chkAll.setBackground(PAGE_BG);
		chkAll.setFocusPainted(false);
		chkAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
		chkAll.setBounds(0, 4, 95, 22);
		chkAll.addActionListener(e -> {
			boolean all = chkAll.isSelected();
			checkedMap.replaceAll((id, v) -> all);
			refreshList();
		});
		bar.add(chkAll);

		// N개 선택됨 라벨 (체크박스 바로 오른쪽)
		lblSelCount = new JLabel("0개 선택됨", SwingConstants.LEFT);
		lblSelCount.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
		lblSelCount.setForeground(TEXT_SECONDARY);
		lblSelCount.setBounds(98, 7, 80, 16);
		bar.add(lblSelCount);

		// 선택 삭제 버튼 (오른쪽 끝)
		JButton btnDelSel = makeSmallDeleteButton("선택 삭제");
		btnDelSel.setBounds(560 - 72, 4, 72, 22);
		btnDelSel.addActionListener(e -> {
			long selCount = checkedMap.values().stream().filter(v -> v).count();
			if (selCount == 0) {
				JOptionPane.showMessageDialog(this, "삭제할 항목을 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int res = JOptionPane.showConfirmDialog(this, String.format("선택한 %d개 상품을 삭제하시겠습니까?", selCount), "선택 삭제",
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				// 체크된 상품 ID를 순회하며 cart에서 제거 + checkedMap에서도 제거
				new ArrayList<>(checkedMap.entrySet()).stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
						.forEach(id -> {
							cart.removeItem(id);
							checkedMap.remove(id);
						});
				refreshList();
			}
		});
		bar.add(btnDelSel);

		// 창 크기 변경 시 선택 삭제 버튼을 오른쪽 끝에 유지
		bar.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				btnDelSel.setBounds(bar.getWidth() - 72, 4, 72, 22);
			}
		});

		return bar;
	}

	// 선택 삭제 버튼 - 빨간 버튼 생성 (호버 시 더 진한 빨간색 표시)
	private JButton makeSmallDeleteButton(String text) {
		Color bg = new Color(196, 69, 61);
		Color hoverBg = new Color(150, 55, 45);
		JButton btn = new JButton(text);
		btn.setFont(new Font("맑은 고딕", Font.BOLD, 11));
		btn.setBackground(bg);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createEmptyBorder());
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		applyHoverColor(btn, bg, hoverBg);
		return btn;
	}

	// 장바구니 목록 갱신: 장바구니 상품 목록 전체 렌더링 + 금액 합산 업데이트
	private void refreshList() {
		listPanel.removeAll(); // 기존 카드 전부 제거

		Map<Item, CartItem> cartMap = cart.getCartList();

		if (cartMap.isEmpty()) {
			// 장바구니가 빈 경우
			checkedMap.clear();
			JLabel empty = label("장바구니가 비어 있습니다.", 14, Font.PLAIN, TEXT_SECONDARY);
			empty.setHorizontalAlignment(SwingConstants.CENTER);
			empty.setAlignmentX(Component.CENTER_ALIGNMENT);
			empty.setBorder(new EmptyBorder(60, 0, 60, 0));
			listPanel.add(empty);
		} else {
			// 신규 상품은 기본 체크 false로 추가, 없어진 상품은 checkedMap에서 제거
			syncCheckedMap(cartMap);

			listPanel.add(buildSelectionToolbar()); // 전체선택 툴바
			listPanel.add(Box.createVerticalStrut(6)); // 6px 간격

			// 각 CartItem에 대해 카드 생성 후 추가
			for (CartItem ci : new ArrayList<>(cartMap.values())) {
				listPanel.add(buildCartCard(ci));
				listPanel.add(Box.createVerticalStrut(10)); // 카드 사이 10px 간격
			}

			// 모든 상품이 체크되어 있으면 전체 선택 체크박스도 체크
			if (chkAll != null)
				chkAll.setSelected(!checkedMap.isEmpty() && checkedMap.values().stream().allMatch(v -> v));

			updateSelCount(); // N개 선택됨 라벨 갱신
		}

		updateTotals(); // 하단 금액 합산 갱신
		listPanel.revalidate();
		listPanel.repaint();
	}

	// checkedMap을 현재 cartMap 기준으로 동기화 (신규 추가 / 삭제된 항목 반영)
	private void syncCheckedMap(Map<Item, CartItem> cartMap) {
		for (Item it : cartMap.keySet())
			checkedMap.putIfAbsent(it.getId(), true);
		checkedMap.keySet()
				.retainAll(cartMap.keySet().stream().map(Item::getId).collect(java.util.stream.Collectors.toSet()));
	}

	// N개 선택됨 라벨 텍스트를 현재 체크된 항목 수로 갱신
	private void updateSelCount() {
		if (lblSelCount == null)
			return;
		long cnt = checkedMap.values().stream().filter(v -> v).count();
		lblSelCount.setText(cnt + "개 선택됨");
	}

	// 상품 카드: 체크박스, 상품명, 카테고리, 수량 조절, 금액 등등
	private JPanel buildCartCard(CartItem ci) {
		Item item = ci.getItem();
		boolean isChecked = Boolean.TRUE.equals(checkedMap.get(item.getId()));

		// 카드 패널 (선택 여부에 따라 배경/테두리 색 다름)
		JPanel card = new JPanel(null);
		card.setBackground(cardBg(isChecked));
		card.setBorder(isChecked ? BorderFactory.createLineBorder(ACCENT2, 1)
				: BorderFactory.createLineBorder(BORDER_COLOR, 1));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		card.setPreferredSize(new Dimension(560, 100));
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 체크박스
		JCheckBox chk = new JCheckBox();
		chk.setBounds(6, 38, 22, 22);
		chk.setSelected(isChecked);
		chk.setBackground(card.getBackground());
		chk.setOpaque(true);
		chk.setFocusPainted(false);
		chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
		chk.addActionListener(e -> {
			checkedMap.put(item.getId(), chk.isSelected());
			if (chkAll != null)
				chkAll.setSelected(checkedMap.values().stream().allMatch(v -> v));
			updateSelCount();
			applyCardStyle(card, chk, chk.isSelected());
			updateTotals();
		});
		card.add(chk);

		// ✕ 개별 삭제 버튼 (우상단)
		JButton btnDel = new JButton("✕");
		btnDel.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnDel.setForeground(new Color(187, 176, 168));
		btnDel.setBackground(card.getBackground());
		btnDel.setOpaque(true);
		btnDel.setBorder(BorderFactory.createEmptyBorder());
		btnDel.setFocusPainted(false);
		btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnDel.setBounds(card.getPreferredSize().width - 29, 1, 28, 27);
		// 창 크기 변경 시 버튼을 항상 오른쪽 끝에 유지
		card.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				btnDel.setBounds(card.getWidth() - 29, 1, 28, 27);
			}
		});
		btnDel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnDel.setForeground(ACCENT1);
				btnDel.setBackground(CARD_HOVER_BG);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnDel.setForeground(new Color(187, 176, 168));
				btnDel.setBackground(card.getBackground());
			}
		});
		btnDel.addActionListener(e -> {
			cart.removeItem(item.getId()); // 장바구니에서 해당 상품 제거
			checkedMap.remove(item.getId()); // 체크 상태 맵에서도 제거
			refreshList();
		});
		card.add(btnDel);

		// 상품 이미지
		JLabel imgLbl = new JLabel("", SwingConstants.CENTER);
		imgLbl.setBounds(32, 10, 80, 80);
		imgLbl.setBackground(Color.WHITE);
		imgLbl.setOpaque(true);
		imgLbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		if (item.getMainImg() != null && !item.getMainImg().isBlank()) {
			try {
				Image scaled = new ImageIcon(item.getMainImg()).getImage().getScaledInstance(76, 76,
						Image.SCALE_SMOOTH);
				imgLbl.setIcon(new ImageIcon(scaled));
			} catch (Exception ignored) {
				setNoImgText(imgLbl); // 이미지 로드 실패 시 텍스트 대체
			}
		} else {
			setNoImgText(imgLbl);
		}

		// 카드 및 이미지 공동 MouseAdapter (클릭 시 상품 상세 화면으로 이동, 호버 시 배경색 변경)
		MouseAdapter cardClick = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FrameBase.getInstance(new FrameItemInfo(item, cart, order, loginUser));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				applyCardChildBg(card, btnDel, imgLbl, cardHoverBg(isChecked));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				applyCardChildBg(card, btnDel, imgLbl, cardBg(isChecked));
			}
		};
		card.addMouseListener(cardClick);
		imgLbl.addMouseListener(cardClick); // 이미지도 동일한 클릭/호버 동작
		card.add(imgLbl);

		// 텍스트 정보
		JLabel lblName = label(item.getName(), 14, Font.BOLD, TEXT_PRIMARY);
		lblName.setBounds(124, 8, 320, 20);
		lblName.addMouseListener(cardClick);
		card.add(lblName);

		JLabel lblKind = label(item.getKind() != null ? item.getKind() : "", 11, Font.PLAIN, TEXT_SECONDARY);
		lblKind.setBounds(124, 30, 240, 16);
		lblKind.addMouseListener(cardClick);
		card.add(lblKind);

		// 재고 리본 배지 (재고 수량에 따라 색상/문구 자동 변경)
		JLabel ribbon = makeRibbonBadge(item.getQuantity());
		ribbon.setBounds(124, 53, 120, 20);
		ribbon.addMouseListener(cardClick);
		card.add(ribbon);

		// 단가
		JLabel lblPrice = label(fmtPrice(item.getPrice()), 13, Font.BOLD, ACCENT2);
		lblPrice.setBounds(124, 72, 200, 20);
		lblPrice.addMouseListener(cardClick);
		card.add(lblPrice);

		// 소계 (수량 × 단가) — 우하단
		JLabel lblSub = label(fmtPrice(ci.getTotal()), 12, Font.PLAIN, TEXT_SECONDARY);
		lblSub.setBounds(455, 68, 90, 20);
		lblSub.setHorizontalAlignment(SwingConstants.RIGHT);
		card.add(lblSub);

		// 수량 조절 필 (-|수량|+)
		card.add(buildQtyPill(ci, item));

		return card;
	}

	// 수량 조절 필 패널 생성 (−|수량|+)
	private JPanel buildQtyPill(CartItem ci, Item item) {
		JPanel pill = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(new Color(225, 210, 195)); // 필 배경색
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(BORDER_COLOR); // 필 테두리
				g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g2.dispose();
			}
		};
		pill.setOpaque(false);
		pill.setBounds(345, 60, 120, 28);

		// − 버튼: 수량 1 감소
		JButton btnMinus = makeQtyButton("−");
		btnMinus.setBounds(0, 0, 36, 28);
		btnMinus.addActionListener(e -> {
			cart.minusQuantity(item.getId(), 1);
			refreshList();
		});
		pill.add(btnMinus);

		// 현재 수량 표시 라벨 (중앙)
		JLabel lblQty = new JLabel(String.valueOf(ci.getQuantity()), SwingConstants.CENTER);
		lblQty.setBounds(36, 0, 48, 28);
		lblQty.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		lblQty.setForeground(TEXT_PRIMARY);
		pill.add(lblQty);

		// + 버튼: 수량 1 증가
		JButton btnPlus = makeQtyButton("+");
		btnPlus.setBounds(84, 0, 36, 28);
		btnPlus.addActionListener(e -> {
			cart.addQuantity(item.getId(), 1);
			refreshList();
		});
		pill.add(btnPlus);

		// 세로 구분선 (− 버튼과 수량 사이, 수량과 + 버튼 사이)
		pill.add(makeVertSep(36));
		pill.add(makeVertSep(84));

		return pill;
	}

	// 수량 조절 버튼(+/−) 공통 생성 메소드
	private JButton makeQtyButton(String text) {
		JButton btn = new JButton(text);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setFont(new Font("Dialog", Font.PLAIN, 16));
		btn.setForeground(TEXT_PRIMARY);
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return btn;
	}

	// 수량 필 내부 세로 구분선 생성
	private JSeparator makeVertSep(int x) {
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		sep.setBounds(x, 5, 1, 18);
		sep.setForeground(BORDER_COLOR);
		return sep;
	}

	// 카드 스타일 헬퍼: 체크 상태 변경 시 카드 배경/테두리/체크박스 배경 일괄 갱신
	private void applyCardStyle(JPanel card, JCheckBox chk, boolean checked) {
		Color bg = cardBg(checked);
		card.setBackground(bg);
		card.setBorder(
				checked ? BorderFactory.createLineBorder(ACCENT2, 1) : BorderFactory.createLineBorder(BORDER_COLOR, 1));
		chk.setBackground(bg);
		card.repaint();
	}

	// 호버/언호버 시 카드와 불투명 자식 컴포넌트(이미지 제외, 일반 JButton 제외, btnDel 포함)에 배경 적용
	private void applyCardChildBg(JPanel card, JButton btnDel, JLabel imgLbl, Color bg) {
		card.setBackground(bg);
		for (Component c : card.getComponents()) {
			if (c.isOpaque() && (!(c instanceof JButton) || c == btnDel) && c != imgLbl)
				c.setBackground(bg);
		}
		card.repaint();
	}

	// 결제 처리
	private void handleCheckout() {
		if (cart.getCartList().isEmpty()) {
			JOptionPane.showMessageDialog(this, "장바구니에 상품이 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 체크된 아이템만 추출 (선택 결제)
		Map<Item, CartItem> selectedItems = cart.getCartList().entrySet().stream()
				.filter(e -> Boolean.TRUE.equals(checkedMap.get(e.getKey().getId())))
				.collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (selectedItems.isEmpty()) {
			JOptionPane.showMessageDialog(this, "결제할 상품을 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int total = selectedItems.values().stream().mapToInt(CartItem::getTotal).sum();

		int res = JOptionPane.showConfirmDialog(this,
				String.format("<html><b>결제 수단:</b> %s<br><b>결제 금액:</b> %s<br><b>상품 수:</b> %d개<br><br>결제하시겠습니까?</html>",
						selectedPayment.getLabel(), fmtPrice(total), selectedItems.size()),
				"결제 확인", JOptionPane.YES_NO_OPTION);

		if (res != JOptionPane.YES_OPTION)
			return;

		// 선택된 항목만 담은 임시 Cart 생성 후 주문 처리
		Cart tempCart = new Cart();
		selectedItems.values().forEach(ci -> tempCart.addItem(ci.getItem(), ci.getQuantity()));

		OrderRecord record = order.placeOrder(tempCart, selectedPayment);
		if (record != null) {
			// 결제 완료된 항목만 원래 cart에서 제거
			selectedItems.keySet().forEach(item -> {
				cart.removeItem(item.getId());
				checkedMap.remove(item.getId());
			});
			showOrderComplete(record);
			refreshList();
		} else {
			JOptionPane.showMessageDialog(this, "결제에 실패했습니다. 재고를 확인해 주세요.", "결제 실패", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 결제 완료 후 주문 상세 정보를 HTML 형식으로 팝업 출력 (주문번호, 주문시간, 결제수단, 주문 상품, 합계)
	private void showOrderComplete(OrderRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body style='font-family:맑은 고딕;'>");
		sb.append("<b style='color:#554137;font-size:14px;'>결제가 완료되었습니다!</b><br><br>");
		sb.append(String.format("주문번호: <b>%d</b><br>", record.getOrderId()));
		sb.append(String.format("주문시간: %s<br>", record.getOrderTime()));
		sb.append(String.format("결제수단: %s<br>", record.getPayment().getLabel()));
		sb.append("<br><b>주문 상품:</b><br>");
		for (CartItem ci : record.getCart().values()) {
			sb.append(String.format("&nbsp;· %s &nbsp;%d개 &nbsp;%s<br>", ci.getItem().getName(), ci.getQuantity(),
					fmtPrice(ci.getTotal())));
		}
		sb.append(String.format("<br><b>합계: %s</b>", fmtPrice(record.getTotalPrice())));
		sb.append("</body></html>");
		JOptionPane.showMessageDialog(this, sb.toString(), "주문 완료", JOptionPane.INFORMATION_MESSAGE);
	}

	// 금액 합산 갱신 (체크된 상품만 합산, 미선택시 기본값 0원 표시)
	// 선택된 상품 금액만 표시
	// 배송비 0원 처리 -- 배송비 로직도 추후 추가하면 좋을 듯함
	private void updateTotals() {
		long checkedCount = checkedMap.values().stream().filter(v -> v).count();

		int total;
		if (checkedCount == 0) {
			total = 0; // 선택 없으면 0원
		} else {
			total = cart.getCartList().entrySet().stream()
					.filter(e -> Boolean.TRUE.equals(checkedMap.get(e.getKey().getId())))
					.mapToInt(e -> e.getValue().getTotal()).sum();
		}

		String fmt = fmtPrice(total);
		lblGoodsTotal.setText(fmt);
		lblFinalTotal.setText(fmt); // 상품 금액 = 최증 금액
	}

	// 폰트, 색상으로 JLabel 생성
	private JLabel label(String text, int size, int style, Color fg) {
		JLabel l = new JLabel(text);
		l.setFont(new Font("맑은 고딕", style, size));
		l.setForeground(fg);
		return l;
	}

	// 이미지 파일을 지정 크기로 스케일한 ImageIcon 생성
	private ImageIcon scaledIcon(String path, int w, int h) {
		return new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	// 이미지 로드 실패 시 라벨에 텍스트 적용
	private void setNoImgText(JLabel lbl) {
		lbl.setText("NO IMG");
		lbl.setFont(new Font("맑은 고딕", Font.BOLD, 9));
		lbl.setForeground(TEXT_SECONDARY);
	}

	// 버튼에 호버 색상 MouseAdapter 적용 (배경색 전환)
	private void applyHoverColor(JButton btn, Color normal, Color hover) {
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(normal);
			}
		});
	}

	// 리본 뱃지 생성 (재고 수량에 따라 색상 및 문구 변동)
	private JLabel makeRibbonBadge(int qty) {
		Color color;
		String text;
		if (qty == 0) {
			color = new Color(115, 115, 115);
			text = "품절";
		} else if (qty < 3) {
			color = new Color(196, 69, 61);
			text = qty + "병 남음";
		} else if (qty < 8) {
			color = new Color(220, 146, 31);
			text = qty + "병 남음";
		} else {
			color = new Color(36, 108, 145);
			text = "재고 있음";
		}
		return new RibbonLabel(text, color);
	}

	// 장바구니 비우기, 결제하기 공통 버튼 생성 메소드
	private JButton makeActionButton(String text, Color bg, Color hoverBg) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		btn.setBackground(bg);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createEmptyBorder());
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		applyHoverColor(btn, bg, hoverBg);
		return btn;
	}

	// 정수 금액 포맷팅 (00,000원)
	private String fmtPrice(int price) {
		return NumberFormat.getNumberInstance(Locale.KOREA).format(price) + "원";
	}

	// 리본 모양의 커스텀 라벨
	private static class RibbonLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private final Color ribbonColor; // 리본 배경 색상

		RibbonLabel(String text, Color ribbonColor) {
			super(text, SwingConstants.CENTER);
			this.ribbonColor = ribbonColor;
			setForeground(Color.WHITE);
			setFont(new Font("맑은 고딕", Font.BOLD, 11));
			setOpaque(false);
		}

		// 왼쪽 직사각형, 오른쪽 리본 모양 그리기
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth() - 1, h = getHeight() - 1, notch = 8; // notch: 노치 깊이
			int[] xs = { 0, w, w - notch, w, 0 };
			int[] ys = { 0, 0, h / 2, h, h };
			g2.setColor(ribbonColor);
			g2.fillPolygon(xs, ys, 5);
			g2.dispose();
			super.paintComponent(g); // 텍스트 렌더링
		}
	}
}