<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>专家信息管理</title>
		<link rel="stylesheet" href="css/exInfoM.css" > 
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/cleanAll.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script> 
		var ps = {};
		var ds = {};
		var cs = {};
		var curpage = 1;
		function createUNameAndPass() {
			var id = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";//0到61。字母0到51，数字52到61  
			var password = "";
//			random.nextInt() % (n-m+1)+m; 生成[m,n]之间的整数
			for(var i = 0; i < 5; i++) //生成账号
				password += "" + id.charAt(Math.floor(Math.random()*62));
			if($("#txt2").val() == "")
				$("#txt2").val(password);
			password = "";
			for(var i = 0; i < 5; i++) //生成密码 
				password += "" + id.charAt(Math.floor(Math.random()*62));
			if($("#txt3").val() == "")
				$("#txt3").val(password);
		}
		
		function regCheckName(name) { //检测名称 
			var reg = /^[a-zA-Z\u4e00-\u9fa5]+$/;
			if(!reg.test(name)) 
				return false
			return true;
		}
		function regCheckPhono(phono) { //检测电话
			var reg = /^1\d{10,10}$/;
			if(!reg.test(phono)) 
				return false
			return true;
		}
		function regCheckAge(age) {//检测年龄
			var reg = /^[1-9][0-9]{0,2}$/;
			console.log(reg + " " + age);
			if(!reg.test(age)) 
				return false
			return true;
		}
		function messageDisplay() {
			$("#message").css("display", "inline-block");
		}
		function hid() {
			$("#message").css("display", "none");
		}
			$(function() {
				$("#txt1").focus(function() {
					createUNameAndPass();
				})
				$(".tdiv").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".tdiv").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("table tr:eq(0)").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
							"left": "0px",
	// 						"background-color": "white",
// 							"z-index": "21"
						});
					})
				})
				
// 				//姓名、电话、年龄 检验 
// 				$("#txt1").blur(function() {
// 					if(!regCheckName($(this).val())){
// 						$("#message").text("姓名无效");
// 						messageDisplay();
// 						setTimeout(hid, 3000);
// 						$(this).val("");
// 					}
// 				})
// 				$("#txt4").blur(function() {
// 					if(!regCheckPhono($(this).val())){
// 						$("#message").text("电话无效");
// 						messageDisplay();
// 						setTimeout(hid, 3000);
// 						$(this).val("");
// 					}
// 				})
// 				$("#txt7").blur(function() {
// 					if(!regCheckAge($(this).val())){
// 						$("#message").text("年龄无效");
// 						messageDisplay();
// 						setTimeout(hid, 3000);
// 						$(this).val("");
// 					}
// 				})
				
				curpage = 1;		
				$.post("exInfoMServlet/getPageBean", function(data) {
					ps = data.ps;ds = data.ds;cs = data.cs;
					if(ps != null && ps.length != 0)
						dataShow(ps);
					else if(cs != null && cs.length != 0)
						dataShow(cs);
					else if(ds != null && ds.length != 0)
						dataShow(ds);
					else {
						/* $("#message").text("当前无专家记录，请添加专家账号");
						messageDisplay();
						setTimeout(hid, 3000); */
					}
				}, "json")
			})
			
			//展示数据
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
// 				<tr>
// 					<th class='t1'>姓名</th>
// 					<th class='t2'>电话</th>
// 					<th class='t3'>单位</th>
// 					<th class='t4'>专业</th>
// 					<th class='t5'>年龄</th>
// 					<th class='t6'>职称</th>
// 					<th class='t7'>职务</th>
// 					<th class='t8'>擅长领域</th>
// 					<th class='t9'>学段</th>
// 					<th class='t10'>级别</th>
// 					<th class='t11'>操作</th>
// 				</tr>
	    		 $("table").html("");
	    		 trs = "<tr><th class='t1'>姓名</th><th class='t2'>电话</th><th class='t3'>单位</th><th class='t4'>专业</th><th class='t5'>年龄</th><th class='t6'>职称</th><th class='t7'>职务</th><th class='t8'>擅长领域</th><th class='t9'>学段</th><th class='t10' style='display:none'>级别</th><th class='t11'>操作</th></tr>";
	    		 $("table").append(trs);

		    	 
		    	 var tableJson = [];
	    		 var mposition = (curpage - 1) * 20;
	    		 for(var count = 0; count < 20 && mposition < data.length ; count++,mposition++)
	    	 		tableJson.push(data[mposition]);
		    	 if(tableJson.length == 0) {
		    		 return ;
		    	 }
		    	 
