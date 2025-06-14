package in.bg.ByeByeBG.service;

import in.bg.ByeByeBG.dto.UserDto;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto getUserByClerkId(String clerkId);

    void deleteUserByClerkId(String clerkId);
}
