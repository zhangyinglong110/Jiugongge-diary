package com.poppy.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.poppy.dao.UserDao;
import com.poppy.model.CityMap;
import com.poppy.model.User;

/**
 * 用户操作的Servlet
 * 
 * @author Poppy(张应龙)
 *
 */
@WebServlet(name = "UserServlet", urlPatterns = "/UserServlet")
public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserDao userDao = null;

	public UserServlet() {
		userDao = new UserDao();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");// 传过来的是什么动作
		if ("getProvince".equals(action)) {// 获取省份信息
			this.getProvince(req, resp);
		} else if ("getCity".equals(action)) {// 获取市县信息
			this.getCity(req, resp);
		} else if ("save".equals(action)) { // 保存用户注册信息
			this.save(req, resp);
		} else if ("login".equals(action)) { // 用户登录
			System.out.println("------->action:" + action);
			this.login(req, resp);
		} else if ("exit".equals(action)) { // 用户退出登录
			this.exit(req, resp);
		} else if ("forgetPwd1".equals(action)) { // 找回密码第一步
			this.forgetPwd1(req, resp);
		} else if ("forgetPwd2".equals(action)) {
			this.forgetPwd2(req, resp);
		} else if ("checkUser".equals(action)) {
			this.checkUser(req, resp); // 检查用户名是否被注册
		}
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void checkUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String username = req.getParameter("username"); // 获取用户输入的用户名
		String sql = "SELECT * FROM tb_user WHERE username = '" + username + "'";
		String result = userDao.checkUser(sql);// 调用UserDao类的checkUser()方法判断用户是否被注册
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print(result); // 输出检测结果
		out.flush();
		out.close();
	}

	/**
	 * 找回密码第二步
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void forgetPwd2(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String username = req.getParameter("username");
		String question = req.getParameter("question");
		String answer = req.getParameter("answer");
		String pwd = userDao.forgetPwd2(username, question, answer); // 返回答案
		PrintWriter out = resp.getWriter();
		if ("您输入的密码提示问题答案错误！".equals(pwd)) {
			out.println("<script>alert('您输入的密码提示问题答案错误！');history.back();</script>");
		} else {
			out.println("<script>alert('您的密码是：\\r\\n" + pwd
					+ "\\r\\n请牢记！');window.location.href='DiaryServlet?action=listAllDiary';</script>");
		}
	}

	/**
	 * 找回密码第一步
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	private void forgetPwd1(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String username = req.getParameter("username");// 获取用户名
		System.out.println(username);
		String question = userDao.forgetPwd1(username); // 获取问题
		PrintWriter out = resp.getWriter();
		if ("".equals(question)) {// 判断密码提示问题是否为空
			out.println("<script>alert('您没有设置密码提示问题，不能找回密码！')</script>history.back();");
		} else if ("您输入的用户名不存在！".equals(question)) {
			out.println("<script>alert('您输入的用户名不存在！');history.back();</script>");
		} else {// 获取密码提示问题成功
			req.setAttribute("question", question);// 保存密码提示问题
			req.setAttribute("username", username);// 保存用户名
			req.getRequestDispatcher("forgetPwd_2.jsp").forward(req, resp);// 重定向页面
		}
	}

	/**
	 * 用户退出
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	private void exit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();// 获取HttpSession的对象
		session.invalidate();// 销毁session
		req.getRequestDispatcher("DiaryServlet?action=listAllDiary").forward(req, resp);// 重定向页面
	}

	/**
	 * 用户登录
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("-------用户登录------------");
		System.out.println(req.getParameter("username"));
		System.out.println(req.getParameter("pwd"));
		User f = new User();
		f.setUsername(req.getParameter("username")); // 获取并设置用户名
		f.setPwd(req.getParameter("pwd")); // 获取并设置密码
		int r = userDao.login(f);
		if (r > 0) {// 当用户登录成功时
			HttpSession session = req.getSession();
			session.setAttribute("userName", f.getUsername());// 保存用户名
			session.setAttribute("uid", r);// 保存用户ID
			req.setAttribute("returnValue", "登录成功！");// 保存提示信息
			req.getRequestDispatcher("userMessage.jsp").forward(req, resp);// 重定向页面
		} else {// 当用户登录不成功时
			req.setAttribute("returnValue", "您输入的用户名或密码错误，请重新输入！");
			req.getRequestDispatcher("userMessage.jsp").forward(req, resp);// 重定向页面
		}
	}

	/**
	 * 注册用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("user"); // 获取用户名
		String pwd = request.getParameter("pwd"); // 获取密码
		String email = request.getParameter("email"); // 获取E-mail地址
		String city = request.getParameter("city"); // 获取市县
		String question = request.getParameter("question"); // 获取密码提示问题
		String answer = request.getParameter("answer"); // 获取密码提示问题答案

		String sql = "INSERT INTO tb_user (username,pwd,email,question,answer,city) value ('" + username + "','" + pwd
				+ "','" + email + "','" + question + "','" + answer + "','" + city + "')";
		String result = userDao.save(sql);
		response.setContentType("text/html"); // 设置响应的类型
		PrintWriter out = response.getWriter();
		out.print(result); // 输出执行结果
		out.flush();
		out.close();// 关闭输出流对象
	}

	/**
	 * 获取省份信息
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void getProvince(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String result = "";
		CityMap cityMap = new CityMap();// 实例化保存省份信息的CityMap类的实例
		Map<String, String[]> map = cityMap.model; // 获取省份信息保存到Map中
		Set<String> set = map.keySet(); //// 获取Map集合中的键，并以Set集合返回
		Iterator<String> iterator = set.iterator(); // 获取一个遍历器
		while (iterator.hasNext()) {// 将获取的省份连接为一个以逗号分隔的字符串
			result = result + iterator.next() + ",";
		}
		result = result.substring(0, result.length() - 1); // 去除最后一个逗号
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print(result); // 输出获取的省份字符串
		out.flush();
		out.close();// 关闭输出流对象
	}

	/**
	 * 获取市县信息
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void getCity(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String result = "";
		String selProvince = req.getParameter("parProvince"); // 获取选择的省份
		CityMap cityMap = new CityMap(); // 实例化保存省份信息的CityMap类的实例
		Map<String, String[]> map = cityMap.model; // 获取省份信息保存到Map中
		String[] arrCity = map.get(selProvince); // 获取指定键的值
		for (int i = 0; i < arrCity.length; i++) { // 将获取的市县连接为一个以逗号分隔的字符串
			result = result + arrCity[i] + ",";
		}
		result = result.substring(0, result.length() - 1); // 去除最后一个逗号
		resp.setContentType("text/html");// 向响应的内容类型
		PrintWriter out = resp.getWriter();
		out.print(result); // 输出获取的市县字符串
		out.flush();
		out.close();// 关闭输出流对象
	}

}
