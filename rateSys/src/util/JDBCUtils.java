package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class JDBCUtils {

	private static DataSource ds;
	private static JdbcTemplate template;
	//静态代码块加载数据库配置文件
	static {
		try {
			Properties pro = new Properties();
			pro.load(JDBCUtils.class.getClassLoader().getResourceAsStream ("druid.properties"));
			ds = DruidDataSourceFactory.createDataSource(pro);
			template = new JdbcTemplate(ds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//获取数据库连接
	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
	//关闭连接
	public static void close(Statement stmt,Connection conn) {
		
		close(null, stmt, conn);
	}
	//重载关闭连接
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static DataSource getDataSource() {
		return ds;
	}
	
	public static JdbcTemplate getTemplate() {
//		System.out.println(ds);
		return template;
	}
	
}
