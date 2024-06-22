let rows;
let rowCnt;
let rowOption;

function rowsInit() { // 선택한 행개수
	rows = document.getElementById("rows");
	rowCnt = document.querySelector("#rowCnt select");
	rowOption = document.querySelectorAll("#rowCnt select option");
	for(let idx = 0; idx < rowOption.length; idx++){
		if(rows.value == rowOption[idx].value){
			rowOption[idx].selected = true;
		}
	}
}

function reloadRowCnt(path){
	let reloadRow = rowCnt.value;
	location.href = path + "/titleListPaging?recPerPage="+reloadRow;
}