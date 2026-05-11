package search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box; // Box 임포트 누락 수정
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI; // UI 임포트 추가

import DTO.Item;
import DTO.MyData;
import frame.FrameBase;
import frame.FrameItemInfo;

public class FrameSearchResult extends JPanel {
	private static final long serialVersionUID = 1L;
	// [디자인 테마 ] 일관된 UI를 위한 색상 상수를 정의합니다.
	private static final Color PAGE_BG = new Color(247, 244, 238);
	private static final Color CARD_BG = new Color(255, 252, 247);
	private static final Color ACCENT_COLOR = new Color(122, 31, 43);
	private static final Color TEXT_PRIMARY = new Color(43, 43, 43);
	private static final Color TEXT_SECONDARY = new Color(108, 99, 92);

	public FrameSearchResult(String keyword, MyData d) {
		// 패널 기본
		setBackground(PAGE_BG);
		setLayout(new BorderLayout());
		setSize(600, 800);

		ArrayList<Item> results = SearchLogic.getSearchResult(keyword);

		add(createHeader(keyword, results.size()), BorderLayout.NORTH);
		add(createScrollList(results, d), BorderLayout.CENTER);
	}

	// 검색 결과 제목 개수 표시 하는 헤더 영역 생성
	private JPanel createHeader(String keyword, int count) {
		JPanel header = new JPanel(null);
		header.setPreferredSize(new Dimension(600, 100));
		header.setBackground(PAGE_BG);
		// 메인 "검색 결과"
		JLabel titleLabel = new JLabel("검색 결과", SwingConstants.CENTER);
		titleLabel.setBounds(0, 25, 600, 40);
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		titleLabel.setForeground(TEXT_PRIMARY);
		header.add(titleLabel);
		// 검색어 및 결과 개수 요약
		JLabel resultSummary = new JLabel("'" + keyword + "' 검색결과 (" + count + ")");
		resultSummary.setBounds(25, 70, 400, 25);
		resultSummary.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		resultSummary.setForeground(TEXT_SECONDARY);
		header.add(resultSummary);

		return header;
	}

	private JScrollPane createScrollList(ArrayList<Item> results, MyData d) {
		JPanel listPanel = new JPanel();
		// 세로로 아이템 쌓기 위해 BoxLayout 사용
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(PAGE_BG);
		listPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

		// 품절 상품을 맨 아래로 정렬
		results.sort((a, b) -> Boolean.compare(a.getQuantity() == 0, b.getQuantity() == 0));

		if (results.isEmpty()) {
			// 검색 결과가 없을 떄의 화면 처리
			JLabel emptyLabel = new JLabel("검색 결과가 없습니다.", SwingConstants.CENTER);
			emptyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
			emptyLabel.setForeground(TEXT_SECONDARY);
			emptyLabel.setBorder(new EmptyBorder(100, 0, 0, 0));
			listPanel.add(emptyLabel);
		} else {
			// 아이템 카드 생성 및 간격 조절
			for (Item item : results) {
				listPanel.add(createResultCard(item, d));
				listPanel.add(Box.createVerticalStrut(15));
			}
		}

		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(PAGE_BG);

		// 스크롤바 디자인 적용
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(180, 170, 160);
				this.trackColor = PAGE_BG;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			// 스크롤바 너비 및 휠 속도 설정
			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}
		});

		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		return scrollPane;
	}

	// 개별 상품 정보 담은 카드 형태 버튼 생성
	private JButton createResultCard(Item item, MyData d) {
		JButton card = new JButton();
		card.setLayout(new BorderLayout(15, 0));
		card.setBackground(CARD_BG);
		card.setMaximumSize(new Dimension(540, 110));
		card.setPreferredSize(new Dimension(540, 110));
		// 테두리와 내부 여백 설정
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 225, 215), 1),
				new EmptyBorder(10, 10, 10, 10)));
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// 1. 이미지 영역 (왼쪽 )
		JLabel imgLbl = new JLabel();
		imgLbl.setPreferredSize(new Dimension(90, 90));
		imgLbl.setOpaque(true);
		imgLbl.setBackground(new Color(245, 240, 235));
		imgLbl.setHorizontalAlignment(JLabel.CENTER);
		try {
			ImageIcon icon = new ImageIcon(item.getMainImg());
			Image img = icon.getImage().getScaledInstance(85, 85, Image.SCALE_SMOOTH);
			imgLbl.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			imgLbl.setText("No Img");
		}
		card.add(imgLbl, BorderLayout.WEST);
		// 2. 정보 영역 (중앙 :상품,종류 ,재고 상태 )
		JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 3));
		infoPanel.setOpaque(false);

		JLabel nameLbl = new JLabel(item.getName());
		nameLbl.setFont(new Font("맑은 고딕", Font.BOLD, 17));
		nameLbl.setForeground(TEXT_PRIMARY);

		JLabel kindLbl = new JLabel(item.getKind());
		kindLbl.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		kindLbl.setForeground(ACCENT_COLOR);

		JLabel stockLbl = createStockLabel(item.getQuantity());

		infoPanel.add(nameLbl);
		infoPanel.add(kindLbl);
		infoPanel.add(stockLbl);
		card.add(infoPanel, BorderLayout.CENTER);

		// 3. 가격 영역 (오른쪽)
		JLabel priceLbl = new JLabel(String.format("%,d원", item.getPrice()), SwingConstants.RIGHT);
		priceLbl.setFont(new Font("Dialog", Font.BOLD, 18));
		priceLbl.setForeground(TEXT_PRIMARY);
		card.add(priceLbl, BorderLayout.EAST);
		// 마우스 오버 시 색상 변경 효과
		card.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				card.setBackground(new Color(250, 245, 235));
			}

			public void mouseExited(MouseEvent e) {
				card.setBackground(CARD_BG);
			}
		});
		// 클릭 시 상세 정보 페이지 이동
		card.addActionListener(e -> FrameBase.getInstance(new FrameItemInfo(item, d)));

		return card;
	}

	// 재고 수량에 따라 다른 텍스트와 색상 라벨 반환
	private JLabel createStockLabel(int quantity) {
		String text;
		Color color;

		if (quantity == 0) {
			text = "품절";
			color = new Color(115, 115, 115);
		} else if (quantity < 3) {
			text = quantity + "병 남음";
			color = new Color(196, 69, 61);
		} else if (quantity < 8) {
			text = quantity + "병 남음";
			color = new Color(220, 146, 31);
		} else {
			text = "재고 있음";
			color = new Color(36, 108, 145);
		}

		JLabel badge = new JLabel(text);
		badge.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		badge.setForeground(color);
		return badge;
	}
}