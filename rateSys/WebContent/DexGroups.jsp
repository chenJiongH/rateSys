<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>建立专家组</title>
		<link href="css/exGrounps.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<style type="text/css">
	        form label{
	            display: inline-block;
	            width: 2.2rem;
	            height: 2.2rem;
				border-radius: 0.5rem;
				background-image: url(images/Uncheckbox.png);
	            vertical-align: middle;
    			position: absolute;
    			right: 25%; 
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
			var pageBean = {};
			var selectedData = {};
			// 存放所有的项目 
			var PS = {};
			function messageDisplay() {
				$("#message").css("display", "inline-block");
				setTimeout(hid, 3000);
			}
			function hid() {
				$("#message").css("display", "none");
			}
			
			$(function() {
				//显示是否进行实地考察 
				$("#projectName").change(function() {
					var pid = $(this).val();
					$("#onSpot").prop("checked", false);
					if(pid == "") {
						$("#onSpot").css("display", "none");
					} else 
					for(var i = 0; i < pageBean.ps.length; i++) {
						if(pid == pageBean.ps[i].pid) {
							if(pageBean.ps[i].pprocess == null || pageBean.ps[i].pprocess.indexOf("抽查") == -1)
								$("#onSpot").css("display", "none");
							else 
								$("#onSpot").css("display", "inline-block");
						}
					}
				})
				curpage = 1;
				pageBean = {};
				$.post("ExGroupsServlet/findPageBean", {'curpage':curpage}, function(data){
					console.log(data);
					if(data.message != undefined) {
						$("#message").text("页面请求失败，请刷新重试");
						messageDisplay();
						return ;
					}
					var options = "";
					if(data.ps.length != 0)
						options = "<option value='' selected='selected'></option>";
					for(var i = 0; i < data.ps.length; i++) 
						options += "<option value='" + data.ps[i].pid + "'>" + data.ps[i].pname + "</option>";		
					$("select").html(options);
					pageBean.ps = data.ps;
					PS = data.ps; 
					if(data.pgs != null)
						pageBean.gs = data.pgs;
					else if(data.cgs != null )
						pageBean.gs = data.cgs;
					else if(data.dgs != null )
						pageBean.gs = data.dgs;
					dataShow(pageBean);
				}, 'json')
				
				$("#addData").click(function() { //添加
					if($("#projectName").val() == "" || $("#groupName").val() == "") {
						$("#message").text("请补充项目名称和专家组名称");
						messageDisplay();
						return ;
					}
					$.post("ExGroupsServlet/addData", $("form").serialize(), function(data) {
						if(data.message == "添加成功") 
							location.reload(true);
						else {
							$("#message").text(data.message);
							messageDisplay();
						}
					}, 'json')
				})
				
				$("#changeData").click(function() { //修改 
					if($("#projectName").val() == "" || $("#groupName").val() == "") {
						$("#message").text("请补充项目名称和专家组名称");
						messageDisplay();
						return ;
					}
					$.post("ExGroupsServlet/changeData", $("form").serialize(), function(data) {
						$("#message").text(data.message);
						messageDisplay();
						if(data.message == "修改成功") 
							location.reload(true);
					}, 'json')
				})
				
				//点击查询按钮，获取符合查询条件的记录 
				$("#findData").click(function(){
					if($("#projectName").val() == "" && $("#groupName").val() == "") {
						$("#message").text("请输入任意查询条件进行查询");
						messageDisplay();
						return ;
					}
					$.post("ExGroupsServlet/queryData", $("form").serialize(), function(data) {
						if(data.message != undefined) {
							$("#message").text(data.message);
							messageDisplay(); 
							return ;
						}
						/* var options = "<option value='' selected='selected'></option>";
						for(var i = 0; i < data.ps.length; i++) 
							options += "<option value='" + data.ps[i].pid + "'>" + data.ps[i].pname + "</option>";		
						$("select").html(options); */
						selectedData.ps = data.ps;
						if(data.pgs != null )
							selectedData.gs = data.pgs;
						else if(data.cgs != null )
							selectedData.gs = data.cgs;
						else if(data.dgs != null)
							selectedData.gs = data.dgs;
						dataShow(selectedData);
					}, 'json')
				})
			})
			
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
				$.post("ExGroupsServlet/findPageBean", {'curpage':curpage}, function(data){
					if(data.message != undefined) {
						$("#message").text("页面请求失败，请刷新重试");
						messageDisplay();
						return ;
					}
					var options = "";
					if(data.ps.length != 0)
						options = "<option value='' selected='selected'></option>";
					for(var i = 0; i < data.ps.length; i++) 
						options += "<option value='" + data.ps[i].pid + "'>" + data.ps[i].pname + "</option>";		
					$("select").html(options);
					pageBean.ps = data.ps;
					PS =  data.ps;
					if(data.pgs != null)
						pageBean.gs = data.pgs;
					else if(data.cgs != null )
						pageBean.gs = data.cgs;
					else if(data.dgs != null)
						pageBean.gs = data.dgs;
					dataShow(pageBean);
				}, 'json')
			}			
			
			//数据显示 
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
// 					<th class='t1'>项目名称</th>
// 					<th class='t2'>专家组名称</th>
// 					<th class='t3'>操作</th>
// 				</tr>
		    	 $("table").html("");
	    		 trs = "<tr><th class='t1'>项目名称</th><th class='t2'>专家组名称</th><th class='t3'>操作</th></tr>";
	    		 $("table").append(trs);
		    	 trs = "";
		    	 var tableJson = {};
		    	 tableJson.ps = data.ps;
		    	 tableJson.gs = [];
		    	 if(Object.keys(selectedData).length != 0){
		    		 var mposition = (curpage - 1) * 20;
		    		 for(var count = 0; count < 20 && mposition < (selectedData.gs).length ; count++,mposition++)
		    	 		tableJson.gs.push((selectedData.gs)[mposition]);
		    	 } else {
			    	 tableJson.gs = data.gs;		    		 
		    	 }
		    	 if(tableJson.gs == undefined || tableJson.gs.length == 0) {
		    		 $("#projectName option:eq(0)").prop("selected", true);
		    		 $("#groupName").val("");
		    		 $("#onSpot").css("display", 'none');
		    		 $("#onSpot").prop("checked", false);
		    		 return ;
		    	 }
		    	 console.log(tableJson.gs);
		    	 console.log(tableJson.ps);
		    	 for(var i = 0; i < tableJson.ps.length; i++) {
		    		var flag = true;
		    		for(var j = 0 ; j < tableJson.gs.length; j++) {
		    			if(tableJson.gs[j].pid == tableJson.ps[i].pid) {
	// 						<tr>
	// 							<td class='t1'>fjut</td>
	// 							<td class='t2'>专家组</td>
	// 							<td class='t9'><input type="button" name="" id="" value="删除" onclick="delData(this)"></td>
	// 						</tr>
							trs = "<tr onclick='trClick(this);'>";
			    			if(flag) 
					    		 trs += "<td class='t1'>" + tableJson.ps[i].pname + "</td>";
					    	else trs += "<td class='t1'></td>";
				    		 trs += "<td class='t2'>" + tableJson.gs[j].spgname + "</td>";
				    		 trs += "<td class='t9'><input type='button' value='删除' onclick='delData(this)'></td>";
				    		 var gid ;
				    		 if(tableJson.gs[j].pspgid != undefined) gid = tableJson.gs[j].pspgid;
				    		 else if(tableJson.gs[j].cspgid != undefined) gid = tableJson.gs[j].cspgid;
				    		 else if(tableJson.gs[j].dspgid != undefined) gid = tableJson.gs[j].dspgid;
				    		 trs += "<td style='display:none'>" + gid + "</td>";
				    		 trs += "</tr>";
				    		 $("table").append(trs);
			    			 flag = false;
			    		}
		    		}
		    	 }
		    	 
		    	 $("table tr:eq(1)").click();
		    	 $("#projectName").change();
			}
			
			function delData(e) {
		      zdconfirm('系统确认框','确定删除该专家组吗？',function(r){ //项目删除确定框 
		    	     if(r) {					
							 //确定删除项目后，删除无指标项目 
							$.post("ExGroupsServlet/delData",{'spgid': $(e).parent().parent().find("td:eq(3)").text()}, function(data) {
								if(data.message == "删除成功")
									location.reload(true);
								else {
									$("#message").text(data.message);
									messageDisplay();
								}
							}, 'json')
		    	      }
	    	    });
			}
			$("#groupName").change(function() {
				
			})
			//复制选中行到form表单中
			function trClick(e) {
				$("#groupName").val($(e).find("td:eq(1)").text());
				$("#gid").val($(e).find("td:eq(3)").text());
				//查找点击行专家组所在的项目名称，显示在下拉框中 
				var data = {}; var pid = "";
				if(Object.keys(selectedData).length != 0)
					data = selectedData;
				if(Object.keys(pageBean).length != 0)
					data = pageBean;
				for(var i = 0; i < data.gs.length; i++) {
					for(var key in data.gs[i]) {
						if(data.gs[i][key] == $("#gid").val())
							pid = data.gs[i].pid;
						break; 
					}
					if(pid != "")
						break;
				}

				$("select option").each(function() {
					if($(this).val() == pid) {
						$(this).attr("selected", true);
					} else $(this).attr("selected", false);
				})
			}
		</script>
		<!-- 		实现模糊查询功能 -->
		<script>
			$(document).ready(function(){
				document.getElementById("groupName").oninput=function(){
					var val = $(this).val();
					var lis = "";
					$.post("ExGroupsServlet/fuzzySelect", { 'gName' : $("#groupName").val()}, function(data) {
						console.log(data);
						for(var i = 0; i < data.length; i++)
							lis += "<li onclick='mayNameClick(this)'>" + data[i].SPGNAME + "</li>";
						console.log(lis);
						$("#mayName").css("display","block");
						$("#mayName").html(lis);
					}, 'json')
				}
				
				$("body").click(function(){
					$("#mayName").css("display","none")
				})
				
				$("#groupName").click(function(){ //点击时，则显示所有专家组 
						var val = $(this).val();
						flage = true; 
						var lis = "";
						console.log("here");
						$.post("ExGroupsServlet/fuzzySelect", { 'gName' : ''}, function(data) {
							console.log(data);
							for(var i = 0; i < data.length; i++)
								lis += "<li onclick='mayNameClick(this)'>" + data[i].SPGNAME + "</li>";
							console.log(lis);
							$("#mayName").css("display","block");
							$("#mayName").html(lis);
						}, 'json')
				})
			})
			
			function mayNameClick(e) {
				//专家组成员的值为当前点击值的文本
				$("#groupName").val($(e).text());
			}
		</script>
		<style>
			#test1{
				visibility: hidden;
			}
			#test1 +label{
				width: 18px;
				height: 18px;
				background-color: #f8f8f8;
				border: #dedede solid 1px;
				display: inline-block;
				position: relative;
				top: -20px;
				left: 60px;
				border-radius: 4px;
				cursor: pointer;
				overflow: hidden;
			}	
			#test1:checked +label:before{
				content: " ";
				width: 18px;
				height: 18px;
				background: url("images/boxSelected.png") no-repeat;
				background-size: 100% auto;
				display: block;
				color: #ffffff;
				text-align: center;
				font-weight: bolder;
				line-height: 18px;
			} 
			#onSpot::before {
			    content: "实地考察：";
			    font: 16px "微软雅黑";
			    position: relative;
			    color: #505050;
			    /* width: 147px; */
			    left: -50px;
			}
			#onSpot {
			margin-left: 50px;
			margin-left: 110px;
    		height: 25px;
   		    margin-top: -5px;
			}
		</style>
	</head>
	<body>
	<div class="wrapper" style="width:190.3rem; height:88rem; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;建立专家组</div>
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
					<a href="DexGroups.jsp"><li class="now"><img src="images/left6-s.png">建立专家组</li></a>
					<a href="GroupSchoolD.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
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
				<!-- <div class="rhead">
					建立专家组<span>ESTABLISHMENT OF EXPERT GROUP</span>
				</div> -->
				<form action="">
					<span id='message' class='message'>提示信息</span>
					<span>项目名称：</span>
						<select id='projectName' name='projectName'> </select>
					<span  style='margin-left: 4rem;'>专家组名称：</span>
						<input style='background: #f8f8f8 url(images/selectArr.png) no-repeat 28.4rem center;' autocomplete="off" type='text' id='groupName' name='groupName'>
					<ol id='mayName' class='mayName'>
