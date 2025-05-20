package pl.pjatk.Stepify.user.mapper;

import org.mapstruct.Mapper;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.dto.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
}
