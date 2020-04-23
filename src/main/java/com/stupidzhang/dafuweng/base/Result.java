package com.stupidzhang.dafuweng.base;


/**
 * @Author wb.liujie02
 * @Date 2019/8/12 16:23
 **/
public class Result {

    private int code;

    private String message;

    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Result() {

    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }



    public static Result packError(int code, String message, Object data) {
        return new Result(code, message, data);
    }


}
