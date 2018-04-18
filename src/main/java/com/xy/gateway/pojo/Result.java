package com.xy.gateway.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 接口返回对象
 * 
 * @author xiongyan
 * @date 2017年6月5日 下午2:04:33
 */
@Getter
@Setter
public class Result<T> {

	/**
	 * 状态编码
	 * 0 表示成功
	 * 1开头 系统级错误
	 * 2开头 业务级错误
	 */
	private Integer code;
	
	/**
	 * 信息
	 */
	private String message;
	
	/**
	 * 数据
	 */
	private T data;
	
	public Result() {
		
	}
	
	public Result(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public Result(Integer code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> Result<T> success(T data) {
		return new Result<>(0, "成功", data);
	}
	
	public static <T> Result<T> error(Integer code, String message) {
		return new Result<>(code, message);
	}

    public static final Result<String> SYSTEMERROR = new Result<>(10001, "系统错误");
    public static final Result<String> NOTLOGIN = new Result<>(10002, "未登录");
    public static final Result<String> NOTBLANK = new Result<>(10003, "参数为空");
    public static final Result<String> NOACCESS = new Result<>(10004, "无权访问");
    public static final Result<String> NODATA = new Result<>(10005, "无数据");
    public static final Result<String> NORESOURCE = new Result<>(10006, "资源不存在");
    public static final Result<String> PARAMERROR = new Result<>(10007, "参数格式错误");
    
}
