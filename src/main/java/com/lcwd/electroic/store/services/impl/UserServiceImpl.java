package com.lcwd.electroic.store.services.impl;

import com.lcwd.electroic.store.controllers.UserController;
import com.lcwd.electroic.store.dtos.PageableResponse;
import com.lcwd.electroic.store.dtos.UserDto;
import com.lcwd.electroic.store.entities.Role;
import com.lcwd.electroic.store.entities.User;
import com.lcwd.electroic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electroic.store.helper.Helper;
import com.lcwd.electroic.store.repositories.RoleRepository;
import com.lcwd.electroic.store.repositories.UserRepository;
import com.lcwd.electroic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;
    @Value("${user.profile.image.path}")
    private String imagePath;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${normal.role.id}")
    private String normalRoleId;

    @Autowired
    private RoleRepository roleRepository;
    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Override
    public UserDto createUser(UserDto userDto) {
        //generate unique id in string format
        String userId=UUID.randomUUID().toString();
        userDto.setUserId(userId);
        //encoding password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        //dto->entity
        User user = dtoToEntity(userDto);

        //fetch role of normal and set it to user
        Role role = roleRepository.findById(normalRoleId).get();
        user.getRoles().add(role);
        User savedUser= userRepository.save(user);
        //entity->dto
        UserDto newDto = entityToDto(savedUser);

        return newDto;
    }

    private UserDto entityToDto(User savedUser) {
//        UserDto userDto = UserDto.builder().userId(savedUser.getUserId()).name(savedUser.getName()).email(savedUser.getEmail()
//        ).password(savedUser.getPassword()).about(savedUser.getAbout()).gender(savedUser.getGender()).imageName(savedUser.getImageName()).build();
        return mapper.map(savedUser, UserDto.class);
    }

    private User dtoToEntity(UserDto userDto) {
//        User user = User.builder().userId(userDto.getUserId()).name(userDto.getName()).email(userDto.getEmail()
//                ).password(userDto.getPassword()).about(userDto.getAbout()).gender(userDto.getGender()).imageName(userDto.getImageName()).build();
        return mapper.map(userDto, User.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with given Id"));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setImageName(userDto.getImageName());
        user.setPassword(userDto.getPassword());
        //save data
        User updatedUser = userRepository.save(user);
        UserDto updatedDto = entityToDto(updatedUser);
        return updatedDto;
    }

    @Override
    public void deleteUser(String userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found by id"));
        String fullPath = imagePath+ user.getImageName();
        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }catch(NoSuchFileException e){
            logger.error("User image not found in folder");
            e.printStackTrace();
        }
        //delete user
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
        PageableResponse<UserDto> response= Helper.getPageableResponse(page,UserDto.class);
        return response;

    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found by id"));
        UserDto userDto = entityToDto(user);
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Email not found"));
        UserDto userDto = entityToDto(user);
        return userDto;
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        List<UserDto> userDtos = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public Optional<User> findUserByEmailOptional(String email) {
        return userRepository.findByEmail(email);

    }
}
