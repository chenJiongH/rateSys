<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>专家组学校任务</title>
		<link href="css/GroupSchoolM.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
			//C市 D县 G专家组 
			var cdg = {};
			var pageBeanData = {};
			var curpage;
			var spgsch;
			var changeGroup = false;//改变组标记
		    var mulTrDrag1 = {}; //多选左表格单元行
		    var mulTrDrag2 = {}; //多选右表格单元行 
		    var initFlush = true;
			$(function() {
				
				//固定表格头行 
				$(".first").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".first").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("#table1 tr:eq(0)").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
							"left": "0px",
	// 						"background-color": "white",
							"z-index": "21"
						});
					})
				})
		 		
								//固定表格头行 
				$(".second").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".second").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("#table2 tr:eq(0)").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
							"left": "0px",
	// 						"background-color": "white",
							"z-index": "21"
						});
					})
				})
				
				curpage = 1;
				//联动嵌套三层分别查找项目 -》 专家组 -》 专家组学校和该项目未分配的学校 
				$.post("GroupSchoolM/findAllProORGroupByPid", function(data) {
					console.log(data);
					if(data.message != undefined) {
						alert(data.message);
						return ;
					}
					//显示所有的项目 
					var options = "";
					if(data.length > 0)
						options = "<option value='" + data[0].pid + "' name=" + data[0].Pname + " selected='selected'>" + data[0].Pname + "</option>";
					for(var i = 1; i < data.length; i++)
						options += "<option value='" + data[i].pid + "' name=" + data[i].Pname + ">" + data[i].Pname + "</option>";
					$("#group").html(options);
					console.log($("#group").val());
					//显示所有专家组
					$.post("GroupSchoolM/findAllProORGroupByPid", {'pid' : $("#group").val()}, function(data) {
						console.log(data);
						var options = "";
						if(data.length > 0)           
							options = "<option value='" + data[0].gid + "' selected='selected'>" + data[0].gname + "</option>";
						for(var i = 1; i < data.length; i++)
							options += "<option value='" + data[i].gid + "'>" + data[i].gname + "</option>";
						$("#city").html(options);
						//初始化页面默认查找当前项目和专家组的学校，左边显示当前专家组的学校，右边显示项目待分配的学校 
						$.post("GroupSchoolM/findPageBean", {'getAllspgsch' : 'true' , 'pid':$('#group').val(), 'gid' : $('#city').val() }, function(data) {
							console.log(data);
							pageBeanData = data;
							spgsch = data.spgsch;
							// 将会刷新右table 
							changePageFlag = true;
							dataShow();
						}, 'json')
					}, 'json')
				}, 'json')
				
				
				//给专家组下拉框 绑定 内容改变事件，同步获取左表格该专家组所分配学校，右表格可以不刷新 仍然为当前项目剩余学校  
				$("#city").change(function() {
					// 此时可以不重复获取项目的剩余学校 
					$.post("GroupSchoolM/findPageBean", {'getAllspgsch' : 'true' , 'pid':$('#group').val(), 'gid' : $('#city').val() }, function(data) {
						console.log(data);
						pageBeanData = data;
						spgsch = data.spgsch;
						changePageFlag = true;
					    mulTrDrag1 = {}; //多选右表格单元行 
						dataShow();
					}, 'json')
				})
				// 项目改变，去重新寻找该项目的专家组，和两边表格数据 
				$("#group").change(function() {
					//显示所有专家组
					curpage = 1;
					$.post("GroupSchoolM/findAllProORGroupByPid", {'pid' : $("#group").val()}, function(data) {
						console.log(data);
						var options = "";
						if(data.length > 0)           
							options = "<option value='" + data[0].gid + "' selected='selected'>" + data[0].gname + "</option>";
						for(var i = 1; i < data.length; i++)
							options += "<option value='" + data[i].gid + "'>" + data[i].gname + "</option>";
						$("#city").html(options);
						//初始化页面默认查找当前项目和专家组的学校，左边显示当前专家组的学校，右边显示项目待分配的学校 
						$.post("GroupSchoolM/findPageBean", {'getAllspgsch' : 'true' , 'pid':$('#group').val(), 'gid' : $('#city').val() }, function(data) {
							console.log(data);
							pageBeanData = data;
							spgsch = data.spgsch;
							changePageFlag = true;
							// 在原页面是true
							changeGroup = true;
						    mulTrDrag1 = {}; //多选左表格单元行
						    mulTrDrag2 = {}; //多选右表格单元行 
							dataShow();
						}, 'json')
					}, 'json')
				})
				
				$(document).on('mouseenter', "table td", function () {
			        if (this.offsetWidth < this.scrollWidth) {
			            $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
			        }
			    });
			    //鼠标离开时，tooltip消失
			    $(document).on('mouseleave', 'table td', function () {
			        $(this).attr('data-toggle', '');
			    });
			    var tableUp; //1拖动左table，2拖动右table 

			    //拖拽功能实现:
	            document.ondragstart = function (e) {
	            	console.log(mulTrDrag1);
	            	console.log(mulTrDrag2);
	            	
// 		            e.target.style.opacity = 0.7; //被拖动时 模糊处理  
	            	$(e.target).css("backgroundColor", "rgb(18, 183, 245, 0.4)"); 
		            e.dataTransfer.setData("text", e.target.id); //存放拖动行的id（校id）
		            if(mulTrDrag1[$(e.target).prop("id")] == undefined) 
		            	mulTrDrag1[$(e.target).prop("id")] = true;
		            if(mulTrDrag2[$(e.target).prop("id")] == undefined) 
		            	mulTrDrag2[$(e.target).prop("id")] = true;
		            tableUp = $(e.target).parent().prop("class");//拖动行，因为行有可拖拽属性，单元格不具备（获取table的class名称）
		        }
	            /*浏览器默认会阻止ondrop事件：我们必须在ondrapover中阻止默认行为*/
	            document.ondragover = function (e) {
	                e.preventDefault();
	            }
	            //松开鼠标时更新数据 
	            document.ondrop = function (e) {
	                /*通过e.dataTransfer.setData存储的数据，只能在drop事件中获取*/
	                var data = e.dataTransfer.getData("text");
	                var sch = $(document.getElementById(data)); //拖动前的校所在行 
						//从右table到左table
					var tableDown1 = $(e.target).parent().parent().prop("class");//放置在某个单元格中
					var tableDown2 = $(e.target).attr("name");
					if( (tableDown1 != "table1" && tableDown2!= "table1") && (tableDown1 != "table2" && tableDown2!= "table2")) //不是拖动到左或者右表格 
						return ;
					if( tableDown1 == tableUp || tableDown2 == tableUp) //自身拖动到自身 
						return ;
					if(tableDown1 == "table1" || tableDown2 == "table1") {
						
			            if(mulTrDrag1[$(e.target).prop("id")] != undefined) {
			            	$(this).css("backgroundColor", "#fff");
			            	delete mulTrDrag1[$(e.target).prop("id")];
			            }
			            
						//增添新数据，获取新页面数据 
						var sids = [];
						for(var key in mulTrDrag2) {
							sids.push(key);
						}
						//批量增加sids校组任务 
						$.post("GroupSchoolM/addSpgsch", {'pid':$('#group').val(), 'gid' : $('#city').val(), 'sids' : JSON.stringify(sids)}, function(data) {
							if(data.message != undefined)
								return ; 
							var groupName = "";
							var pname = "";
							//获取当前项目名称和专家组名称
							pname = $("#group option:selected").text();
							groupName = $("#city option:selected").text()
							for(var i = 0; i < sids.length; i++) {
								console.log(sids[i]);
								var trs = "<tr draggable='true' id='" + $(".table2 #" + sids[i]).find("td:eq(1)").text() + "'>";
 								trs += "<td class='t1'>" + groupName + "</td>";
								trs += "<td class='t2'>" + $("#" + sids[i]).find("td:eq(0)").text() + "</td>";
								trs += "<td class='t3'>" + pname + "</td>";
								trs += "<td class='t4'>" + pageBeanData.mana.mname + "</td>";
								trs += "<td style='display:none'>" + pageBeanData.mana.mid + "</td>";
								trs += "<td style='display:none'>" + $(".table2 #" + sids[i]).find("td:eq(1)").text() + "</td>";
								trs += "</tr>";
					    		 //往json中增加该条记录 
								spgsch.push({"pname" : pname,"spgname" : groupName, "sid" :  $("#" + sids[i]).find("td:eq(1)").text(), "sname" :  $("#" + sids[i]).find("td:eq(0)").text()});
								$("#table1").append(trs);
							}
							for(var key in mulTrDrag2) {
								if(mulTrDrag1[key] != undefined) {
									delete mulTrDrag1[key];
								}
								$(".table2 #" + key).remove();
							}
							console.log("here");
							//防止多次绑定 
							$("#table1 tr").unbind("mousedown");
							//重新为新加入的行绑定事件 
						    $("#table1 tr").mousedown(function(){
						    	console.log("down");
						    	console.log(mulTrDrag1);
					            if(mulTrDrag1[$(this).prop("id")] == undefined) {
//			 		            	this.style.opacity = 0.7; //被选中时 模糊处理
					            	$(this).css("backgroundColor", "rgb(18, 183, 245, 0.4)");
					            	mulTrDrag1[$(this).prop("id")] = true;
					            } else {  
					            	$(this).css("backgroundColor", "#fff");
					            	delete mulTrDrag1[$(this).prop("id")];
					            }
						    })
						    mulTrDrag2 = {};
						}, 'json') 
						//从左table到右table 
					} else if(tableDown1 == "table2" || tableDown2 == "table2") {
						
			            if(mulTrDrag2[$(e.target).prop("id")] != undefined) {
			            	$(this).css("backgroundColor", "#fff");
			            	delete mulTrDrag2[$(e.target).prop("id")];
			            }
			            
						//批量删除数据
						var sids = [];
						for(var key in mulTrDrag1) {
							sids.push(key);
						}
						console.log(sids);
						//删除数据，获取新页面数据 
						$.post("GroupSchoolM/delSpgsch", {'pid':$('#group').val(), 'gid' : $('#city').val(), 'sids' : JSON.stringify(sids)}, function(data) {
							location.reload(true);
						}, 'json') 
					}
	            }
			})
			var changePageFlag = false;
				//数据展示 
				function dataShow() {
			    	 var start = 1;
			    	 var end = 10;
			    	 //全局变量curpage，在点击分页和页面刷新时更新其值 ,显示页码 
			    	 if(curpage >= 10) {
			    		 start = curpage - 8;
						 end = curpage + 1;	    		 
			    	 }
					$(".page").html("");
			    	 lis = "<li class='up' onclick='changePage(this);'>上一页</li>";
			    	 for(var i = start; i <= end; i++) {
			    		 if(i == curpage)
			    			 lis += "<li class='curpage' onclick='changePage(this);'>" + i + "</li>";
			    		 else lis += "<li onclick='changePage(this);'>" + i + "</li>";
			    	 }
			    	 lis += "<li class='next' onclick='changePage(this);'>下一页</li>";
			    	 $(".page").append(lis);
			    	 
		    		 if(changePageFlag == true) {//点击分页，改变左表格。不是点击分页，改变右表格
			    		 //获取并生成当前页码左表格显示的数据 
			    		 dataShowLeft();
		    			 changePageFlag = false;
		    			 if(changeGroup == false && initFlush == false) //此时显示页面不是由于切换分组和刷新页面引起，不刷新右table 
		    			 	return ;
		    		 }
					 initFlush = false;	
		    		 
					 var tableJson = {};
			    	 tableJson.ss = pageBeanData.ss;
		    		 //显示右table数据 
			    	 $("#table2").html("");
		    		 var trs = "<tr ><th >学校名称</th></tr>";
		    		 for(var i = 0; i < tableJson.ss.length; i++) {
						 trs += "<tr draggable='true' id='" + tableJson.ss[i].sid + "'>";
		    			 trs += "<td>" + tableJson.ss[i].sname + "</td>";
		    			 trs += "<td style='display:none'>" + tableJson.ss[i].sid + "</td>";
		    			 trs += "</tr>";
		    		 }
		    		 $("#table2").append(trs);
		    		 
				    $("#table2 tr").mousedown(function(){
			            if(mulTrDrag2[$(this).prop("id")] == undefined) {
			            	$(this).css("backgroundColor", "rgb(18, 183, 245, 0.4)");
			            	mulTrDrag2[$(this).prop("id")] = true;
			            	console.log(mulTrDrag2);
			            } else {
			            	$(this).css("backgroundColor", "#fff");
			            	delete mulTrDrag2[$(this).prop("id")];
			            }
				    })
				}
			
			//显示左表格数据 
			function dataShowLeft() {
		    	 $("#table1").html("");
				 var trs = "<tr style='background-color: #eceffd;'><th class='t2'>专家组</th><th class='t2'>学校名称</th><th class='t3'>项目</th><th class='t4'>管理员</th></tr>";
	    		 $("#table1").append(trs);
				 var tableJson = {};
		    	 tableJson.spgsch = [];
				var gid = $("#group").val();
	    		 var mposition = (curpage - 1) * 20;
	    		 for(var count = 0; count < 20 && mposition < (spgsch).length ; count++,mposition++) {
    	 			tableJson.spgsch.push((spgsch)[mposition]);
	    		 }
		    	 if(tableJson.spgsch.length == 0 ) {
		    		 if(curpage != 1)
		    			 ;
		    	 }
		    	 //显示左table 
// 				<tr style="background-color: #eceffd;">
// 					<th class="t1">专家组名称</th>
// 					<th class="t2">学校名称</th>
// 					<th class="t3">项目</th>
// 					<th class="t4">管理员</th>
// 				</tr>
				var trs = "";
		    	 for(var i = 0; i < tableJson.spgsch.length; i++) {
					 trs += "<tr draggable='true' id='" + tableJson.spgsch[i].sid + "'>";
		    		 trs += "<td class='t2'>" + tableJson.spgsch[i].spgname + "</td>";
		    		 trs += "<td class='t2'>" + tableJson.spgsch[i].sname + "</td>";
		    		 trs += "<td class='t3'>" + tableJson.spgsch[i].pname + "</td>";
		    		 trs += "<td class='t4'>" + pageBeanData.mana.mname + "</td>";
		    		 trs += "<td style='display:none'>" + pageBeanData.mana.mid + "</td>";
		    		 trs += "<td style='display:none'>" + tableJson.spgsch[i].sid + "</td>";
			    	 trs += "</tr>";
		    	}
	    		 $("#table1").append(trs);
			    $("#table1 tr").mousedown(function(){
		            if(mulTrDrag1[$(this).prop("id")] == undefined) {
		            	$(this).css("backgroundColor", "rgb(18, 183, 245, 0.4)");
		            	mulTrDrag1[$(this).prop("id")] = true;
		            } else {  
		            	$(this).css("backgroundColor", "#fff");
		            	delete mulTrDrag1[$(this).prop("id")];
		            }
			    })
			}
			
			//点击分页事件
			function changePage(e) {
				pageBean = {};
				var content = $(e).text();
				if(content == "上一页") {
					if(curpage == 1)
						return;
					else 
						curpage--;
				} else if(content == "下一页") {
					if($("#table1").html() == "")
						return ;
					else 
						curpage++;
				} else curpage = parseInt(content);
				changePageFlag = true;
			    mulTrDrag1 = {}; //多选左表格单元行
				dataShow();
			}
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:190.3rem; height:88rem; margin:0 auto; position:relative;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;专家组学校任务</div>
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
					<span class="s2"><span class='welcomeName' style='display: flex;width: 120px;'></span></span>
					</li>
					<a href="DexInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a> 
					<br>
					<a href="DexGroups.jsp"><li><img src="images/left6.png">建立专家组</li></a>
					<a href="GroupSchoolD.jsp"><li class="now"><img src="images/left7-s.png">专家组学校任务</li></a>
					<a href="DaddMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="DexDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<br>
					<a href="Dproject_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="Dplace-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="DChangeSubPin.jsp"><li><img src="images/left2.png">下级人员密码修改</li></a>
					<a href="DchangePin.jsp"><li><img src="images/left9.png">修改个人密码</li></a>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
					专家组学校任务<span>GROUP SCHOOL TASK</span>
				</div>
				<form class="center">
					<span class="s1">项目名称：</span>
						<select name='group' id='group' style='width:300px' class="sele1" ></select>
					<span class="s2" >专家组名称：</span>
						<select style='margin-top: 3.2rem;' name='city' id='city' class="sele2"></select>
					</br>
					<span class="s3" style='display:none'>县：</span>
						<select name='dist' id='dist' class="sele3" style='display:none'></select>
				</form>
				<div class="first" name='table1'>
					<table id="table1" class='table1'>
						<tr style="background-color: #eceffd;">
