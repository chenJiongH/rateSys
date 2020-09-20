<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>县专家评分</title>
		<link href="css/DSPRate.css" rel="stylesheet"/>
		<style>
			.titleShow {
				max-width: 45rem;
				position: absolute;
				font: 1.5rem "微软雅黑"bold;
				color: black;
				border-radius: 0.5rem;
				border: #dedede solid 0.1rem;
				padding: 0.7rem;
				background: white;
				word-break: break-word;
				z-index: "555555";
			}	
		</style>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/getWebSiteHead.js"></script>
<!-- 		富文本编辑器 -->
		<script type="text/javascript" charset="utf-8" src="utf8-jsp/ueditor.config.js"></script>
 	 	<script type="text/javascript" charset="utf-8" src="utf8-jsp/ueditor.all.js"> </script>
		<script type="text/javascript" charset="utf-8" src="utf8-jsp/lang/zh-cn/zh-cn.js"></script>
		
		<script src="js/forbiddenFB.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script>
		var curpage = "";
		var sidcidScore = {};
		var sidcidSpName = {};
		var pageBean = [];
		var mana = {};
		//存放所有分数和说明的json数组，用于提交和保存 
		var formData = {};
		var sidcidExplain = {};
		var readonly = false;
		var projects = {}; // 存放所有项目 
		var HTML_FONT_SIZE = 10;
		function messageDisplay() {
			$("#message").css("display", "inline-block");
		}
		function hid() {
			$("#message").css("display", "none");
		}

		function showProcess(pData) {
			$("div[class=processRate]").html('');
			$("div[class=ratemessage]").html('');
			var data = pData;
			var flag = false;
			data.process = "-" + data.process;
			var split = '<img src="images/splitLine.png" class="split"/>';
			while(data.process.indexOf('-') != -1) {
				console.log(data.process);
				console.log(flag);
				var rank = data.process.charAt(1);// 忽略字符串开头的 '-'
				data.process = data.process.substring(2);// 去除字符串的 '-X'
				//获取当前循环所需的图片
				var img = '<img src="images/did.png" style="width: 39px"  />';
				if(flag == true) {
					img = '<img src="images/notStart.png"  style="width: 39px" />';
					split = '<img src="images/splitLine.png" class="split"/>';
				}
				else if(rank == data.rate || (rank == "抽" && data.rate == "抽查")) {
					img = '<img src="images/doing.png" style="width: 39px"  />';
					flag = true;
					split = '<img src="images/splitLine.png" class="split"/>';
				}
				if(data.rate == "结束") {
					img = '<img src="images/down.png"  style="width: 39px" />';
					split = '<img src="images/splitLine.png" class="split"/>';
				}
				$("div[class=processRate]").append(img);
					
				
				if(rank == "校") {
					$("div[class=ratemessage]").append('<label style="margin-right: 72px">校级</label>');
				} else if(rank == "县") {
					$("div[class=ratemessage]").append('<label style="margin-right: 49px;">县（市、区）级</label>');
				} else if(rank == "市") {
					$("div[class=ratemessage]").append('<label style="margin-right: 93px;">设区市级</label>');
				} else if(rank == "省"){
					$("div[class=ratemessage]").append('<label style="margin-right: 93px;">省级</label>');
				} else if(rank == "抽") { 
					$("div[class=ratemessage]").append('<label>省级抽查</label>'); 
					break;
				}
				// 如果被上面的break，则不会重复输出一次
				if(data.process.indexOf('-') != -1) 
					$("div[class=processRate]").append(split);

			}
		}
			$(function() {
				// 获取屏幕分辨率 
				var HtmlWidth = screen.width;
				if(HtmlWidth ==1366 || HtmlWidth ==1360)
					HTML_FONT_SIZE = 9.5; 
				if(HtmlWidth ==1280)
					HTML_FONT_SIZE = 9;
				if(HtmlWidth == 800)
					HTML_FONT_SIZE = 6;
				if(HtmlWidth == 400)
					HTML_FONT_SIZE = 4; 
				
				$(".tdiv").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".tdiv").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("#row1").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
							"left": "0px",
	// 						"background-color": "white",
							"z-index": "55"
						});
					})
				$("#row2").children().each(function() {
					$(this).css({
						"position": "relative",
						"top": top,
						"left": "0px",
// 						"background-color": "white",
						"z-index": "55"
					});
				})
			})
					
			    //查找页面待显示内容，即当前校级管理员指标内容
				$.post("DSPRateServlet/findPr", function(data) {
					console.log(data);
					mana = data[1];
					if(data.message != undefined) {
						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
						messageDisplay();
						setTimeout(hid, 3000);
			    		return ;
					}
					$(".exName").text(data[data.length-1].spname);
					$(".unit").text(data[data.length-1].spo);
					//显示所有已开启并所有学校完成自评的项目
// 					<option value=''></option>
					projects = data;
					var options = "";
					for(var i = 0; i < data.length - 1; i++) 
						options += "<option class='" + data[i].commState + "' value='" + data[i].pid + "'>" + data[i].pname + "</option>";
					$("#Pr").append(options);
					if(data[0].commState == "已提交") {
						setReadonly();
					}
					//请求当前所在项目的所有学校和专家C指标信息
					$.post("DSPRateServlet/findCByPid", {'pid' : $("#Pr").val()}, function(data) {
						console.log(data);
						if(data.message != undefined) {
							$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
							messageDisplay();
							setTimeout(hid, 3000);
							return ;
						}
						pageBean = data;
						if(curpage == "") 
							curpage = data.sch[0].sid;
// 						构造所有的sidcid键值对，用空间换取时间、减少提交时判断数
						var scoreName = "districtScore", explainName = "districtExplain";
						console.log(scoreName)
						for(var i = 0; i < data.c.length; i++) {
							// 如果分数为空，要填写分数
							if(data.c[i][scoreName] === null || data.c[i][scoreName] === "")
								sidcidScore[data.c[i].sid + "" + data.c[i].cid] = 0;
							// 如果分数不为空，要根据分数和阈值、是否说明，来填写说明项。 
							else if(data.c[i][scoreName] !== "" ){
								// 以防用户直接提交之后没有数据 
								formData[data.c[i].sid + "" + data.c[i].cid] = data.c[i][scoreName];
								if(data.c[i][scoreName] < data.c[i].threshhold && (data.c[i].isexplain == 'TRUE' || data.c[i].isexplain== 'true') && (data.c[i][explainName] === "" || data.c[i][explainName] === null))
									sidcidExplain[data.c[i].sid + "" + data.c[i].cid] = true;
							}
							if(data.c[i][scoreName] !== "") 
								formData["0" + data.c[i].sid + data.c[i].cid] = data.c[i][explainName];
						}
						dataShow(data);
					}, 'json')
					//显示当前项目流程	
					$.post("DSPRateServlet/findProcessRate", {'pid':$("select").val()}, function(data) {
						console.log(data);
						var str = JSON.stringify(data);
						processData = JSON.parse(str);
						showProcess(data);
					}, 'json')
				}, 'json')
				
			$("#Pr").change(function() {
				// 分页去除上一页的行数据 
				 $("table tr").each(function() {
					 console.log(this);
					 if($(this).prop("id") != "row1" && $(this).prop("id") != "row2") {
						 $(this).remove();
					 }
				 })
					for(var i = 0; i < projects.length - 1; i++) 
						if(projects[i].pid == $("#Pr").val()) {
							if(projects[i].commState == "已提交")
								setReadonly();
							else 
								readonly = false;
						}
				curpage = "";
				sidcidScore = {};
				sidcidExplain = {};
				sidcidSpName = {};
				formData = {};
				pageBean = [];
				formData = {};
				mana = {};
				var prState = $(this).attr("class");
				$.post("DSPRateServlet/findProcessRate", {'pid':$("select").val()}, function(data) {
					console.log(data);
					var str = JSON.stringify(data);
					processData = JSON.parse(str);
					showProcess(data);
				}, 'json')
				//请求当前所在项目的所有学校和专家C指标信息 
				$.post("DSPRateServlet/findCByPid", {'pid' : $("#Pr").val()}, function(data) {
					if(data.message != undefined) {
						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					pageBean = data;
					if(curpage == "")
						if(data.sch.length != 0)
							curpage = data.sch[0].sid;
//						构造所有的sidcid键值对，用空间换取时间、减少提交时判断数
					var scoreName = "districtScore", explainName = "districtExplain";
					console.log(data.c)
					for(var i = 0; i < data.c.length; i++) {
						console.log(data.c[i][scoreName])
						// 如果分数为空，要填写分数
						if(data.c[i][scoreName] === null || data.c[i][scoreName] === "")
							sidcidScore[data.c[i].sid + "" + data.c[i].cid] = 0;
						// 如果分数不为空，要根据分数和阈值、是否说明，来填写说明项。 
						else if(data.c[i][scoreName] !== "" ){
							// 以防用户直接提交之后没有数据 
							formData[data.c[i].sid + "" + data.c[i].cid] = data.c[i][scoreName];
							if(data.c[i][scoreName] < data.c[i].threshhold && (data.c[i].isexplain == 'TRUE' || data.c[i].isexplain== 'true') && (data.c[i][explainName] === "" || data.c[i][explainName] === null))
									sidcidExplain[data.c[i].sid + "" + data.c[i].cid] = true;
							console.log(sidcidExplain);
						}
						if(data.c[i][scoreName] !== "") 
							formData["0" + data.c[i].sid + data.c[i].cid] = data.c[i][explainName];
					}
					dataShow(data);
					if(prState == "已提交") {
						setReadonly();
					} else {
						$(":input").css("pointer-events", "auto");	
						readonly = false;
					}
				}, 'json') 
					//显示当前项目流程	
					$.post("DSPRateServlet/findProcessRate", {'pid':$("select").val()}, function(data) {
						console.log(data);
						var str = JSON.stringify(data);
						processData = JSON.parse(str);
						showProcess(data);
					}, 'json')
			})
			})
			// 该项目已经被提交，设置全屏只读 
			function setReadonly() {
				$(":input").css("pointer-events", "none"); 
				$("#draft").removeAttr("onclick");
				$(".in2").removeAttr("onclick");
				$("#uploadOverall").removeAttr("onclick");
				$(":input").removeAttr("onclick");
				$("input[value='退出登录']").css("pointer-events", "auto");	
				$("input[value='退出登录']").attr("onclick", "window.location.href=('exit.jsp')");	
				readonly = true;
			}
			//显示表格数据
			function dataShow(data) {
				$(".titleShow").remove();
				//显示校页码，并绑定该校总评位置 
				var lis = "";
				for(var i = 0; i < data.sch.length; i++) {
					if(curpage == data.sch[i].sid) {
							lis += "<li title='" + data.sch[i].sname + "' class='curPage' name='" + data.sch[i].sid + "' onclick='changePage(this);'>" + data.sch[i].sname + "</li>";
							$("#uploadOverall").prop("name", data.sch[i].reportlocation);
		 				}
						else  {
							lis += "<li title='" + data.sch[i].sname + "' name='" + data.sch[i].sid + "' onclick='changePage(this);'>" + data.sch[i].sname + "</li>";
						}
				}
				$(".page").html(lis);
				//开始标志，减少循环次数 ,0 未遇到该页，1正在显示该页，2该页显示完毕 
				var start = 0;
				var aname = "";
				var bname = "";
				var trs = "";
				/*
					var trs = "	<tr id='row1'><th colspan='3'></th><th class='t5' ></td><th class='t5' ></td><th class='t5' ></td><th class='t6'></td><th class='t7' ></th><th class='t8' >县级专家</th></tr><tr id='row2'><th class='t1'>A指标</td><th class='t2'>B指标</td><th class='t3'>C指标</td><th class='t4'>分值</td>	<th class='t5'>分数</td><th class='t6'>描述</td><th class='t6'>附件</td><th class='t7'>分数</th>	<th class='t8'>说明</th></tr>";
				*/
				for(var i = 0; i < data.c.length && start != 2; i ++) {
					if(curpage == data.c[i].sid) {
						start = 1;
						trs += "<tr>";
						if(aname != data.c[i].aname)
							aname = data.c[i].aname,
							trs += "<td class='t1' title='"+data.c[i].aname+"'>" + data.c[i].aname + "</td>";
						else trs += "<td class='t1'></td>"; 
						if(bname != data.c[i].bname)
							bname = data.c[i].bname,
							trs += "<td class='t2' title='" + data.c[i].bname+"'>" + data.c[i].bname + "</td>";
						else trs += "<td class='t2'></td>";
						trs += "<td class='t3' title='" +data.c[i].cname +"'>" + data.c[i].cname + "</td>";
						trs += "<td class='t4'>" + data.c[i].score + "</td>";
						trs += "<td class='t5'>" + data.c[i].schoolscore + "</td>";
						trs += "<td class='t6'><input type='button' value='查看' name='" + data.c[i].description + "' onclick='lookDescri(this)'></td>";
						if(data.c[i].isannex == "true" || data.c[i].isannex == "TRUE")
							trs += "<td class='t6'><input type='button' class='" + data.c[i].sid + "" + data.c[i].cid +"' value='下载' name='" + data.c[i].annexlocation + "' onclick='download(this)'></td>";
						else trs += "<td class='t6'></td>";
						if(data.c[i].districtScore == null) data.c[i].districtScore = "";
						if(data.c[i].districtExplain == null) data.c[i].districtExplain = "";
						if(data.c[i].isexplain == 'TRUE' || data.c[i].isexplain== 'true') {/*  是否需要说明，无需说明则设置阈值为负无穷大  */
							sidcidExplain[data.c[i].sid + "" + data.c[i].cid] = true;
							if((data.c[i].districtScore != null && data.c[i].districtScore !== "") || formData[data.c[i].sid + "" + data.c[i].cid] != undefined) {
								delete sidcidScore[data.c[i].sid + "" + data.c[i].cid];
								if(formData[data.c[i].sid + "" + data.c[i].cid] == undefined) {
									// 清除说明标记 
									if(data.c[i].districtScore >= data.c[i].threshhold) 
										delete sidcidExplain[data.c[i].sid + "" + data.c[i].cid];
									formData[data.c[i].sid + "" + data.c[i].cid] = data.c[i].districtScore;
									// id=sid+cid name=阈值 class=分段值  sidcidExplain
									trs += "<td class='t7' title='分段值:" + data.c[i].segscore +"'><input segscore='" + data.c[i].segscore +"'  type='text' id='" + data.c[i].sid + "" + data.c[i].cid +"' name='" + data.c[i].threshhold +"'  class='TRUE' onblur='grade(this)' value='" + data.c[i].districtScore + "'></td>";
								} else {		
									if(formData[data.c[i].sid + "" + data.c[i].cid] >= data.c[i].threshhold)
										delete sidcidExplain[data.c[i].sid + "" + data.c[i].cid];
									trs += "<td class='t7' title='分段值:" + data.c[i].segscore +"'><input segscore='" + data.c[i].segscore +"'  type='text' id='" + data.c[i].sid + "" + data.c[i].cid +"' name='" + data.c[i].threshhold +"' class='TRUE' onblur='grade(this)' value='" + formData[data.c[i].sid + "" + data.c[i].cid] + "'></td>";
								}
							}				
							else {
								trs += "<td class='t7' title='分段值:" + data.c[i].segscore +"'><input  segscore='" + data.c[i].segscore +"' type='text' id='" + data.c[i].sid + "" + data.c[i].cid +"' name='" + data.c[i].threshhold +"' class='TRUE' onblur='grade(this)'></td>";
							}
						}
						else {		
							if((data.c[i].districtScore != null && data.c[i].districtScore !== "") || formData[data.c[i].sid + "" + data.c[i].cid] != undefined) {
								delete sidcidScore[data.c[i].sid + "" + data.c[i].cid];
								if(formData[data.c[i].sid + "" + data.c[i].cid] == undefined) {
									formData[data.c[i].sid + "" + data.c[i].cid] = data.c[i].districtScore;
									trs += "<td class='t7' title='分段值:" + data.c[i].segscore +"'><input segscore='" + data.c[i].segscore +"'  type='text' id='" + data.c[i].sid + "" + data.c[i].cid +"' name=''  onblur='grade(this)' value='" + data.c[i].districtScore + "'></td>";
								} else {
									trs += "<td class='t7' title='分段值:" + data.c[i].segscore +"'><input segscore='" + data.c[i].segscore +"'  type='text' id='" + data.c[i].sid + "" + data.c[i].cid +"' name=''  onblur='grade(this)' value='" + formData[data.c[i].sid + "" + data.c[i].cid] + "'></td>";
								}
							} else {
								trs += "<td class='t7' title='分段值:" + data.c[i].segscore +"'><input  segscore='" + data.c[i].segscore +"' type='text' id='" + data.c[i].sid + "" + data.c[i].cid +"' name=''  onblur='grade(this)'></td>";
							}
						}
						if(data.c[i].districtExplain != null && data.c[i].districtExplain != "" || formData["0" + data.c[i].sid + data.c[i].cid] != undefined) {
							delete sidcidExplain[data.c[i].sid + "" + data.c[i].cid];
							if(formData["0" + data.c[i].sid + data.c[i].cid] == undefined) {
								formData["0" + data.c[i].sid + data.c[i].cid] = data.c[i].districtExplain;
								trs += "<td class='t8' title='"+data.c[i].districtExplain+"'><input maxlength='300' type='text' class='0" + data.c[i].sid + "" + data.c[i].cid + "' onblur='explain(this)' value='" + data.c[i].districtExplain + "'></td>";
							} else {
								trs += "<td class='t8' title='"+formData["0" + data.c[i].sid + data.c[i].cid]+"'><input maxlength='300' type='text' class='0" + data.c[i].sid + "" + data.c[i].cid + "' onblur='explain(this)' value='" + formData["0" + data.c[i].sid + data.c[i].cid] + "'></td>";
							}
						}
						else trs += "<td class='t8' title=''><input maxlength='300' type='text' class='0" + data.c[i].sid + "" + data.c[i].cid + "' onblur='explain(this)'></td>";
						trs += "</tr>";
					} else if(start == 1) {
						start = 2;
					}
				}
				$("table").append(trs);
				titleShow();
				$("input").prop("autocomplete", "off");
			    if(readonly == true) {
			    	$("input").attr("readonly", "readonly");
					$(":input").css("pointer-events", "none");
					$(":input[value='退出登录']").css("pointer-events", "auto");	
					$("input[value='退出登录']").attr("onclick", "window.location.href=('exit.jsp')");	
					$("#Pr").css("pointer-events", "auto");
			    } else {
					$("#draft").attr("onclick", "saveDraft()");
					$(".in2").attr("onclick", "rate();");
			    }
			    createTitleDiv($(".in2")[0]);
			    $(".page li").each(function() {
			    	createTitleDiv(this);
			    })
			}
			
			//分页时，设置curpage为点击校码的sid
			function changePage(e) {
				curpage = $(e).attr("name");
				// 分页去除上一页的行数据
				 $("table tr").each(function() {
					 console.log(this);
					 if($(this).prop("id") != "row1" && $(this).prop("id") != "row2") {
						 $(this).remove();
					 }
				 })
				dataShow(pageBean);
			}
			
			//每次评分后触发 
			function grade(e) {
				if($(e).val() == "") {
					sidcidScore[$(e).prop("id")] = true;
					return ;
				}
// <!-- <td class='t7'><input type='text' id='sidcid' name='阈值' class='分值段' onchange='(this)'></td> -->
// 				获取小数位数, 将有可能为小数的分值段转化为整数，再利用取余运算符
				var num = $(e).attr("segscore").substring($(e).attr("segscore").lastIndexOf('.') + 1).length;
				if((Math.pow(10, num) * $(e).val()) % (Math.pow(10, num) * $(e).attr("segscore")) != 0) { 
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "该指标的分段值为:" + $(e).attr('segscore') + " 请输入有效分值");
					messageDisplay();
					setTimeout(hid, 3000);
					$(e).val("");
					return ;
				}
				var score = $(e).parent().parent().find("td:eq(3)").text();
				if(parseFloat(score) < parseFloat($(e).val()) || parseFloat($(e).val()) < 0) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "自评分数无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$(e).val("");
					return ;
				}
				if($(e).val() < $(e).prop("name")) {
					// 是否已经存在说明 
					if($(e).parent().next().children().val() == "") {
						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "评分小于该项阈值" + $(e).prop("name") + "，请务必填写评分说明");
						messageDisplay();
						setTimeout(hid, 3000);
						if($(e).prop("class") == "TRUE")
							sidcidExplain[$(e).prop("id")] = true;
					}
				} else {
					delete sidcidExplain[$(e).prop("id")];
				}
				delete sidcidScore[$(e).prop("id")];
				formData[$(e).prop("id")] = $(e).val();
			}
			//每次提供了说明后，删除 该校该指标的待说明标识
			function explain(e) {
				formData[$(e).prop("class")] = $(e).val();
				$(e).parent().attr('title', $(e).val());
				createTitleDiv($(e).parent()[0]);
				if($(e).val() != "") {
					delete sidcidExplain[$(e).prop("class").substring(1)];
				} else {
					// 分数小于阈值，且需要说明 
					var scoreInput = $($(e).parent().parent().find("td:eq(7)").children("input").get(0));
					if($(scoreInput).val() < $(scoreInput).prop("name")) {
						if($(scoreInput).prop("class") == "TRUE")
							sidcidExplain[$(e).prop("class").substring(1)] = true;
					}
				}
				
			}
			
			//点击下载某个C指标附件或总评文件 
			function download(e) {
				if($(e).prop('name') == "") {return;}
				$("#form2 input").val($(e).prop('name'));
				$("#form2").submit();
				$(e).css("backgroundColor","rgb(150, 132, 224, 0.5)");
			}
			
			//提交评分时，判断所有分数、姓名 ，和必须的说明是否已经填写
			function rate() {
				$("#draft").removeAttr("onclick");
				$(".in2").removeAttr("onclick");
				if(Object.keys(sidcidScore).length != 0) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "请评完各校指标分数之后再提交项目评分");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#draft").attr("onclick", "saveDraft()");
					$(".in2").attr("onclick", "rate()");
					return ;
				}
				if(Object.keys(sidcidExplain).length != 0) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "请填写必须的说明项");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#draft").attr("onclick", "saveDraft()");
					$(".in2").attr("onclick", "rate()");
					return ;
				}
		      zdconfirm('系统确认框',"提交后，评分信息将不可修改、无法回退。请确定是否提交？",function(r){ //项目删除确定框 
	    	     if(r) {
					formData.pid = $("#Pr").val();
					$.post("DSPRateServlet/rate", {'c' : JSON.stringify(formData)}, function(data) {
						if(data.message == "评定提交成功。"){
							location.reload(true);
							return ;
						}
						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
						messageDisplay();
						setTimeout(hid, 3000);
						if(data.message != "评定提交成功。"){
							$(".in2").attr("onclick", "rate()");
							$("#draft").removeAttr("onclick");
						}
					}, 'json')
	    	      } else {
						$(".in2").attr("onclick", "rate()");
						$("#draft").removeAttr("onclick");
	    	      }
	    	    });
			}
			function saveDraft() { 
				formData.pid = $("#Pr").val();
				formData.draft = "true";
				$.post("DSPRateServlet/rate", {'c' : JSON.stringify(formData)}, function(data) {
					if(data.message == "评定提交成功。")
						data.message = "保存草稿成功";
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
					messageDisplay();
					setTimeout(hid, 3000);
				}, 'json')
			}
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:190.03rem; height:88rem; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
		
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;专家评分</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c = null;
					String username = ""; 
					for(int i = 0; cs != null &&  i < cs.length; i++) {
						c = cs[i];
						if("username".equals(c.getName())){
							username = c.getValue();
						}
					} 
				%> 
				<p style="width:180px;">用户：<%=username %></p>
				<ul>
					<li style="height: 70px;margin-top: 20px;">
					<span class="s1" style='margin-left: -1rem;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName' style='display: flex;width: 120px;'></span>
					</li>
					<a href="#"><li class="now"><img src="images/left11-s.png">专家评分</li></a>
					<a href="PCDSPChangePin.jsp"><li><img src="images/left2.png">修改密码</li></a>
				</ul>
			</div>	
			<div class="right">
				<div class="rhead">
				</div>
				<div class='ratemessage'></div>
				<div class='processRate'></div>
