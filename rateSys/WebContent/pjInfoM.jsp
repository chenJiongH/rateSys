<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>评估项目</title>
		<link href="css/pjInfoM.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script src="js/forbiddenAuto.js"></script>
<!-- 		时间控件 -->
	    <script language="javascript" type="text/javascript" src="My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath }/My97DatePicker/WdatePicker.js"></script>
		<style type="text/css">
	        li label{
	            display: inline-block;
	            width: 22px;
	            height: 22px;
				border-radius: 0.5rem;
				background-image: url(images/Uncheckbox.png);
	            vertical-align: middle;
    			position: absolute;
   			    cursor: pointer;
	            border: #dedede solid 0px;
	        } 
	        
 	        input[type=checkbox]:checked+label { 
          	   background-image: url(images/Chcheckbox.png); 
 	        }
	        input[type=checkbox] {
          	  display: none;
	        }
	    </style>
		<script> 
			var curpage;
	    	var selectedData = {};
			$(function() {
				
				$(".lab").click(function(){
					$(this).prev().click();
					console.log($(this).prev());
				})
				//固定表格头行 
				$(".tdiv").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".tdiv").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("table tr:eq(0)").children().each(function() {
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
				$.post("PjInfoMServlet/findPageBean", {'curpage':'1'}, function(data){
					if(data.message != undefined) {
						$("#message").text("页面请求失败，请刷新重试");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					dataShow(data);
				}, 'json')
			})
			
			function messageDisplay() {
				$("#message").css("display", "inline-block");
			}
			function hid() {
				$("#message").css("display", "none");
			}
			//分页数据展示函数
			function dataShow(data) {
		    	 var start = 1;
		    	 var end = 10;
		    	 //全局变量curpage，在点击分页和页面刷新时更新其值 
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
// 					<th class="t1">项目名称</th>
// 					<th class="t2">项目开放时间</th>
// 					<th class="t3">项目结束时间</th>
// 					<th class="t4">是否附件</th>
// 					<th class="t5">启动项目</th>
// 					<th class="t6">显示评分</th>
// 					<th class="t7">项目流程</th>
// 					<th class="t9">操作</th>
// 					<th class="t8" style="display:none" id='pid' name='pid' >项目id</th>
	    		 $("table").html("");
	    		 trs = "<tr><th class='t1'>项目名称</th><th class='t2'>项目开放时间</th><th class='t3'>项目结束时间</th><th class='t4'>是否附件</th><th class='t5'>启动项目</th><th class='t6'>显示评分</th><th class='t7'>项目流程</th><th class='t9'>操作</th></tr>";
	    		 $("table").append(trs);
		    	 trs = "";
		    	 //判断当前页面要显示的是查询出来的数据还是数据库中的数据
		    	 var tableJson = [];
		    	 if(Object.keys(selectedData).length != 0){
		    		 var mposition = (curpage - 1) * 20;
		    		 for(var count = 0; count < 20 && mposition < (selectedData.pj).length ; count++,mposition++)
		    	 		tableJson.push((selectedData.pj)[mposition]);
		    	 } else {
			    	 tableJson = data.pj;		    		 
		    	 }
		    	 if(tableJson.length == 0) {
		    		 return ;
		    	 }
// 				<tr onclick='trClick(this);'>
// 					<td class="t1">漳州市芗城区校长绩效考核评价细则</td>
// 					<td class="t2">2010:02:13 09:20</td>
// 					<td class="t3">2010:02:15 09:20</td>
// 					<td class="t4">TRUE</td>
// 					<td class="t5">TRUE</td>
// 					<td class="t6">TRUE</td>
// 					<td class="t7">校-县-市-省</td>
// 					<td class="t9"><input type="button" name="" id="" value="删除" /></td>
// 					<th class="t8" style="display:none" id='pid' name='pid' >项目id</th>
// 				</tr>
		    		 var reg = new RegExp("-","g");
		    		 var pprocess = "";
		    	 for(var i = 0; i < tableJson.length; i++) {
		    		 
		    		 trs = "<tr onclick='trClick(this);'>";
		    		 trs += "<td class='t1'>" + tableJson[i].pname + "</td>";
		    		 trs += "<td class='t2'>" + tableJson[i].pstime + "</td>";
		    		 trs += "<td class='t3'>" + tableJson[i].petime + "</td>";
		    		 if(tableJson[i].pisannex != 'true' && tableJson[i].pisannex != 'TRUE') {
			    		 trs += "<td class='t4'>否</td>";
		    			 tableJson[i].pisannex = "FALSE";
		    		 } 
		    		 else  
		    			 trs += "<td class='t4'>是</td>"; 
	    		 	 if(tableJson[i].pisstart != 'true' && tableJson[i].pisstart != 'TRUE') {
		    		 	 trs += "<td class='t5'>未启动</td>";
	    		 		 tableJson[i].pisstart = "FALSE";
	    		 	 } else
	    		 		trs += "<td class='t5'>启动</td>";
	    		 	 if(tableJson[i].pdisplayExplain != 'true' && tableJson[i].pdisplayExplain != 'TRUE') {
		    		 	 trs += "<td class='t6'>不显示</td>";
	    		 		 tableJson[i].pdisplayExplain = "FALSE";
	    		 	 } else
	    		 		trs += "<td class='t6'>显示</td>";
	    		 	 pprocess = tableJson[i].pprocess;
	    		 	 pprocess = pprocess.replace(reg, "级-");
	    		 	 if(pprocess.indexOf("抽查") < 0)
	    		 		 pprocess += "级";
    		 	 	 trs += "<td class='t7'>" + pprocess + "</td>";
  					 trs += "<td class='t9'><input type='button' name='' id='' value='删除' onclick='delData(this)'/></td>";
		    		 trs += "<td class='t8' style='display:none' id='pid' name='pid'>" + tableJson[i].pid + "</td>";
		    		 trs += "</tr>";
		    		 $("table").append(trs);
				}
		    	 $("tr:eq(1)").click();
  					$(document).on('mouseenter', "table td", function () {
       
  				        if (this.offsetWidth < this.scrollWidth) {
  				            $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
  				        }
  				    });			
  		
  				    //鼠标离开时，tooltip消失
  				    $(document).on('mouseleave', 'table td', function () {
  				        $(this).attr('data-toggle', '');
  				    });
		     }
			
			//删除数据
			function delData(e) {
		      zdconfirm('系统确认框','确定删除该项目吗？',function(r){ //项目删除确定框 
		    	     if(r) {
		 				$.post("PjInfoMServlet/checkPByIndex", {'pid':$(e).parent().parent().find("td:eq(8)").text()}, function(data) {
		 					// message : true 已经评审完，可以归档。 false 未评审完，已关闭，可以删除，不归档。 middle 未评审完，未关闭，提示关闭后再删除 
							if(data.message == "true") {
							      zdconfirm('系统确认框','是否确定归档并删除该项目？',function(r){ //删除包含指标的项目二次 确定框
						    	     if(r) {				 	  
											$.post("PjInfoMServlet/delData", {'message':'true', 'pid':$(e).parent().parent().find("td:eq(8)").text()}, function(data) {
												if(data.message == "删除项目出错，请重试") {
													$("#message").text(data.message);
													messageDisplay();
													setTimeout(hid, 3000);
													return ;													
												}
												else
													location.reload(true);
											}, 'json')
						    	      }
					    	    });
							} else if(data.message == "false") { //确定删除项目后，直接删除无指标项目 
								zdconfirm('系统确认框','该项目未评审完毕，是否确定删除？',function(r){ //删除包含指标的项目二次 确定框
						    	     if(r) {	 	  
										$.post("PjInfoMServlet/delData", {'message':'false', 'pid':$(e).parent().parent().find("td:eq(8)").text()}, function(data) {
											if(data.message == "删除项目出错，请重试") {
												$("#message").text(data.message);
												messageDisplay();
												setTimeout(hid, 3000);
												return ;													
											}
											else
												location.reload(true);
										}, 'json')
			    	     			}
								})
							} else {
								$("#message").text("该项目未评审完毕，请关闭项目后，再进行删除");
								messageDisplay();
								setTimeout(hid, 3000);
							}
						}, 'json')
		    	      }
	    	    });  
			}
			
			//点击单元行。复制数据
			function trClick(e) {
				$("#projectName").val($(e).find("td:eq(0)").text());
				//同步下拉列表
				$("select option").each(function() {
					 if($(this).text() == $(e).find("td:eq(6)").text()) {
						 $(this).prop("selected", true);
						 return ;
					 }
				})
				
				$("#startTime").val($(e).find("td:eq(1)").text());
				$("#endTime").val($(e).find("td:eq(2)").text());
				//同步复选框
				if($(e).find("td:eq(3)").text() == "是")
					$("#box1").prop("checked", true);
				else $("#box1").prop("checked", false);
				if($(e).find("td:eq(4)").text() == "启动")
					$("#box2").prop("checked", true);
				else $("#box2").prop("checked", false);
				if($(e).find("td:eq(5)").text() == "显示")
					$("#box3").prop("checked", true);
				else $("#box3").prop("checked", false);
				$("#pid").val($(e).find("td:eq(8)").text());
			}

			//点击分页事件
			function changePage(e) {
				var content = $(e).text();
				if(content == 100 || $(e).prev().text() >= 100) { //超过100页不移动 
					return ;
				}
				if(content == "上一页") {
					if(curpage == 1)
						return;
					else 
						curpage--;
				} else if(content == "下一页") {
					if($("table").html() == "")
						return ;
					else 
						curpage++;
				} else curpage = parseInt(content);
				//如果此时有查询数据，则点击分页之后仍然显示此查询数据
				if(Object.keys(selectedData).length != 0){
					dataShow(selectedData);
					return ;					
				}
				$.post("PjInfoMServlet/findPageBean", {'curpage': curpage}, function(data){
					if(data.message != undefined) {
						$("#message").text("页面请求失败，请刷新重试");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					dataShow(data);
				}, 'json')
			}
			
			var selectedData;
			$(function() {
				//查询功能 				
				$("#findData").click(function(){
					$.post("PjInfoMServlet/queryData", $("form").serialize(), function(data) {
						if(data.message != undefined) {
							$("#message").text(data.message);
							messageDisplay();
							setTimeout(hid, 3000);
							return ;
						}
						curpage = 1;
						selectedData = data;
						dataShow(data);
					}, 'json')
				})
				
				//点击增加按钮，增加项目 
				$("#addData").click(function() {
					if($("#projectName").val() == "" || $("#startTime").val() == "" || $("#endTime").val() == "") {
						$("#message").text("请补齐项目的名称、起止时间和流程");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					if($("#startTime").val() >= $("#endTime").val()) {
						$("#message").text("请输入有效的起止日期！！");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					//判断项目名是否已存在 
					$.post("PjInfoMServlet/findPnameByPname", {"pname" : $("#projectName").val()}, function(data) {
						if(data.message == "该项目名称已存在") {
							$("#message").text("该项目名称已存在！！");
							messageDisplay();
							setTimeout(hid, 3000);
							return ;
						}
						else 
						//判断项目名未存在、添加 
						$.post("PjInfoMServlet/addData", $("form").serialize(), function(data) {
							if(data.message == "添加成功") 
								location.reload(true);
							$("#message").text(data.message);
							messageDisplay();
							setTimeout(hid, 3000);
						}, 'json')
						
					}, 'json')

				})
				
				//点击修改按钮，修改当前项目编号 
				$("#changeData").click(function() {
					if($("#startTime").val() >= $("#endTime").val()) {
						$("#message").text("请输入有效的起止日期！！");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					if($("#pid").val() == "") {
						$("#message").text("请点击下方项目记录后，再对项目记录进行修改");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					if($("#projectName").val() == "") {
						$("#message").text("请补充项目名称");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					//判断项目名是否已存在 
					$.post("PjInfoMServlet/changeCheckPnameByPid", {"pname" : $("#projectName").val(), 'pid' : $("#pid").val()}, function(data) {
						if(data.message == "该项目名称已存在") {
							$("#message").text("该项目名称已存在！！");
							messageDisplay();
							setTimeout(hid, 3000);
							return ;
						}		
						//判断项目名未存在、修改 
						else 
						$.post("PjInfoMServlet/changeData", $("form").serialize(), function(data){
							if(data.message == "修改成功")
								location.reload(true);
							else {
								$("#message").text(data.message);
								messageDisplay();
								setTimeout(hid, 3000);
							}
						}, 'json')
						
					}, 'json')

				})
			})
		</script>
	
	</head>
	<body>
	<div class="wrapper" style="width:190.3rem; height:88rem; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;评估项目</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c = null;
					String username = ""; 
					for(int i = 0; cs != null && 
							i < cs.length; i++) {
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
					<a href="CSet.jsp"><li><img src="images/left1.png">区划和学校</li></a>
					<a href="acM.jsp"><li><img src="images/left1.png">下级账号</li></a>
<!-- 					<a href="changeSubPin.jsp"><li><img src="images/left2.png">下级密码</li></a> -->
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="pjInfoM.jsp"><li class="now"><img src="images/left4-s.png">评估项目</li></a>
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
				<!-- <div class="rhead">
					评估项目<span>EVALUATION OF PROJECT</span>
				</div> -->
				<form action="">
					<span id='message' class='message'>提示信息</span>
					<ul>
						<li class="first">项目名称：<input name="projectName" id="projectName" type="text" value=""/></li>
						<li class="second">流程选择：
							<select id='process' name="process">
								<option value="校-县">校级-县（市、区）级</option>
								<option value="校-市">校级-设区市级</option>
								<option value="校-省">校级-省级</option>
								<option value="校-抽查">校级-省级抽查</option>
								<option value="校-县-市">校级-县（市、区）级-设区市级</option>
								<option value="校-县-省">校级-县（市、区）级-省级</option>
								<option value="校-县-抽查">校级-县（市、区）级-省级抽查</option>
								<option value="校-市-省">校级-设区市级-省级</option>
								<option value="校-市-抽查">校级-设区市级-省级抽查</option>
								<option value="校-省-抽查">校级-省级-省级抽查</option>
								<option value="校-县-市-省">校级-县（市、区）级-设区市级-省级</option>
								<option value="校-县-市-抽查">校级-县（市、区）级-设区市级-省级抽查</option>
								<option value="校-县-市-省-抽查">校级-县（市、区）级-设区市级-省级-省级抽查</option>
							</select>
						</li>
						<li class="thirst">项目开放时间：<input autocomplete="off" type="text" id="startTime" name="startTime" style="z-index:55;border: #dedede solid 0.1rem;background-color:#f4f4f4;width: 308px;height:35px" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly"/></li>
						<li class="four">项目结束时间：<input autocomplete="off" type="text" id="endTime" name="endTime" style="z-index:55;border: #dedede solid 0.1rem;background-color:#f4f4f4;width: 306px;height:35px" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly"/></li>
						<li class="ch1"><span>是否附件：</span><input  type="checkbox" name="box1" id="box1" value="TRUE"/><label class='lab'></label></li>
						<li class="ch2"><span>项目启动：</span><input type="checkbox" name="box2" id="box2" value="TRUE"/><label class='lab'></label></li>
						<li class="ch3"><span>显示评分要求：</span><input type="checkbox" name="box3" id="box3" value="TRUE"/><label class='lab'></<label></li>
						<input style="display: none;" name="pid" id="pid" type="text" /></li><br />
						<input style="display: none;" name="" id="" type="text" /></li>
						<br><br><br>
					</ul>
						<span></span><input type="button" name="" id="changeData" class="in1" value="修改" />
						<input type="button" name="" id="addData" class="in2" value="增加" />
						<input type="button" name="" id="findData" class="in3" value="查询" />
				</form>
				<div class='tdiv' style="width: 935px;height: 48rem;overflow-y:scroll;overflow-x:hidden;">
				<table>
					<tr>
						<th class="t1">项目名称</th>
						<th class="t2">项目开放时间</th>
						<th class="t3">项目结束时间</th>
						<th class="t4">是否附件</th>
						<th class="t5">启动项目</th>
						<th class="t6">显示评分</th>
						<th class="t7">项目流程</th>
						<th class="t9">操作</th>
						<th class="t8" style="display:none" id='pid' name='pid' >项目id</th>
					</tr>
				</table>
				</div>
				<ul class="page">
				</ul>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
<script type="text/javascript"> 
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
     
        $("#mb_msg").css({ padding: '2rem', lineHeight: '2rem', 'padding-left': '10rem',
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