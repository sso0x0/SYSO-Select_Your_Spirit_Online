package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

import DTO.MyData;
import DTO.Item;
import shoppingCart.Cart;
import shoppingCart.Order;

// 상품 상세 화면 
public class FrameItemInfo extends JPanel {

	private static final long serialVersionUID = 1L;

	// 전체 페이지 배경색 (따뜻한 아이보리)
	private static final Color PAGE_BACKGROUND = new Color(247, 244, 238);
	// 상품 정보 카드 배경색
	private static final Color CARD_BACKGROUND = new Color(255, 252, 247);
	// 상품 대표 이미지 영역 배경색
	private static final Color HERO_BACKGROUND = new Color(236, 229, 219);
	// 구매 버튼 기본 색상
	private static final Color ACCENT_COLOR1 = new Color(85, 65, 55);
	// 장바구니 버튼 기본 색상
	private static final Color ACCENT_COLOR2 = new Color(120, 80, 60);
	// 구매 버튼 호버 색상
	private static final Color ACCENT_HOVER1 = new Color(96, 45, 38);
	// 장바구니 버튼 호버 색상
	private static final Color ACCENT_HOVER2 = new Color(95, 60, 45);
	// 품절 상태 버튼/배지 색상
	private static final Color SOLDOUT_COLOR = new Color(120, 120, 120);
	// 본문 주요 텍스트 색상
	private static final Color TEXT_PRIMARY = new Color(43, 43, 43);
	// 보조 텍스트 색상 (카테고리, 라벨 등)
	private static final Color TEXT_SECONDARY = new Color(108, 99, 92);
	// 구분선 색상
	private static final Color DIVIDER_COLOR = new Color(219, 210, 200);

	// 현재 로그인된 사용자 데이터
	protected MyData d;

