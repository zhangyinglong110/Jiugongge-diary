package com.poppy.servlet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
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
		} else if ("preview".equals(action)) {
			preview(req, resp); // 预览日记
		} else if ("save".equals(action)) {
			save(req, resp); // 保存用户日记
		} else if ("delDiary".equals(action)) {
			this.delDiary(req, resp);// 删除日记
		}
	}

	/**
	 * 功能：删除日记
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void delDiary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int id = Integer.parseInt(req.getParameter("id"));// 获取要删除的ID
		String imgName = req.getParameter("imgName"); // 获取要删除的图片
		String url = req.getParameter("url"); // 获取返回的URL地址
		int result = dao.deleteDiary(id);// 返回数据库执行的结果
		PrintWriter out = resp.getWriter();
		if (result > 0) {
			/************* 删除日记图片及缩略图 ******************/
			String path = getServletContext().getRealPath("\\") + "images\\diary\\";
			File file = new File(path + imgName + "scale.jpg");
			file.delete();
			file = new File(path + imgName + ".png");// 获取日记图片
			file.delete();
			out.println("<script>alert('删除日记成功！');window.location.href='DiaryServlet?action=" + url + "';</script>");
		} else {
			out.println("<script>alert('删除日记失败，请稍后重试！');history.back();</script>");
		}

	}

	/**
	 * 功能：保存用户的日记
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession(true); // 获取session对象
		BufferedImage image = (BufferedImage) session.getAttribute("diaryImg");
		String url = req.getRequestURL().toString();// 获取请求的URL地址
		url = req.getRealPath("/");// 获取请求的实际地址
		long date = new Date().getTime();// 获取当前时间
		Random r = new Random(date);
		long value = r.nextLong();// 生成一个长整型的随机数
		url = url + "images/diary/" + value;// 生成图片的URL地址
		String scaleImgUrl = url + "scale.jpg";// 生成缩略图的URL地址
		url = url + ".png";
		ImageIO.write(image, "PNG", new File(url));
		/******************* 生成缩略图 ********************************/
		File file = new File(url);// 获取原文件
		Image src = ImageIO.read(file);
		int old_width = src.getWidth(null);// 获取原图片的宽
		int old_height = src.getHeight(null);// 获取原图片的高
		int new_w = 0;// 新图片的宽
		int new_h = 0;// 新图片的高
		double temp = 0;// 缩放比例
		/********* 计算缩放比例 ***************/
		double tagSize = 60;
		if (old_width > old_height) {
			temp = old_width / tagSize;
		} else {
			temp = old_height / tagSize;
		}
		/*************************************/
		new_w = (int) Math.round(old_width / temp);// 计算新图片的宽
		new_h = (int) Math.round(old_height / temp);// 计算新图片的高
		image = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		src = src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH);
		image.getGraphics().drawImage(src, 0, 0, new_w, new_h, null);
		ImageIO.write(image, "JPG", new File(scaleImgUrl)); // 保存缩略图文件
		/***********************************************************************/
		/**** 将填写的日记保存到数据库中 *****/
		Diary diary = new Diary();
		diary.setAddress(String.valueOf(value));// 设置地址
		diary.setTitle(session.getAttribute("title").toString());// 设置标题
		diary.setUserid(Integer.parseInt(session.getAttribute("uid").toString()));
		int result = dao.saveDiary(diary);
		PrintWriter out = resp.getWriter();
		if (result > 0) {
			out.println("<script>alert('保存成功！');window.location.href='DiaryServlet?action=listAllDiary';</script>");
		} else {
			out.println("<script>alert('保存日记失败，请稍后重试！');history.back();</script>");
		}
	}

	/**
	 * 功能：预览九宫格日记
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	private void preview(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String title = req.getParameter("title"); // 获取日记标题
		String template = req.getParameter("template"); // 获取日记模板
		String weather = req.getParameter("weather");// 获取天气
		String[] content = req.getParameterValues("content"); // 获取日记的内容
		for (int i = 0; i < content.length; i++) {
			if (content[i].equals(null) || content[i].equals("") || content[i].equals("请在此输入文字")) {
				content[i] = "没啥可说的";// 为没有设置内容的项目设置默认值
			}
		}
		HttpSession session = req.getSession(true);// 获取session
		session.setAttribute("template", template);// 保存选择的模板
		session.setAttribute("weather", weather); // 保存天气
		session.setAttribute("title", title); // 保存日记标题
		session.setAttribute("diary", content); // 保存日记内容
		req.getRequestDispatcher("preview.jsp").forward(req, resp);
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
