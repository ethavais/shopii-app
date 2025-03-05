//import com.example.shopii.data.dto.UserDto;
//import com.example.shopii.data.mapper.UserMapper;
//import com.example.shopii.data.repository.UserRepository;
//import com.example.shopii.models.User;
//import java.util.List;
//
//public class UserService {
//    private final UserRepository userRepository;
//
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public UserDto getUserById(int id) {
//        return UserMapper.toDto(userRepository.findById(id));
//    }
//
//    public List<UserDto> getAllUsers() {
//        return UserMapper.toDtoList(userRepository.findAll());
//    }
//
//    public long createUser(UserDto userDto) {
//        return userRepository.insert(UserMapper.toEntity(userDto));
//    }
//}