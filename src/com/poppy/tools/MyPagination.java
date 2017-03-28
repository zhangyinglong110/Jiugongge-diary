package com.poppy.tools;

import java.util.ArrayList;
import java.util.List;

import com.poppy.model.Diary;

public class MyPagination {
	public List<Diary> list = null;
	private int recordCount = 0; // 保存记录总数的变量
	private int pagesize = 0; // 保存每页显示的记录数的变量
	private int maxPage = 0; // 保存最大页数的变量

	/**
	 * 
	 * @param list
	 *            保存查询结果的List对象list
	 * @param page
	 *            当前页面
	 * @param pagesize
	 *            每页显示的数量
	 * @return
	 */
	public List<Diary> getInitPage(List<Diary> list, int page, int pagesize) {
		List<Diary> newList = new ArrayList<Diary>();
		this.list = list;
		recordCount = list.size();
		this.pagesize = pagesize;
		this.maxPage = getMaxPage();// 获取最大页数
		try {
			for (int i = (page - 1) * pagesize; i <= page * pagesize - 1; i++) {
				try {
					if (i >= recordCount) {
						break; // 跳出循环
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				newList.add((Diary) list.get(i));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return newList;
	}

	/**
	 * 
	 * @param page指定页数
	 * @return 获取指定页数的数据的方法
	 */
	public List<Diary> getAppointPage(int page) {
		List<Diary> newList = new ArrayList<Diary>();
		try {
			for (int i = (page - 1) * pagesize; i <= page * pagesize - 1; i++) {
				if (i >= recordCount) {
					break;
				}
				newList.add((Diary) list.get(i));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return newList;
	}

	/**
	 * 最大页数
	 * 
	 * @return
	 */
	public int getMaxPage() {
		int maxPage = (recordCount % pagesize == 0) ? (recordCount / pagesize) : (recordCount / pagesize + 1);
		return maxPage;
	}

	/**
	 * 获取总记录数的方法
	 * 
	 * @return
	 */
	public int getRecordSize() {
		return recordCount;
	}

	/**
	 * 获取当前页数
	 * 
	 * @param str
	 * @return
	 */
	public int getPage(String str) {
		if (str == null) {
			str = "0";
		}
		int page = Integer.parseInt(str);
		if (page < 1) {
			page = 1; // 当页数小于1时，让其等于1
		} else {
			if (((page - 1) * pagesize + 1) > recordCount) {
				page = maxPage; // 当页数大于最大页数时，让其等于最大页数
			}
		}
		return page;
	}

	/**
	 * 用于输入记录导航的方法
	 * 
	 * @param page
	 *            当前页数
	 * @param url
	 *            url地址
	 * @param para
	 *            要传递的参数
	 * @return
	 */
	public String printCtrl(int page, String url, String para) {
		String strHtml = "<table width='100%'  border='0' cellspacing='0' cellpadding='0'><tr> <td height='24' align='right'>当前页数：【"
				+ page + "/" + maxPage + "】&nbsp;";
		try {
			if (page > 1) {
				strHtml = strHtml + "<a href='" + url + "&Page=1" + para + "'>第一页</a>　";
				strHtml = strHtml + "<a href='" + url + "&Page=" + (page - 1) + para + "'>上一页</a>";
			}
			if (page < maxPage) {
				strHtml = strHtml + "<a href='" + url + "&Page=" + (page + 1) + para + "'>下一页</a>　<a href='" + url
						+ "&Page=" + maxPage + para + "'>最后一页&nbsp;</a>";
			}
			strHtml = strHtml + "</td> </tr>	</table>";
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return strHtml;
	}

}
