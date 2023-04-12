package ksmart.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.GoodsMapper;
import ksmart.mybatis.mapper.MemberMapper;
import ksmart.mybatis.service.GoodsService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

	private final GoodsService goodsService;
	private final MemberMapper memberMapper;
	private final GoodsMapper goodsMapper;
	
	public GoodsController(GoodsService goodsService, MemberMapper memberMapper, GoodsMapper goodsMapper) {
		this.goodsService = goodsService;
		this.goodsMapper = goodsMapper;
		this.memberMapper = memberMapper;
	}
	
	@PostMapping("/removeGoods")
	public String removeGoods(@RequestParam(name="goodsCode") String goodsCode
							 ,@RequestParam(name="memberId") String memberId
							 ,@RequestParam(name="memberPw") String memberPw
							 ,HttpSession session	//웹에 접속했을때부터 생성된 Httpsession 객체를 사용한다
							 ,RedirectAttributes reAttr) {		//// 해당 매개변수를 선언하면 RedirectAttributes 객체가 생성된다. 소멸 시점은 가비지 컬렉터가 알아서 하는거라 잘 몰름
		
		String memberLevel = (String) session.getAttribute("SLEVEL");
		boolean isDelete = true;
		String msg = "";
		if(memberLevel != null && "2".equals(memberLevel)) {
			isDelete = goodsMapper.isSellerByGoodsCode(memberId, goodsCode);	//특정 상품에 해당하는 판매자가 맞는지 조회
		}
		
		Member member = memberMapper.getMemberInfoById(memberId);	//ID에 해당하는 member정보 조회. 비밀번호 확인용 
		if(member != null) {	//id에 해당하는 member정보가 있으면
			String checkPw = member.getMemberPw();	
			if(!checkPw.equals(memberPw)) isDelete = false;		//입력한 checkPw가 memberPw와 같지 않다면 false
		}
		if(isDelete) {
			goodsService.removeGoods(goodsCode);   
			msg = "상품코드: "+ goodsCode + " 가 삭제되었습니다.";
		}else {
			msg = "상품코드: "+ goodsCode + " 가 삭제할 수 없습니다.";			
		}
		reAttr.addAttribute("msg", msg);	//redirect 재요청 url? 뒤에 파라미터 형식으로 msg 추가
											//http://localhost/goods/goodsList?msg=상품코드%3A+g056가+삭제할+수+없습니다.
		
		return "redirect:/goods/goodsList";
	}
	
	@GetMapping("/removeGoods")
	public String removeGoods(Model model
							 ,@RequestParam(name="goodsCode") String goodsCode) {	//List에서 삭제 버튼을 누를 시 매개변수로 goodsCode가 자동바인딩
		model.addAttribute("title", "상품삭제");
		model.addAttribute("goodsCode", goodsCode);	//List에서 전달된 매개변수 goodsCode 값을 걍 바로 삭제 화면에 꽂아줌
													//회원 아이디는 session값으로 처리
		return "goods/removeGoods";
	}
	
	@PostMapping("/modifyGoods")
	public String modifyGoods(Goods goods) {	//받은 상품 정보의 name과 value들이 매개변수 타입으로 설정한 Goods dto와 일치하므로 스프링부트가 알아서 알맞게 담아 전달
		goodsService.modifyGoods(goods);
		return "redirect:/goods/goodsList";
	}
	
	@GetMapping("/modifyGoods")
	public String modifyGoods(Model model	//th를 통해 view에 값을 전달할(그러나 화면단이 아닌 서버사이드 처리중) model 객체 설정
							 ,@RequestParam(name="goodsCode") String goodsCode) {	//list.html에서 수정할 goods의 코드를 매개변수로 가져옴
		
		Goods goodsInfo = goodsService.getGoodsInfoByCode(goodsCode);
		
		model.addAttribute("title", "상품수정");
		model.addAttribute("goodsInfo", goodsInfo);
		
		return "goods/modifyGoods";
	}
	
	@PostMapping("/addGoods") //화면에서 넘어온 요청 경로와 방식을 따라 매핑된 메서드 실행
	public String addGoods(Goods goods) { //스트링 데이터타입을 반환하는 addGoods 메서드를 실행. 매개변수 타입은 Goods 이며, #addGoodsForm이하 요소들의 name과 value값이 dto Goods의 필드명과 데이터 타입에 부합하기 때문에, Spring boot 측에서 자동으로 담아준다(단, getter, setter 필수). 즉, @RequestParam이 필요 없다.
		goodsService.addGoods(goods); //goodsService에 있는 addGoods에 goods 매개변수를 넣어서 메서드 호출
		return "redirect:/goods/goodsList"; //페이지전환이 필요한 경우 redirect로 url재요청 -> 주소창에 [localhost:/goods/goodsList]라고 요청한 것과 같음. 따라서 다시 /goods 로 매핑 후 /goodsList로 get매핑
	}
	
	@GetMapping("/addGoods") //요청된 주소 경로에 맞는 컨트롤러에 와서 매핑된 메서드를 실행
	public String addGoods(Model model) { //스트링 데이터타입을 반환하는 addGoods 메서드를 실행 
		
		model.addAttribute("title", "상품등록"); //addAttribute 메서드를 사용하여 model 객체에 키워드(스트링)과 키워드에 해당하는 값(object)을 담는다
		
		return "goods/addGoods"; //논리경로 문자열을 반환한다
	}
	
	
	@PostMapping("/sellersInfo")
	@ResponseBody
	public List<Member> sellersInfo(){
		String searchKey = "m.m_level";
		String searchValue = "2";
		List<Member> memberList = memberMapper.getMemberList(searchKey, searchValue);
		memberList.forEach(seller -> seller.setMemberPw(""));
		return memberList;
	}
		
	@GetMapping("/sellerList")
	public String getGoodsListBySeller( Model model
									   ,@RequestParam(name="checkSearch", required = false) String[] checkArr
									   ,@RequestParam(name="searchValue", required = false) String searchValue) {
		
		List<Member> goodsListBySeller = goodsService.getGoodsListBySeller(checkArr, searchValue);
		model.addAttribute("title", "판매자별상품조회");
		model.addAttribute("goodsListBySeller", goodsListBySeller);
		
		return "goods/sellerList";
	}
	
	@GetMapping("/goodsList")
	public String getGoodsList(Model model	//반환 결과를 담음
			   				  ,HttpSession session	//현재 세션에 담긴 값을 사용
			   				  ,@RequestParam(name="msg", required = false) String msg) {	//파라미터로 msg를 가져옴. 필수는 아님. 현재 코드에선 삭제 -> 조회로 페이지 이동할 때 생김.
		
		String memberLevel = (String) session.getAttribute("SLEVEL");	//현재 세션에 담긴 값 SLEVEL을 get
		Map<String, Object> paramMap = null;	//임시로 값을 데이터 타입 상관 없이 담아갈 Map 인터페이스 자료형 변수를 생성. <>는 제네릭을 이용한거라는데?
		if(memberLevel != null && "2".equals(memberLevel)) {	//만약 세션 memberLevel이 존재하며 2라면
			String sellerId = (String)session.getAttribute("SID");	//현재 세션에 담긴 아이디를 get
			
			paramMap = new HashMap<String, Object>();	//인터페이스 구현 객체 생성 후 할당
			paramMap.put("searchKey", "g_seller_id");	//키, 키값 설정. id관련 컬럼명
			paramMap.put("searchValue", sellerId);		//키, 키값 설정	. 실접속 중인 세션 아이디
		}
		
		List<Goods> goodsList = goodsService.getGoodsList(paramMap);	//Map을 매개변수로 함수 호출
		
		model.addAttribute("title", "상품조회");
		model.addAttribute("goodsList", goodsList);
		if(msg != null) model.addAttribute("msg", msg);	//만약 상품 삭제 프로세스 후 reAttr를 통해 반환 url? 뒤로 파라미터 msg가 반환되었다면 model에 msg값 할당
														//http://localhost/goods/goodsList?msg=상품코드%3A+g056가+삭제할+수+없습니다.
		return "goods/goodsList";
	}
}
