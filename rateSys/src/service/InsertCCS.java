package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import domain.CityCountySchool;
import util.JDBCUtils;

public class InsertCCS {

	public static boolean insert1(List<CityCountySchool> cs) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "";
		//城市编号配置文件
		Properties pro = new Properties();
		try {
		ClassLoader classLoader = InsertCCS.class.getClassLoader();
		InputStream in = classLoader.getResourceAsStream("citiesNum.properties");
		pro.load(new InputStreamReader(in, "utf-8"));
		in.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("共有:" + pro.stringPropertyNames().size());
//		System.out.println("asdf");
//		cityNum = pro.getProperty("福州市");
		try {
			conn = JDBCUtils.getConnection();
			conn.setAutoCommit(false);
			sql = "delete from Cities";
			String sql1 = "delete from Districts";
			String sql2 = "delete from Schools";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			stmt = conn.prepareStatement(sql1);
			stmt.executeUpdate();

			stmt = conn.prepareStatement(sql2);
			stmt.executeUpdate();
			
			int countyNum = 0;
			for(int i = 0; i < cs.size(); i++) {
				if(cs.get(i).getCname() != null) {
					//获取当前城市在配置文件中的编号
					String city = cs.get(i).getCname();
					String cityNum = pro.getProperty(city);
					if(cityNum == null) {
						int count = pro.stringPropertyNames().size()+1;
						if(count < 10) 
							cityNum = "C0000" +  count;
						else if(count < 100) 
							cityNum = "C000" + count;
						else if(count < 1000) 
							cityNum = "C00" + count;
						else if(count < 10000) 
							cityNum = "C0" + count;
						else cityNum = "C" + count;
						pro.setProperty(city, cityNum);
					}
					sql = "insert into Cities values(?,?)";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, cityNum);
					stmt.setString(2, city);
//					System.out.println(stmt);
					stmt.executeUpdate();
					//插入当前城市的县区
					if(i+1 < cs.size() && cs.get(i+1).getDname() != null) {
						for(i+=1 ; i < cs.size(); i++) {
							if(cs.get(i).getDname() != null) {
								String county = cs.get(i).getDname();
								String countyN = "D";
								if(++countyNum < 10)
									countyN += "0000" + countyNum;
								else if(countyNum < 100)
									countyN += "000" + countyNum;
								else if(countyNum < 1000)
									countyN += "00" + countyNum;
								else if(countyNum < 10000)
									countyN += "0" + countyNum;
								else countyN += countyNum;
								sql = "insert into Districts values(?,?,?)";
								stmt = conn.prepareStatement(sql);
								stmt.setString(1, countyN);
								stmt.setString(2, county);
								stmt.setString(3, cityNum);
	//							System.out.println(stmt);
								stmt.executeUpdate();
								//插入该县区下面的校
								if(i+1 < cs.size() && cs.get(i+1).getSname() != null) {
									int schoolNum = 0;
									for(i++; i < cs.size(); i++) {
										if(cs.get(i).getSname() != null) {
											schoolNum ++;
											String school = cs.get(i).getSname();
											String schoolType = cs.get(i).getType();
											String schoolN = "S01" + cityNum.substring(4) + countyN.substring(3);
											if(schoolNum < 10) 
												schoolN += "00" + schoolNum;
											else if(schoolNum < 100) 
												schoolN += "0" + schoolNum;
											else schoolN += schoolNum;
											sql = "insert into Schools values(?,?,?,?)";
											stmt = conn.prepareStatement(sql);
											stmt.setString(1, schoolN);
											stmt.setString(2, school);
											stmt.setString(3, countyN);
											stmt.setString(4, schoolType);
											stmt.executeUpdate();
										} else {
											i--;
											break;
										}
									}
								}
								//校插入结束
							} else {
								i--;
								break;
							}
						}
					}
					//县插入结束
				}
			}
			//市插入结束
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		} finally {
			JDBCUtils.close(stmt, conn);
		}
		return true;
	}
}