<!-- 							<th class="t1">专家组名称</th> --> 
							<th class="t2">专家组</th>
							<th class="t2">学校名称</th>
							<th class="t3">项目</th>
							<th class="t4">管理员</th>
						</tr>
<!-- 						<tr> -->
<!-- 							<td class="t1">123456</td> -->
<!-- 							<td class="t2">福建大学</td> -->
<!-- 							<td class="t3">专家组</td> -->
<!-- 							<td class="t4">项目名称</td> -->
<!-- 						</tr> -->
					</table>
				</div>
				<img src="images/taskM-arr.png" style="position: relative; top: 15rem; right: -1.2rem;"/>
				<div class="second" name='table2'>
					<table id='table2' class='table2'>
						<tr>
							<th>学校名称</th>
						</tr>
					</table>
				</div>
				<ul class="page"><li class="up" onclick="changePage(this);">上一页</li><li class="curpage" onclick="changePage(this);">1</li><li onclick="changePage(this);">2</li><li onclick="changePage(this);">3</li><li onclick="changePage(this);">4</li><li onclick="changePage(this);">5</li><li onclick="changePage(this);">6</li><li onclick="changePage(this);">7</li><li onclick="changePage(this);">8</li><li onclick="changePage(this);">9</li><li onclick="changePage(this);">10</li><li class="next" onclick="changePage(this);">下一页</li></ul>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
