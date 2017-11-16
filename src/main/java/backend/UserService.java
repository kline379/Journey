package backend;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;

import backend.User;
import backend.UserRepository;

@Service("userService")
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;


	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}


	public void saveUser(User user) {
		//user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	@Override
	public UserDetails 	loadUserByUsername(String username){
		User user = this.findUserByEmail(username);
		return user;
	}

}
