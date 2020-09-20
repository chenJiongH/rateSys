package test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import util.JDBCUtils;

public class testTx {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(JDBCUtils.getDataSource());
	
	public static void main(String[] args) {
		testTx testTransaction = new testTx();
		for(int i = 0; i < 10; i++) {
			new Thread(() -> {
				try {
					testTransaction.testTx();
				} catch (Exception e) { }
			}, "线程" + i).start(); 
		}
	}
	
	@Test //跨函数事务 
	public void testTx() throws SQLException {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
		    testTx1();
		    testTx2();
		    transactionManager.commit(status);
		} catch (Exception e) {
			
			transactionManager.rollback(status);
		} finally {
		}
	}
	
	public void testTx1() throws Exception {
	    String sql = "insert into projects(pid) values('P00047')";
	    jdbcTemplate.update(sql);
	     sql = "insert into projects(pid) values('P00048')";
	    jdbcTemplate.update(sql);
	     sql = "insert into projects(pid) values('P00049')";
	    jdbcTemplate.update(sql);
	}
	
	public void testTx2() throws Exception {
	    String sql = "insert into projects(pid) values('P00050')";
	    jdbcTemplate.update(sql);
	     sql = "insert into projects(pid) values('P00047')";
	    jdbcTemplate.update(sql);
	}
	
}
