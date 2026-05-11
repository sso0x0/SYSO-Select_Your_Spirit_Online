package shoppingCart;

// 결제 수단 관리: 신용카드, 간편결제, 계좌이체
public enum Payment {

	CREDIT_CARD("신용카드"), EASY_PAYMENT("간편 결제"), BANK_TRANSFER("계좌이체");

	private final String label;

	Payment(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}

// 주문 상태 관리: 결제 대기, 결제 완료, 취소됨
enum OrderStatus {
	PENDING("결제 대기"), COMPLETED("결제 완료"), CANCELLED("결제 취소");

	private final String label;

	OrderStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}