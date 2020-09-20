<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8" name='viewport' content='width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0'>
		<title>区划和学校</title>
		<link href="css/CSet.css" rel="stylesheet"/>
		<style type="text/css">

		</style>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
		var json; //全局json数据 
		var cities = []; //获取所有的城市-json
		var pages; //保存总页数
		var curpage; //保存当前页码 
		var city; //保存当前城市 
		var distFold = {};
		function messageDisplay() {
			$("#message").css("display", "inline-block");
			setTimeout(hid, 3000);
		}
		function hid() {
			$("#message").css("display", "none");
		}
		
		$(function() {
			
			$(".tdiv").scroll(function() { //给table外面的div滚动事件绑定一个函数
				var top = $(".tdiv").scrollTop(); //获取滚动的距离
				
				//该行的所有单元格随着滚动 
				$("table tr:eq(0)").children().each(function() {
					$(this).css({
						"position": "relative",
						"top": top,
						"left": "0px",
// 						"background-color": "white",
						"z-index": "2147483584"
					});
				})
			})
		})
		
		function changeText(e) {
			//获取当前单击行行号 
			var Row = $(e).parent().parent().index();
			var Col = $(e).parent().parent().find("td").index($(e).parent()[0]);
			if(Col == 0) {
				json[Row].cname = $(e).val();
			} else if(Col == 1) {
				json[Row].dname = $(e).val();
			} else if(Col == 2) {
				json[Row].sname = $(e).val();
			} else if(Col == 3) {
				json[Row].type = $(e).val();
			}
		}
