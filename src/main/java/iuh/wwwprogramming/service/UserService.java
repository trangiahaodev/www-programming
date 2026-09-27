package iuh.wwwprogramming.service;
import iuh.wwwprogramming.dto.*;
import org.springframework.data.domain.Page;
public interface UserService {
    Page<UserResponseDTO> search(UserSearchDTO query);

    UserUpdateDTO getForEdit(String id);
    void update(String id, @jakarta.validation.Valid UserUpdateDTO dto, String actorId);
}
