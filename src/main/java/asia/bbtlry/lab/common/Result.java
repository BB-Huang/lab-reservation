package asia.bbtlry.lab.common;

import lombok.Data;

@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    // 构造器私有，强制外部用静态方法创建
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }  // code=0, message="success",data=data
    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }         // 不带数据
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
    // 三个静态工厂方法
}