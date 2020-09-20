<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>评估指标</title>
		<style type="text/css">
	        td label{
	            display: inline-block;
	            width: 22px;
	            height: 22px;
				border-radius: 5px;
				background-image: url(images/Uncheckbox.png);
	            vertical-align: middle;
				position: relative;
   			    cursor: pointer;
	            border: #dedede solid 0px;
	            right: -30px;
	        } 
 	        input[type=checkbox]:checked+label { 
          	   background-image: url(images/Chcheckbox.png); 
 	        }
	        input[type=checkbox] {
          	  display: none;
	        }
	    </style>
		<style>
		.titleShow {
			max-width: 450px;
			position: absolute;
			font: 15px/1.5 "微软雅黑"bold;
			color: black;
			border-radius: 5px;
			border: #dedede solid 1px;
			padding: 7px;
			background: white;
			word-break: break-word;
			z-index: "555555";
		}	
		</style>
		<link href="css/indexSet.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script> 
		
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
 						"z-index": "214"
					});
				})
			})
		})
		
		function getFilename(path) {
			var po = path.lastIndexOf("\\");
			return path.substring(po+1);
		}
		
		function messageDisplay() {
			$("#message").css("display", "inline-block");
		}
		function hid() {
			$("#message").css("display", "none");
		}
		
