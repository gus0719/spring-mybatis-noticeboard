package sample.spring.noticeBoard;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import common.MyBatisSession;

/**
 * Handles requests for the application home page.
 */
@Controller
public class BoardController {
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	// ===================
	
	String resource = "sample/mybatis/boardMapper/mybatis-config.xml";
	SqlSession sqlSession = MyBatisSession.getSession(resource);
	
	int bod_no; // 글 번호
	String bod_writer; // 작성자
	String bod_email; // 작성자 이메일주소
	String bod_subject; // 글 제목
	String bod_pwd; // 작성자 비밀번호
	// String bod_logtime; <- sysdate
	String bod_content; // 작성내용
	int bod_readCnt; // 조회수
	String bod_connIp; // 작성자 ip주소

	String msg;
	
	BoardV01DTO bodDTO = new BoardV01DTO();
	
	// 인덱스
//	@RequestMapping(value = "/bodIndex", method = RequestMethod.GET)
//	public ModelAndView index() {
//		HashMap<String, Integer> paging = new HashMap<String, Integer>();
//		paging.put("recPerPage", 10);
//		paging.put("nowPage", 0);
//		paging.put("nowBlock", 0);
//		List<BoardV01DTO> dtoL = sqlSession.selectList("bodSel");
//		ModelAndView mav = new ModelAndView();
//		mav.addObject("dtoL", dtoL);
//		mav.addObject("paging", paging);
//		mav.setViewName("boardSpringMyBatis/boardTitleListPaging");
//		return mav;
//	}

	// 메인화면, 페이징 기법
	@RequestMapping(value = {"/bodIndex", "/titleListPaging"}, method = RequestMethod.GET)
	public ModelAndView titleListPaging(@RequestParam Map<String, Object> map) {
		HashMap<String, Integer> paging = new HashMap<String, Integer>();
		String recPerPage = (map.get("recPerPage") != null) ?
				map.get("recPerPage").toString() : "10";
		String nowPage = (map.get("nowPage") != null) ?
				map.get("nowPage").toString() : "0";
		String nowBlock = (map.get("nowBlock") != null) ?
				map.get("nowBlock").toString() : "0";
		
		paging.put("recPerPage", Integer.parseInt(recPerPage));
		paging.put("nowPage", Integer.parseInt(nowPage));
		paging.put("nowBlock", Integer.parseInt(nowBlock));
		List<BoardV01DTO> dtoL = sqlSession.selectList("bodSel");
		ModelAndView mav = new ModelAndView();
		mav.addObject("dtoL", dtoL);
		mav.addObject("paging", paging);
		mav.setViewName("boardSpringMyBatis/boardTitleListPaging");
		return mav;
	}
	
	// 페이지 리스트
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public ModelAndView pageList(@RequestParam Map<String, Object> map) {
		HashMap<String, Integer> paging = new HashMap<String, Integer>();
		String recPerPage = (map.get("recPerPage") != null) ?
				map.get("recPerPage").toString() : "10";
		String nowPage = (map.get("nowPage") != null) ?
				map.get("nowPage").toString() : "0";
		String nowBlock = (map.get("nowBlock") != null) ?
				map.get("nowBlock").toString() : "0";
		paging.put("recPerPage", Integer.parseInt(recPerPage));
		paging.put("nowPage", Integer.parseInt(nowPage));
		paging.put("nowBlock", Integer.parseInt(nowBlock));
		
		List<BoardV01DTO> dtoL = sqlSession.selectList("bodSel");
		ModelAndView mav = new ModelAndView();
		mav.addObject("dtoL", dtoL);
		mav.addObject("paging", paging);
		mav.setViewName("boardSpringMyBatis/boardList");
		return mav;
	}
		
	// 작성 페이지 ============================================================
	@RequestMapping(value = "/pageWrite", method = RequestMethod.GET)
	public ModelAndView write() {
		return new ModelAndView("boardSpringMyBatis/boardWriteFrame");
	}
	