// 		<td class='t4'><select id='select' onchange='changeText(this)' ><option value=''></option><option value="小学">小学</option><option value="初中">初中</option><option value="九年一贯制学校">九年一贯制学校</option><option value="高中">高中</option><option value="完全中学">完全中学</option><option value="十二年一贯制学校">十二年一贯制学校</option></select><img class='img2' src='images/delIcon.png' onclick='delfun(this);'></td>
		function addfun(e) {
			var Row = $(e).parent().parent().index();
			var Col = $(e).parent().parent().find("td").index($(e).parent()[0]);		
			var tr = "";
			tr += "<td class='t1'><input type='text' value='' onchange='changeText(this)'><img class='img1' src='images/SetAdd.png' onclick='addfun(this);'><img class='img2' src='images/delIcon.png' onclick='delfun(this);'>" + "</td>";
			tr += "<td class='t2'><input type='text' value='' onchange='changeText(this)'><img class='img1' src='images/SetAdd.png' onclick='addfun(this);'><img class='img2' src='images/delIcon.png' onclick='delfun(this);'>" + "</td>";
			tr += "<td class='t3'><input type='text' value='' onchange='changeText(this)'><img class='img1' src='images/SetAdd.png' onclick='addfun(this);'><img class='img2' src='images/delIcon.png' onclick='delfun(this);'>" + "</td>";
			tr += "<td class='t4'><select id='select' onchange='changeText(this)' ><option value=''></option><option value='小学'>小学</option><option value='初中'>初中</option><option value='九年一贯制学校'>九年一贯制学校</option><option value='高中'>高中</option><option value='完全中学'>完全中学</option><option value='十二年一贯制学校'>十二年一贯制学校</option></select><img class='img2' src='images/delIcon.png' onclick='delfun(this);'>" + "</td>";
			//在当前单击位置的行后面再插入一行 
			$("table tr:eq(" + Row + ")").after("<tr>" + tr + "</tr>");
			var j = {"cname":null, "dname":null, "sname":null, "type":null};
			json.splice(Row+1, 1, j);
		}
		function delfun(e) {
			var Row = $(e).parent().parent().index();
			var Col = $(e).parent().parent().find("td").index($(e).parent()[0]);
			var dr = 1;
			if(Col == 0) {
				for (var i = Row+1; i < json.length ; i++) {
					var j = json[i];
					if(j.cname == null) {
						dr++;
					} else {
						break;
					}
				}
			} else if (Col == 1) {
				for (var i = Row+1; i < json.length ; i++) {
					var j = json[i];
					if(j.dname == null && j.cname == null) {
						dr++;						
					} else {
						break;
					}
				}
			}
			for(var i = 0; i < dr; i++) 
				$("table tr:eq(" + Row + ")").remove();		
				json.splice(Row, dr);
		}
		var dataByUpload = {};
		function fun() {
			var filename = getFilename($("#file").val());
			var type = filename.substring(filename.lastIndexOf('.')+1);
			if(type != "xls") {
				$("#message").text("请导入.xls文件");
				messageDisplay();
				return ;
			}
			$("#message").text("正在导入数据，请稍后...");
			messageDisplay();
			$("#filepath").val(filename);
			//异步提交文件表单   
			var targetUrl = $("#form1").attr("action");    
		    var form1 = new FormData($( "#form1" )[0]);
		    $.ajax({ 
		     type:'post',
		     url:targetUrl, 
		     cache: false,    //上传文件不需缓存
		     processData: false, //需设置为false。因为data值是FormData对象，不需要对数据做处理
		     contentType: false, //需设置为false。因为是FormData对象，且已经声明了属性enctype="multipart/form-data"
		     data: form1,
		     dataType: 'json',
		     success:function(data) { 
				$("#message").text("数据导入成功");
				messageDisplay();
 			    setTimeout(function(){
 	 			    $("#success").css("display", "none");
				}, 1000);
	 				dataByUpload = data;
	 				cities = {};
	 				var cityNum = 1;
	 				for(var i = 0; i < data.length; i++) {
	 					if(data[i].cname != undefined && data[i].cname != null) {
	 						cities["" + cityNum] = data[i].cname;
	 						cityNum++;
	 					}
	 				}
	 				curpage = 1;
	 				city = cities["" + curpage];
	 				//获取总页数
					pages = Object.keys(cities).length;
					var lis = "";
		 			for(var i = 1; i <= pages; i++) {
		 				if(i == curpage) { 
							lis += "<li class='curPage' name='" + i + "' onclick='changePage(this);'>" + cities["" + i] + "</li>";
		 				}
						else  {
							lis += "<li onclick='changePage(this);' name='" + i + "' >" + cities["" + i] + "</li>";
						}
		 			}
			 		$(".page").html(lis);
			 		
			 		AllUploadDataShow(dataByUpload);
		     }
		    });
		}
		
		//预览导入数据
		function AllUploadDataShow(data) {
			//以空间换时间 
			trNum = {};
			   var table = $("table");
			   table.html("");
			   var start=0;
			   var i = 0;

			   var tr = "<tr><th style='text-indent: 6.2rem;text-align: left;' class='t2'>县区</th><th class='t3'>学校</th><th class='t4'>类别</th></tr>";
			   var tableRow = 0;
			   for( ; i < data.length && data[i].cname != cities["" + curpage]; i++) ;
			   var rCount = 0;
			   start = i + 1;
			   var dnameFlag = "";
			   for( ++i ; i < data.length && data[i].cname == null; i++) {
					var c = data[i];
					if(c.dname != null) {
// 						        <tr class="parent" id="row123" id='row0'>	
						dnameFlag = c.dname;
						rCount++;
						tr += "<tr class='parent' id='row" + rCount + "'>";
//	 					   var tr = "<tr class='parent' id='row" + rCount + "'>";
						   if(data[i + 1] != undefined && data[i + 1].dname == null) {
								trNum["" + tableRow] = tableRow; //当前行可以折叠扩展 
								tr += "<td class='t2'><span class='minusStyle'>-</span>" + c.dname + "</td>";
						   }
						else
							tr += "<td class='t2'>" + c.dname + "</td>";
						tr += "<td class='t3'></td>";
						tr += "<td class='t4'></td>";
// 						table.append(tr + "</tr>");
						tableRow ++;
						if(c.sname != null)
							i--;
						continue;
					} else {
						//另起一行 						
						tableRow ++;
						tr += "<tr class='child-row" + rCount + "'>";
// 						tr = "<tr class='child-row" + rCount + "'>";
						tr += "<td class='t2'></td>";
					}
					if(c.sname != null) {
						tr += "<td class='t3'>" + c.sname + "</td>";						
					} else {
						tr += "<td class='t3'></td>";
					}
					if(c.sname != null) {
						if(c.type != null) 		
							tr += "<td class='t4'>" + c.type + "</td>";
						else tr += "<td class='t4'></td>";								
					} else {
						tr += "<td class='t4'></td>";
					}
					tr += "</tr>";
// 					table.append(tr + "</tr>");
			   };
			   table.html(tr);
			   $('tr.parent')
	            .css("cursor","pointer")
	            .attr("title","点击这里展开/关闭")
	            .click(function(){
	                $(this).siblings('.child-'+this.id).toggle();//当前点击某行同胞行，查找制定子元素类，折叠隐藏
	                var icon = $(this).find("td:eq(0)").find("span");
	                if(icon != undefined) {
	                	if(icon.text() == '+') {
	                		//记录展开标志 
							distFold[ cities["" + curpage] + $(this).find("td:eq(0)").text()] = $(this).prop("id");	
	                		icon.text('-');
							icon.prop('class', 'minusStyle');	
							console.log(distFold)
	                	} else if(icon.text() == '-') {
	                		//点击折叠时，删除展开标志 
	                		icon.text('+');
							var foldFlag = $(this).find("td:eq(0)").text();
							delete distFold[ cities["" + curpage] + foldFlag];
							
							icon.prop('class', 'plusStyle');
							console.log(distFold)
	                	}
	                }
	            });
			   
			   //以空间换取时间 ,来初始化折叠所有未记忆行 
			   $("tr[class=parent]").each(function() {
					console.log(this);
				   var foldFlag = $(this).find("td:eq(0)").text();
					if(distFold[ cities["" + curpage] + "+" + foldFlag.substring(foldFlag.indexOf('-') + 1)] == undefined)
					   if($(this).text().indexOf('-') != -1)
						   $(this).click();
			   })
		}
		
		var map = {};
		var trNum = {};
		function dataPreview(data) {
			//以空间换时间 
			trNum = {};
			   var table = $("table");
			   console.log("hree");
			   table.html();
			   var start=0;
			   var i;
			   var rCount = 0;
			   start = i + 1;
			   var dnameFlag = "";
			   var tr = "<tr><th style='text-indent: 6.2rem;text-align: left;' class='t2'>县区</th><th class='t3'>学校</th><th class='t4'>类别</th></tr>";
			   var tableRow = 0;
			   for(var i = 0; i < data.length; i++) {
					var c = data[i];
					if(c.dname != dnameFlag) {
// 						        <tr class="parent" id="row123" id='row0'>	
						dnameFlag = c.dname;
						rCount++;
						//另起一行 
						tr += "<tr class='parent' id='row" + rCount + "'>";
// 					   var tr = "<tr class='parent' id='row" + rCount + "'>";
					   if(data[i + 1] != undefined && data[i + 1].dname == dnameFlag) {
							trNum["" + tableRow] = tableRow; //当前行可以折叠扩展 
							tr += "<td class='t2'><span class='minusStyle'>-</span>" + c.dname + "</td>";
					   }
						else
						tr += "<td class='t2'>" + c.dname + "</td>";
						tr += "<td class='t3'></td>";
						tr += "<td class='t4'></td>";
						tr += "</tr>";
						tableRow ++;
// 						table.append(tr + "</tr>");
						if(c.sname != null)
							i--;
						continue;
					} else {
						//另起一行 						
						tableRow ++;
						tr += "<tr class='child-row" + rCount + "'>";
// 						tr = "<tr class='child-row" + rCount + "'>";
						tr += "<td class='t2'></td>";
					}
					if(c.sname != null) {
						tr += "<td class='t3'>" + c.sname + "</td>";						
					} else {
						tr += "<td class='t3'></td>";
					}
					if(c.sname != null) {
						if(c.type != null) 		
							tr += "<td class='t4'>" + c.type + "</td>";
						else tr += "<td class='t4'></td>";								
					} else {
						tr += "<td class='t4'></td>";
					}
					tr += "</tr>";
// 					table.append(tr + "</tr>");
			   };
			   table.html(tr);
			   
			   $('tr.parent')
	            .css("cursor","pointer")
	            .attr("title","点击这里展开/关闭")
	            .click(function(){
	                $(this).siblings('.child-'+this.id).toggle();//当前点击某行同胞行，查找制定子元素类，折叠隐藏
	                var icon = $(this).find("td:eq(0)").find("span");
	                if(icon != undefined) {
	                	if(icon.text() == '+') {
	                		//记录展开标志 
							distFold[ cities["" + curpage] + $(this).find("td:eq(0)").text()] = $(this).prop("id");	
	                		icon.text('-');
							icon.prop('class', 'minusStyle');	
							console.log(distFold)
	                	} else if(icon.text() == '-') {
	                		//点击折叠时，删除展开标志 
	                		icon.text('+');
							var foldFlag = $(this).find("td:eq(0)").text();
							delete distFold[ cities["" + curpage] + foldFlag];
							
							icon.prop('class', 'plusStyle');
							console.log(distFold)
	                	}
	                }
	            });
			   
			   $("tr[class=parent]").each(function() {
					console.log(this);
				   var foldFlag = $(this).find("td:eq(0)").text();
					if(distFold[ cities["" + curpage] + "+" + foldFlag.substring(foldFlag.indexOf('-') + 1)] == undefined)
					   if($(this).text().indexOf('-') != -1)
						   $(this).click();
			   })
			   
			   //以空间换取时间 ,来初始化折叠所有未记忆行 
// 			   for(var key in trNum) {
// 				   var tr = $("table tr:eq(" + key + ")");
// 				   console.log(key);
// 				   var foldFlag = tr.find("td:eq(0)").text();
// 					if(distFold[ cities["" + curpage] + "+" + foldFlag.substring(foldFlag.indexOf('-') + 1)] == undefined)
// 					   if(tr.text().indexOf('-') != -1)
// 						   tr.click();
// 			   }
		}
		
		
		function changePage(e) {
			city = $(e).text();
			for(var i = 1; i <= pages; i++) 
				if(cities["" + i] == city)
					curpage = i;
			var lis = "";
 			for(var i = 1; i <= pages; i++) {
 				if(i == curpage) { 
					lis += "<li class='curPage' name='" + i + "' onclick='changePage(this);'>" + cities["" + i] + "</li>";
 				}
				else  {
					lis += "<li onclick='changePage(this);' name='" + i + "' >" + cities["" + i] + "</li>";
				}
 			}
	 		$(".page").html(lis);
	 		//导入预览或者发送页面请求
			if(Object.keys(dataByUpload).length != 0) {
				AllUploadDataShow(dataByUpload);
				return ;
			}

 			$.post("CCSPreviewServlet", {'cname' : cities["" + curpage]}, function(data) {
 				json = data;
 				dataPreview(json);
 			}, "json");
		}
		
		function getFilename(path) {
			var po = path.lastIndexOf("\\");
			return path.substring(po+1);
		}
		</script>
		
		<script>
			function tableEx() {
				$("#form2 input").val(JSON.stringify(dataByUpload));
				$("#form2").submit();
			}
			//确认提交导入数据 
			function tableSub() {
				if(Object.keys(dataByUpload).length == 0) {
					$("#message").text("请先导入数据，再点击提交");
					messageDisplay();
					return ;
				}
				$("#message").text("正在提交数据，请稍后...");
				messageDisplay();
				$.post("acMServlet/ExcelUpdate", { 'cds' : JSON.stringify(dataByUpload)}, function(data) {
					$("#message").text("数据提交成功");
					messageDisplay();
				});
			}
		</script>
		<script>
			var cityName;
		//获取所有市，再根据当前市获取学校 
		$(function() {
			dataByUpload = {};
			$.post("CCSServlet/findCities", function(data) {
				cities = data;
 				curpage = 1;
 				city = cities["" + curpage];
 				//获取总页数
				pages = Object.keys(cities).length;
				var lis = "";
	 			for(var i = 1; i <= pages; i++) {
	 				if(i == curpage) { 
						lis += "<li class='curPage' name='" + i + "' onclick='changePage(this);'>" + cities["" + i] + "</li>";
	 				}
					else  {
						lis += "<li onclick='changePage(this);' name='" + i + "' >" + cities["" + i] + "</li>";
					}
	 			}
		 		$(".page").html(lis);
	 			$.post("CCSPreviewServlet", {'cname' : cities["" + curpage]}, function(data) {
	 				json = data;
	 				dataPreview(json);
	 			}, "json");
			}, "json");
	
		})
		</script>
	</head>
	<body>
	<img id="img" style="display:none">
	<div class="wrapper" style="width:190.3rem;height:88rem; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;区划和学校</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c = null;
					String username = "";
					for(int i = 0; cs != null &&  i < cs.length; i++) {
						c = cs[i];
						if("username".equals(c.getName()))
							username = c.getValue();
					}
				%>
				<p>用户：<%=username %></p>
				<ul>
					<li style="height: 70px;margin-top: 20px;">
					<span class="s1" style='margin-left: -1rem;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName' style="display: flex;width: 120px;"></span></span>
					</li>
					<a href="CSet.jsp"><li class="now"><img src="images/left1-s.png">区划和学校</li></a>
					<a href="acM.jsp"><li><img src="images/left1.png">下级账号</li></a>
