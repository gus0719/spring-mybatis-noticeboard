<%@page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>
<%@ page import="sample.spring.noticeBoard.BoardV01DTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
	prefix="c"%>
<!DOCTYPE html>
<%
String contextPath = request.getContextPath();
String prjPath = contextPath + "/boardMVC";
%>
<html>
<head>
    <meta charset='utf-8'>
    <title>BoardTitleListFrame</title>
    <link rel="stylesheet" href="resources/css/boardTitleList.css">
    <link rel = "stylesheet" href = "resources/css/pagination.css">
    <script src = "resources/js/board.js"></script>
    <script src = "resources/js/board_select.js"></script>
    
    <%
	List<BoardV01DTO> dtoL = (List<BoardV01DTO>)request.getAttribute("dtoL");
    HashMap<String, Integer> paging = (HashMap<String, Integer>)request.getAttribute("paging");
    int totalRecord = dtoL.size();
	int recPerPage = 10;
	int pagePerBlock = 10;
	if(paging.containsKey("recPerPage")){
		recPerPage = paging.get("recPerPage");
	}
    %>
</head>
<body onload = "mInit(), rowsInit()">
<%! // MV 선언
int bod_no;
String bod_subject;
String bod_writer;
String bod_logtime;
int bod_readCnt;
String bod_connIp;
%>
    <div id = "wrapper">
        <h1>** Board Title List **</h1>
        <p id = "navi">
            <a href = "<%=contextPath%>/pageWrite">[게시판 쓰기]</a>
            <a href = "<%=contextPath%>/pageList">[게시판 내용 List]</a>
            <span id = "rowCnt">행개수
            	<select name = "rowCnt" onchange = "reloadRowCnt('<%=contextPath%>')"
            		style="width : 70px; font-size : 20px; vertical-align : middle;">
            		<option value = "5">5</option>
            		<option value = "10">10</option>
            		<option value = "15">15</option>
            		<option value = "20">20</option>
            		<option value = "25">25</option>
            	</select>
            </span>
        </p>
        <input type = "hidden" id = "rows" value = "<%=recPerPage%>">
        <form method = "get" action = "<%=contextPath%>/selSubject">
        	<input type = "hidden" name = "bod_no" id = "bod_no">
        </form>
        <%
        if(dtoL != null){
        	int totalPage = (int)Math.ceil((double)totalRecord / recPerPage); // 총 페이지 : dtoL.size() / 10
        	int totalBlock = (int)Math.ceil((double)totalPage / pagePerBlock); // 총 블럭 : totalPage / 10
        	
        	int nowPage = 0;
        	int nowBlock = 0;

        	if(paging.containsKey("nowPage"))
        		nowPage = paging.get("nowPage"); // 페이지 이동할때마다 값을 할당
        	if(paging.containsKey("nowBlock"))
        		nowBlock = paging.get("nowBlock"); // 블럭 이동할때마다 값을 할당
        	
        	int recOfBeginPage = nowPage * recPerPage; // 0 * 10 >> 페이지idx가 1일때 10 ~ 19번째 인덱스의 dto 출력
        	int pageOfBeginBlock = nowBlock * pagePerBlock; // 0 * 10 >> 블럭idx가 1일때 페이지 수 : 10 ~ 19번재 인덱스의 페이지 출력
        %>
        
        <div id = "columns">
            <span class = "column_short">번호</span>
            <span class = "titles">제목</span>
            <span class = "column_short">글쓴이</span>
            <span class = "column_short">등록일</span>
            <span class = "column_short">조회</span>
            <span class = "ips">IP</span>
            <ul id = "lists">
            <%
            for(int idx = recOfBeginPage; idx < recOfBeginPage + recPerPage; idx++){
            	// 현재 페이지의 레코드부터 현재레코드 + 10까지
        		if(idx >= totalRecord) break; // idx가 totalRecord보다 크거나 같으면 반복 중지
        		bod_no = dtoL.get(idx).getBod_no();
        		bod_subject = dtoL.get(idx).getBod_subject();
        		bod_writer = dtoL.get(idx).getBod_writer();
        		bod_logtime = dtoL.get(idx).getBod_logtime();
        		bod_readCnt = dtoL.get(idx).getBod_readCnt();
        		bod_connIp = dtoL.get(idx).getBod_connIp();
            %>
            	<li onclick = "valSend(<%= bod_no %>)">
	            	<span class = "column_short"><%=bod_no%></span>
	            	<span class = "titles"><%=bod_subject%></span>
	            	<span class = "column_short"><%=bod_writer%></span>
	            	<span id = "logtime" class = "column_short"><%=bod_logtime%></span>
	            	<span class = "column_short"><%=bod_readCnt%></span>
	            	<span class = "ips"><%=bod_connIp%></span>
            	</li>
            <%
            }
            if(nowBlock != 0){ // nowBlock이 0이면 뒤로가기 출력 X
            %>
            	<a class = "select" href = "<%=contextPath%>/titleListPaging?recPerPage=<%=recPerPage%>&nowPage=<%=(nowBlock-1)*pagePerBlock%>&nowBlock=<%=nowBlock-1%>">≪</a>
            <%
            }
            %>
        	<%
        	for(int idx1 = pageOfBeginBlock; idx1 < pageOfBeginBlock + pagePerBlock; idx1++){
        		// 현재 블럭의 페이지부터 페이지 + 10까지 페이지 선택 출력
        		if(idx1 == nowPage){
        	%>
        		<a class = "select" style = "background-color : black; color : white;" href = "<%=contextPath%>/titleListPaging?recPerPage=<%=recPerPage%>&nowPage=<%=idx1%>&nowBlock=<%=nowBlock%>"><%=idx1+1%></a>
        		<%
        			continue;
        		} 
        		if(idx1 + 1 > totalPage) break; // 총 페이지까지 페이지 선택 버튼 출력
        		if(idx1 != nowPage) {
        		%>
        		<a class = "select" href = "<%=contextPath%>/titleListPaging?recPerPage=<%=recPerPage%>&nowPage=<%=idx1%>&nowBlock=<%=nowBlock%>"><%=idx1+1%></a>
        	<%
        		}
        	}
        	if(nowBlock + 1 < totalBlock){ // 총 블럭의 수 전의 블럭에 있으면 앞으로가기 출력
        	%>
        	<a class = "select" href = "<%=contextPath%>/titleListPaging?recPerPage=<%=recPerPage%>&nowPage=<%=(nowBlock+1)*pagePerBlock%>&nowBlock=<%=nowBlock+1%>">≫</a>
       	<%
       		}
       	}
       	%>
            </ul>
        </div>
    </div>
</body>
</html>