package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;

import domain.Acriterion;
import domain.Bcriterion;
import domain.Ccriterion;
import domain.IndexPageBean;
import util.JDBCUtils;

public class IndexSetDao {
	JdbcTemplate template = JDBCUtils.getTemplate();
	
	public void batchAInsert(List<Acriterion> criAS) throws Exception{
		
		String sql = "insert into Acriterion values(?,?,?)";
		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Acriterion a = criAS.get(i);
			    ps.setString(1, a.getAid());
			    ps.setString(2, a.getAname());
			    ps.setString(3, a.getPid());
			}

			@Override
			public int getBatchSize() {
				return criAS.size();
			}
		});
	}
	
	public void batchBInsert(List<Bcriterion> criBS) throws Exception{
			String sql = "insert into Bcriterion values(?,?,?)";
			template.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Bcriterion a = criBS.get(i);
				    ps.setString(1, a.getBid());
				    ps.setString(2, a.getBname());
				    ps.setString(3, a.getAid());
				}
	
				@Override
				public int getBatchSize() {
					return criBS.size();
				}
			});
		
	}
	
	public void batchCInsert(List<Ccriterion> criCS) throws Exception{
		//开启事务管理，当事务管理开启时，template每次获取的都是同一个连接，此时设置连接手动提交则有效
			String sql = "insert into Ccriterion values(?,?,?,?,?,?,?,?,?)";
			template.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Ccriterion a = criCS.get(i);
				    ps.setString(1, a.getCid());
				    ps.setString(2, a.getCname());
				    ps.setString(3, a.getBid());
				    ps.setString(4, a.getIsexplain());
				    ps.setString(5, a.getIsannex());
				    ps.setFloat(6, a.getScore());
				    ps.setFloat(7, a.getSegscore());
				    ps.setFloat(8, a.getThreshhold());
				    ps.setString(9, a.getAssessmethod());
				}
	
				@Override
				public int getBatchSize() {
					return criCS.size();
				}
			});
	}

	public List<Acriterion> findAllAIndex() {
		String sql = "select * from Acriterion";
		return template.query(sql, new BeanPropertyRowMapper<Acriterion>(Acriterion.class));
	}
	
	public List<Bcriterion> findAllBIndex() {
		String sql = "select * from Bcriterion";
		return template.query(sql, new BeanPropertyRowMapper<Bcriterion>(Bcriterion.class));
	}
	
	public List<Ccriterion> findAllCIndex() {
		String sql = "select * from Ccriterion";
		return template.query(sql, new BeanPropertyRowMapper<Ccriterion>(Ccriterion.class));
	}

	public List<Acriterion> findPidAIndex(String pid) {
		String sql = "select * from Acriterion where pid=?";
		return template.query(sql, new BeanPropertyRowMapper<Acriterion>(Acriterion.class), pid);
	}

	public Collection<? extends Bcriterion> findBIndexByAid(String aid) {
		String sql = "select * from Bcriterion where aid=?";
		return template.query(sql, new BeanPropertyRowMapper<Bcriterion>(Bcriterion.class), aid);
	}

	public Collection<? extends Ccriterion> findCIndexByBid(String bid) {
		String sql = "select * from Ccriterion where bid=?";
		return template.query(sql, new BeanPropertyRowMapper<Ccriterion>(Ccriterion.class), bid);
	}

	public void update(IndexPageBean pageBean) throws Exception {
		String sql = "";
		//更新c指标
		for(Ccriterion c : pageBean.getCs()) {
			sql = "update Ccriterion set ";
			sql += "isexplain='" + c.getIsexplain() + "',";
			sql += "Threshhold=" + c.getThreshhold() + ",";
			sql += "Assessmethod='" + c.getAssessmethod() + "',";		
			sql += "Isannex='" + c.getIsannex() + "',";
			sql += "Score=" + c.getScore() + ",";
			sql += "Segscore=" + c.getSegscore();
			sql += " where cid='" + c.getCid() + "'";
			if(template.update(sql) == 0)
				throw new Exception();
		}
	}

	public void deletePageBean(IndexPageBean pageBean) {	
		if(pageBean.getAs() != null && pageBean.getAs().size() != 0)
			deleteAs(pageBean.getAs());
//		System.out.println(template.query("select * from Acriterion", new BeanPropertyRowMapper<Acriterion>(Acriterion.class)).get(0));
		if(pageBean.getBs() != null && pageBean.getBs().size() != 0)
			deleteBs(pageBean.getBs());
		if(pageBean.getCs() != null && pageBean.getCs().size() != 0)
			deleteCs(pageBean.getCs());
	}
	
	public void deleteAs(List<Acriterion> as) {
		String sql = "";
		for(Acriterion a : as) {
			sql = "delete from Acriterion where aid='" + a.getAid() + "'";
			template.update(sql);
//			System.out.println(sql);
		}
	}


	private void deleteCs(List<Ccriterion> cs) {
		String sql = "";
		for(Ccriterion c : cs) {
			sql = "delete from Ccriterion where cid='" + c.getCid() + "'";
			template.update(sql);
		}
		
	}

	private void deleteBs(List<Bcriterion> bs) {
		String sql = "";
		for(Bcriterion b : bs) {
			sql = "delete from Bcriterion where bid='" + b.getBid() + "'";
			template.update(sql);
		}
		
	}

	public List<Acriterion> findLastA() throws Exception{
		String sql = "select * from Acriterion order by aid desc limit 1";
		return template.query(sql, new BeanPropertyRowMapper<Acriterion>(Acriterion.class));
	}

	public List<Bcriterion> findLastB() {
		String sql = "select * from Bcriterion order by bid desc limit 1";
		return template.query(sql, new BeanPropertyRowMapper<Bcriterion>(Bcriterion.class));
	}

	public List<Ccriterion> findLastC() {
		String sql = "select * from Ccriterion order by cid desc limit 1";
		return template.query(sql, new BeanPropertyRowMapper<Ccriterion>(Ccriterion.class));
	}

	public boolean findOneNoCommitByPid(String pid) {
		try {
			String sql = "SELECT * FROM pcm WHERE pid = ? AND commState='未提交' LIMIT 1";
			System.out.println(template.queryForMap(sql, pid));
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkShutDownByPid(String pid) {
		String sql = "SELECT pisstart FROM projects WHERE pid = ? LIMIT 1";
		String isStart = template.queryForObject(sql, String.class, pid);
		System.out.println("isStart: " + isStart);
		if("false".equalsIgnoreCase(isStart) == true)
			return true;
		return false;
	}
}
