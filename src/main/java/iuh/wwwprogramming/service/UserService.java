package iuh.wwwprogramming.service;
import iuh.wwwprogramming.dto.*;
import org.springframework.data.domain.Page;
public interface UserService {
    Page<UserResponseDTO> search(UserSearchDTO query);
}
