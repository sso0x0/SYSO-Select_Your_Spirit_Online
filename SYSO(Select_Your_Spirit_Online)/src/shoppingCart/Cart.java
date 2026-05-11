package shoppingCart;

import java.util.HashMap;
import java.util.Map;

import DTO.Item;

// 장바구니 추가, 삭제, 총 금액 계산 클래스
public class Cart {

	// 상품 리스트 생성
	private Map<Item, CartItem> cartList = new HashMap();

	// 장바구니 상품 확인용
	private CartItem findItem(int itemId) {
		for (Map.Entry<Item, CartItem> e : cartList.entrySet()) {
			CartItem ci = e.getValue(); // value 추출
			if (ci.getItem().getId() == itemId) {
				return ci;
			}
		}
		return null;
	}

	// 상품 수량 추가 (상품이 없을 경우, 재고보다 많을 경우 false 처리, 그 외는 true 처리)
	public boolean addItem(Item item, int quantity) {
		if (quantity <= 0)
			return false; // 상품이 없을 경우

		CartItem existItem = findItem(item.getId());

		// 상품이 있을 경우 수량 증가, 없으면 새로운 상품 추가
		if (existItem != null) {
			int newQty = existItem.getQuantity() + quantity;
			// 재고보다 더 많은 상품을 담으려 할 경우
			if (newQty > item.getQuantity()) {
				return false;
			}
			existItem.addQuantity(quantity);
		} else {
			if (quantity > item.getQuantity()) {
				return false;
			}
			cartList.put(item, new CartItem(item, quantity));
		}
		return true;
	}

	// 상품 삭제
	public void removeItem(int itemId) {
		for (Map.Entry<Item, CartItem> e : cartList.entrySet()) {
			if (e.getValue().getItem().getId() == itemId) { // 키 값을 비교해 추출
				cartList.remove(e.getKey());
				return;
			}
		}
	}

	// 수량 증가
	public void addQuantity(int itemId, int amount) {
		CartItem target = findItem(itemId);
		if (target != null) {
			int newQty = target.getQuantity() + amount;
			if (newQty > target.getItem().getQuantity()) {
				return;
			}
			target.addQuantity(amount);
		}
	}

	// 수량 감소
	public void minusQuantity(int itemId, int amount) {
		CartItem target = findItem(itemId);
		if (target != null) {
			target.minQuantity(amount);
			if (target.getQuantity() <= 0) {
				removeItem(itemId);
			}
		}
	}

	// 수량 직접 수정
	public void updateQuantity(int itemId, int quantity) {
		CartItem target = findItem(itemId);

		if (target != null) {
			if (quantity <= 0) {
				removeItem(itemId);
			} else if (quantity > target.getItem().getQuantity()) {
			} else {
				target.setQuantity(quantity);
			}
		}
	}

	// 총 금액 계산
	public int getTotalPrice() {
		int total = 0;
		for (CartItem ci : cartList.values()) {
			total += ci.getTotal();
		}
		return total;
	}

	// 장바구니 비움
	public void clearCart() {
		cartList.clear();
	}

	// 장바구니 목록 조회
	public Map<Item, CartItem> getCartList() {
		return cartList;
	}

}