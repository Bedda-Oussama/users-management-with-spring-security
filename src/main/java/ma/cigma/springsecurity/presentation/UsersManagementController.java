package ma.cigma.springsecurity.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ma.cigma.springsecurity.domaine.RoleVo;
import ma.cigma.springsecurity.domaine.UserVo;
import ma.cigma.springsecurity.service.IUserService;

@Controller
@RequestMapping("/super/user")
public class UsersManagementController {
	@Autowired
	private IUserService srv;
	
	@Autowired
	private JavaMailSender javaMailSender; 
	
	@GetMapping("/form")
	public String form(Model model) {
		
	//get all roles for select ant role we need it for our new user 
	List<RoleVo> rolevo =	srv.getAllRoles();
		
		model.addAttribute("UserVo", new UserVo());
		model.addAttribute("RoleVo", rolevo);

		 return "/super/newUserForm";
	}
	
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("UserVo") @Valid UserVo uservo, BindingResult bindingResult,
			                   Model m , @RequestParam(required = true) String email) {
		
		
		System.out.println("user done " +uservo);
	    System.out.println("user is /////"+uservo.getRoles());
	    System.out.println("email is  /////"+email);

	    // check data form with @valid and return errors with #binding-result if has errors
     	if(bindingResult.hasErrors()!=true) { 
			
        System.out.println("this is the uservO obect=====>>>>>>>"+uservo);
			
			srv.save(uservo);
	    System.out.println("bnr dont catsh errors");
		System.out.println("user save");
		
		//-------------------------------------------
		
		
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject("notfication from super admin ");
		msg.setText("this is the new new information about your accont  yr username is ="+uservo.getUsername()+"\\n     and your password is ="+uservo.getPassword());
		javaMailSender.send(msg);
		

		
		return "redirect:/super/user/listUsers";
		}
		
		else {
			
          System.out.println("bnr catsh errors");
     	  List<RoleVo> rolevo =	srv.getAllRoles();
  		   m.addAttribute("RoleVo", rolevo);

			 return "/super/newUserForm";
     
		                    }
			
               
	}
	
	
	
	@GetMapping("/listUsers")
	public String getAllUsers(Model model) {
		
	
	model.addAttribute("listUsers", srv.getAllUsers());
		
		
		return "/super/ListeUsers";
		
		
	}
	
	@GetMapping("/getone/{id}")
	public String getOne(@PathVariable Long id ,Model m) {
		
	UserVo usrVo=	srv.findById(id);
	
	 List<RoleVo> rolevo =	srv.getAllRoles();
	 
    	m.addAttribute("UserVo",usrVo);
	
	    m.addAttribute("RoleVo", rolevo);
		
		return "super/newUserForm";
		
		
		
	}
	
	
	@GetMapping("/delete/{id}")
	public String DeleteUser(@PathVariable Long id) {
		srv.deletUser(id);
		
		return "redirect:/super/user/listUsers";
	
	}
}
 