<!-- 				用于下载总评或附件 -->
				<form style='display:none' id='form2' method='post' action='DSPRateServlet/download'>
					<input type='text' name='location' value=''>
				</form> 
				<form id='form1' method='post' action='DSPRateServlet/rate' enctype="multipart/form-data">
					<span class="break"></span>
					<div id='editorBg' style='background:#bfbfbf;width: 100%; height: 100%; z-index: 99119; position: fixed; top: 0px; left: 0px; opacity: 0.6;display:none'></div>
					<div id='editorBox' style='height: 76rem;margin-bottom: 2.9rem;z-index: 99999;width: 93rem;background-color: rgb(249, 249, 249);display: none;position: absolute;top: -109px;left: -17%;'>
					<input id='editorCancel' type="button" style="margin: -14px;position: absolute;right: 5rem;top: 2.4rem;width: 9.5rem;border-radius: 10rem;height: 3rem;color: white;border: 0.1rem solid rgb(218, 220, 224);background-color: #53c2d6;" value="确定"/>
						<span style='display: block;padding-left: 5rem;font: 2.3rem "微软雅黑";color: black;padding-top: 2rem;padding-bottom: 2rem;border-bottom: 0.1rem #c3c3ba solid'>描述查看</span>
			<!-- 			编辑框 -->
					    <script id="editor" type="text/plain" style="    overflow-x: hidden;width: 82.5rem;margin: 0 auto;margin-top: 2.3rem;border-right: 0.1rem solid #d4d4d4;"></script>
					</div> 
					<label id='message' class='message'><img src="images/warn.png" class='messageWarn'>提示消息</label>
					<span style="position:absolute;right: 91%;margin-top: 0.6rem;">项目名称:</span>
					<select id='Pr' style="position:relative;right:-9%;outline:none"></select>
					<input id='draft' class='draft' type='button' value='保存草稿' onclick='saveDraft();'/>
					<input type="button" name="" id="uploadOverall" class='in3' value="查阅总评" onclick='lookDescri(this)'/>
					<input type="button" name="" title='请确认<span style="color:red">所有学校</span>都已评定完成' class="in2" value="提交" onclick='rate();'/>
					<div class="pagediv">
						<ul class="page">
						</ul>
					</div>
					<div class='tdiv'>
					<table>
  						<tr id='row1'>
							<th colspan='3'></th>
							<th class='t5' ></td>
							<th class='t5' ></td>
							<th class='t5' ></td>
							<th class='t6'></td>
							<th class='t7' ></th>
							<th class='t8' style='text-indent:-18.4rem;' >县级专家</th>
						</tr>
						<tr id='row2'>
							<th class='t1' style='vertical-align: top;line-height: 1.5rem;'>A指标</td>
							<th class='t2' style='vertical-align: top;line-height: 1.5rem;'>B指标</td>
							<th class='t3' style='vertical-align: top;line-height: 1.5rem;'>C指标</td>
							<th class='t4' style='vertical-align: top;line-height: 1.5rem;'>分值</td>
							<th class='t5' style='vertical-align: top;line-height: 1.5rem;'>分数</td>
							<th class='t6' style='vertical-align: top;line-height: 1.5rem;'>描述</td>
							<th class='t6' style='vertical-align: top;line-height: 1.5rem;'>附件</td>
							<th class='t7'>分数</th>
							<th class='t8'>评分说明</th>
						</tr> 
					</table>
					</div>
				</form>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