<!-- 						<li onclick='mayNameClick(this)'>1231</li> -->
<!-- 						<li onclick='mayNameClick(this)'>1231</li> -->
<!-- 						<li onclick='mayNameClick(this)'>1231</li> -->
					</ol>
					<input style="display:none" type='text' id='gid' name='gid'>
					<input id='addData' type="button" value='添加'>
					<input id='changeData' type="button" value='修改'>
					<input id='findData' type="button" value='查询'>
<!-- 					<input class='onSpot' style="display : none;" name='onSpot' value='1' id='onSpot' type='checkbox' > -->
				</form>
				<div class='tdiv'>
				<table>
					<thead>
						<tr>
							<th class='t1'>项目名称</th>
							<th class='t2'>专家组名称</th>
							<th class='t3'>操作</th>
						</tr>
					</thead>
					<tbody>
					
					</tbody>
				</table>
				</div>
				<ul class="page">
				<li class="up" onclick="changePage(this);">上一页</li>
				<li class="curpage" onclick="changePage(this);">1</li><li onclick="changePage(this);">2</li><li onclick="changePage(this);">3</li><li onclick="changePage(this);">4</li><li onclick="changePage(this);">5</li><li onclick="changePage(this);">6</li><li onclick="changePage(this);">7</li><li onclick="changePage(this);">8</li><li onclick="changePage(this);">9</li><li onclick="changePage(this);">10</li><li class="next" onclick="changePage(this);">下一页</li>
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