// 		导入按钮，发送文件 
		function fun() {
			var filename = getFilename($("#file").val());
			var type = filename.substring(filename.lastIndexOf('.')+1);
			if(type != "xls") {
				$("#message").text("请导入.xls文件");
				messageDisplay();
				setTimeout(hid, 3000);
				return ;
			}
// 			$("#filepath").val(filename);
			//判断该项目是否已经有了指标信息 
			var pid = "";
			for(var i = 0; i < dataJson.pj.length; i++) {
				if(dataJson.pj[i].pname == filename.substring(0, filename.lastIndexOf('.'))) {
					pid = dataJson.pj[i].pid;
					for(var j = 0; j < dataJson.as.length; j++) {
						//已经有了该项目的指标，弹出提示框 
						if(dataJson.as[j].pid == pid) {
							if(!confirm("该项目已经有指标信息，是否替换")) 
								return ;
							else 
								break;							
						} 
					}
				}
			}
			//异步提交文件表单 
		    var form1 = new FormData($( "form" )[0]);
			//清空file数据
			$("#file").val("");
		    $.ajax({ 
		     type: 'post',
		     url: 'IndexSetServlet/upload', 
		     cache: false,    //上传文件不需缓存
		     processData: false, //需设置为false。因为data值是FormData对象，不需要对数据做处理
		     contentType: false, //需设置为false。因为是FormData对象，且已经声明了属性enctype="multipart/form-data"
		     data: form1,
		     dataType: 'json',
// 		     async:false,
		     success:function(data) {   
		    	 if(data.message == "数据读取失败，请检查文件数据和是否已导入该项目...") {
					$("#message").text(data.message);
					messageDisplay();
					setTimeout(hid, 3000);
		    		 return ;
		    	 }
		       location.reload(true);
		     }
		    });
			$("#message").text("数据正在导入过程中，请稍做休息");
			messageDisplay();
			setTimeout(hid, 3000);
		}
		//保存数据 
		var dataJson;
		//保存当前页的A级指标名称
		var curpageName = "";
		//刷新页面请求数据
		$(function() {
// 			console.log(new Date().getTime());
			$.post("IndexSetServlet/findPageBean", function(data){
				if(data.message != undefined) {
					$("#message").text(data.message);
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				} 
// 				console.log(new Date().getTime());
				dataJson = data;
				if(data.as.length > 0)
					curpageName = (data.as)[0].aname;
				dataShow(data);
// 				console.log(new Date().getTime());
			}, 'json')
		})
		
		//点击页码,改变页码
		function changePage(e) {
			curpageName = $(e).text();
			dataShow(dataJson);
		}
		
		function dataShow(data) {
			$(".page").html("");
			$("table").html("");
			var options = '';
			for(var i = 0; i < data.pj.length; i++) {
				if(data.curPid == data.pj[i].pid) 
					options += "<option value='" + data.pj[i].pid + "' selected='selected'>" + data.pj[i].pname + "</option>";
				else 
					options += "<option value='" + data.pj[i].pid + "'>" + data.pj[i].pname + "</option>"; 
			}
			$('.select').html(options);

			var aid = "";
			var lis = "";
			for(var i = 0; i < data.as.length; i++) {
				if(curpageName == (data.as)[i].aname) {
					lis += "<li class='curpage' onclick='changePage(this);'>" + (data.as)[i].aname + "</li>";
					aid = (data.as)[i].aid;				
				}
				else
					lis += "<li onclick='changePage(this);'>" + (data.as)[i].aname + "</li>";
			}
			$(".page").html(lis);

			$("table").html("");
			$("table").append("<tr><th class='t2'>B级指标</th><th class='t3'>C级指标</th><th class='t6'>考核方式</th><th class='t10'>分值</th><th class='t8'>分值段</th><th class='t9'>阈值</th><th class='t5'><label class='score'>评分</label></th><th class='t7' style='text-align: left;text-indent: 27px;'>附件</th></tr>");
			for(var i = 0; i < data.bs.length; i++) {
				//优化i循环，当进入i循环再次出来，再次跳出时可以提交中断循环 
				var flag2 = true;
					if((data.bs)[i].aid == aid) {
						flag2 = false;
						var bid = (data.bs)[i].bid;
						var bname = (data.bs)[i].bname;
						//是标记当前表格行是否需要打印B指标名称，如果已经打印过，则不用再次打印
						var flag = true;
						//优化j循环，当进入j循环再次出来，再次跳出时可以提交中断循环 
						var flag1 = true;
						for(var j = 0; j < data.cs.length; j++) {
							if((data.cs)[j].bid == bid) {
								var tr = "<tr>"
								
								flag1 = false;
								c = (data.cs)[j];
								if(flag == true)
									tr += "<td class='t2' title='" + bname + "'>" + bname + "</td>";
								else tr += "<td class='t2'></td>";
								tr += "<td class='t3' title='" + c.cname + "'>" + c.cname + "</td>";
								tr += "<td class='t6'><input type='text' value='" + c.assessmethod + "' onchange='changeText(this);'></td>";
								tr += "<td class='t10'><input type='text' value='" + c.score + "' onchange='changeText(this);'></td>";
								tr += "<td class='t8'><input type='text' value='" + c.segscore + "' onchange='changeText(this);'></td>";
								tr += "<td class='t9'><input type='text' value='" + c.threshhold + "' onchange='changeText(this);'></td>";
								if(c.isexplain == "true" || c.isexplain == "TRUE")
									tr += "<td class='t5'><input type='checkbox' checked='checked' onclick='changeCheckbox(this);'><label class='lab'></label></td>";
								else tr += "<td class='t5'><input type='checkbox' onclick='changeCheckbox(this);'><label class='lab'></label></td>";
								if(c.isannex == "true" || c.isannex == "TRUE")
									tr += "<td class='t7'><input type='checkbox' checked='checked' onclick='changeCheckbox(this);'><label class='lab'></label></td>";
								else
									tr += "<td class='t7'><input type='checkbox' onclick='changeCheckbox(this);'><label class='lab'></label></td>";
								tr += "<td id='cid' style='display:none'>" + c.cid + "</td>";
								tr += "</tr>";
								flag = false;
								$("table").append(tr);
							} else if(flag1 == false)
								break;
						}
					} else if(flag2 == false) 
						break;
				}
					titleShow();
					
					$(".lab").click(function(){
						$(this).prev().click();
						console.log($(this).prev());
					})
	// 			$(document).on('mouseenter', "table td", function () {
			        
	// 		        if (this.offsetWidth < this.scrollWidth) {
	// 		            $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
	// 		        }
	// 		    });			
	
	// 		    //鼠标离开时，tooltip消失
	// 		    $(document).on('mouseleave', 'table td', function () {
	// 		        $(this).attr('data-toggle', '');
	// 		    });
			    
				$(document).on('mouseenter', ".page li", function () {
			        
			        if (this.offsetWidth < this.scrollWidth) {
		           		 $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
		       		 }
	    		});
		  	  // 鼠标离开时，tooltip消失
			    $(document).on('mouseleave', '.page li', function () {
			        $(this).attr('data-toggle', '');
			    });
		}
		
		//修改table里面的text文本 
		function changeText(e) {
			//查找修改框的列号
			var col = $(e).parent().parent().find("td").index($(e).parent());
			
			//获取当前修改框所在的c级指标编号 
			var cid = $(e).parent().parent().find("td:eq(8)").text();
			for(var i = 0; i < dataJson.cs.length; i++) {
				if(dataJson.cs[i].cid == cid) {
					if(col == 2) 
						dataJson.cs[i].assessmethod = $(e).val();
					else if(col == 3) 
						dataJson.cs[i].score = $(e).val();
					else if(col == 4) 
						dataJson.cs[i].segscore = $(e).val();
					else if(col == 5) 
						dataJson.cs[i].threshhold = $(e).val();
				}
			}
		}
		
		//点击table里面的复选框
		function changeCheckbox(e) {
			//查找修改框的列号
			var col = $(e).parent().parent().find("td").index($(e).parent());
			//获取当前修改框所在的c级指标编号 
			var cid = $(e).parent().parent().find("td:eq(8)").text();
			for(var i = 0; i < dataJson.cs.length; i++) {
				if(dataJson.cs[i].cid == cid) {
					if(col == 6) {
						if(dataJson.cs[i].isexplain == "TRUE" || dataJson.cs[i].isexplain == "true")
							dataJson.cs[i].isexplain = "FALSE";
						else dataJson.cs[i].isexplain = "TRUE";
					} else if(col == 7) {
						if(dataJson.cs[i].isannex == "TRUE" || dataJson.cs[i].isannex == "true")
							dataJson.cs[i].isannex = "FALSE";
						else dataJson.cs[i].isannex = "TRUE";
					}
				}
			}
		}
		//点击提交按钮，把当前已经被修改的数据提交到后端，后端更新数据库
		function submitChange() {
			if(dataJson.as.length == 0) {
				$("#message").text("当前项目无指标数据");
				messageDisplay();
				setTimeout(hid, 3000);
				return ;
			}
			$("#message").text("正在提交数据");
			messageDisplay();
			setTimeout(hid, 3000);
			$.post("IndexSetServlet/submitChange", {'data':JSON.stringify(dataJson)}, function(data) {
				$("#message").text(data.message);
				messageDisplay();
				setTimeout(hid, 3000);
			}, 'json')
		}
		
		$(function() {
			//绑定点击导出按钮发送导出请求
			$("#export").click(function(){
				if($("select").val() == null && $("select").val() == "")
					return ;
				if($(".page").html() == "" ) {
					$("#message").text("当前项目暂无指标信息");
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				}
				$("#exportPid").val($("select").val());
				$("#exportform").submit();		
			});
		})
		
		//点击项目下拉框，选择查看项目
		function showPj(e) {
			$.post("IndexSetServlet/findPageBean", {'pid':$(e).val()}, function(data) {
				if(data.message != undefined) {
					$("#message").text(data.message);
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				}
				dataJson = data;
				if(data.as.length == 0) {
					$(".page").html("");
					$("table tr").eq(0).nextAll().remove();
					console.log($("#message").text());
					$("#message").text("当前项目暂无指标信息");
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				}
				curpageName = (data.as)[0].aname;
				dataShow(data);
			}, 'json')
		}