// 				<tr onclick='copy(this);'>
// 					<td style='display:none'>spid</td>
// 					<td class='t1'>张晓琳</td>
// 					<td class='t2'>13699969696</td>
// 					<td class='t3'>福州市</td>
// 					<td class='t4'>会计</td>
// 					<td class='t5'>35岁</td>
// 					<td class='t6'>中级</td>
// 					<td class='t7'>会计</td>
// 					<td class='t8'>金融</td>
// 					<td class='t9'>第三阶段</td>
// 					<td class='t10'>C001</td>
// 					<td class='t11'><input type='button' name='' id='' value='删除' /></td>
// 				</tr>

		    	 for(var i = 0; i < tableJson.length; i++) {
		    		 trs = "<tr onclick='copy(this);'>";
		    		 if(tableJson[i].pspid != undefined)
		    		 	trs += "<td style='display:none'>" + tableJson[i].pspid + "</td>";
		    		 else if(tableJson[i].cspid != undefined) 
		    			trs += "<td style='display:none'>" + tableJson[i].cspid + "</td>";
		    		 else if(tableJson[i].dspid != undefined)
		    			trs += "<td style='display:none'>" + tableJson[i].dspid + "</td>";
		    		 trs+= "<td class='t1'>" + tableJson[i].spname + "</td>";
		    		 trs+= "<td class='t2'>" + tableJson[i].spphone + "</td>";
		    		 trs+= "<td class='t3'>" + tableJson[i].sporganization + "</td>";
		    		 trs+= "<td class='t4'>" + tableJson[i].spspecialty + "</td>";
		    		 trs+= "<td class='t5'>" + tableJson[i].spage + "</td>";
		    		 trs+= "<td class='t6'>" + tableJson[i].sptitle + "</td>";
		    		 trs+= "<td class='t7'>" + tableJson[i].sprank + "</td>";
		    		 trs+= "<td class='t8'>" + tableJson[i].spfields + "</td>";
		    		 trs+= "<td class='t9'>" + tableJson[i].spgrade + "</td>";
		    		 trs+= "<td class='t10' style='display:none'>" + tableJson[i].mid + "</td>";
		    		 trs+= "<td class='t11'><input type='button' name='' id='' value='删除' onclick='del(this);'/></td>"
	    		 	 trs += "<td style='display:none'>" + tableJson[i].spusername + "</td>";
	    		 	 trs += "<td style='display:none'>" + tableJson[i].sppassword + "</td>";

		    		 trs += "</tr>";
		    		 $("table").append(trs);
		    	 }
		    	 
					$(document).on('mouseenter', "table td", function () {
				        
				        if (this.offsetWidth < this.scrollWidth) {
				            $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
				        }
				    });
				    //鼠标离开时，tooltip消失
				    $(document).on('mouseleave', 'table td', function () {
				        $(this).attr('data-toggle', '');
				    });
				    
				    $("table tr:eq(1)").click();
			}
			
			//点击分页事件
			function changePage(e) {
				var content = $(e).text();
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
				if(ps != null && ps.length != 0)
					dataShow(ps);
				if(cs != null && cs.length != 0)
					dataShow(cs);
				if(ds != null && ds.length != 0)
					dataShow(ds);
			}
			
			//删除单元行
			function del(e) {
			     
			      zdconfirm('系统确认框','确认删除该专家信息吗？',function(r){
				     if(r) {
						var row = $(e).parent().parent().index();
						var delId = $(e).parent().parent().find("td:eq(0)").text();
						$.post("exInfoMServlet/del",{"spid":delId},function() {
							$("#message").text("删除成功");
							messageDisplay();
							setTimeout(hid, 3000);
							$("table tr:eq(" + row + ")").remove();	
							if(ps != null && ps.length != 0)
								ps.splice(row, 1);
							if(cs != null && ps.length != 0)
								cs.splice(row, 1);
							if(ds != null && ps.length != 0)
								ds.splice(row, 1);
						})
				      }
			    });  

			}
			
			//点击单元行。复制数据
			function copy(e) {
				$("#txt0").val($(e).find("td:eq(0)").text());
				$("#txt1").val($(e).find("td:eq(1)").text());
				$("#txt2").val($(e).find("td:eq(12)").text());
				$("#txt3").val($(e).find("td:eq(13)").text());
				$("#txt4").val($(e).find("td:eq(2)").text());
				$("#txt5").val($(e).find("td:eq(3)").text());
				$("#txt6").val($(e).find("td:eq(4)").text());
				$("#txt7").val($(e).find("td:eq(5)").text());
				$("#txt8").val($(e).find("td:eq(6)").text());
				$("#txt9").val($(e).find("td:eq(7)").text());
				$("#txt10").val($(e).find("td:eq(8)").text());
				$("#txt11").val($(e).find("td:eq(9)").text());
				$("#txt12").val($(e).find("td:eq(10)").text());
			}
			//增加按钮
			function add(){
				if($("#txt2").val() == "" || $("#txt3").val() == "") {
					$("#message").text("请输入账号和密码");
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				}
// 				//姓名、电话、年龄 检验 
				if(!regCheckName($("#txt1").val())){
					$("#message").text("姓名无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt1").focus("");
					return ; 
				}
				/* if(!regCheckPhono($("#txt4").val())){
					$("#message").text("电话无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt4").focus("");
					return ;
				}
				if(!regCheckAge($("#txt7").val())){
					$("#message").text("年龄无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt5").focus("");
					return ;
				} */
// 				var data = $("form").serialize();
				$.post("exInfoMServlet/add",$("form").serialize(),function(data) {
					if(data.message == "添加成功")
						location.reload(true);
					else {
						$("#message").text(data.message);
						messageDisplay();
						setTimeout(hid, 3000);
					}
				}, 'json')
			}
			
			//修改按钮 
			function change(){
				if($("#txt0").val() == "") {
					alert("请点击下方账号后，再进行该账号信息的修改");
				}
// 				//姓名、电话、年龄 检验 
				if(!regCheckName($("#txt1").val())){
					$("#message").text("姓名无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt1").focus(""); 
					return ;
				} 
				/* if(!regCheckPhono($("#txt4").val())){
					$("#message").text("电话无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt4").focus("");
					return ;
				}
				if(!regCheckAge($("#txt7").val())){
					$("#message").text("年龄无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt5").focus("");
					return ;
				} */
				$.post("exInfoMServlet/change",$("form").serialize(),function(data){
					$("#message").text(data.message);
					messageDisplay();
					setTimeout(hid, 3000);
					if(data.message == "修改成功")
						location.reload(true);
				}, 'json')
			}
		</script>
		<script>
		//查询按钮
		function selected(){
			$.post("exInfoMServlet/select",$("form").serialize(),function(data){
				if(data.message != undefined) {
					return ;
				}
				ps = data.ps;ds = data.ds;cs = data.cs;
				if(ps != null && ps.length != 0)
					dataShow(ps);
				else if(cs != null && cs.length != 0)
					dataShow(cs);
				else if(ds != null && ds.length != 0)
					dataShow(ds);
				else {
					$("table tr").eq(1).nextAll().remove();
				}
			}, 'json');
			$("#message").text("正在查询中");
			messageDisplay();
			setTimeout(hid, 1000);
		}
		</script>
		<!-- 		实现模糊查询功能 -->
		<script>
		$(function() {
			$("#txt1").change(function() {
				console.log("heere");
				createUNameAndPass();
			})
			
		})
		
			$(document).ready(function(){
				document.getElementById("txt1").oninput=function(){
					var val = $(this).val();
					if(val != ""){
						var lis = "";
						$.post("exInfoMServlet/fuzzySelect", {'name': val}, function(data){
							console.log(data);
							for(var i = 0; i < data.length; i++) {
								lis += "<li onclick='mayNameClick(this)'>" + data[i].SPNAME + " : " + data[i].SPORganization + "</li>";
							} 
							console.log(lis);
							$("#mayName").css("display","block");
							$("#mayName").html(lis); 
						}, 'json')
					}else{
						//当input中没有值的时候隐藏ul列表
						$("#mayName").css("display","none");
					}
				}
				
				$("body").click(function(){
					$("#mayName").css("display","none")
				})
			})
			
			function mayNameClick(e) {
				//专家组成员的值为当前点击值的文本去掉所在单位 
				$("#txt1").val($(e).text().substring(0, $(e).text().indexOf(':')));
			} 
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
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;专家信息管理</div>
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
					<a href="CexInfoM.jsp"><li class="now"><img src="images/left3-s.png">专家信息</li></a>
					<br>
					<a href="CexGroups.jsp"><li><img src="images/left6.png">建立专家组</li></a>
					<a href="GroupSchoolC.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="CaddMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="CexDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<br>
					<a href="Cproject_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="Cplace-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="CchangeSubPin.jsp"><li><img src="images/left2.png">下级人员密码修改</li></a>
					<a href="CchangePin.jsp"><li><img src="images/left9.png">修改个人密码</li></a>
				</ul>
			</div>
			<div class="right">
				<!-- <div class="rhead">
					专家信息管理<span>Expert information management</span>
				</div> -->
				<form action="">
					<label id='message' class='message'>提示消息</label>
					<ul>
