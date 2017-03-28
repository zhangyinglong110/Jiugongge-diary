package com.poppy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 配置编码的过滤器类
 * 
 * @author 张应龙(Poppy)
 *
 */
public class CharacterEncodingFilter implements Filter {

	protected String encoding = null; // 定义编码格式的变量
	protected FilterConfig filterConfig = null; // 定义过滤器配置对象

	@Override
	public void destroy() {
		encoding = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (encoding != null) {
			request.setCharacterEncoding(encoding); // 设置请求编码
			// 设置响应对象的内容类型(包括编码格式)
			response.setContentType("text/html;charset=" + encoding);
		}
		chain.doFilter(request, response);// 传递给下一个过滤器
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig; // 初始化过滤器配置对象
		this.encoding = filterConfig.getInitParameter("encoding"); // 获取配偶之文件中指定的编码格式
	}

}
