package com.lcwd.electroic.store.services;

import com.lcwd.electroic.store.dtos.PageableResponse;
import com.lcwd.electroic.store.dtos.UserDto;
import com.lcwd.electroic.store.entities.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    //create

    UserDto createUser(UserDto userDto);
    //update
    UserDto updateUser(UserDto userDto, String userId);
    //delete
    void deleteUser(String userId) throws IOException;
    //get all users
    PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);
    //get single user by id
    UserDto getUserById(String userId);
    //get single user by email
    UserDto getUserByEmail(String email);
    //search user
    List<UserDto> searchUser(String keyword);
    //other user specific feature features

    Optional<User> findUserByEmailOptional(String email);
}
