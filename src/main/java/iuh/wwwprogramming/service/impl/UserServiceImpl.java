package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.*;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.validation.annotation.Validated
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class UserServiceImpl implements UserService {
    private final UserRepository users;
    private final iuh.wwwprogramming.repository.OrderRepository orders;
    @Override
    public Page<UserResponseDTO> search(UserSearchDTO query) {
        String keyword = query.getKeyword() == null ? "" : query.getKeyword().trim();
        int size = Math.max(1, Math.min(100, query.getSize()));
        int page = Math.max(0, query.getPage());
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id"));
        Page<User> result = users.findByUserCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            keyword, keyword, keyword, PageRequest.of(page, size, sort));
        if (page > 0 && result.isEmpty()) {
            result = users.findByUserCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                keyword, keyword, keyword, PageRequest.of(Math.max(0, result.getTotalPages()-1), size, sort));
        }
        return result.map(this::toResponse);
    }
    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder().id(user.getId()).userCode(user.getUserCode()).fullName(user.getFullName())
            .email(user.getEmail()).phone(user.getPhone()).role(user.getRole()).active(user.getActive()).build();
    }

    @Override
    public UserUpdateDTO getForEdit(String id) {
        User user = users.findById(id).orElseThrow(iuh.wwwprogramming.exception.UserNotFoundException::new);
        return UserUpdateDTO.builder().fullName(user.getFullName()).email(user.getEmail())
            .phone(user.getPhone()).address(user.getAddress()).active(user.getActive()).build();
    }
    @Override @Transactional
    public void update(String id, UserUpdateDTO dto, String actorId) {
        var admins = users.lockActiveAdmins();
        User user = users.findForUpdate(id).orElseThrow(iuh.wwwprogramming.exception.UserNotFoundException::new);
        if (!dto.getActive()) guardDeactivation(user, actorId, admins.size());
        String email = dto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        if (users.existsByEmailIgnoreCaseAndIdNot(email, id)) throw new IllegalArgumentException("Email này đã được sử dụng.");
        user.setFullName(dto.getFullName().trim());
        user.setEmail(email);
        user.setPhone(dto.getPhone() == null ? null : dto.getPhone().trim());
        user.setAddress(dto.getAddress() == null ? null : dto.getAddress().trim());
        user.setActive(dto.getActive());
        users.saveAndFlush(user);
    }
    private void guardDeactivation(User user, String actorId, int activeAdmins) {
        if (user.getId().equals(actorId)) throw new IllegalArgumentException("Bạn không thể khóa hoặc xóa chính tài khoản đang dùng.");
        if ("ROLE_ADMIN".equals(user.getRole()) && Boolean.TRUE.equals(user.getActive()) && activeAdmins <= 1)
            throw new IllegalArgumentException("Không thể khóa hoặc xóa Admin hoạt động cuối cùng.");
    }

    @Override @Transactional
    public void delete(String id, UserDeleteDTO dto, String actorId) {
        var admins = users.lockActiveAdmins();
        User user = users.findForUpdate(id).orElseThrow(iuh.wwwprogramming.exception.UserNotFoundException::new);
        guardDeactivation(user, actorId, admins.size());
        if (orders.existsByUserId(id))
            throw new IllegalArgumentException("Không thể xóa người dùng đã có đơn hàng, kể cả đơn đã hủy.");
        users.delete(user);
        users.flush();
    }
}
