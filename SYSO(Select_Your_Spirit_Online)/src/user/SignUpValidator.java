package user;

import java.time.LocalDate;

// 회원가입 유효성 검사 클래스
public class SignUpValidator {

	// 인스턴스 생성 방지
	private SignUpValidator() {
	}

	// 비밀번호 형식 검사 (영문 대소문자, 숫자, 특수기호 포함 9자 이상)
	public static boolean isValidPassword(String pw) {
		return pw.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{9,}$");
	}

	// 이메일 형식 검사 (계정, 도메인, 확장자 형식)
	public static boolean isValidEmail(String email) {
		return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
	}

	// 전화번호 형식 검사 (010으로 시작하는 11자리 숫자)
	public static boolean isValidPhone(String pon) {
		return pon.matches("\\d{11}") && pon.startsWith("010");
	}

	// 생년월일 유효성 검사 (8자리 숫자, 유효한 월/일, 만 18세 이상)
	public static String validateBirthday(String bir) {
		if (!bir.matches("\\d{8}"))
			return "* yyyymmdd 8자리 숫자로 입력하세요.";

		int year = Integer.parseInt(bir.substring(0, 4));
		int month = Integer.parseInt(bir.substring(4, 6));
		int day = Integer.parseInt(bir.substring(6, 8));

		if (month < 1 || month > 12)
			return "* 1~12월 사이여야 합니다.";

		int maxDay = maxDayOfMonth(year, month);
		if (day < 1 || day > maxDay)
			return "* " + month + "월은 최대 " + maxDay + "일까지 입력 가능합니다.";

		if (!isAdult(year, month, day))
			return "* 성인만 가입 가능합니다.";

		return null;
	}

	// 주어진 연도, 월의 최대 일수 반환
	private static int maxDayOfMonth(int year, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		case 2:
			boolean leap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
			return leap ? 29 : 28;
		default:
			return 0;
		}
	}

	// 금일 기준 만 18세 이상인지 확인
	private static boolean isAdult(int year, int month, int day) {
		return !LocalDate.now().isBefore(LocalDate.of(year, month, day).plusYears(18));
	}
}