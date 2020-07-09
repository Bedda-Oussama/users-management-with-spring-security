package ma.cigma.springsecurity.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.cigma.springsecurity.service.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	// befor : erreur is : this return list of  roles but we need just one role that we will afected to the user 
	// after : we dont need te find in a List (findByRole().getRole().get(0) ) 
    Role findByRole(String role);
	List<Role> findAll();

}