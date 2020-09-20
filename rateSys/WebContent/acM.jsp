<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<!-- <meta charset="utf-8" name='viewport' content='width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0'>
		 --><title>下级账号</title>
		<link href="css/acM.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script type="text/javascript">
		    var curpage = 1;
		    //点击查询后，保存返回值数据 
		    var selectedData =[];
		    var CCS;
			var trNum = {};
			var distFold = {};
			var nowCity = "s";
			function createUNameAndPass() {
				var id = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";//0到61。字母0到51，数字52到61  
				var password = "s";
//				random.nextInt() % (n-m+1)+m; 生成[m,n]之间的整数
				if ($("#in5").val() == "市级")
					password = "c";
				else if($("#in5").val() == "县级") 
					password = "d";
				for(var i = 0; i < 5; i++) //生成账号
					password += "" + id.charAt(Math.floor(Math.random()*62));
				if($("#in2").val() == "")
					$("#in2").val(password);
				password = "";
				for(var i = 0; i < 5; i++) //生成密码 
					password += "" + id.charAt(Math.floor(Math.random()*62));
				if($("#in3").val() == "")
					$("#in3").val(password);
			}
			$(function() {
				$("#in1").focus(function() {
					createUNameAndPass();
				})
			})
			$(function(){
				$(".wrapper").css({'width':$(document.body).width()});
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
							"z-index": "2147483584"
						});
					})
				})
				
				curpage = 1;
				selectedData = [];
// 				发送post请求，查找所有市，再根据市的改变去获取该市下的县。
				$.post("acMServlet/findAllCity",function(data) {
					CCS = data;
					console.log(CCS);
					var option = "<option value=''></option>";
					$("#city").append(option);
					for(var i = 0; i < data.length; i++) {
						var city = data[i];
						var option = "<option value='" + city.CNAME + "'>" + city.CNAME + "</option>";
						$("#city").append(option);
						if(i == curpage -1) {
							var li = "<li class='curPage' name='" + data[i].CID + "' onclick='changePage(this);'>" + data[i].CNAME + "</li>";
						} else {
							li = "<li onclick='changePage(this);' name='" + data[i].CID + "'>" + data[i].CNAME + "</li>"
						}
						$("ul[class=page]").append(li);
					}
					//生成页码之后，优先获取第一页的市数据 
// 					console.log(Date.getMilliseconds());
					$.post("acMServlet/findCityManaByCid", {'cid' : $("li[class=curPage]").attr("name")}, function(data){
						console.log(data);
// 					console.log(Date.getMilliseconds());
						dataShowByCity(data);
// 					console.log(Date.getMilliseconds());
					}, 'json')
				}, "json");
				/*


				*/

				//绑定点击生成默认数据按钮发送生成请求 
				$("#createInitData").click(function(){
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					$("#createform").submit();
					console.log("here");
				});
				
				//绑定点击导出按钮发送导出请求 
				$("#export").click(function(){
					$("#exportform").submit();
					$("#ChangeMessage").text("信息正在导出中，请稍后");
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
				});
				
				$("#city").change(function() {
					$.post("acMServlet/findDistByCid", {'cname' : $(this).val()}, function(data) {
						var option = "<option value=''></option>";
						$("#dist").html(option);
						for(var i = 0; i < data.length; i++) {
							var dist = data[i];
							if(i == 0)
								var option = "<option value='" + dist.DNAME + "' selected='selected'>" + dist.DNAME + "</option>";
							else 
							var option = "<option value='" + dist.DNAME + "'>" + dist.DNAME + "</option>";
							$("#dist").append(option);
						}
						$.post("acMServlet/findSchByDid", {"dname" : $("#dist").val(), "cname" : $('#city').val()}, function(data) {
							var option = "<option value=''></option>";
							$("#sch").html(option);
							for(var i = 0; i < data.length; i++) {
								var sch = data[i];
								if(sch.sname == null)
									continue;
								if(i == 0)
									var option = "<option value='" + sch.sname + "' selected='selected'>" + sch.sname + "</option>";
								else 
								var option = "<option value='" + sch.sname + "'>" + sch.sname + "</option>";
								$("#sch").append(option);
							}	
							if($("#sch").val() != "")
								$("#in5").find("option:eq(3)").prop("selected", true);
						}, 'json')
						$("#in5").find("option:eq(2)").prop("selected", true);
					}, 'json')
				})
				
				$("#dist").change(function() {
					if($("#dist").val() == "") {
						$("#in5").find("option:eq(1)").prop("selected", true);
						return;
					}
					$.post("acMServlet/findSchByDid", {"dname" : $("#dist").val(), "cname" : $('#city').val()}, function(data) {
						var option = "<option value=''></option>";
						$("#sch").html(option);
						for(var i = 0; i < data.length; i++) {
							var sch = data[i];
							if(sch.sname == null)
								continue;
							if(i == 0)
								var option = "<option value='" + sch.sname + "' selected='selected'>" + sch.sname + "</option>";
							else 
							var option = "<option value='" + sch.sname + "'>" + sch.sname + "</option>";
							$("#sch").append(option);
						}
						$("#in5").find("option:eq(2)").prop("selected", true);
					}, 'json')
				})
			});

			$("#sch").change(function() {
				if($("#sch").val() != "")
					$("#in5").find("option:eq(3)").prop("selected", true);
				else 
					$("#in5").find("option:eq(2)").prop("selected", true);
			})
			function showPage() {
				$("ul[class=page]").html("");
				for(var i = 0; i < CCS.length; i++) {
					if(i == curpage -1) {
						var li = "<li class='curPage' name='" + CCS[i].CID + "' onclick='changePage(this);'>" + CCS[i].CNAME + "</li>";
					} else {
						li = "<li onclick='changePage(this);' name='" + CCS[i].CID + "'>" + CCS[i].CNAME + "</li>"
					}
					$("ul[class=page]").append(li);
				}
			}