	// 작성 처리
	@RequestMapping(value = "/frmWrite", method = RequestMethod.GET)
	public ModelAndView write(@RequestParam Map<String, Object> map) {
		bod_no = (int)sqlSession.selectOne("bodMaxNo") + 1;
		System.out.println(bod_no);
		bod_writer = map.get("bod_writer").toString();
		bod_email = map.get("bod_email").toString();
		bod_subject = map.get("bod_subject").toString();
		bod_pwd = map.get("bod_pwd").toString();
		bod_content = map.get("bod_content").toString();
		bod_connIp = map.get("bod_connIp").toString();
		
		bodDTO = new BoardV01DTO(bod_no, bod_writer, bod_email, bod_subject, bod_pwd, bod_content, bod_connIp);
		
		int su = sqlSession.insert("bodIns", bodDTO);
		sqlSession.commit();
		String msg = (su != 0) ?
				bod_writer + "님 게시글 등록 완료" : "게시글 등록 실패";
		ModelAndView mav = new ModelAndView();
		mav.addObject("msg", msg);
		mav.setViewName("boardSpringMyBatis/boardMsgChk");
		return mav;
	}
	
	// 게시글 확인
	@RequestMapping(value = "/selSubject", method = RequestMethod.GET)
	public ModelAndView selSubject(@RequestParam Map<String, Object> map) {
		bod_no = Integer.parseInt(map.get("bod_no").toString());
		bodDTO.setBod_no(bod_no);
		int su = sqlSession.update("addReadCnt", bodDTO);
		sqlSession.commit();
		//System.out.println((su != 0) ? "조회수 증가" : "조회수 증가 X");
		bodDTO = (BoardV01DTO)sqlSession.selectOne("selSubject", bodDTO);
		ModelAndView mav = new ModelAndView();
		mav.addObject("content", bodDTO);
		mav.setViewName("boardSpringMyBatis/boardContentFrame");
		return mav;
	}
	
	// 비밀번호 체크
	@RequestMapping(value = "/delUpdChk", method = RequestMethod.GET)
	public ModelAndView delUpdChk(@RequestParam Map<String, Object> map) {
		String bod_no_string = map.get("bod_no").toString();
		bod_pwd = map.get("bod_pwd").toString();
		String category = map.get("category").toString();
		HashMap<String, String> delUpd = new HashMap<String, String>();
		delUpd.put("bod_no", bod_no_string);
		delUpd.put("bod_pwd", bod_pwd);
		delUpd.put("category", category);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("delUpd", delUpd);
		mav.setViewName("boardSpringMyBatis/boardDelUpdChk");
		return mav;
	}
	
	// 삭제 처리
	@RequestMapping(value = "/bodDel", method = RequestMethod.GET)
	public ModelAndView bodDel(@RequestParam Map<String, Object> map) {
		bod_no = Integer.parseInt(map.get("bod_no").toString());
		bod_pwd = map.get("bod_pwd").toString();
		bodDTO.setBod_no(bod_no);
		bodDTO.setBod_pwd(bod_pwd);
		int su = sqlSession.delete("bodDel", bodDTO);
		sqlSession.commit();
		String msg = (su != 0) ?
				bod_no + "번글 삭제 성공" : "글 삭제 실패";
		ModelAndView mav = new ModelAndView();
		mav.addObject("msg", msg);
		mav.setViewName("boardSpringMyBatis/boardMsgChk");
		return mav;
	}
	
	// 업데이트 페이지
	@RequestMapping(value = "/pageUpd", method = RequestMethod.GET)
	public ModelAndView pageUpd(@RequestParam Map<String, Object> map) {
		bod_no = Integer.parseInt(map.get("bod_no").toString());
		bodDTO.setBod_no(bod_no);
		bodDTO = sqlSession.selectOne("selSubject", bodDTO);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("updDTO", bodDTO);
		mav.setViewName("boardSpringMyBatis/boardUpdFrame");
		return mav;
	}
	
	// 업데이트 처리
	@RequestMapping(value = "/bodUpd", method = RequestMethod.GET)
	public ModelAndView bodUpd(@RequestParam Map<String, Object> map) {
		bod_no = Integer.parseInt(map.get("bod_no").toString());
		bod_writer = map.get("bod_writer").toString();
		bod_pwd = map.get("bod_pwd").toString();
		bod_subject = map.get("bod_subject").toString();
		bod_email = map.get("bod_email").toString();
		bod_content = map.get("bod_content").toString();
		bodDTO = new BoardV01DTO(bod_no, bod_writer, bod_email, bod_subject, bod_pwd, bod_content);
		int su = sqlSession.update("bodUpd", bodDTO);
		sqlSession.commit();
		String msg = (su != 0) ?
				bod_no + "번 글 수정 완료" : "글 수정 실패";
		ModelAndView mav = new ModelAndView();
		mav.addObject("msg", msg);
		mav.setViewName("boardSpringMyBatis/boardMsgChk");
		return mav;
	}
}