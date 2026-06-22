package com.learnloop.backend.service;

import com.learnloop.backend.dto.RegisterRequest;
import com.learnloop.backend.dto.SkillDto;
import com.learnloop.backend.dto.UserProfileDto;
import com.learnloop.backend.model.User;
import com.learnloop.backend.model.UserSkill;
import com.learnloop.backend.repository.UserRepository;
import com.learnloop.backend.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .experienceLevel("BEGINNER") // default
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllExcept(Long currentUserId) {
        return userRepository.findAllExcept(currentUserId);
    }

    @Transactional
    public User updateProfile(User user, UserProfileDto dto) {
        user.setFullName(dto.getFullName());
        user.setBio(dto.getBio());
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        user.setExperienceLevel(dto.getExperienceLevel());

        // Clear existing skills
        userSkillRepository.deleteByUserId(user.getId());
        user.getSkills().clear();

        // Add new skills
        if (dto.getSkills() != null) {
            for (SkillDto skillDto : dto.getSkills()) {
                UserSkill skill = UserSkill.builder()
                        .user(user)
                        .skillName(skillDto.getSkillName())
                        .isTeach(skillDto.isTeach())
                        .proficiency(skillDto.getProficiency())
                        .build();
                user.getSkills().add(skill);
            }
        }

        return userRepository.save(user);
    }
}
