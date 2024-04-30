package com.lcwd.electroic.store.dtos;

import com.lcwd.electroic.store.entities.Role;
import com.lcwd.electroic.store.validation.ImageNameValid;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDto {
    private String userId;

    @Size(min=3,max=15, message="Invalid Name!!")
    @ApiModelProperty(value = "user_name",name = "username", required = true,notes = "user name of new user")
    private String name;


    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid Email!!")
    @NotBlank(message="Email is required!")
    private String email;

    @NotBlank(message="Password is required!")
    private String password;

    @Size(min=4, max=6, message = "Invalid Gender!!")
    private String gender;

    @NotBlank(message = "Write something about yourself!!")
    private String about;

    @ImageNameValid(message ="wrong data")
    private String imageName;

    private Set<RoleDto> roles = new HashSet<>();
}
