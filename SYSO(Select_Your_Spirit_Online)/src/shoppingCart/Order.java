package shoppingCart;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import DTO.Item;

public class Order {

	// 주문 1건의 정보를 담음
	public static class OrderRecord {

		// 고정값
		private final int orderId; // 주문 고유 번호 (자동 증가)
		private final Map<Item, CartItem> cartSnapshot; // 결제 시점의 장바구니 상태
		private final int totalPrice; // 결제 총 금액
		private final String orderTime; // 주문 시각
		private final Payment paymentMethod; // 결제 수단
		// 변경 O
		private OrderStatus status; // 현재 주문 상태

		public OrderRecord(int orderId, Map<Item, CartItem> cartSnapshot, int totalPrice, Payment paymentMethod) {
			this.orderId = orderId;
			this.cartSnapshot = cartSnapshot;
			this.totalPrice = totalPrice;
			this.paymentMethod = paymentMethod;
			this.status = OrderStatus.PENDING; // 생성 시 초기 상태는 결제대기
			this.orderTime = LocalDateTime.now() // 생성 시각을 문자열로 포맷해서 저장
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}

		public int getOrderId() {
			return orderId;
		}

		public Map<Item, CartItem> getCart() {
			return cartSnapshot;
		}

		public int getTotalPrice() {
			return totalPrice;
		}

		public String getOrderTime() {
			return orderTime;
		}

		public Payment getPayment() {
			return paymentMethod;
		}

		public OrderStatus getStatus() {
			return status;
		}

		public void setStatus(OrderStatus s) {
			this.status = s;
		}

		// 주문 1건을 보기 좋게 출력
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("주문번호: ").append(orderId).append(" ===\n");
			sb.append("주문시간: ").append(orderTime).append("\n");
			sb.append("결제수단: ").append(paymentMethod.getLabel()).append("\n");
			sb.append("주문상태: ").append(status.getLabel()).append("\n");
			sb.append("상품목록:\n");
			sb.append("총 금액: ").append(totalPrice).append("원\n");
			return sb.toString();
		}
	}

	// Order 클래스
	private static int orderCounter = 1; // 주문번호 1번부터 자동 증가
	private final ArrayList<OrderRecord> orderHistory = new ArrayList<>(); // 지금까지의 주문 내역 목록

	// 결제(주문 생성)
	// 1. 장바구니 비어있으면 결제 불가
	// 2. 재고 수량 체크 후 차감
	// 3. OrderRecord 생성 → 상태 COMPLETED로 변경 → 내역에 저장
	// 4. 장바구니 초기화
	// 장바구니 비어있음 결제 불가
	public OrderRecord placeOrder(Cart cart, Payment payment) {
		// 장바구니에 상품이 없을 경우 결제 불가
		if (cart.getCartList().isEmpty()) {
			return null;
		}

		// 재고 차감: 장바구니의 각 상품 수량만큼 Item의 재고를 줄임
		for (CartItem ci : cart.getCartList().values()) {
			Item item = ci.getItem();
			int remain = item.getQuantity() - ci.getQuantity();
			if (remain < 0) {
				// 재고보다 많이 담겨 있으면 결제 중단
				return null;
			}
			item.setQuantity(remain); // 재고 차감 적용
		}

		// 주문 기록 생성
		// Map.copyOf()로 현재 장바구니 상태를 복사해 스냅샷으로 저장
		// 이후 장바구니가 바뀌어도 주문 내역은 유지됨
		OrderRecord record = new OrderRecord(orderCounter++, Map.copyOf(cart.getCartList()), cart.getTotalPrice(),
				payment);
		record.setStatus(OrderStatus.COMPLETED); // 결제 성공
		orderHistory.add(record); // 주문 내역에 추가

		cart.clearCart(); // 결제 완료 후 장바구니 비우기
		return record;
	}

	public void cancelOrder(int orderId) {
		OrderRecord record = findOrder(orderId); // 주분 번호용
		// 주문 내역이 없을 경우
		if (record == null) {
			return;
		}
		// 이미 취소된 주문일 경우
		if (record.getStatus() == OrderStatus.CANCELLED) { // 결제 취소
			return;
		}

		// 주문 취소
		// 재고 복구: 취소된 주문의 상품 수량만큼 다시 Item 재고에 더해줌
		for (CartItem ci : record.getCart().values()) {
			Item item = ci.getItem();
			item.setQuantity(item.getQuantity() + ci.getQuantity());
		}

		record.setStatus(OrderStatus.CANCELLED); // 결제 취소
	}

	// 주문번호로 단일 주문 조회, 없으면 null
	public OrderRecord findOrder(int orderId) {
		for (OrderRecord r : orderHistory) {
			if (r.getOrderId() == orderId)
				return r;
		}
		return null;
	}

	// 전체 주문 내역 출력
	public void printAllOrders() {
		// 주문 내역 X일 경우
		if (orderHistory.isEmpty()) {
			return;
		}
		// 주문 내역 O일 경우
		for (OrderRecord r : orderHistory) {
			System.out.println(r);
		}
	}

	// 주문 내역 전체 리스트 반환
	public Map<String, List<OrderRecord>> getOrderHistoryByDate() {
		return orderHistory.stream()
				.collect(Collectors.groupingBy(r -> r.getOrderTime().substring(0, 10), LinkedHashMap::new, // 삽입 순서 유지
						Collectors.toList()));
	}
}