<!-- 					<a href="changeSubPin.jsp"><li><img src="images/left2.png">下级密码</li></a> -->
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="pjInfoM.jsp"><li><img src="images/left4.png">评估项目</li></a>
					<a href="indexSet.jsp"><li><img src="images/left5.png">评估指标</li></a>
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="exInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a>
					<a href="taskM.jsp"><li><img src="images/left7.png">项目学校任务</li></a>
					<a href="exGroups.jsp"><li><img src="images/left6.png">新建专家组</li></a>
					<a href="GroupSchoolP.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="addMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="exDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="project_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="place-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="changePin.jsp"><li><img src="images/left9.png">个人密码修改</li></a>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
					市县区校设置<span>SETUP OF REGION</span>
				</div>
				<span id='message' class='message'>提示信息</span>
				<form id="form1" method="post" action="UploadServlet" enctype="multipart/form-data">
					<label>文件名称：</label>
					<input type="text" class="select" id="filepath">
					<input type="file" name="uploadFile" id="file" onchange="fun();" style="display:none">
					<input type="button" name="" id="" class="in1" value="导入" onclick="$('#file').click();"/>
					<input type="button" name="" id="" class="in2" value="提交"  onclick="tableSub();"/>
				</form>
				<sapn id='wait' style='position:absolute;color: red;left: 20%;top: 20%;display: none'>正在导入数据，请稍后...</sapn>
		    	<span id='pleaseUpload' style='position: absolute;left: 56%;top: 20%;color: red;display: none;'>请先导入数据，再点击提交</span>
				<span id='success' style='position:absolute;color: red;left: 52%;top: 20%;display: none;'>数据导入成功</span>
				<!-- 导出信息时，表单发送此时的表格json数据 -->
				<form id="form2" method="post" action="ExcleExportSmall" style="display:none">
					<input name="jsonObj" type="text">	
				</form>
				<!-- 提交表格 -->
				<form id="form3" method="post" action="SubmitCCS" style="display:none">
					<input name="jsonObj" type="text">	
				</form>
				<div class="phead">
				</div>
				<div class="pagediv">
					<ul class="page">
					</ul>
				</div>
				<div class="tdiv">
					<table>
						<tr><th style='text-indent: 6.2rem;text-align: left;' class='t2'>县区</th><th class='t3'>学校</th><th class='t4'>类别</th></tr>
					</table>
				</div>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
