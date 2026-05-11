package user;

import java.util.HashMap;

import DTO.MyData;

public class UserMap {

	public static HashMap<String, MyData> umap = new HashMap<String, MyData>();

	// 테스트 로그인 유저, 삭제 예정
	/*
	 * static { MyData testUser = new MyData("test", "1234", "테스트유저",
	 * "test@test.com", "2000.01.01", "010-1234-5678"); umap.put("test", testUser);
	 * }
	 */
	public static MyData getUserInfoMap(String userId) {
		return umap.get(userId);
	}

	public UserMap(String userId, MyData m) {
		if (umap.get(userId) == null) {
			init(userId, m);
		}
	}

	private void init(String userId, MyData m) {
		umap.put(userId, m);
	}

}