<script type="text/javascript">
    //实例化编辑器
    //建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
    var ue = UE.getEditor('editor', {
    	  enterTag : 'br',
		  wordCount:true,        
		  maximumWords:500,		  
    	  autoHeightEnabled: false,//是否自动长高
    	  autoFloatEnabled: false//是否保持toolbar的位置不动
    });
    $("#editor").css("height", '56rem');
    setTimeout(fun, 200);
    function fun() {
	    $("#edui1_bottombar").css("display", "none");
	    $("#edui1_message_holder").css("display", "none");
		ue.setDisabled('fullscreen');
    }
    function lookDescri(e) {
		$(e).css("backgroundColor","rgb(150, 132, 224, 0.5)");	
    	var descriContent = $(e).attr('name');
	    $("#editorBg").css("display", "block");
	    $("#editorBox").css("display", "block");
		ue.setContent(descriContent);
    }
    $("#editorCancel").click(function(){
	    $("#editorBg").css("display", "none");
	    $("#editorBox").css("display", "none");
    })
    
</script>
<script type="text/javascript">
//重写确认和提示框样式 
(function($) {
    $.alerts = {       
        alert: function(title, message, callback) {
            if( title == null ) title = 'Alert';
            $.alerts._show(title, message, null, 'alert', function(result) {
                if( callback ) callback(result);
            });
        },
        confirm: function(title, message, callback) {
            if( title == null ) title = 'Confirm';
            $.alerts._show(title, message, null, 'confirm', function(result) {
                if( callback ) callback(result);
            });
        },
        _show: function(title, msg, value, type, callback) {
                    var _html = "";
                    _html += '<div id="mb_box"></div><div id="mb_con"><span id="mb_tit">' + title + '</span>';
                    _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
                      if (type == "alert") {
                      _html += '<input id="mb_btn_ok" type="button" value="确定" />';
                    }
                    if (type == "confirm") {
                      _html += '<input id="mb_btn_ok" type="button" value="确定" />';
                      _html += '<input id="mb_btn_no" type="button" value="取消" />';
                    }
                    _html += '</div></div>';
                 
                    //必须先将_html添加到body，再设置Css样式
                    $("body").append(_html); GenerateCss();
         
            switch( type ) {
                case 'alert':
        
                    $("#mb_btn_ok").click( function() {
                        $.alerts._hide();
                        callback(true);
                    });
                    $("#mb_btn_ok").focus().keypress( function(e) {
                        if( e.keyCode == 13 || e.keyCode == 27 ) $("#mb_btn_ok").trigger('click');
                    });
                break;
                case 'confirm':
                   
                    $("#mb_btn_ok").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(true);
                    });
                    $("#mb_btn_no").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(false);
                    });
                    $("#mb_btn_no").focus();
                    $("#mb_btn_ok, #mb_btn_no").keypress( function(e) {
                        if( e.keyCode == 13 ) $("#mb_btn_ok").trigger('click');
                        if( e.keyCode == 27 ) $("#mb_btn_no").trigger('click');
                    });
                break;
            }
        },
        _hide: function() {
             $("#mb_box,#mb_con").remove();
        }
    }
    // Shortuct functions
    zdalert = function(title, message, callback) {
        $.alerts.alert(title, message, callback);
    }
     
    zdconfirm = function(title, message, callback) {
        $.alerts.confirm(title, message, callback);
    };
      //生成Css
    var GenerateCss = function () {
    	 
        $("#mb_box").css({ width: '100%', height: '100%', zIndex: '99999', position: 'fixed',
          filter: 'Alpha(opacity=60)', top: '0', left: '0', opacity: '0.6'
        });
     
        $("#mb_con").css({ zIndex: '999999', width: '20%', position: 'fixed',
          backgroundColor: 'White', borderRadius: '1.5rem', boxShadow: '0px 0.3rem 0.9rem #8b8585'
        });
        
        $("#mb_tit").css({ display: 'block', fontSize: '1.5rem', color: '#455fe7', padding: '1rem 1.5rem 1.0rem 14.4rem', //系统确认框字眼控制
          backgroundColor: '#DDD', borderRadius: '1.5rem 1.5rem 0 0',
          borderBottom: '0.3rem solid #455fe7', fontWeight: 'bold'
        });
        $("#mb_btn_no").css({    'border-radius': '7.1rem',border: '0.1rem rgb(218, 220, 224) solid', backgroundColor: '#dddddd', color: '#455fe7', marginLeft: '2rem' });
        $("#mb_btn_ok").css({ backgroundColor: '#455fe7', borderRadius: '2.2rem', color: 'white' });
     
        $("#mb_msg").css({ padding: '2rem', lineHeight: '2rem', 'padding-left': '10px',
          borderBottom: '0.1rem dashed #DDD', fontSize: '1.3rem'
        });
     
        $("#mb_ico").css({ display: 'block', position: 'absolute', right: '1rem', top: '0.9rem',
          border: '0.1 solid Gray', width: '1.8rem', height: '1.8rem', textAlign: 'center',
          lineHeight: '1.6rem', cursor: 'pointer', borderRadius: '1.2rem', fontFamily: '微软雅黑'
        });
     
        $("#mb_btnbox").css({ margin: '1.5rem 0 1rem 0', textAlign: 'center' });
        $("#mb_btn_ok,#mb_btn_no").css({ width: '8.5rem', height: '3rem', border: 'none' });
     
     
        //右上角关闭按钮hover样式
        $("#mb_ico").hover(function () {
          $(this).css({ backgroundColor: 'Red', color: 'White' });
        }, function () {
          $(this).css({ backgroundColor: '#DDD', color: 'black' });
        });
     
        var _widht = document.documentElement.clientWidth; //屏幕宽
        var _height = document.documentElement.clientHeight; //屏幕高
     
        var boxWidth = $("#mb_con").width();
        var boxHeight = $("#mb_con").height();
     
        //让提示框居中
        $("#mb_con").css({ top: (_height - boxHeight)/20 + "rem", left: (_widht - boxWidth)/20 + "rem" });
      }
     
     
    })(jQuery);
    	</script>
    		<script>
    		var oTitle = null;
    		var sTitle = null;
    	function titleShow() {
    		var aA = document.getElementsByTagName('td');
    		for (var i = 0; i < aA.length; i++) {
    			if (aA[i].title) { //假如a标签中存在title的话
    				createTitleDiv(aA[i])
    			}
    		}
    	}
    	function createTitleDiv(elementTitle) {
    		elementTitle.onmouseover = function(ev) {
    			sTitle = this.title;
    			this.title = '';
    			oTitle = document.createElement('div');
    			oTitle.className = 'titleShow';
    			oTitle.innerHTML = sTitle;
    			document.body.appendChild(oTitle);
    			 //获取鼠标所在x座标并增加10个像素,下同
    			oTitle.style.left = (ev.pageX + 10) + 'px';
    			oTitle.style.top = (ev.pageY + 10) + 'px';
    			$(oTitle).css("zIndex", "5555");
    		};
    		elementTitle.onmousemove = function(ev) {
    			var ev = ev || window.event;
    			oTitle.style.left = (ev.pageX + 10) + 'px'; //获取鼠标所在x座标并增加10个像素,下同
    			oTitle.style.top = (ev.pageY + 10) + 'px';
    			$(oTitle).css("zIndex", "5555");
    		}
    		elementTitle.onmouseout = function() {
    			this.title = sTitle;
    			if(oTitle != null)
    				document.body.removeChild(oTitle);
    		}
    	}
    </script>
