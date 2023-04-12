package ksmart.mybatis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.GoodsMapper;
import ksmart.mybatis.mapper.MemberMapper;

@Service
@Transactional
public class GoodsService {

	private final GoodsMapper goodsMapper;
	private final MemberMapper memberMapper;
	
	public GoodsService(GoodsMapper goodsMapper, MemberMapper memberMapper) {
		this.goodsMapper = goodsMapper;
		this.memberMapper = memberMapper;
	}
	
	public void modifyGoods(Goods goods) {
		goodsMapper.modifyGoods(goods);
	}
	
	public Goods getGoodsInfoByCode(String goodsCode) {
		Goods goodsInfo = goodsMapper.getGoodsInfoByCode(goodsCode);
		return goodsInfo;
	}
	
	public int addGoods(Goods goods) { //리턴데이터타입이 int인 addGoods 메서드 실행 매개변수는 Goods타입
		int result = goodsMapper.addGoods(goods); //goodsMapper에 있는 addGoods 메서드 호출하여 그 값을 result로 담는다
		return result;
	}
	
	public List<Member> getGoodsListBySeller(String[] checkArr, String searchValue){
		Map<String, Object> searchMap = null;
		
		if(checkArr != null) {			
			for(int i=0; i < checkArr.length; i++) {
				String searchKey = checkArr[i];
				switch (searchKey) {
				case "memberId":
					searchKey = "m.m_id";
					break;
				case "memberEmail":
					searchKey = "m.m_email";					
					break;
				default:
					searchKey = "g.g_name";					
					break;
				}
				checkArr[i] = searchKey;
			}
			searchMap = new HashMap<String, Object>();
			searchMap.put("checkArr", checkArr);
			searchMap.put("searchValue", searchValue);
		}
		
		List<Member> goodsListBySeller = memberMapper.goodsListBySeller(searchMap);
		return goodsListBySeller;
	}
	
	public List<Goods> getGoodsList(Map<String,Object> paramMap){	//Map 파라미터. List<Goods> 반환형
		List<Goods> goodsList = goodsMapper.getGoodsList(paramMap);	//Map 파라미터로 함수 호출
		return goodsList;
	}

	public void removeGoods(String goodsCode) {
		goodsMapper.removeOrderByGoodsCode(goodsCode);
		goodsMapper.removeGoodsByGoodsCode(goodsCode);
	}

	
}
