package shoppingCart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DTO.Item;

// 장바구니 수량 체크
public class CartItem {

	private Item item; // 상품 정보들
	private int quantity; // 상품 수량

	public CartItem(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	// 상품 수량 더하기
	public void addQuantity(int amount) {
		if (amount <= 0)
			return;
		quantity += amount;
	}

	// 상품 수량 빼기
	public void minQuantity(int amount) {
		if (amount <= 0)
			return;
		if (quantity - amount <= 0) {
			quantity = 0;
		} else {
			quantity -= amount;
		}
	}

	// 총 금액
	public int getTotal() {
		return item.getPrice() * quantity;
	}

	// 장바구니 아이템 존재 확인용
	public Item getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		if (quantity < 0)
			return;
		this.quantity = quantity;
	}

}
