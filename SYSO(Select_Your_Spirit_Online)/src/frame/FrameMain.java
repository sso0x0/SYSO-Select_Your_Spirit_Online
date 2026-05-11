package frame;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

import DAO.ItemDAO;
import DTO.MyData;
import DTO.Item;
import search.FrameSearchResult;
import shoppingCart.Cart;
import shoppingCart.Order;

// 쇼핑몰 메인 화면
public class FrameMain extends JPanel {
	private JPanel contentArea; // 상품 리스트가 실제로 표시되는 가변 영역
	private ItemDAO dao; // 데이터베이스(또는 리스트)에서 상품 정보를 가져오는 객체
	private MyData loginUser; // 로그인용

	// 디자인 일관성을 위한 메인 컬러 테마 설정
	private final Color mainBgColor = new Color(225, 215, 200); // 연한 베이지 (배경)
	private final Color pointColor = new Color(85, 65, 55); // 짙은 갈색 (포인트/텍스트)

	private ArrayList<JButton> categoryButtons = new ArrayList<>();

	public static Cart cart = new Cart();
	public static Order order = new Order();

	// 데이터 받게끔 추가
	public FrameMain(MyData d) {
		this.loginUser = d;

		dao = new ItemDAO(); // 데이터 접근 객체 초기화

		// 메인 패널 기본 설정
		setBounds(0, 0, 600, 800);
		setLayout(new BorderLayout()); // 위/중앙/아래 구조
		setBackground(mainBgColor);

		// 상단 패널(로고, 마이페이지, 검색창, 메뉴) 추가
		add(createTopPanel(), BorderLayout.NORTH);

		// 상품 리스트가 들어갈 영역 초기화 및 CENTER 영역에 추가
		contentArea = new JPanel(new BorderLayout());
		contentArea.setBackground(mainBgColor);
		add(contentArea, BorderLayout.CENTER);

		// 초기 실행 시 전체 카테고리의 상품 리스트를 보여줌
		showProductList("전체");
	}

	// 로고, 마이페이지, 장바구니 버튼을 포함하는 패널 좌표를 직접 지정하는 null 레이아웃을 사용해 배치
	private JPanel myPageAndCart() {
		JPanel top = new JPanel();
		top.setLayout(null); // 컴포넌트의 위치(setBounds)를 직접 지정하기 위해 null 사용
		top.setBackground(mainBgColor);
		top.setPreferredSize(new Dimension(580, 80));

		// 로고 이미지 배치
		JLabel titleLogo = new JLabel();
		titleLogo.setBounds(230, 5, 140, 75);

		try {
			ImageIcon icon = new ImageIcon("ui_img/SYSOLogo.png");
			// 부드러운 이미지 스케일링 적용
			Image img = icon.getImage().getScaledInstance(130, 80, Image.SCALE_SMOOTH);
			titleLogo.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			titleLogo.setText("NO IMG");
		}

		int headerY = 20; // 상단 버튼들의 세로 기준 위치

		// 마이페이지 아이콘과 버튼
		ImageIcon myPageIcon = new ImageIcon(
				new ImageIcon("ui_img/mypage.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		JButton iconMypage = new JButton(myPageIcon);
		iconMypage.setBounds(470, headerY, 30, 30);
		iconMypage.setContentAreaFilled(false); // 버튼 배경 투명하게
		iconMypage.setBorderPainted(false); // 버튼 테두리 제거
		iconMypage.setFocusPainted(false); // 클릭 시 생기는 포커스 테두리 제거
		iconMypage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameBase.getInstance(new MyPage(cart, order, loginUser));
			}
		});

		JLabel mypage = new JLabel("마이페이지");
		mypage.setBounds(462, 40, 92, 26);
		mypage.setFont(new Font("맑은 고딕", Font.PLAIN, 9));

