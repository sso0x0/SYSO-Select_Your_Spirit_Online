package DTO;

import java.util.HashMap;
import java.util.jar.Attributes.Name;

// 회원 정보
public class MyData {

	String id; // 아이디
	String pw; // 비밀번호
	String name; // 이름
	String email; // 이메일
	String bir; // 생년월일
	String pon; // 전화번호

	public MyData(String id, String pw, String name, String email, String bir, String pon) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.email = email;
		this.bir = bir;
		this.pon = pon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBir() {
		return bir;
	}

	public void setBir(String bir) {
		this.bir = bir;
	}

	public String getPon() {
		return pon;
	}

	public void setPon(String pon) {
		this.pon = pon;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