	public FrameItemInfo(Item item, Cart cart, Order order, MyData d) {
		this.d = d;
		setBackground(PAGE_BACKGROUND);
		setLayout(null);
		setSize(600, 800);

		// 상단 로고
		JLabel titleLabel = new JLabel();
		titleLabel.setBounds(230, 5, 140, 70);
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 30));
		titleLabel.setForeground(TEXT_SECONDARY);
		add(titleLabel);

		try {
			// 로고 이미지를 130×80 크기로 축소하여 표시
			ImageIcon icon = new ImageIcon("ui_img/SYSOLogo.png");
			Image img = icon.getImage().getScaledInstance(130, 80, Image.SCALE_SMOOTH);
			titleLabel.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			// 이미지 로드 실패 시 텍스트로 대체
			titleLabel.setText("NO IMG");
		}

		// 홈버튼: 클릭 시 메인 화면으로 이동
		ImageIcon homeIcon = new ImageIcon(
				new ImageIcon("ui_img/home.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		ImageIcon homePressedIcon = new ImageIcon(
				new ImageIcon("ui_img/home_pressed.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		JButton btnHome = new JButton(homeIcon);
		btnHome.setPressedIcon(homePressedIcon);
		btnHome.setOpaque(false);
		btnHome.setContentAreaFilled(false);
		btnHome.setBorderPainted(false);
		btnHome.setFocusPainted(false);
		btnHome.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnHome.setBounds(550, 25, 30, 30);
		btnHome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameBase.getInstance(new FrameMain(d));
			}
		});
		add(btnHome);

		// 뒤로가기 버튼: 클릭 시 이전 화면으로 돌아감
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

		// 상품 대표 이미지
		JLabel heroImage = createImageLabel(item);
		heroImage.setBounds(0, 80, 600, 380);
		add(heroImage);

		// 상품 정보 카드
		JLabel infoCard = new JLabel();
		infoCard.setBounds(0, 470, 600, 270);
		infoCard.setBackground(CARD_BACKGROUND);
		infoCard.setOpaque(true);
		infoCard.setLayout(null);
		infoCard.setBorder(BorderFactory.createLineBorder(new Color(231, 223, 214), 1));
		add(infoCard);

		// 카테고리 텍스트
		JLabel categoryValue = new JLabel("Category  |  " + safeText(item.getKind(), "Premium Selection"));
		categoryValue.setBounds(20, 5, 220, 24);
		categoryValue.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		categoryValue.setForeground(TEXT_SECONDARY);
		infoCard.add(categoryValue);

		// 상품명
		JLabel nameLabel = new JLabel(item.getName());
		nameLabel.setBounds(17, 26, 320, 38);
		nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
		nameLabel.setForeground(TEXT_PRIMARY);
		infoCard.add(nameLabel);

		// 재고 상태 배지 (품절 / N병 남음 / 재고 있음)
		JLabel stockBadge = createStockBadge(item.getQuantity());
		stockBadge.setBounds(452, 5, 150, 25);
		infoCard.add(stockBadge);

		// 가격
		JLabel priceLabel = new JLabel(formatPrice(item.getPrice()), JLabel.RIGHT);
		priceLabel.setBounds(0, 25, 580, 40);
		priceLabel.setFont(new Font("Dialog", Font.BOLD, 25));
		priceLabel.setForeground(ACCENT_COLOR1);
		infoCard.add(priceLabel);

		// 카드 내부 수평 구분선
		JPanel divider = new JPanel();
		divider.setBackground(DIVIDER_COLOR);
		divider.setBounds(10, 70, 580, 1);
		infoCard.add(divider);

		// 상품 상세 설명 텍스트
		JTextArea txtInfo = new JTextArea(item.getItemInfo());
		txtInfo.setEditable(false);
		txtInfo.setLineWrap(true);
		txtInfo.setWrapStyleWord(true);
		txtInfo.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		txtInfo.setForeground(TEXT_PRIMARY);
		txtInfo.setBackground(CARD_BACKGROUND);
		txtInfo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

		// 상품 상세 설명 텍스트 스크롤 패널
		JScrollPane scrollPane = new JScrollPane(txtInfo);
		scrollPane.setBounds(10, 80, 585, 180);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(CARD_BACKGROUND);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// 커스텀 스크롤바 UI: 위아래 화살표 버튼 제거, 색상만 지정
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(180, 170, 160); // 스크롤 핸들 색상
				this.trackColor = new Color(225, 215, 200); // 트랙 배경 색상
			}

			// 위쪽 화살표 버튼 숨김
			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			// 아래쪽 화살표 버튼 숨김
			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			// 화면에 보이지 않는 크기 버튼
			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(10, 0));
				return button;
			}
		});

		// 세로 스크롤바 두께 8px, 스크롤 속도 설정
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		infoCard.add(scrollPane, BorderLayout.CENTER);
		infoCard.revalidate();
		infoCard.repaint();
		infoCard.add(scrollPane);

		// 하단 버튼 영역
		final int buttonY = 750;
		final int buttonHeight = 40;

		if (item.getQuantity() == 0) {
			// 재고 없음: 클릭 불가 라벨로 버튼 영역을 채워 품절임을 시각적으로 알림
			JLabel soldoutBuyLabel = soldoutButtonChange("구매불가");
			soldoutBuyLabel.setBounds(300, buttonY, 280, buttonHeight);
			add(soldoutBuyLabel);

			JLabel soldoutCartLabel = soldoutCartLabelChange();
			soldoutCartLabel.setBounds(20, buttonY, 272, buttonHeight);
			add(soldoutCartLabel);
		} else {
			// 재고 있음: 실제 동작 버튼을 배치
			JButton btnBuy = buyButtonChange(item);
			btnBuy.setBounds(300, buttonY, 280, buttonHeight);
			add(btnBuy);

			JButton btnCart = cartButtonChange(item);
			btnCart.setBounds(20, buttonY, 272, buttonHeight);
			add(btnCart);
		}
	}

	// 상품, 사용자 정보만으로 상세 화면을 생성하는 보조 생성자
	public FrameItemInfo(Item item, MyData d) {
		this(item, FrameMain.cart, FrameMain.order, d);
	}

	// UI 생성 메소드
	// 상품 대표 이미지를 담은 라벨 생성
	private JLabel createImageLabel(Item item) {
		JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
		imageLabel.setOpaque(true);
		imageLabel.setBackground(HERO_BACKGROUND);
		imageLabel.setBorder(BorderFactory.createLineBorder(new Color(216, 206, 195), 1));
		imageLabel.setIcon(new ImageIcon(item.getInfoImg()));
		return imageLabel;
	}

	// 재고 수량에 따라 다른 색상, 텍스트를 가진 리본 뱃지 생성
	private JLabel createStockBadge(int quantity) {
		StockRibbonLabel stockBadge = new StockRibbonLabel("");
		stockBadge.setFont(new Font("맑은 고딕", Font.BOLD, 14));

		if (quantity == 0) {
			stockBadge.setText("품절");
			stockBadge.setRibbonColor(new Color(115, 115, 115));
		} else if (quantity < 3) {
			stockBadge.setText(quantity + "병 남음");
			stockBadge.setRibbonColor(new Color(196, 69, 61));
		} else if (quantity < 8) {
			stockBadge.setText(quantity + "병 남음");
			stockBadge.setRibbonColor(new Color(220, 146, 31));
		} else {
			stockBadge.setText("재고 있음");
			stockBadge.setRibbonColor(new Color(36, 108, 145));
		}
		return stockBadge;
	}

	// 품절 시 구매 버튼 자리에 표시할 클릭 불가 라벨 생성
	private JLabel soldoutButtonChange(String text) {
		JLabel soldoutLabel = new JLabel(text, SwingConstants.CENTER);
		soldoutLabel.setBackground(SOLDOUT_COLOR);
		soldoutLabel.setForeground(Color.WHITE);
		soldoutLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		soldoutLabel.setOpaque(true);
		return soldoutLabel;
	}

	// 품절 시 장바구니 버튼 자리에 표시할 클릭 불가 라벨 생성
	private JLabel soldoutCartLabelChange() {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBackground(SOLDOUT_COLOR);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder());

		try {
			ImageIcon icon = new ImageIcon("ui_img/Cart30.png");
			Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
			label.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			// 이미지 로드 실패 시 텍스트로 대체
			label.setText("NO IMG");
			label.setForeground(Color.WHITE);
			label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		}
		return label;
	}

	// 재고가 있을 때 표시할 장바구니 버튼 생성
	private JButton cartButtonChange(Item item) {
		JButton btnCart = new JButton(new ImageIcon("ui_img/Cart30.png"));
		btnCart.setBackground(ACCENT_COLOR2);
		btnCart.setForeground(Color.WHITE);
		btnCart.setFont(new Font("Dialog", Font.BOLD, 24));
		btnCart.setFocusPainted(false);
		btnCart.setBorder(BorderFactory.createEmptyBorder());
		btnCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 마우스 호버 시 색상 변경
		btnCart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnCart.setBackground(ACCENT_HOVER2);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnCart.setBackground(ACCENT_COLOR2);
			}
		});

		// 클릭 시 수량 입력받음, 재고 초과 여부 확인 후 장바구니에 담음
		btnCart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 담을 수량 입력 다이얼로그
				String input = JOptionPane.showInputDialog(null,
						item.getName() + "\n몇 개 담으시겠습니까? (재고: " + item.getQuantity() + "개)", "장바구니 담기",
						JOptionPane.QUESTION_MESSAGE);

				if (input == null)
					return; // 취소 클릭 시 아무것도 하지 않음

				try {
					int qty = Integer.parseInt(input);
					if (qty <= 0)
						throw new NumberFormatException();

					boolean success = FrameMain.cart.addItem(item, qty);

					if (!success) {
						// 입력 수량이 현재 재고를 초과하는 경우
						JOptionPane.showMessageDialog(null, "재고 수량(" + item.getQuantity() + "개)을 초과하여 담을 수 없습니다.",
								"담기 실패", JOptionPane.WARNING_MESSAGE);
						return;
					}

					// 담기 완료 후 장바구니 화면 이동 여부 확인
					int res = JOptionPane.showConfirmDialog(null, "장바구니에 담았습니다.\n장바구니로 이동하시겠습니까?", "담기 완료",
							JOptionPane.YES_NO_OPTION);

					if (res == JOptionPane.YES_OPTION) {
						FrameBase.getInstance(new CartFrame(FrameMain.cart, FrameMain.order, d));
					}

				} catch (NumberFormatException ex) {
					// 숫자가 아닌 값 또는 0 이하 입력 시 오류 안내
					JOptionPane.showMessageDialog(null, "올바른 수량을 입력해 주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		return btnCart;
	}

	// 재고가 있을 때 표시할 결제하기 버튼 생성
	private JButton buyButtonChange(Item item) {
		JButton buyButton = new JButton("결제하기");
		buyButton.setBackground(ACCENT_COLOR1);
		buyButton.setForeground(Color.WHITE);
		buyButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		buyButton.setFocusPainted(false);
		buyButton.setBorder(BorderFactory.createEmptyBorder());
		buyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 마우스 호버 시 색상 변경
		buyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				buyButton.setBackground(ACCENT_HOVER1);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				buyButton.setBackground(ACCENT_COLOR1);
			}
		});

		// 클릭 시 해당 상품 1개를 장바구니에 자동으로 담음
		buyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 버튼 클릭 시점에 재고가 없을 경우
				if (item.getQuantity() == 0) {
					JOptionPane.showMessageDialog(null, "재고가 없어 구매하실 수 없습니다.", "SOLD OUT", JOptionPane.WARNING_MESSAGE);
					return;
				}

				// 상품 1개를 장바구니에 담는다.
				boolean success = FrameMain.cart.addItem(item, 1);

				if (!success) {
					// 재고 초과(0개인 경우 포함) 담기 실패
					JOptionPane.showMessageDialog(null, "재고 수량(" + item.getQuantity() + "개)을 초과하여 담을 수 없습니다.", "담기 실패",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// 담기 완료 후 장바구니 이동 여부 확인
				int res = JOptionPane.showConfirmDialog(null, item.getName() + " 1개를 담았습니다.\n장바구니로 이동할까요?", "담기 완료",
						JOptionPane.YES_NO_OPTION);

				if (res == JOptionPane.YES_OPTION) {
					FrameBase.getInstance(new CartFrame(FrameMain.cart, FrameMain.order, d));
				}
			}
		});

		return buyButton;
	}

	// 정수 가격을 0,000원 형식으로 표시
	private String formatPrice(int price) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
		return numberFormat.format(price) + "원";
	}

	// 문자열이 없을 경우 대체 문구 반환
	private String safeText(String value, String fallback) {
		if (value == null || value.isBlank())
			return fallback;
		return value;
	}

	// 리본 모양 재고 뱃지
	private static class StockRibbonLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		// 리본 배경 색상 (기본값: 파란색 계열)
		private Color ribbonColor = new Color(36, 108, 145);

		// 텍스트 지정 리본 라벨 생성
		private StockRibbonLabel(String text) {
			super(text, SwingConstants.CENTER);
			setForeground(Color.WHITE);
			setOpaque(false);
		}

		// 리본 배경 색상 변경
		private void setRibbonColor(Color ribbonColor) {
			this.ribbonColor = ribbonColor;
		}

		// 오각형 리본을 그림
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth() - 1;
			int h = getHeight() - 1;
			int notch = 10; // 왼쪽 삼각형 노치의 깊이(px)

			// 오각형 꼭짓점: 좌상→노치→좌하→우하→우상
			int[] xPoints = { 0, notch, 0, w, w };
			int[] yPoints = { 0, h / 2, h, h, 0 };

			g2.setColor(ribbonColor);
			g2.fillPolygon(xPoints, yPoints, 5);
			g2.dispose();

			super.paintComponent(g);
		}
	}
}