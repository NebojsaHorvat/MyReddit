package services;


import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import Data.DataBase;
import beans.Consts;
import beans.Model;
import beans.User;




@Path("/loginService")
public class LoginService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext context;
	
	@GET
	@Path("/test")
	public String test() {
		return "REST is working.";
	}
	
	
	
	
	
	
	@GET
	@Path("/update/{role}/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String update(@PathParam("role") String role, @PathParam("username") String username) {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Model model = DataBase.getInstance().getModel();
		
		if(user != null) {

			if(user.getRole().equals("ADMIN")) {
				
				if(role.equals("admin")) {
					model.changeUserRole(username, Consts.admin);
				}
				else if(role.equals("moderator")) {
					model.changeUserRole(username, Consts.moderator);
				}
				else if(role.equals("user")) {
					model.changeUserRole(username, Consts.user);
				}
				//dao.saveDatabase();
				
				DataBase.getInstance().saveDatabase();
				
				return "Successfully updated!";
			}
			else {
				return "You don't have permission to edit user role";
			}
			
		}
		else {
			return "Must be logged in to update subforum!";
		}
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String register(@FormParam("name") String name, @FormParam("surname") String surname, @FormParam("email") String email, @FormParam("username") String username
			, @FormParam("phone") String phone, @FormParam("password") String password) {
	  
		
		Model model = DataBase.getInstance().getModel();
		
		
		
		for(User u : model.getUsers()){
			if(u.getUsername().equals(username) || u.getEmail().equals(email))
			{
				return "User already exists";
			}				
		}
		User newUser = new User(username, password, email, username, surname, phone);
		model.getUsers().add(newUser);

		DataBase.getInstance().saveDatabase();
		
		return "Register successful";
	 
		
		
	}
	
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> users() {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Model model = DataBase.getInstance().getModel();
		
		if(user != null) {
			if(user.getRole().equals("MODERATOR") || user.getRole().equals("ADMIN")) {
				return model.getUsers();
			}
		}
		
		return null;
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@FormParam("username") String username, @FormParam("password") String password) {
	  
		
		Model model = DataBase.getInstance().getModel();
		User user=(User)request.getSession().getAttribute("user");
		
		if(user == null || ( user!= null && !user.getUsername().equals(username)  ))
		{
			
				for(User u : model.getUsers()){
					if(u.getUsername().equals(username) && u.getPassword().equals(password))
					{
						user=u;
						request.getSession().setAttribute("user", user);
						return "Login successuful";
					}
				}
				return "Login unsuccessful";
			 
				
		}
		else 
			return "User already logdin!";
	}
	
	@POST
	@Path("/logout")

	@Produces(MediaType.TEXT_PLAIN)
	public String logout() {
	  
		
		Model model = DataBase.getInstance().getModel();
		User user=(User)request.getSession().getAttribute("user");
		
		if(user != null)
		{
			request.getSession().removeAttribute("user");
			request.getSession().invalidate();
				
			return "Logout successful";
		}
		else 
			return "User already logedOut!";
	}
	
	
	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser() {
	
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if(user != null) {
			return user;
		}
		else {
			return null;
		}
	
	}

	
}