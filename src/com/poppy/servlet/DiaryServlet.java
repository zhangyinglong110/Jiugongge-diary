package com.poppy.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.poppy.dao.DiaryDao;
import com.poppy.model.Diary;
import com.poppy.tools.MyPagination;

/**
 * 首頁處理Servlet類
 * 
 * @author 张应龙(Poppy)
 *
 */
@WebServlet(name = "DiaryServlet", urlPatterns = "/DiaryServlet")
public class DiaryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	MyPagination pagination = null;// 数据分页类的对象
	DiaryDao dao = null;// 日记相关的数据库操作类的对象

	// 默认的构造函数
	public DiaryServlet() {
		dao = new DiaryDao();// 实例化日记相关的数据库操作类的对象
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		if ("listAllDiary".equals(action)) {
			listAllDiary(req, resp); // 查询全部九宫格日记
		} else if ("listMyDiary".equals(action)) {
			listMyDiary(req, resp);
		}
	}

	/**
	 * 功能：查询我的日记
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	private void listMyDiary(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String strPage = req.getParameter("Page"); // 获取当前页码
		int page = 1;
		List<Diary> list = null;
		if (strPage == null) {
			// 用户的ID号
			int userid = Integer.parseInt(session.getAttribute("uid").toString());
			String sql = "select d.*,u.username from tb_diary d inner join tb_user u on u.id=d.userid  where d.userid="
					+ userid + " order by d.writeTime DESC";
			pagination = new MyPagination();
			list = dao.queryDiary(sql); // 获取日记内容
			int pagesize = 4; // 指定每页显示的记录数
			list = pagination.getInitPage(list, page, pagesize); // 初始化分页信息
			req.getSession().setAttribute("pagination", pagination);// 保存分页信息
		} else {
			pagination = (MyPagination) req.getSession().getAttribute("pagination");// 获取分页信息
			page = pagination.getPage(strPage);
			list = pagination.getAppointPage(page); // 获取指定页数据
		}
		req.setAttribute("diaryList", list); // 保存当前页的日记信息
		req.setAttribute("page", page); // 保存的当前页码
		req.setAttribute("url", "listMyDiary");// 保存当前页的URL地址
		req.getRequestDispatcher("listAllDiary.jsp").forward(req, resp);// 重定向页面到listAllDiary.jsp
	}

	/**
	 * 查询全部九宫格日记
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	private void listAllDiary(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String strPage = req.getParameter("Page"); // 获取当前页面的页码
		int page = 1;
		List<Diary> list = null;
		if (strPage == null) { // 当页面首页执行
			String sql = "select d.*,u.username from tb_diary d inner join tb_user u on u.id=d.userid order by d.writeTime DESC limit 50";
			pagination = new MyPagination();
			list = dao.queryDiary(sql);// 获取日记内容
			System.out.println("list.size()----------->" + list.size());
			int pagesize = 4; // 指定每页显示的记录数
			list = pagination.getInitPage(list, page, pagesize); // 初始化分页信息
			req.getSession().setAttribute("pagination", pagination);
		} else {
			pagination = (MyPagination) req.getSession().getAttribute("pagination");
			page = pagination.getPage(strPage);// 获取当前页码
			list = pagination.getAppointPage(page); // 获取指定页数据
		}
		req.setAttribute("diaryList", list); // 保存当前页的日记信息
		req.setAttribute("page", page); // 保存的当前页码
		req.setAttribute("url", "listAllDiary");// 保存当前页面的URL
		req.getRequestDispatcher("listAllDiary.jsp").forward(req, resp);// 重定向页面
	}

}