// 		function		
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:1903px; height:880px; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;评估指标</div>
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
					<span class="s2"><span class='welcomeName'  style='display: flex;width: 120px;'></span></span>
					</li>
					<a href="CSet.jsp"><li><img src="images/left1.png">区划和学校</li></a>
					<a href="acM.jsp"><li><img src="images/left1.png">下级账号</li></a>
<!-- 					<a href="changeSubPin.jsp"><li><img src="images/left2.png">下级密码</li></a> -->
					<li style='padding: 0px;height: 12px;'></li>
					
					<a href="pjInfoM.jsp"><li><img src="images/left4.png">评估项目</li></a>
					<a href="indexSet.jsp"><li class="now"><img src="images/left5-s.png">评估指标</li></a>
					<li style='padding: 0px;height: 12px;'></li>
					
					<a href="exInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a>
					<a href="taskM.jsp"><li><img src="images/left7.png">项目学校任务</li></a>
					<a href="exGroups.jsp"><li><img src="images/left6.png">新建专家组</li></a>
					<a href="GroupSchoolP.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="addMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="exDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<li style='padding: 0px;height: 12px;'></li>
					
					<a href="project_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="place-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="changePin.jsp"><li><img src="images/left9.png">个人密码修改</li></a>
				</ul>
			</div>
			<div class="right">
				<!-- <div class="rhead">
					评估指标<span class="rheadspan">EVALUATION INDEICATORS</span>
				</div> -->
				<form action="" method="post" enctype="multipart/form-data">
				<label id='message' class='message'>提示信息</label>
					<label>项目名称：</label>
