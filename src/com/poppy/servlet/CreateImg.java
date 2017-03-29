package com.poppy.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author Poppy(张应龙)
 *
 */
@WebServlet(name = "CreateImg", urlPatterns = "/CreateImg")
public class CreateImg extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 禁止缓存
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "No-cache");
		response.setDateHeader("Expires", 0);
		// 指定生成的响应是图片
		response.setContentType("image/jpeg");
		int width = 600;
		int height = 600;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics(); // 获取Graphics对象

		HttpSession session = request.getSession(true); // 获得session对象
		String template = session.getAttribute("template").toString();// 内容
		String weather = session.getAttribute("weather").toString(); // 天气
		weather = request.getRealPath("images/" + weather + ".png");// 获取图片的完整路径
		// 获取背景图片
		String[] content = (String[]) session.getAttribute("diary");
		File bgImageFile;
		if ("默认".equals(template)) {
			bgImageFile = new File(request.getRealPath("images/bg_00.jpg"));
			Image src = ImageIO.read(bgImageFile);// 构造Image对象
			graphics.drawImage(src, 0, 0, width, height, null);// 绘制背景图片
			outWord(graphics, content, weather, 0, 0);
		} else if ("女孩".equals(template)) {
			bgImageFile = new File(request.getRealPath("images/bg_01.jpg"));
			Image src = ImageIO.read(bgImageFile); // 构造Image对象
			graphics.drawImage(src, 0, 0, width, height, null); // 绘制背景图片
			outWord(graphics, content, weather, 25, 110);
		} else {
			bgImageFile = new File(request.getRealPath("images/bg_02.jpg"));
			Image src = ImageIO.read(bgImageFile); // 构造Image对象
			graphics.drawImage(src, 0, 0, width, height, null); // 绘制背景图片
			outWord(graphics, content, weather, 30, 5);
		}
		/********************************************************/
		// 将生成的日记图片保存到Session中
		ImageIO.write(image, "PNG", response.getOutputStream());
		session.setAttribute("diaryImg", image);
	}

	/**
	 * 功能：将九宫格日记的内容写在图片上
	 * 
	 * @param graphics
	 * @param content
	 * @param weather
	 * @param i
	 * @param j
	 */
	private void outWord(Graphics graphics, String[] content, String weather, int offsetX, int offsetY) {
		Font mFont = new Font("微软雅黑", Font.PLAIN, 26); // 通过Font构造字体
		graphics.setFont(mFont);// 设置字体
		graphics.setColor(new Color(0, 0, 0)); // 字体颜色设置成黑色
		int contentLen = 0;
		int x = 0; // 文字的横坐标
		int y = 0; // 文字的纵坐标
		for (int i = 0; i < content.length; i++) {
			contentLen = content[i].length(); // 获取内容的长度
			x = 45 + (i % 3) * 170 + offsetX;
			y = 130 + (i / 3) * 140 + offsetY;
			if (content[i].equals("weathervalue")) {
				File bgImgFile = new File(weather);
				mFont = new Font("微软雅黑", Font.PLAIN, 14); // 通过Font构造字体
				graphics.setFont(mFont); // 设置字体
				Date date = new Date();
				String newTime = new SimpleDateFormat("yyyy年M月d日 E").format(date);
				graphics.drawString(newTime, x - 12, y - 60);
				Image src;
				try {
					src = ImageIO.read(bgImgFile);
					graphics.drawImage(src, x + 10, y - 40, 80, 80, null); // 绘制天气图片
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 构造Image对象
				continue;
			}
			if (contentLen < 5) {
				switch (contentLen % 5) {
				case 1:
					mFont = new Font("微软雅黑", Font.PLAIN, 40); // 通过Font构造字体
					graphics.setFont(mFont); // 设置字体
					graphics.drawString(content[i], x + 40, y);
					break;
				case 2:
					mFont = new Font("微软雅黑", Font.PLAIN, 36); // 通过Font构造字体
					graphics.setFont(mFont); // 设置字体
					graphics.drawString(content[i], x + 25, y);
					break;
				case 3:
					mFont = new Font("微软雅黑", Font.PLAIN, 30); // 通过Font构造字体
					graphics.setFont(mFont); // 设置字体
					graphics.drawString(content[i], x + 20, y);
					break;
				case 4:
					mFont = new Font("微软雅黑", Font.PLAIN, 28); // 通过Font构造字体
					graphics.setFont(mFont); // 设置字体
					graphics.drawString(content[i], x + 10, y);
				}
			} else {
				mFont = new Font("微软雅黑", Font.PLAIN, 22); // 通过Font构造字体
				graphics.setFont(mFont); // 设置字体
				if (Math.ceil(contentLen / 5.0) == 1) {
					graphics.drawString(content[i], x, y);
				} else if (Math.ceil(contentLen / 5.0) == 2) {
					// 分两行写
					graphics.drawString(content[i].substring(0, 5), x, y - 20);
					graphics.drawString(content[i].substring(5), x, y + 10);
				} else if (Math.ceil(contentLen / 5.0) == 3) {
					// 分三行写
					graphics.drawString(content[i].substring(0, 5), x, y - 30);
					graphics.drawString(content[i].substring(5, 10), x, y);
					graphics.drawString(content[i].substring(10), x, y + 30);
				}
			}
		}
		graphics.dispose();
	}
}
