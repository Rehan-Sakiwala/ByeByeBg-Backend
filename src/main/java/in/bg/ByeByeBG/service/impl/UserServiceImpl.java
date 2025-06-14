package in.bg.ByeByeBG.service.impl;

import in.bg.ByeByeBG.dto.UserDto;
import in.bg.ByeByeBG.entity.UserEntity;
import in.bg.ByeByeBG.repository.UserRepository;
import in.bg.ByeByeBG.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        Optional<UserEntity> optionalUser = userRepository.findByClerkId(userDto.getClerkId());

        if(optionalUser.isPresent()){
            UserEntity existingUser = optionalUser.get();
            existingUser.setEmail(userDto.getEmail());
            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setPhotoUrl(userDto.getPhotoUrl());
            if(userDto.getCredits()!=null) {
                existingUser.setCredits(userDto.getCredits());
            }
            existingUser = userRepository.save(existingUser);
            return mapToDto(existingUser);
        }
        UserEntity newUSer= mapToEntity(userDto);
        userRepository.save(newUSer);
        return mapToDto(newUSer);
    }

    @Override
    public UserDto getUserByClerkId(String clerkId) {
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(()-> new UsernameNotFoundException("USer not found!"));
        return mapToDto(userEntity);
    }

    @Override
    public void deleteUserByClerkId(String clerkId) {
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        userRepository.delete(userEntity);
    }

    private UserEntity mapToEntity(UserDto userDto) {
        return UserEntity.builder()
                .clerkId(userDto.getClerkId())
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .photoUrl(userDto.getPhotoUrl())
                .build();
    }

    private UserDto mapToDto(UserEntity userEntity){
        return UserDto.builder()
                .clerkId(userEntity.getClerkId())
                .credits(userEntity.getCredits())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .build();
    }


}