<!-- 						spid -->
						<input style="display:none" name="txt0" id="txt0" type="text">
						<li class="first" style='margin-left: 5rem;'>姓名：<input autocomplete="off" name="txt1" id="txt1" type="text" value=""/></li>
						<ol id='mayName'>
<!-- 							<li>123</li><li>123</li><li>123</li>  -->
						</ol>
						<li>账号：<input name="txt2" id="txt2" type="text" /></li>
						<li>密码：<input name="txt3" id="txt3" type="text" /></li>
						<li class="first">年龄：<input name="txt7" id="txt7" type="text" /></li>
						<li style='    margin-left: 5rem;'>电话：<input name="txt4" id="txt4" type="text" /></li>
						<li>单位：<input name="txt5" id="txt5" type="text" /></li>
						<li class="first">专业：<input name="txt6" id="txt6" type="text" /></li>
						<li>职称：<input name="txt8" id="txt8" type="text" /></li>
						<li>擅长领域：<input name="txt10" id="txt10" type="text" /></li>
						<li class="longtext">学段：<input name="txt11" id="txt11" type="text" /></li>
						<li>职务：<input name="txt9" id="txt9" type="text" /></li>
						<!-- <li>级别：<input style='background-color: #fff;border:none' name="txt12" id="txt12" type="text" readonly="readonly" /></li> -->
					</ul><br><br><br><br><br><br><br>
					<input style='    position: absolute;    left: 9%;    top: 73%;    left: 16%;' name="" id="" class="in1" type="button" value="增加" onclick="add();"/>
					<input style='    position: absolute;    left: 9%;    top: 73%;' name="" id="" class="in2" type="button" value="修改" onclick="change();"/>
					<input style='    position: absolute;    left: 9%;    top: 73%;    left: 37%;' name="" id="" class="in3" type="button" value="查询" onclick="selected();"/>
					<input style='background-color:#4bc1d1;    position: absolute;    left: 9%;    top: 73%;    left: 51.5%;' name=""  id="" class="in3" type="button" value="清空" onclick="cleanAll();"/>
				
				</form>
				
				<div class="tdiv">
				<table>
					<tr>
						<th class="t1">姓名</th>
						<th class="t2">电话</th>
						<th class="t3">单位</th>
						<th class="t4">专业</th>
						<th class="t5">年龄</th>
						<th class="t6">职称</th>
						<th class="t7">职务</th>
						<th class="t8">擅长领域</th>
						<th class="t9">学段</th>
						<th class="t10" style='display:none'>级别</th>
						<th class="t11">操作</th>
					</tr>
<!-- 					<tr onclick="copy(this);"> -->
<!-- 						<td style="display:none">spid</td> -->
<!-- 						<td class="t1">张晓琳</td> -->
<!-- 						<td class="t2">13699969696</td> -->
<!-- 						<td class="t3">福州市</td> -->
<!-- 						<td class="t4">会计</td> -->
<!-- 						<td class="t5">35岁</td> -->
<!-- 						<td class="t6">中级</td> -->
<!-- 						<td class="t7">会计</td> -->
<!-- 						<td class="t8">金融</td> -->
<!-- 						<td class="t9">第三阶段</td> -->
<!-- 						<td class="t10">C001</td> -->
<!-- 						<td class="t11"><input type="button" name="" id="" value="删除" /></td> -->
<!-- 					</tr> -->
				</table>
				</div>
				<ul class="page">
<!-- 					<li class="up">上一页</li> -->
<!-- 					<li>1</li> -->
<!-- 					<li>2</li> -->
<!-- 					<li>3</li> -->
<!-- 					<li>4</li> -->
<!-- 					<li>5</li> -->
<!-- 					<li>6</li> -->
<!-- 					<li>7</li> -->
<!-- 					<li>8</li> -->
<!-- 					<li class="next">下一页</li> -->
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