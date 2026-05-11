package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import DTO.MyData;
import shoppingCart.Cart;
import shoppingCart.CartItem;
import shoppingCart.Order;

public class OrderLibrary extends JPanel {

	private static final long serialVersionUID = 1L;

	// ── 색상 상수 (CartFrame과 통일) ──────────────────────────────
	private static final Color PAGE_BG = new Color(225, 215, 200);
	private static final Color CARD_BG = new Color(255, 252, 247);
	private static final Color CARD_HOVER_BG = new Color(245, 238, 228);
	private static final Color TOPBAR_BG = new Color(210, 198, 182);
	private static final Color ACCENT1 = new Color(85, 65, 55);
	private static final Color ACCENT2 = new Color(120, 80, 60);
	private static final Color TEXT_PRIMARY = new Color(85, 65, 55);
	private static final Color TEXT_SECONDARY = new Color(130, 110, 95);
	private static final Color DIVIDER = new Color(196, 180, 165);
	private static final Color BORDER_COLOR = new Color(210, 190, 180);
	private static final Color DATE_HEADER_BG = new Color(210, 198, 182); // 날짜 헤더 배경

	public OrderLibrary() {
	}

	public OrderLibrary(MyData d, Cart cart, Order order) {
		setBackground(PAGE_BG);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(600, 800));

