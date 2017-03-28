package com.poppy.dao;

import java.sql.SQLException;
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
		String sql = "INSERT INTO tab_diary(title,address,userid) VALUES('" + diary.getTitle() + "','"
				+ diary.getAddress() + "','" + diary.getUserid() + ")";
		System.out.println(sql); // 打印保存日记数据库的sql语句
		// int ret = conn.executeUpdate(sql); // 执行保存数据库的操作
		// conn.close();// 关闭数据库
		return 0;
	}

	/**
	 * 功能：删除指定id的日记记录
	 * 
	 * @param id
	 * @return
	 */
	public int deleteDiary(int id) {
		String sql = "DELETE FROM tb_diary WHERE id = " + id;
		System.out.println("打印删除数据时的sql" + sql);
		int ret = 0;
		try {
			// ret = conn.executeUpdate(sql); // 执行更新语句
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			// conn.close(); // 关闭数据库连接
		}
		return ret;
	}

}
