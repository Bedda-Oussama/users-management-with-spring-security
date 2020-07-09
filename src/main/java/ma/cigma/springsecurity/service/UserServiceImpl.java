package ma.cigma.springsecurity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.cigma.springsecurity.dao.EmpRepository;
import ma.cigma.springsecurity.dao.RoleRepository;
import ma.cigma.springsecurity.dao.UserRepository;
import ma.cigma.springsecurity.domaine.RoleConverter;
import ma.cigma.springsecurity.domaine.RoleVo;
import ma.cigma.springsecurity.domaine.UserConverter;
import ma.cigma.springsecurity.domaine.UserVo;
import ma.cigma.springsecurity.service.model.Role;
import ma.cigma.springsecurity.service.model.User;

@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private EmpRepository empRepository;

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked, getAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(List<Role> roles) {
		List<GrantedAuthority> springSecurityAuthorities = new ArrayList<>();
		for (Role r : roles) {
			springSecurityAuthorities.add(new SimpleGrantedAuthority(r.getRole()));
		}
		return springSecurityAuthorities;
	}

	@Override
	public void save(UserVo userVo) {
		User user = UserConverter.toBo(userVo);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		List<Role> rolesPersist = new ArrayList<>();
		
		if(user.getRoles()!=null) {
			
	for (Role role : user.getRoles()) {
		System.out.println("role not null");
		Role userRole = roleRepository.findByRole(role.getRole());
		rolesPersist.add(userRole);
		}
		user.setRoles(rolesPersist);
		                           }
		
		userRepository.save(user);
	}

	@Override
	public void save(RoleVo roleVo) {
		roleRepository.save(RoleConverter.toBo(roleVo));
	}

	@Override
	public List<UserVo> getAllUsers() {
		return UserConverter.toVoList(userRepository.findAll());
	}

	@Override
	public List<RoleVo> getAllRoles() {
		return RoleConverter.toVoList(roleRepository.findAll());
	}

	@Override
	public RoleVo getRoleByName(String role) {
		return RoleConverter.toVo(roleRepository.findByRole(role));
	}

	@Override
	public void cleanDataBase() {
		userRepository.deleteAll();
		roleRepository.deleteAll();
		empRepository.deleteAll();
	}

	@Override
	public UserVo findById(Long id) {
	User user =	userRepository.findOneById(id);
		return UserConverter.toVo(user);
	}

	@Override
	public void deletUser(Long id) {
	      userRepository.deleteById(id);
		
	}
}