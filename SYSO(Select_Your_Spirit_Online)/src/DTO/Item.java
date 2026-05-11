package DTO;

import java.util.ArrayList;

// 상품
public class Item {

	int id; // 상품 인덱스
	String name; // 상품명
	String kind; // 상품 카테고리
	int price; // 상품 가격
	int quantity; // 상품 수량
	String itemInfo; // 상품 설명
	String mainImg; // 상품 이미지
	String infoImg; // 상품 상세 이미지

	public Item(int id, String name, String kind, int price, int quantity, String itemInfo, String mainImg,
			String infoImg) {
		this.id = id;
		this.name = name;
		this.kind = kind;
		this.price = price;
		this.quantity = quantity;
		this.itemInfo = itemInfo;
		this.mainImg = mainImg;
		this.infoImg = infoImg;

	}

	public String getMainImg() {
		return mainImg;
	}

	public void setMainImg(String mainImg) {
		this.mainImg = mainImg;
	}

	public String getInfoImg() {
		return infoImg;
	}

	public void setInfoImg(String infoImg) {
		this.infoImg = infoImg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(String itemInfo) {
		this.itemInfo = itemInfo;
	}

}
