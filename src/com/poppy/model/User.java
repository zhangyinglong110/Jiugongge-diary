package com.poppy.model;

/**
 * 用户类
 * 
 * @author Poppy(张应龙)
 *
 */
public class User {
	private int id; // 用户ID
	private String username; // 用户名
	private String pwd; // 密码
	private String email; // E-mail地址
	private String qusetion; // 密码提示问题
	private String answer; // 答案
	private String city; // 城市

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQusetion() {
		return qusetion;
	}

	public void setQusetion(String qusetion) {
		this.qusetion = qusetion;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
