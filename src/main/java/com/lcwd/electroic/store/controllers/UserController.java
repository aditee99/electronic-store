package com.lcwd.electroic.store.controllers;

import com.lcwd.electroic.store.dtos.ApiResponseMessage;
import com.lcwd.electroic.store.dtos.ImageResponse;
import com.lcwd.electroic.store.dtos.PageableResponse;
import com.lcwd.electroic.store.dtos.UserDto;
import com.lcwd.electroic.store.services.FileService;
import com.lcwd.electroic.store.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
@Api(value = "UserController",description= "REST APIs related to perform user operations!!")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;
    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    Logger logger = LoggerFactory.getLogger(UserController.class);
    //create
    @PostMapping
    @ApiOperation("Create New User")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success | OK"),
            @ApiResponse(code = 201, message = "New User Created ")
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto){
        UserDto userDto= userService.createUser(dto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    //update
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") String userId,
                                              @Valid @RequestBody UserDto userDto){
        UserDto updatedUserDto = userService.updateUser(userDto,userId);
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }
    //delete
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable("userId") String userId) throws IOException {
        userService.deleteUser(userId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("User is deleted successfully").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
    //get all users
    @GetMapping()
    @ApiOperation(value ="Get All Users",response = ResponseEntity.class, tags = {"user-controller"})
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(@RequestParam(value ="pageNumber",defaultValue = "0",required = false) int pageNumber, @RequestParam(value="pageSize",defaultValue = "0", required = false) int pageSize,
    @RequestParam(value ="sortBy",defaultValue = "name",required = false)String sortBy,
    @RequestParam(value ="sortDir",defaultValue = "asc",required = false)String sortDir){
        PageableResponse<UserDto> userDtoList =  userService.getAllUser(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }
    //getsingle
    @GetMapping ("/{userId}")
    @ApiOperation(value = "Get Single User by userId")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") String userId){
        UserDto userDto = userService.getUserById(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    //getbyemail
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email) {
        UserDto userDto = userService.getUserByEmail(email);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    //searchuser
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> getByKeyword(@PathVariable("keywords")String keywords){
        List<UserDto> userDto = userService.searchUser(keywords);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    //upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage") MultipartFile image,
                                                         @PathVariable String userId) throws IOException {
        String imageName = fileService.uploadFile(image,imageUploadPath);
        UserDto user = userService.getUserById(userId);
        user.setImageName(imageName);
        UserDto userDto = userService.updateUser(user,userId);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).success(true).status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }


    //serve user image
    @GetMapping("/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        UserDto user = userService.getUserById(userId);
        logger.info("User image name : {}",user.getImageName());
        InputStream resource = fileService.getResource(imageUploadPath,user.getImageName());
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }
}