		add(buildTopBar(d, cart, order), BorderLayout.NORTH);
		add(buildScrollArea(order), BorderLayout.CENTER);
		add(buildBottomBar(d, cart, order), BorderLayout.SOUTH);
	}

	// ── 상단 바 ──────────────────────────────────────────────────
	private JPanel buildTopBar(MyData d, Cart cart, Order order) {
		JPanel bar = new JPanel(null);
		bar.setPreferredSize(new Dimension(600, 80));
		bar.setBackground(TOPBAR_BG);
		bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));

		// 로고
		JLabel logoImg = new JLabel();
		logoImg.setBounds(10, 5, 140, 75);
		try {
			Image img = new ImageIcon("ui_img/SYSOLogo.png").getImage().getScaledInstance(130, 80, Image.SCALE_SMOOTH);
			logoImg.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			logoImg.setText("NO IMG");
		}
		bar.add(logoImg);

		// 타이틀
		JLabel title = new JLabel("주문 내역", SwingConstants.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		title.setForeground(TEXT_PRIMARY);
		title.setBounds(200, 26, 200, 28);
		bar.add(title);

		// 홈 버튼
		JButton btnHome = iconButton("ui_img/home.png", "ui_img/home_pressed.png");
		btnHome.setBounds(550, 25, 30, 30);
		btnHome.addActionListener(e -> FrameBase.getInstance(new FrameMain(d)));
		bar.add(btnHome);

		return bar;
	}

	// ── 스크롤 영역 (날짜별 그룹) ────────────────────────────────
	private JScrollPane buildScrollArea(Order order) {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(PAGE_BG);
		listPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

		Map<String, List<Order.OrderRecord>> byDate = order.getOrderHistoryByDate();

		if (byDate.isEmpty()) {
			// 주문 내역 없을 때
			JLabel empty = new JLabel("주문 내역이 없습니다.", SwingConstants.CENTER);
			empty.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
			empty.setForeground(TEXT_SECONDARY);
			empty.setAlignmentX(Component.CENTER_ALIGNMENT);
			empty.setBorder(new EmptyBorder(80, 0, 80, 0));
			listPanel.add(empty);
		} else {
			// 날짜 내림차순 정렬 (최신 날짜가 위)
			byDate.entrySet().stream().sorted(Map.Entry.<String, List<Order.OrderRecord>>comparingByKey().reversed())
					.forEach(entry -> {
						listPanel.add(buildDateHeader(entry.getKey())); // 날짜 헤더
						listPanel.add(Box.createVerticalStrut(6));

						for (Order.OrderRecord record : entry.getValue()) {
							listPanel.add(buildOrderCard(record)); // 주문 카드
							listPanel.add(Box.createVerticalStrut(8));
						}
						listPanel.add(Box.createVerticalStrut(10));
					});
		}

		JScrollPane scroll = new JScrollPane(listPanel);
		scroll.setBorder(null);
		scroll.setBackground(PAGE_BG);
		scroll.getViewport().setBackground(PAGE_BG);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		// 모던 스크롤바 (CartFrame과 동일)
		scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
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
				b.setPreferredSize(new Dimension(10, 0));
				return b;
			}
		});
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));

		return scroll;
	}

	// ── 날짜 헤더 ────────────────────────────────────────────────
	// "2025-01-15" 형태를 "2025년 01월 15일" 로 표시
	private JPanel buildDateHeader(String dateStr) {
		JPanel header = new JPanel(null);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
		header.setPreferredSize(new Dimension(560, 34));
		header.setBackground(PAGE_BG);

		// 구분선
		JSeparator sep = new JSeparator();
		sep.setForeground(DIVIDER);
		sep.setBounds(0, 16, 560, 1);
		header.add(sep);

		// 날짜 배지
		String display = formatDateLabel(dateStr);
		JLabel lbl = new JLabel(" " + display + " ");
		lbl.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		lbl.setForeground(TEXT_PRIMARY);
		lbl.setBackground(DATE_HEADER_BG);
		lbl.setOpaque(true);
		lbl.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		// 배지를 중앙에 배치
		lbl.setBounds(200, 4, 160, 26);
		header.add(lbl);

		return header;
	}

	// "yyyy-MM-dd" → "yyyy년 MM월 dd일"
	private String formatDateLabel(String dateStr) {
		try {
			String[] parts = dateStr.split("-");
			return parts[0] + "년 " + parts[1] + "월 " + parts[2] + "일";
		} catch (Exception e) {
			return dateStr;
		}
	}

	// ── 주문 카드 (1건) ──────────────────────────────────────────
	// 주문번호/시간/결제수단 헤더 + 상품 목록 + 합계
	private JPanel buildOrderCard(Order.OrderRecord record) {
		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(CARD_BG);
		card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		// ── 주문 헤더 행 ───────────────────────────────────────
		JPanel headerRow = new JPanel(null);
		headerRow.setPreferredSize(new Dimension(560, 36));
		headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		headerRow.setBackground(CARD_HOVER_BG);
		headerRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));

		// 주문번호
		JLabel lblOrderId = lbl("주문 #" + record.getOrderId(), 12, Font.BOLD, ACCENT1);
		lblOrderId.setBounds(12, 9, 100, 18);
		headerRow.add(lblOrderId);

		// 주문 시각
		JLabel lblTime = lbl(record.getOrderTime(), 11, Font.PLAIN, TEXT_SECONDARY);
		lblTime.setBounds(115, 10, 180, 16);
		headerRow.add(lblTime);

		// 결제 수단 (우측)
		JLabel lblPay = lbl(record.getPayment().getLabel(), 11, Font.PLAIN, ACCENT2);
		lblPay.setBounds(300, 10, 240, 16);
		lblPay.setHorizontalAlignment(SwingConstants.RIGHT);
		headerRow.add(lblPay);

		card.add(headerRow);

		// ── 상품 행 (상품 수만큼) ──────────────────────────────
		for (CartItem ci : record.getCart().values()) {
			card.add(buildItemRow(ci));
			// 상품 사이 얇은 구분선
			JSeparator itemSep = new JSeparator();
			itemSep.setForeground(new Color(235, 225, 215));
			itemSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
			card.add(itemSep);
		}

		// ── 합계 행 ────────────────────────────────────────────
		JPanel totalRow = new JPanel(null);
		totalRow.setPreferredSize(new Dimension(560, 32));
		totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		totalRow.setBackground(CARD_HOVER_BG);
		totalRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER));

		JLabel lblTotalTxt = lbl("합계", 12, Font.PLAIN, TEXT_SECONDARY);
		lblTotalTxt.setBounds(12, 8, 80, 16);
		totalRow.add(lblTotalTxt);

		JLabel lblTotalAmt = lbl(fmtPrice(record.getTotalPrice()), 13, Font.BOLD, ACCENT1);
		lblTotalAmt.setBounds(0, 8, 548, 16);
		lblTotalAmt.setHorizontalAlignment(SwingConstants.RIGHT);
		totalRow.add(lblTotalAmt);

		card.add(totalRow);

		return card;
	}

	// ── 상품 한 줄 행 ────────────────────────────────────────────
	private JPanel buildItemRow(CartItem ci) {
		JPanel row = new JPanel(null);
		row.setPreferredSize(new Dimension(560, 70));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
		row.setBackground(CARD_BG);

		// 상품 이미지
		JLabel imgLbl = new JLabel("", SwingConstants.CENTER);
		imgLbl.setBounds(12, 10, 50, 50);
		imgLbl.setBackground(Color.WHITE);
		imgLbl.setOpaque(true);
		imgLbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		String imgPath = ci.getItem().getMainImg();
		if (imgPath != null && !imgPath.isBlank()) {
			try {
				Image img = new ImageIcon(imgPath).getImage().getScaledInstance(46, 46, Image.SCALE_SMOOTH);
				imgLbl.setIcon(new ImageIcon(img));
			} catch (Exception e) {
				imgLbl.setText("NO IMG");
				imgLbl.setFont(new Font("맑은 고딕", Font.BOLD, 8));
				imgLbl.setForeground(TEXT_SECONDARY);
			}
		} else {
			imgLbl.setText("NO IMG");
			imgLbl.setFont(new Font("맑은 고딕", Font.BOLD, 8));
			imgLbl.setForeground(TEXT_SECONDARY);
		}
		row.add(imgLbl);

		// 상품명
		JLabel lblName = lbl(ci.getItem().getName(), 13, Font.BOLD, TEXT_PRIMARY);
		lblName.setBounds(74, 14, 300, 18);
		row.add(lblName);

		// 카테고리
		String kind = ci.getItem().getKind() != null ? ci.getItem().getKind() : "";
		JLabel lblKind = lbl(kind, 11, Font.PLAIN, TEXT_SECONDARY);
		lblKind.setBounds(74, 34, 200, 16);
		row.add(lblKind);

		// 수량
		JLabel lblQty = lbl(ci.getQuantity() + "개", 12, Font.PLAIN, TEXT_SECONDARY);
		lblQty.setBounds(74, 52, 80, 16);
		row.add(lblQty);

		// 소계 (우측 정렬)
		JLabel lblSub = lbl(fmtPrice(ci.getTotal()), 12, Font.BOLD, ACCENT2);
		lblSub.setBounds(0, 52, 548, 16);
		lblSub.setHorizontalAlignment(SwingConstants.RIGHT);
		row.add(lblSub);

		return row;
	}

	// ── 하단 뒤로가기 바 ─────────────────────────────────────────
	private JPanel buildBottomBar(MyData d, Cart cart, Order order) {
		JPanel bar = new JPanel(null);
		bar.setPreferredSize(new Dimension(600, 60));
		bar.setBackground(CARD_HOVER_BG);
		bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER));

		JButton btnBack = new JButton("← 마이페이지로");
		btnBack.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		btnBack.setBackground(ACCENT1);
		btnBack.setForeground(Color.WHITE);
		btnBack.setFocusPainted(false);
		btnBack.setBorder(BorderFactory.createEmptyBorder());
		btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnBack.setBounds(20, 10, 560, 40);
		btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btnBack.setBackground(new Color(60, 45, 38));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				btnBack.setBackground(ACCENT1);
			}
		});
		btnBack.addActionListener(e -> FrameBase.getInstance(new MyPage(cart, order, d)));
		bar.add(btnBack);

		return bar;
	}

	// ── 유틸 ─────────────────────────────────────────────────────

	private JLabel lbl(String text, int size, int style, Color fg) {
		JLabel l = new JLabel(text);
		l.setFont(new Font("맑은 고딕", style, size));
		l.setForeground(fg);
		return l;
	}

	private JButton iconButton(String normalPath, String pressedPath) {
		JButton btn = new JButton();
		try {
			btn.setIcon(
					new ImageIcon(new ImageIcon(normalPath).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
			btn.setPressedIcon(
					new ImageIcon(new ImageIcon(pressedPath).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		} catch (Exception ignored) {
		}
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return btn;
	}

	private String fmtPrice(int price) {
		return NumberFormat.getNumberInstance(Locale.KOREA).format(price) + "원";
	}
}