// 			var trNum = {};
// 			var distFold = {};
// 			var nowCity = "";
			function dataShowByCity(data) {	
				//以空间换时间 
				trNum = {};
				$("table").html("");
				 $("table").append(trs);
		   		 var trs = "<tr><th class='t1' style='text-indent: 22px;'>姓名</th><th class='t3'>账号</th><th class='t4' style='    text-indent: 10px;'>密码</th><th class='t2' style='text-indent: 30px'>电话</th><th class='t5'>县区</th><th class='t7'>学校</th><th class='t8'>级别</th></tr>";
		    	 var rank = "";
		    	 var dnameFlag = "";
		    	 var tableRow = 1;
		    	 var rCount = 0;
// 		    	 $("#cityNum").text(data.length);
		    	 for(var i = 0; i < data.length; i++) {
		    		 if(data[i].MNAME == null) data[i].MNAME = "";
		    		 if(data[i].MPHONO == null) data[i].MPHONO = "";
		    		 if(data[i].dname != dnameFlag) { //是县级管理员 
							rCount++;
			    		 trs += "<tr class='parent' id='row" + rCount + "'>";
			    		 if(data[i + 1] != undefined && data[i + 1].sname != "") { //该县级管理员账号下有校级管理员 
							trNum["" + tableRow] = tableRow; //当前行可以折叠扩展 
							trs += "<td class='t1'><span class='minusStyle'>-</span>" + data[i].MNAME + "</td>";
			    		 }
			    		 else //该县级管理员账号下有校级管理员 
			    			 trs += "<td class='t1'>" + data[i].MNAME + "</td>";
			    		tableRow++;
		    		 } else {//非管理员账号 
						//另起一行 						
						tableRow ++;
						trs += "<tr class='child-row" + rCount + "'>";
		    			 trs += "<td class='t1'>" + data[i].MNAME + "</td>";
		    		 }
// 		    		 trs += "<tr onclick='trClick(this);'>";
// 		    		 trs += "<td class='t1'>" + data[i].MNAME + "</td>";
		    		 trs += "<td class='t3'>" + data[i].MUSERNAME + "</td>";
		    		 trs += "<td class='t4'>" + data[i].MPASSWORD + "</td>";
		    		 trs += "<td class='t2'>" + data[i].MPHONO + "</td>";
// 	    			 trs += "<td class='t6'>" + data[i].cname + "</td>";/* 福州市 */
					if(data[i].dname != dnameFlag) {
		    			 dnameFlag = data[i].dname;
			    		 trs += "<td class='t5'>" + data[i].dname + "</td>";/* 台江区 */
					} else 
			    		 trs += "<td class='t5'></td>";/* 台江区 */
		    		 trs += "<td class='t7'>" + data[i].sname + "</td>";/* 福州市林则徐小学 */
		    		 if(data[i].TID.charAt(0) == 'S') rank = "校级";
		    		 else if(data[i].TID.charAt(0) == 'D') rank = "县（市、区）级";
		    		 else rank = "设区市级"; 
		    		 trs += "<td class='t8'>" + rank + "</td>";/* 校级 */
		    		 trs += "<td class='t9' style='display:none'>" + data[i].MID + "</td>";
		    		 trs += "</tr>";
				}
	    		 $("table").html(trs);

	    		 
			   $('tr[id]') //折叠显示 
	            .css("cursor","pointer")
	            .attr("title","点击这里展开/关闭")
	            .click(function(){
	                $(this).siblings('.child-'+this.id).toggle();//当前点击某行同胞行，查找制定子元素类，折叠隐藏
	                var icon = $(this).find("td:eq(0)").find("span");
	                if(icon != undefined) {
	                	if(icon.text() == '+') {
	                		//记录展开标志 
	                		distFold[ $(this).find("td:eq(7)").text() ] = true;	
	                		icon.text('-');
							icon.prop('class', 'minusStyle');	
	                	} else if(icon.text() == '-') {
	                		//点击折叠时，删除展开标志 
							var foldFlag = $(this).find("td:eq(7)").text();
							delete distFold[ foldFlag];
							
	                		icon.text('+');
							icon.prop('class', 'plusStyle');
	                	}
	                }
	            });

	    		 
			   //以空间换取时间 ,来初始化折叠所有未记忆行 
// 			   $("table tr").unbind("click", trClick);
			   for(var key in trNum) {
				   var tr = $("table tr:eq(" + key + ")");
				   var foldFlag = tr.find("td:eq(7)").text();
					if(distFold[ foldFlag] == undefined)
					   if(tr.find("td:eq(0)").text().indexOf('-') != -1)
						   tr.click();
			   }
			   //暂无点击事件 
			   $("table tr").bind("click", trClick);
				$("table tr:eq(1)").click();
				$("table tr:eq(1)").click();

				$(document).on('mouseenter', "table td", function () {
			        if (this.offsetWidth < this.scrollWidth) {
			        	if($(this).text().indexOf('-') == -1 && $(this).text().indexOf('+') == -1)
			            	$(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
			        	else 
			        		$(this).attr('data-toggle', 'tooltip').attr('title', $(this).text().substring(1));
			        }
			    });
			    //鼠标离开时，tooltip消失
			    $(document).on('mouseleave', 'table td', function () {
			        $(this).attr('data-toggle', '');
			    });
			}
			
			
			//点击分页事件
			function changePage(e) {
				var cname = "";
				if($(e).text().indexOf('(') != -1)
					cname = $(e).text().substring(0, $(e).text().indexOf('(')); //去除查询条数的影响
				else  cname = $(e).text();
				for(var i = 1; i <= CCS.length; i++) 
					if(CCS[i-1].CNAME == cname)
						curpage = i;
				showPage();//重新生成页码 
				//如果此时有查询数据，则点击分页之后仍然显示此查询数据
				if(selectedData != undefined && selectedData.length != 0){
					$("ul[class=page] li").each(function() {// 统计市的人数 ，显示页码中正确人数 ,给页码中的市，添加人数 
						if(cnameMap[$(this).text()] != 0) {
							var manaNum = "<span>(" + cnameMap[$(this).text()] + ")</sapn>";
							$(this).append(manaNum);
						}
					})
					selectedDataShow();
					return ;					
				}
				selectFlag = 0;
				//生成页码之后，根据市码获取数据 
				$.post("acMServlet/findCityManaByCid", {'cid' : $("li[class=curPage]").attr("name")}, function(data){
// 					console.log("here" + data);
					console.log(data);
					dataShowByCity(data);
				}, 'json')
			}
			var trNowClick;
			//点击行，把该数据显示在form表单中
			function trClick() {
// 				console.log($(this).find("td:eq(5)").text());
				var tr = this;
				trNowClick = this;
				var name = $(tr).find("td:eq(0)").text();
				if(name.charAt(0) == '-' || name.charAt(0) == '+') name = name.substring(1);
				$("#in1").val(name); 
				$("#in2").val($(tr).find("td:eq(1)").text());
				$("#in3").val($(tr).find("td:eq(2)").text());
				$("#in4").val($(tr).find("td:eq(3)").text());
				$("#in6").val($(tr).find("td:eq(7)").text());
				//获取table中的值，再选中option中的该选项 - 级别 
				var option = $(tr).find("td:eq(6)").text();
				$("#in5 option").each(function() {
					if(($(this).val() == "校级" && option == "校级") || ($(this).val() == "县级" &&option == "县（市、区）级") || ($(this).val() == "市级" &&option == "设区市级")) {
						$(this).prop("selected",true);
						return ;
					}
				});
				//市 
// 				var flage = false;//市没有改动，不需要重新请求 县 
				option = $("li[class=curPage]").text();
				if(option.indexOf('(') != -1){ 
					option = option.substring(0, option.indexOf('('));
				}
				$("#city option[value=" + option + "]").prop("selected", true);

				//根据所选城市查找县区
				var distName = $(tr).find("td:eq(4)").text();
				$.post("acMServlet/findDistByCid", {'cname' : $("#city").val()}, function(data) {
					var option = "<option value=''></option>";
					$("#dist").html(option);
					for(var i = 0; i < data.length; i++) {
						var dist = data[i];
						var option = "";
						if(dist.DNAME == distName) {
							console.log(dist.DNAME + " " + distName);							
							option = "<option value='" + dist.DNAME + "' selected='selected'>" + dist.DNAME + "</option>";
						}
						else 
							option = "<option value='" + dist.DNAME + "'>" + dist.DNAME + "</option>";
						$("#dist").append(option);
					}
					//显示该县区 
					option = "";
					var f = tr;
					option = $(f).find("td:eq(4)").text();
					while(option == "" && $(f)[0] != undefined) { //避免当前点击行为校级管理员。县为空
						option = $(f).find("td:eq(4)").text();
						f = $(f).prev()[0];
					}
					console.log(option);
					if(option != "")
						$("#dist option[value='" + option + "']").prop("selected", true);
					//根据当前县区，查找所有校
					$.post("acMServlet/findSchByDid", {"dname" : $("#dist").val(), "cname" : $('#city').val()}, function(data) {
						var option = "<option value=''></option>";
						$("#sch").html(option);
						for(var i = 0; i < data.length; i++) {
							var sch = data[i];
							if(sch.sname == null)
								continue;
							if(i == 0)
								var option = "<option value='" + sch.sname + "'>" + sch.sname + "</option>";
							else 
							var option = "<option value='" + sch.sname + "'>" + sch.sname + "</option>";
							$("#sch").append(option);
						}
						//选中当前校 
						option = $(tr).find("td:eq(5)").text();
						console.log("sch" + " " + option);
						if(option != "")
							$("#sch option[value=" + option + "]").prop("selected", true);
					}, 'json')
				}, 'json')
				
			}
			// 点击清空按钮，方便查询
			function clearInput() {
				$('input[type=text]').val('');
				// 分别选中四个下拉框，逐个清空
				for(var i = 0; i < 4; i++) {					
					$('select:eq(' + i + ') option:eq(0)').prop('selected', true);
				}
			}
			//点击修改 ，修改当前form表单记录 
			function changeData() {
				var reg = /^[\u4e00-\u9fa5]+$/;
				if(!reg.test($("#in1").val())) {
					$("#ChangeMessage").text("姓名无效");
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					$("#in1").focus();
					return ;
				}
				reg = /^[a-zA-Z][@#$%^&*_!+-~`a-zA-Z0-9]{5,10}$/;
				if(!reg.test($("#in2").val())) {
					$("#ChangeMessage").text("账号无效");
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					$("#in2").focus();
					return ;
				}
				reg = /^[a-zA-Z][@#$%^&*_!+-~`a-zA-Z0-9]{4,10}$/;
				if(!reg.test($("#in3").val())) {
					$("#ChangeMessage").text("密码无效 ");
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					$("#in3").focus();
					return ;
				}
				 reg = /^1\d{10,10}$/;
					if(!reg.test($("#in4").val())) {
						$("#ChangeMessage").text("电话无效");
						$("#ChangeMessage").css("display","inline-block");
						setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
						$("#in4").focus();
						return ;
					}  

			
				$.ajax({
					type: 'post',
					url: 'acMServlet/changeData',
					data: {
						"mname":$("#in1").val(),
						"musername":$("#in2").val(),
						"mpassword":$("#in3").val(),
						"mphono":$("#in4").val(),
						"rank":$("#in5").val(),
						"mid":$("#in6").val(),
						"city":$("#city").val(),
						"dist":$("#dist").val(),
						"sch":$("#sch").val()
					},
					dataType: 'json',
					success:function(data) {
						if(data.message == '修改成功') {
							$(trNowClick).find("td:eq(0)").text($("#in1").val());
							$(trNowClick).find("td:eq(1)").text($("#in2").val());
							$(trNowClick).find("td:eq(2)").text($("#in3").val());
							$(trNowClick).find("td:eq(3)").text($("#in4").val());
							return ;
						}
						//提示data.message
						$("#ChangeMessage").text(data.message);
						$("#ChangeMessage").css("display","inline-block");
						setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					},
				    error:function(data){ 
						$("#ChangeMessage").text("修改出错");
						$("#ChangeMessage").css("display","inline-block");
						setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
				    }
				})
			}
			var selectFlag = 0;
			//点击查询 ，查询当前form表单已有条件数据 
			
			var cnameMap = {}; //统计市的人数 
			function selectData() {
				selectFlag = 1;
				if($("input[name=schoolName]").val() == "" && $("#in5").val() == "" && $("#city").val() == "" && $("#dist").val() == "" && $("#sch").val() == "" && $("#in1").val() =="" && $("#in2").val() == "" && $("#in4").val() == "") {
					$("#ChangeMessage").text("请输入任意查询条件");
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					return ;
				} 
// 				if($("#in1").val() == "" && $("#in4").val() == "") {
// 					$("#changemessage").text("姓名和电话不可省略...");
// 					$("#changemessage").css("display","inline-block");
// 					settimeout(function(){$("#changemessage").css("display","none");}, 3000);
// 					return ;
// 				}
				if($("#in5").val() == "市级" && $("#city").val() == "") {
					$("#ChangeMessage").text("请输入待查询市"); 
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					return ;
				}
				if($("#in5").val() == "县级" && ($("#city").val() == "" || $("#dist").val() == "" )) {
					$("#ChangeMessage").text("请输入待查询市与县");
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					return ;
				}
				if($("#in5").val() == "校级" && ($("#city").val() == "" || $("#dist").val() == "" || $("#sch").val() == "" )) {
					$("#ChangeMessage").text("请输入待查询市、县、校");
					$("#ChangeMessage").css("display","inline-block");
					setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
					return ;
				}
				var reg;
				if($("#in1").val() != "") {
					reg = /^[\u4e00-\u9fa5]+$/;
					if(!reg.test($("#in1").val())) {
						$("#ChangeMessage").text("姓名无效");
						$("#ChangeMessage").css("display","inline-block");
						setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
						$("#in1").focus();
						return ;
					}
				}
				if($("#in2").val() != "") {
					reg = /^[a-zA-Z][@#$%^&*_!+-~`a-zA-Z0-9]{5,10}$/;
					if(!reg.test($("#in2").val())) {
						$("#ChangeMessage").text("账号无效");
						$("#ChangeMessage").css("display","inline-block");
						setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
						$("#in2").focus();
						return ;
					}
				}
				if($("#in3").val() != "") {
					reg = /^[a-zA-Z][@#$%^&*_!+-~`a-zA-Z0-9]{4,10}$/;
					if(!reg.test($("#in3").val())) {
						$("#ChangeMessage").text("密码无效 ");
						$("#ChangeMessage").css("display","inline-block");
						setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
						$("#in3").focus();
						return ;
					}
				}
				if($("#in4").val() != "") {
					 reg = /^1\d{10,10}$/;
						if(!reg.test($("#in4").val())) {
							$("#ChangeMessage").text("电话无效");
							$("#ChangeMessage").css("display","inline-block");
							setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
							$("#in4").focus();
							return ;
						}  
				}
				$.ajax({
					type: 'post',
					url: 'acMServlet/selectData',
					data: {
						"mname":$("#in1").val(),
						"musername":$("#in2").val(),
						"mpassword":$("#in3").val(),
						"mphono":$("#in4").val(),
						"rank":$("#in5").val(),
						"mid":$("#in6").val(),
						"city":$("#city").val(),
						"dist":$("#dist").val(),
						"sch":$("#sch").val(),
						'schoolName':$("input[name=schoolName]").val()
						// 增加校名模糊查询 
					},
					dataType: 'json',
					success:function(data) {
						console.log(data);
						if(data.message != undefined) {
							$("#ChangeMessage").text(data.message);
							$("#ChangeMessage").css("display","inline-block");
							setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
						      return ;
						}
						cnameMap = {};
						showPage(); 
						//重新生成页码 
						selectedData = data;
						$("ul[class=page] li").each(function() {
							cnameMap[$(this).text()] = 0
						})
						for(var i = 0; i < data.length; i++) {
							cnameMap[data[i].cname] ++;
						}
						
						$("ul[class=page] li").each(function() {// 给页码中的市，添加人数 
							if(cnameMap[$(this).text()] != 0) {
								var manaNum = "<span>(" + cnameMap[$(this).text()] + ")</sapn>";
								$(this).append(manaNum);
							}
						})
						selectedDataShow();
					},	
				    error:function(data){ 
						$("#ChangeMessage").text("未查询到符合条件的账号");
						$("#ChangeMessage").css("display","inline-block");
						setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
				    }
				})
				
			}
			function selectedDataShow() {
				var data = selectedData;
				//以空间换时间 
				trNum = {};
				$("table").html("");
				 $("table").append(trs);
		   		 var trs = "<tr><th class='t1' style='text-indent: 22px;'>姓名</th><th class='t3'>账号</th><th class='t4' style='    text-indent: 10px;'>密码</th><th class='t2' style='text-indent: 3px'>电话</th><th class='t5'>县区</th><th class='t7'>学校</th><th class='t8'>级别</th></tr>";
		    	 var rank = ""; 
		    	 var dnameFlag = "";
		    	 var tableRow = 1;
		    	 var rCount = 0;
// 		    	 $("#cityNum").text(selectedData.length);
		    	 var textAndNum = $("li[class=curPage]").text();//市+人数 
		    	 var nowPageText = textAndNum.substring(0, textAndNum.indexOf('('));
		    	 for(var i = 0; i < data.length && data[i].cname != nowPageText; i++);
		    	 var start = i;
		    	 for( ; i < data.length && data[i].cname == nowPageText; i++) {
		    		 if(data[i].MNAME == null) data[i].MNAME = "";
		    		 if(data[i].MPHONO == null) data[i].MPHONO = "";
		    		 if(data[i].dname != dnameFlag) { //是县级管理员 
							rCount++;
			    		 trs += "<tr class='parent' id='row" + rCount + "'>";
			    		 if(data[i + 1] != undefined && data[i + 1].sname != "") { //该县级管理员账号下有校级管理员 
							trNum["" + tableRow] = tableRow; //当前行可以折叠扩展 
							trs += "<td class='t1'><span class='minusStyle'>-</span>" + data[i].MNAME + "</td>";
			    		 }
			    		 else //该县级管理员账号下有校级管理员 
			    			 trs += "<td class='t1'>" + data[i].MNAME + "</td>";
			    		tableRow++;
		    		 } else {//非管理员账号 
						//另起一行 						
						tableRow ++;
						trs += "<tr class='child-row" + rCount + "'>";
		    			 trs += "<td class='t1'>" + data[i].MNAME + "</td>";
		    		 }
// 		    		 trs += "<tr onclick='trClick(this);'>";
// 		    		 trs += "<td class='t1'>" + data[i].MNAME + "</td>";
		    		 trs += "<td class='t3'>" + data[i].MUSERNAME + "</td>";
		    		 trs += "<td class='t4'>" + data[i].MPASSWORD + "</td>";
		    		 trs += "<td class='t2'>" + data[i].MPHONO + "</td>";
// 	    			 trs += "<td class='t6'>" + data[i].cname + "</td>";/* 福州市 */
					if(data[i].dname != dnameFlag) {
		    			 dnameFlag = data[i].dname;
			    		 trs += "<td class='t5'>" + data[i].dname + "</td>";/* 台江区 */
					} else 
			    		 trs += "<td class='t5'></td>";/* 台江区 */
		    		 trs += "<td class='t7'>" + data[i].sname + "</td>";/* 福州市林则徐小学 */
		    		 if(data[i].TID.charAt(0) == 'S') rank = "校级";
		    		 else if(data[i].TID.charAt(0) == 'D') rank = "县（市、区）级";
		    		 else rank = "设区市级"; 
		    		 trs += "<td class='t8'>" + rank + "</td>";/* 校级 */
		    		 trs += "<td class='t9' style='display:none'>" + data[i].MID + "</td>";
		    		 trs += "</tr>";
				}
	    		 $("table").html(trs);

	    		 
				   $('tr[id]') //折叠显示 
	            .css("cursor","pointer")
	            .attr("title","点击这里展开/关闭")
	            .click(function(){
	                $(this).siblings('.child-'+this.id).toggle();//当前点击某行同胞行，查找制定子元素类，折叠隐藏
	                var icon = $(this).find("td:eq(0)").find("span");
	                if(icon != undefined) {
	                	if(icon.text() == '+') {
	                		//记录展开标志 
							distFold[ $(this).find("td:eq(7)").text() ] = true;	
	                		icon.text('-');
							icon.prop('class', 'minusStyle');	
	                	} else if(icon.text() == '-') {
	                		//点击折叠时，删除展开标志 
							var foldFlag = $(this).find("td:eq(7)").text();
							delete distFold[ foldFlag];
							
	                		icon.text('+');
							icon.prop('class', 'plusStyle');
	                	}
	                }
	            });
			   //以空间换取时间 ,来初始化折叠所有未记忆行 
// 			   $("table tr").unbind("click", trClick);
			   for(var key in trNum) {
				   var tr = $("table tr:eq(" + key + ")");
				   var foldFlag = tr.find("td:eq(7)").text();
					if(distFold[ foldFlag] == undefined)
					   if(tr.find("td:eq(0)").text().indexOf('-') != -1)
						   tr.click();
			   }
			   //暂无点击事件 
			   $("table tr").bind("click", trClick);
			   
			   trNowClick = $("table tr:eq(1)")[0];
// 			   onclick='trClick(this);'  
// 	    	 setTimeout(function() {$("table tr:eq(1)").click()}, 1000);
				
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
		</script>
		<script>
		function daoru() {
			var filename = getFilename($("input[type=file]").val());
			var type = filename.substring(filename.lastIndexOf('.')+1);
			if(type != "xls") {
				$("#ChangeMessage").text("请导入.xls文件");
				$("#ChangeMessage").css("display","inline-block");
				setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
				return ;
			} 
			$("#filepath").val(filename);
// 			alert($("#filepath").val());
			//异步提交文件表单 
			var targetUrl = $("#daoruform").attr("action");    
		    var form1 = new FormData($( "#daoruform" )[0]);
// 		    alert(form1);
			//false
			console.log(targetUrl);
		    $.ajax({ 
		     type:'post',  
		     url:targetUrl, 
		     cache: false,    //上传文件不需缓存
		     processData: false, //需设置为false。因为data值是FormData对象，不需要对数据做处理
		     contentType: false, //需设置为false。因为是FormData对象，且已经声明了属性enctype="multipart/form-data"
		     data:form1,
		     dataType: 'json',
// 		     async:false,
		     success:function(data) {
				$("#ChangeMessage").text("导入成功");
				$("#ChangeMessage").css("display","inline-block");
				setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
				location.reload(true);
		     },
		     error:function(data){ 
				$("#ChangeMessage").text("导入失败");
				$("#ChangeMessage").css("display","inline-block");
				setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
		     }
		    });
			$("#ChangeMessage").text("数据正在导入过程中，请稍做休息");
			$("#ChangeMessage").css("display","inline-block");
			setTimeout(function(){$("#ChangeMessage").css("display","none");}, 3000);
		}
		
		function getFilename(path) {
			var po = path.lastIndexOf("\\");
			return path.substring(po+1);
		}
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
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;下级账号</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c = null;
					String username = ""; 
					for(int i = 0; cs != null && i < cs.length; i++) {
						c = cs[i];
						if("username".equals(c.getName())){
							username = c.getValue();
						}
					}
				%>
				<p style="width:180px;">用户：<%=username %></p>
				<ul>
					<li style="height: 70px;margin-top: 20px;">
					<span class="s1" style='margin-left: -10px;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName' style='display: flex;width: 120px;'></span></span>
					</li>
					<a href="CSet.jsp"><li><img src="images/left1.png">区划和学校</li></a>
					<a href="acM.jsp"><li class="now"><img src="images/left1-s.png">下级账号</li></a>
<!-- 					<a href="changeSubPin.jsp"><li><img src="images/left2.png">下级密码</li></a> -->
					<li style='padding: 0px;height: 12px;'></li>
					
					<a href="pjInfoM.jsp"><li><img src="images/left4.png">评估项目</li></a>
					<a href="indexSet.jsp"><li><img src="images/left5.png">评估指标</li></a>
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
				<div class="rhead">
					下级账号<span>THE LOWER ACCOUNT</span>
				</div>
				<form action="">
					<span id='ChangeMessage' class='ChangeMessage'>修改不成功</span>
					<ul>

						<li class="first" >姓名：<input name="" id="in1" type="text" value=""/></li>
						<li >账号：<input name="" id="in2" type="text" /></li>
						<li >密码：<input name="" id="in3" type="text" /></li>
						<li class="shorttext">市：
							<select id="city" name=""></select>
						</li>
						<li>县区：
							<select id="dist" name=""></select>
						</li>
						<li>学校：
							<select id="sch" name=""></select>
						</li>
						<li class="first">电话：<input name="" id="in4" type="text" /></li>
						<li style="margin-left: 42px;">级别：
							<select id="in5" name="">
								<option value="" ></option>
								<option value="市级">设区市级</option>
								<option value="县级">县（市、区）级</option>
								<option value="校级">校级</option>
							</select>
						</li>
						<li style='margin-left: 5px'>学校名查询：
							<input type='text' id="in7" name='schoolName'>
						</li>
 							<!-- <span style='position: absolute;left: 69%;top: 50%;font-size: 19px;'>当前市管理员人数： <span id='cityNum' style="font-size: 29px;"></span></span> -->
					</ul>
					<input name="" id="" class="in3" style='background:#4bc1d1;' type="button" value="清空" onclick="clearInput()"/>
					<input name="" id="" class="in2" style='background:#ad47e7;'type="button" value="查询" onclick="selectData();"/>
					<input name="" id="" class="in2" type="button" value="修改" onclick="changeData();"/>
					<input name="" id="" class="in1" type="button" value="导入" onclick="$('input[type=file]').click()"/>
					<input name="" id="export" class="in6" type="button" value="导出"/>
					<input name="" id="createInitData" class="in4" type="button" value="生成默认数据" />
					
				</form>
<!-- 				用于生成默认数据 -->
				<form id="createform" method="post" style="display:none" action="acMServlet/createInitData"></form>
<!-- 				用于导入 -->
				<form id="daoruform" method="post" style="display:none" action="acMServlet/daoru" enctype="multipart/form-data">
					 <input type="file" name="uploadFile" id="file" onchange="daoru();">
					 <input id="filepath" type="text" >
				</form>
				<form id="exportform" method="post" style="display:none" action="acMServlet/export"></form>
				<ul class="page">
					
				</ul>
				<div class="tdiv">
				<table>
					<tr>
					
						<th class="t1">姓名</th>
						<th class="t3">账号</th>
						<th class="t4" >密码</th>
						<th class="t2">电话</th>
<!-- 						<th class="t6">市</th> -->
						<th class="t5">县区</th>
						<th class="t7">学校</th>
						<th class="t8">级别</th>
<!-- 						<th class="t9">操作</th> -->
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
