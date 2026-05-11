package frame;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FrameBase extends JFrame {

	// 전체 공통 기본 화면 크기
	private static final Dimension FRAME_SIZE = new Dimension(600, 800);

	// 싱글 프레임 재사용 구조 (내부에서 content pane만 교체함)
	private static FrameBase instance;
	
	// 뒤로가기 기능용 페이지 기록
	private static Deque<JPanel> history = new ArrayDeque<JPanel>();

	// 현재 표시 중인 페이지가 무엇인지 기록용
	private static JPanel currentPage;

	// 첫 접속 시 페이드오버레이 효과 구현
	private static class FadeOverlay extends JComponent { // 확인 필요_------------------------------

		// 화면 전환 직전의 "이전 화면"을 이미지로 캡처해 둔 값입니다.
		// 새 화면으로 content pane을 바꾼 뒤에도, 이 이미지를 위에 한 장 더 덮어서
		// "이전 화면이 서서히 사라지는" 효과를 만들 수 있습니다.
		// 이전 화면을 이미지로 캡처해둔 값
		private final BufferedImage previousScreen;

		// 현재 오버레이의 투명도입니다.
		// 1.0f = 이전 화면이 완전히 보임
		// 0.0f = 이전 화면이 완전히 사라짐
		// 
		private float alpha = 1.0f;

		FadeOverlay(BufferedImage previousScreen) {
			this.previousScreen = previousScreen;

			// 오버레이 자체는 배경을 따로 칠하지 않고,
			// 필요한 이미지 부분만 그리는 투명 컴포넌트로 사용합니다.
			setOpaque(false);
		}

		void setAlpha(float alpha) {
			// 외부 타이머가 프레임마다 이 값을 조금씩 줄여 주고,
			// repaint()를 통해 다시 그리게 만듭니다.
			this.alpha = alpha;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			// 캡처된 이전 화면이 없거나 이미 완전히 사라졌다면
			// 더 그릴 것이 없으므로 바로 종료합니다.
			if (previousScreen == null || alpha <= 0f) {
				return;
			}

			Graphics2D g2 = (Graphics2D) g.create();

			// "이전 화면 이미지"를 현재 alpha값만큼 반투명하게 그립니다.
			// 이 덕분에 실제 content pane은 이미 새 화면으로 바뀌어 있어도,
			// 사용자 눈에는 이전 화면이 천천히 걷히는 것처럼 보입니다.
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

			// 캡처 이미지를 오버레이 전체 크기에 맞게 다시 그립니다.
			// 여기서는 프레임 내부 전체를 덮는 용도이므로 (0,0)부터 꽉 채워 그립니다.
			g2.drawImage(previousScreen, 0, 0, getWidth(), getHeight(), null);
			g2.dispose();
		}
	} /////////////////////////////////////////

	public FrameBase(JPanel panel) {
		
		Toolkit tk = Toolkit.getDefaultToolkit();

		// 공통 프레임 제목
		setTitle("SYSO(Select Your Spirit Online)");

		// 생성 시점의 첫 화면을 적용
		applyContent(panel);

		// 프레임을 모니터 정중앙에 배치
		setLocation(((int) tk.getScreenSize().getWidth()) / 2 - getWidth() / 2,
				((int) tk.getScreenSize().getHeight()) / 2 - getHeight() / 2);

		// 창 닫기 동작, 화면 표시 설정
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	private void applyContent(JPanel panel) {

		// 모든 화면 패널 영역을 맞춤
		panel.setPreferredSize(FRAME_SIZE);

		// 프레임 내 메인 화면을 새 패널로 교체
		setContentPane(panel);

		// 프레임 바깥 크기를 다시 계산
		pack();

		// Swing 레이아웃 재계산 및 다시 그리기 요청입니다.
		// 화면 교체 직후 새 컴포넌트들이 정상 표시되도록 보장합니다.
		revalidate();
		repaint();
	}

	private BufferedImage captureCurrentContent() {

		// 현재 content pane이 화면으로 그릴 수 있는 Swing 컴포넌트인지 먼저 확인합니다.
		// JComponent가 아니면 paint()로 안전하게 캡처하기 어렵기 때문에 null을 반환합니다.
		if (!(getContentPane() instanceof JComponent)) {
			return null;
		}
		JComponent currentContent = (JComponent) getContentPane();

		// 아직 레이아웃이 끝나지 않아 폭/높이가 0이라면 캡처할 수 없습니다.
		// 이런 경우는 보통 최초 생성 직후이므로 페이드 전환 없이 바로 넘어가도 괜찮습니다.
		int width = currentContent.getWidth();
		int height = currentContent.getHeight();
		if (width <= 0 || height <= 0) {
			return null;
		}

		// 현재 화면을 그대로 "스샷"처럼 저장할 비트맵 버퍼를 만듭니다.
		// TYPE_INT_ARGB를 사용해서 투명도 정보도 함께 저장할 수 있습니다.
		BufferedImage snapshot = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = snapshot.createGraphics();

		// 현재 content pane을 실제 화면에 그리듯이 버퍼 위에 그대로 그립니다.
		// 이 결과가 나중에 glass pane 위에서 사라지는 "이전 화면"이 됩니다.
		currentContent.paint(g2);
		g2.dispose();
		return snapshot;
	}

	private void playFadeTransition(BufferedImage previousScreen) {

		// 이전 화면을 캡처하지 못했다면 페이드 전환 없이 새 화면만 다시 그립니다.
		if (previousScreen == null) {
			repaint();
			return;
		}

		// 캡처된 이전 화면을 덮어 그릴 오버레이를 준비합니다.
		FadeOverlay overlay = new FadeOverlay(previousScreen);

		// glass pane은 프레임의 가장 위 레이어에 올라가는 특수 컴포넌트입니다.
		// content pane 위에 덮어씌워 그리기에 딱 적합해서, 전환 효과용으로 사용합니다.
		setGlassPane(overlay);
		overlay.setVisible(true);

		// 전환 애니메이션 총 프레임 수입니다.
		// 값이 커질수록 더 오래, 더 세밀하게 사라집니다.
		final int totalFrames = 18;

		// 람다 안에서 값을 증가시켜야 해서 배열로 감싼 카운터를 사용합니다.
		final int[] currentFrame = { 0 };

		// 16ms 간격이면 대략 60fps 느낌의 전환이 됩니다.
		Timer timer = new Timer(16, null);
		timer.addActionListener(e -> {
			currentFrame[0]++;

			// 0.0 ~ 1.0 범위의 진행률입니다.
			float progress = currentFrame[0] / (float) totalFrames;

			// 단순 선형 감소 대신 easedAlpha를 사용합니다.
			// 1 - progress^2 형태라서
			// 초반엔 비교적 천천히 유지되고,
			// 후반으로 갈수록 더 자연스럽게 빠르게 사라집니다.
			float easedAlpha = 1f - (progress * progress);

			// 애니메이션이 끝나면 오버레이를 완전히 제거합니다.
			// 여기서 glass pane을 숨겨야 새 화면만 남습니다.
			if (progress >= 1f) {
				overlay.setAlpha(0f);
				timer.stop();
				overlay.setVisible(false);

				// 이후 전환에서 불필요한 이전 오버레이 상태가 남지 않도록
				// 비어 있는 패널로 다시 초기화해 둡니다.
				setGlassPane(new JPanel());
				return;
			}

			// 아직 전환 중이면 계산된 alpha값으로 다시 그립니다.
			overlay.setAlpha(easedAlpha);
		});
		timer.start();
	}

	public static void goBack() {
		// 프레임 자체가 아직 없거나,
		// 돌아갈 이전 페이지 기록이 하나도 없다면 아무 동작도 하지 않습니다.
		// 예를 들어 첫 화면에서는 뒤로가기가 의미 없기 때문에 그대로 종료합니다.
		if (instance == null || history.isEmpty()) {
			return;
		}

		// 가장 최근에 저장해 둔 이전 페이지를 꺼냅니다.
		// push/pop 구조이므로 "가장 마지막에 있던 페이지"로 돌아가게 됩니다.
		JPanel previousPage = history.pop();

		// 현재 보고 있는 페이지 참조도 함께 이전 페이지로 되돌립니다.
		// 이렇게 해야 다음 이동 시 history에 다시 올바른 currentPage가 쌓입니다.
		currentPage = previousPage;

		// 실제 프레임 내용도 이전 페이지로 교체합니다.
		// 뒤로가기는 일반 화면 복귀이므로 별도 페이드 없이 즉시 바꿉니다.
		instance.applyContent(previousPage);
	}

	public static FrameBase getInstance(JPanel panel) {
		// 기존 코드와의 호환을 위한 기본 진입점입니다.
		// 대부분의 페이지 이동은 이 메서드를 그대로 쓰면 되고,
		// 내부적으로는 "페이드 없이 즉시 전환" 모드로 연결합니다.
		return getInstance(panel, false);
	}

	public static FrameBase getInstance(JPanel panel, boolean useFadeTransition) {

		// 이 오버로드는 "이번 화면 전환에서만 페이드를 쓸지"를 호출부가 직접 결정할 수 있게 만든 버전입니다.
		// StartPage -> LoginPage 같은 특정 장면에서는 true를 넘겨 부드러운 전환을 쓰고,
		// 일반적인 메뉴 이동이나 상세 페이지 이동에서는 false로 두어 즉시 전환하게 만들 수 있습니다.

		// 최초 호출이면 프레임 자체가 없으므로 새로 생성합니다.
		if (instance == null) {
			instance = new FrameBase(panel);
			currentPage = panel;
		} else if (useFadeTransition) {

			// 새 화면으로 넘어가기 직전, 현재 화면이 존재하면 history에 먼저 저장합니다.
			// 이렇게 해 두면 페이드 전환으로 이동한 화면도 뒤로가기 대상에 포함됩니다.
			if (currentPage != null) {
				history.push(currentPage);
			}
			currentPage = panel;

			// 페이드 전환이 필요한 경우에만 현재 화면을 캡처합니다.
			// 이렇게 하면 일반적인 페이지 이동은 즉시 교체되고,
			// 특정 화면 전환에서만 부드러운 오버레이 효과를 사용할 수 있습니다.
			BufferedImage previousScreen = instance.captureCurrentContent();

			// 실제 content pane은 먼저 새 화면으로 교체합니다.
			// 즉 사용자 눈에는 새 화면이 이미 뒤에 준비되어 있고,
			// 그 위에 이전 화면 이미지가 덮여 있는 상태가 됩니다.
			instance.applyContent(panel);

			// 그 다음 이전 화면 오버레이를 서서히 지워서
			// 새 화면이 자연스럽게 드러나도록 합니다.
			instance.playFadeTransition(previousScreen);
		} else {

			// 일반 전환도 동일하게 history를 관리합니다.
			// 즉, 페이드 사용 여부와 상관없이 "이전 페이지 기록" 규칙은 같게 유지합니다.
			if (currentPage != null) {
				history.push(currentPage);
			}
			currentPage = panel;

			// 페이드 전환이 필요 없는 일반 화면 이동은
			// 기존처럼 즉시 content pane만 교체합니다.
			// 즉, FrameBase가 싱글 인스턴스를 유지하더라도
			// 모든 화면 이동에 전환 효과가 강제로 적용되지는 않도록 분기한 부분입니다.
			instance.applyContent(panel);
		}
		return instance;
	}

}