<!-- 						<input type="text" class="select" id="filepath"> -->
					<select class='select' id='filepath' onchange='showPj(this);'>
<!-- 						<option value='123'>123</option> -->
<!-- 						<option value='123'>123</option> -->
<!-- 						<option value='123'>123</option> -->
					</select>
					<input type="file" name="uploadFile" id="file" onchange="fun();" style="display:none">
					<input type="button" name="" id="" class="in1" value="导入" onclick="$('#file').click();"/>
<!-- 					<input type="button" name="" id="" class="in1" value="导入"/> -->
					<input type="button" name="" id="" class="in2" value="提交" onclick="submitChange();"/>
					<input type="button" name="" id="export" class="in2" style="background-color:#ad47e7" value="导出" />
				</form>
<!-- 				//导出用的form表单 -->
				<form id="exportform" method="post" style="display:none" action="IndexSetServlet/export">
					<input name="exportPid" id="exportPid" style="display:none" >
				</form>

				<div class="pageDiv">
				<ul class="page">
				</ul>
				 </div>
				<div class="tdiv">
				<!-- 				<span style="margin-left: 148px;width: 139px;">B级指标</span> -->
<!-- 				<span style="width: 146px;text-indent: 24px;">C级指标</span> -->
<!-- 				<span class="s6">考核方式</span> -->
<!-- 				<span class="s2">分值</span> -->
<!-- 				<span class="s4">分值段</span> -->
<!-- 				<span class="s5">阈值</span> -->
<!-- 				<span class="s1">评分</span> -->
<!-- 				<span class="s3">附件</span> -->
					<table >
						<tr>
							<th class='t2'>B级指标</th>
							<th class='t3'>C级指标</th>
							<th class='t6'>考核方式</th>
							<th class='t10' style="">分值</th>
							<th class='t8'>分值段</th>
							<th class='t9'>阈值</th>
							<th class='t5'><label class='score'>评分</label></th>
							<th class='t7' style="text-align: left;text-indent: 27px;">附件</th>
						</tr>
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
<script>
	function titleShow() {
		var oTitle = null;
		var sTitle = null;
		var aA = document.getElementsByTagName('td');
		for (var i = 0; i < aA.length; i++) {
			if (aA[i].title) { //假如a标签中存在title的话
				aA[i].onmouseover = function(ev) {
					sTitle = this.title;
					this.title = '';
					oTitle = document.createElement('div');
					oTitle.className = 'titleShow';
					oTitle.innerHTML = sTitle;
					document.body.appendChild(oTitle);
				};
				aA[i].onmousemove = function(ev) {
					var ev = ev || window.event;
					
					oTitle.style.left = ev.pageX  + 'px'; //获取鼠标所在x座标并增加10个像素,下同
					oTitle.style.top = ev.pageY + 10 + 'px';
				}
				aA[i].onmouseout = function() {
					this.title = sTitle;
					document.body.removeChild(oTitle);
				}
			}
		}
	}
</script>