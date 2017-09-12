package services;


import java.util.ArrayList;
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
import beans.Subforum;
import beans.User;




@Path("/usersService")
public class UsersService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext context;
	
	
	
	@POST
	@Path("/searchUsers")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> searchSubrofum(@FormParam("typeOfSearch") String typeOfSearch, @FormParam("searchText") String searchText ) {
	  
		
		Model model = DataBase.getInstance().getModel();
		List<User> users= model.getUsers();
		List<User> ret = new ArrayList<User>();
		
		for(User user :users){
			
			if(user.getUsername().contains(searchText))
				ret.add(user);
		}		
		
		DataBase.getInstance().saveDatabase();
		
		return ret;
	}
	
	
	@POST
	@Path("/updateUser/{list}/{command}/{what}")
	@Produces(MediaType.TEXT_PLAIN)
	public String update(@PathParam("list") String list, @PathParam("command") String command,@PathParam("what") String what) {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if( list.equals("likedTopics"))
		{
			commandOn(user.getLikedTopics(),command,what);
		}else if( list.equals("dislikedTopics"))
		{
			commandOn(user.getDislikedTopics(),command,what);
		}else if( list.equals("likedComments"))
		{
			commandOn(user.getLikedComments(),command,what);
		}
		else if( list.equals("dislikedComments"))
		{
			commandOn(user.getDislikedComments(),command,what);
		}
		return "Usersuccesful" ;
	}


	private void commandOn(List<String> list, String command, String what) {
		
		if(command.equals("add")){
			list.add(what);
		}
		else if(command.equals("delete")){
			list.remove(what);
		}
		
	}
	
	
	
	
	
}