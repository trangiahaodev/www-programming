package iuh.wwwprogramming.exception;
public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException() { super("Không tìm thấy tài khoản người dùng."); }
}
