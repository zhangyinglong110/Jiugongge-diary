package com.poppy.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.QueryRunner;

import com.poppy.model.User;
import com.poppy.tools.C3P0Util;

public class UserDao {

	/**
	 * 验证用户的方法，返回值为1表示登录成功，否则表示登录失败
	 * 
	 * @param user
	 * @return
	 */
	public int login(User user) {
		Connection conn = C3P0Util.getConnection();
		int flag = 0; // 标志
		String sql = "SELECT * FROM tb_user where userName='" + user.getUsername() + "'";
		Statement stmt = null;
		ResultSet rSet = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_FORWARD_ONLY);
			rSet = stmt.executeQuery(sql);
			if (rSet.next()) {
				String pwd = user.getPwd();
				int uid = rSet.getInt(1);// 获取第一列的数据
				if (pwd.equals(rSet.getString(3))) {
					flag = uid;
					rSet.last(); // 定位到最后一条记录
					int rowSum = rSet.getRow();// 获取记录总数
					rSet.first();// 定位到第一条记录
					if (rowSum != 1) {
						flag = 0;
					}
				} else {
					flag = 0;
				}
			} else {
				flag = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			flag = 0;
		} finally {
			try {
				conn.close();
				rSet.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * 保存用户信息
	 * 
	 * @return
	 */
	public String save(String sql) {
		String result = "";
		QueryRunner runner = new QueryRunner(C3P0Util.getDataSource());
		try {
			int rst = runner.update(sql);
			if (rst > 0) {
				result = "注册成功！！！";
			} else {
				result = "注册失败！！！";
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 找回密码第一步
	 * 
	 * @param username
	 * @return
	 */
	public String forgetPwd1(String username) {
		String sql = "SELECT question FROM tb_user WHERE username= '" + username + "'";
		Connection conn = C3P0Util.getConnection();
		Statement stmt = null;
		ResultSet rSet = null;
		String result = "";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_FORWARD_ONLY);
			rSet = stmt.executeQuery(sql);
			if (rSet.next()) {
				result = rSet.getString(1);// 获取第一列的数据
			} else {
				result = "您输入的用户名不存在！"; // 表示输入的用户名不存在
			}
		} catch (SQLException e) {
			result = "您输入的用户名不存在！"; // 表示输入的用户名不存在
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				rSet.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 找回密码第二步
	 * 
	 * @param username
	 * @param question
	 * @param answer
	 */
	public String forgetPwd2(String username, String question, String answer) {
		String sql = "SELECT pwd FROM tb_user WHERE username='" + username + "'AND answer = '" + answer
				+ "'AND question = '" + question + "'";
		Connection conn = C3P0Util.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String result = "";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_FORWARD_ONLY);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				result = rs.getString(1);// 获取第一列的数据
			} else {
				result = "您输入的密码提示问题答案错误！"; // 表示输入的密码提示问题答案错误
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
