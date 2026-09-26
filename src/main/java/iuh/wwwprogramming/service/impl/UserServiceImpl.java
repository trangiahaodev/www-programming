package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.*;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class UserServiceImpl implements UserService {
    private final UserRepository users;
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
}
