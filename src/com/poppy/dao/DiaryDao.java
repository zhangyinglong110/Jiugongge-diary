package com.poppy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.poppy.model.Diary;
import com.poppy.tools.C3P0Util;

/**
 * 日记表数据库的处理类
 * 
 * @author 张应龙(Poppy)
 *
 */
public class DiaryDao {

	/**
	 * 功能：查询日记记录
	 * 
	 * @return
	 */
	public List<Diary> queryDiary(String sql) {

		QueryRunner queryRunner = new QueryRunner(C3P0Util.getDataSource());
		try {

			return (List<Diary>) queryRunner.query(sql, new BeanListHandler(Diary.class));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 功能：保存日记到数据库中去
	 * 
	 * @param diary
	 *            日记
	 * @return
	 */
	public int saveDiary(Diary diary) {
		String sql = "insert into tb_diary (userid,address,title)values(?,?,?)";
		Connection connection = null;
		int result = 0;
		PreparedStatement preparedStatement = null;
		try {
			connection = C3P0Util.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, diary.getUserid());
			preparedStatement.setString(2, diary.getAddress());
			preparedStatement.setString(3, diary.getTitle());
			result = preparedStatement.executeUpdate();
			C3P0Util.close(connection, preparedStatement, null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

	/**
	 * 功能：删除指定id的日记记录
	 * 
	 * @param id
	 * @return
	 */
	public int deleteDiary(int id) {
		String sql = "DELETE FROM tb_diary WHERE id = " + id;
		Connection conn = C3P0Util.getConnection();
		Statement stmt = null;
		int result = 0;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_FORWARD_ONLY);
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