		// 장바구니 아이콘과 버튼
		ImageIcon cartIcon = new ImageIcon(
				new ImageIcon("ui_img/cart30_2.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		JButton iconCart = new JButton(cartIcon);
		iconCart.setBounds(523, headerY, 26, 26);
		iconCart.setContentAreaFilled(false);
		iconCart.setBorderPainted(false);
		iconCart.setFocusPainted(false);
		iconCart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameBase.getInstance(new CartFrame(FrameMain.cart, FrameMain.order, loginUser));
			}
		});

		JLabel cart = new JLabel("장바구니");
		cart.setBounds(520, 40, 70, 26);
		cart.setFont(new Font("맑은 고딕", Font.PLAIN, 9));
		cart.setHorizontalAlignment(SwingConstants.LEFT);

		top.add(titleLogo);
		top.add(iconMypage);
		top.add(mypage);
		top.add(iconCart);
		top.add(cart);

		return top;
	}

	// 헤더 영역: 상단 바, 검색창, 카테고리 메뉴
	private JPanel createTopPanel() {
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS)); // 위에서 아래로 순서대로 배치
		top.setBackground(mainBgColor);
		top.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // 좌우 여백

		// 검색 하단에 점선
		JPanel search = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// 점선 스타일 설정 (두께 3, 10픽셀 간격)
				float[] dash = { 10f, 10f };
				g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3f, dash, 0f));
				g2.setColor(Color.WHITE);

				int y = getHeight() - 1; // 패널 맨 하단 좌표
				g2.drawLine(20, y, getWidth() - 20, y); // 하단에 가로 점선 긋기
				g2.dispose();
			}
		};
		search.setBackground(mainBgColor);
		search.setPreferredSize(new Dimension(580, 55));
		search.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		// 검색 입력 필드 (둥근 테두리 적용)
		JTextField searchField = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(pointColor);
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
			}
		};
		searchField.setBounds(110, 8, 360, 36);
		searchField.setOpaque(false); // 배경 투명화 (모서리 깨짐 방지)
		searchField.setBackground(Color.WHITE);
		searchField.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 85)); // 좌우 텍스트 여백

		// 검색 버튼 (둥근 테두리 적용)
		JButton searchBtn = new JButton("검색") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.setColor(pointColor);
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
				super.paintComponent(g);
			}
		};

		searchBtn.setBounds(400, 8, 70, 36);
		searchBtn.setContentAreaFilled(false);
		searchBtn.setBorderPainted(false);
		searchBtn.setFocusPainted(false);
		searchBtn.setBackground(pointColor);
		searchBtn.setForeground(Color.WHITE);
		searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		searchBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));

		search.add(searchBtn);
		search.add(searchField);

		// 검색 버튼 동작
		searchBtn.addActionListener(e -> {
			String keyword = searchField.getText().trim();
			if (!keyword.isEmpty()) {
				contentArea.removeAll();
				contentArea.add(new FrameSearchResult(keyword, loginUser), BorderLayout.CENTER);
				contentArea.revalidate();
				contentArea.repaint();
			}
		});

		// 엔터키로 검색
		searchField.addActionListener(e -> searchBtn.doClick());

		// 카테고리 메뉴 영역: 전체보기와 DB에서 가져온 종류별 버튼을 생성
		JPanel menu = new JPanel(new BorderLayout(5, 0));
		menu.setBackground(mainBgColor);
		menu.setPreferredSize(new Dimension(580, 50));

		// 전체보기 버튼: 둥근 모서리 커스텀 디자인
		JButton allBtn = new JButton("전체보기") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.setColor(pointColor);
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
				super.paintComponent(g);
			}
		};

		allBtn.setContentAreaFilled(false);
		allBtn.setBorderPainted(false);
		allBtn.setFocusPainted(false);
		allBtn.setPreferredSize(new Dimension(85, 70));
		allBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));

		// 초기 상태: 전체보기 버튼이 선택된 상태로 세팅
		allBtn.setBackground(pointColor);
		allBtn.setForeground(Color.WHITE);

		// 리스트에 추가 및 이벤트 설정
		categoryButtons.add(allBtn);

		allBtn.addActionListener(e -> {
			setActiveButton(allBtn); // 상태 업데이트 및 UI 갱신
			showProductList("전체");
		});

		// 중복 제거를 위해 Set을 사용하여 DB에서 카테고리(Kind) 목록 추출
		Set<String> categorySet = new LinkedHashSet<String>();
		for (Item item : ItemDAO.itemList) {
			if (item.getKind() != null && !item.getKind().isBlank()) {
				categorySet.add(item.getKind());
			}
		}

		// 추출된 카테고리별로 버튼 생성하여 그리드 배치
		JPanel subBtns = new JPanel(new GridLayout(1, 5, 5, 0));
		subBtns.setBackground(mainBgColor);

		for (String cat : categorySet) {
			JButton btn = new JButton(cat) {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(getBackground());
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
					g2.setColor(pointColor);
					g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
					g2.dispose();
					super.paintComponent(g);
				}
			};
			btn.setContentAreaFilled(false);
			btn.setBorderPainted(false);
			btn.setFocusPainted(false);
			btn.setFont(new Font("맑은 고딕", Font.BOLD, 11));

			btn.setBackground(Color.WHITE);
			btn.setForeground(pointColor);

			categoryButtons.add(btn); // 전역 리스트에 추가

			btn.addActionListener(e -> {
				setActiveButton(btn); // 상태 업데이트 및 UI 갱신
				showProductList(cat);
			});

			subBtns.add(btn);
		}

		menu.add(allBtn, BorderLayout.WEST);
		menu.add(subBtns, BorderLayout.CENTER);

		// 상단 패널에 조립한 구역들을 순서대로 추가
		top.add(myPageAndCart(), BorderLayout.NORTH);
		top.add(search);
		top.add(Box.createVerticalStrut(15)); // 간격 조절용 투명 공간
		top.add(menu);

		return top;
	}

	private void setActiveButton(JButton clickedBtn) {
		for (JButton btn : categoryButtons) {
			if (btn == clickedBtn) {
				// 선택된 버튼
				btn.setBackground(pointColor);
				btn.setForeground(Color.WHITE);
			} else {
				// 선택 해제된 버튼
				btn.setBackground(Color.WHITE);
				btn.setForeground(pointColor);
			}
			// 변경된 색상이 즉시 반영되도록 버튼을 다시 그림
			btn.repaint();
		}
	}

	// 카테고리에 따라 상품 리스트를 화면에 표시
	public void showProductList(String filter) {
		contentArea.removeAll(); // 기존에 표시되던 상품들 싹 지우기

		// 가로 4열 고정, 세로는 무제한(0)인 그리드 레이아웃
		JPanel bottomPanel = new JPanel(new GridLayout(0, 4, 10, 20));
		bottomPanel.setBackground(mainBgColor);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// 전체 상품 리스트를 돌면서 조건에 맞는 상품만 필터링
		ArrayList<Item> list = ItemDAO.itemList;
		ArrayList<Item> filtered = new ArrayList<>();
		for (Item item : list) {
			if (filter.equals("전체") || item.getKind().equals(filter)) {
				filtered.add(item);
			}
		}

		// 품절 상품을 맨 아래로 정렬
		filtered.sort((a, b) -> {
			boolean aOut = a.getQuantity() == 0;
			boolean bOut = b.getQuantity() == 0;
			return Boolean.compare(aOut, bOut); // false(재고있음)가 앞, true(품절)가 뒤
		});

		for (Item item : filtered) {
			bottomPanel.add(createProductCard(item));
		}

		// 그리드 패널이 위쪽부터 채워지도록 NORTH에 배치
		JPanel alignTop = new JPanel(new BorderLayout());
		alignTop.setBackground(mainBgColor);
		alignTop.add(bottomPanel, BorderLayout.NORTH);

		// 상품이 많을 경우를 대비해 스크롤 패널에 담기
		JScrollPane scroll = new JScrollPane(alignTop);
		scroll.setBorder(null);
		scroll.setBackground(mainBgColor);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// 모던 스크롤바 UI 커스텀 적용
		scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(180, 170, 160); // 얇고 연한 스크롤바 색상
				this.trackColor = mainBgColor; // 스크롤바 배경은 패널 배경과 통일
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			// 위아래 화살표 버튼 숨기기
			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(10, 0));
				return button;
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}
		});
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		contentArea.add(scroll, BorderLayout.CENTER);
		contentArea.revalidate(); // 레이아웃 재계산
		contentArea.repaint(); // 화면 다시 그리기
	}

	// 개별 상품 하나를 나타내는 카드형 버튼 (이미지, 이름, 가격, 재고 표시)
	private JButton createProductCard(Item item) {
		JButton card = new JButton();
		card.setLayout(null); // 내부 컴포넌트 자유 배치를 위해 null 레이아웃
		card.setBackground(new Color(255, 252, 247));
		card.setPreferredSize(new Dimension(130, 190));
		card.setOpaque(true);

		// 카드 클릭 시 해당 상품의 상세 정보 프레임으로 이동
		card.addActionListener(e -> {
			FrameBase.getInstance(new FrameItemInfo(item, loginUser));
		});

		// 상품 이미지 레이블
		JLabel imgLabel = new JLabel();
		imgLabel.setBounds(13, 5, 110, 110);
		imgLabel.setOpaque(true);
		imgLabel.setBackground(Color.WHITE);
		imgLabel.setBorder(BorderFactory.createLineBorder(pointColor, 1));
		imgLabel.setHorizontalAlignment(JLabel.CENTER);

		try {
			ImageIcon icon = new ImageIcon(item.getMainImg());
			// 부드러운 이미지 스케일링 적용
			Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			imgLabel.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			imgLabel.setText("NO IMG");
		}

		// 상품명
		JLabel name = new JLabel(item.getName(), JLabel.CENTER);
		if (item.getName().length() > 12) {
			name.setFont(new Font("맑은 고딕", Font.BOLD, 11));
		} else {
			name.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		}
		name.setForeground(pointColor);
		name.setBounds(2, 120, 130, 20);

		// 가격 (천단위 콤마 포맷)
		JLabel price = new JLabel(String.format("%,d원", item.getPrice()), JLabel.CENTER);
		price.setFont(new Font("맑은 고딕", Font.BOLD, 11));
		price.setForeground(new Color(120, 80, 60));
		price.setBounds(0, 135, 130, 20);

		// 재고 상태 리본 배지
		JLabel stockBadge = createStockBadge(item.getQuantity());
		stockBadge.setBounds(35, 160, 100, 25);
		stockBadge.setFont(new Font("맑은 고딕", Font.BOLD, 10));

		card.add(imgLabel);
		card.add(name);
		card.add(price);
		card.add(stockBadge);

		return card;
	}

	// 재고 수량에 따라 리본 텍스트, 색상 결정
	private JLabel createStockBadge(int quantity) {
		StockRibbonLabel stockBadge = new StockRibbonLabel("");
		stockBadge.setFont(new Font("맑은 고딕", Font.BOLD, 14));

		if (quantity == 0) {
			stockBadge.setText("품절");
			stockBadge.setRibbonColor(new Color(115, 115, 115)); // 회색
		} else if (quantity < 3) {
			stockBadge.setText(quantity + "병 남음");
			stockBadge.setRibbonColor(new Color(196, 69, 61)); // 빨간색
		} else if (quantity < 8) {
			stockBadge.setText(quantity + "병 남음");
			stockBadge.setRibbonColor(new Color(220, 146, 31)); // 주황색
		} else {
			stockBadge.setText("재고 있음");
			stockBadge.setRibbonColor(new Color(36, 108, 145)); // 파란색
		}

		return stockBadge;
	}

	// 리본 모양의 배경을 직접 그리는 커스텀 JLabel
	private static class StockRibbonLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private Color ribbonColor = new Color(36, 108, 145);

		private StockRibbonLabel(String text) {
			super(text, SwingConstants.CENTER);
			setForeground(Color.WHITE);
			setOpaque(false); // 배경을 직접 그릴 것이므로 투명하게 설정
		}

		private void setRibbonColor(Color ribbonColor) {
			this.ribbonColor = ribbonColor;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth() - 1;
			int h = getHeight() - 1;
			int notch = 10; // 왼쪽 V자 파임 정도

			// 왼쪽이 파인 5각형(리본 모양)을 그리기 위한 좌표 설정
			int[] xPoints = { 0, notch, 0, w, w };
			int[] yPoints = { 0, h / 2, h, h, 0 };

			g2.setColor(ribbonColor);
			g2.fillPolygon(xPoints, yPoints, 5); // 다각형 채우기

			g2.dispose();
			super.paintComponent(g); // 텍스트를 그리기 위해 호출
		}
	}
}