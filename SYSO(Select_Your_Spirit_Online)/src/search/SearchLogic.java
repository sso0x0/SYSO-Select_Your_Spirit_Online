package search;

import java.util.ArrayList;

import DAO.ItemDAO;
import DTO.Item;

public class SearchLogic {
	/**
	 * @param keyword 사용자가 검색창에 입력한 단어
	 * @return 검색어에 맞는 아이템 리스트 (없으면 빈 리스트)
	 */
	public static ArrayList<Item> getSearchResult(String keyword) {
		ArrayList<Item> resultList = new ArrayList<>();

		// 검색어가 비어있으면 바로 빈 리스트 반환
		if (keyword == null || keyword.isEmpty())
			return resultList;

		// 전체 아이템을 돌면서 확인
		for (Item item : ItemDAO.itemList) {
			// 대소문자 무시, 공백 무시하고 포함 여부 확인 (예: '발베니' 검색 시 '발베니 12년' 찾기)
			String itemName = item.getName().toLowerCase().replace(" ", "");
			String searchKey = keyword.toLowerCase().replace(" ", "");

			if (itemName.contains(searchKey)) {
				resultList.add(item);
			}
		}
		return resultList;
	}
}