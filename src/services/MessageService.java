package services;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import Data.DataBase;
import beans.Comment;
import beans.Message;
import beans.Model;
import beans.Report;
import beans.Subforum;
import beans.Topic;
import beans.User;


@Path("/messagesService")
public class MessageService {
	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext context;
	
	private Model model = DataBase.getInstance().getModel();
	
	
	@POST
	@Path("/warnAuthor/{reportId}")
	@Produces(MediaType.TEXT_PLAIN)
	public void warnAuthor(@PathParam("reportId") String reportId) {
		
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Report rem=null;
		for(Report report: DataBase.getInstance().getModel().getReports() ){
			
			if(report.getReportId().equals(reportId))
			{
				if(report.getTypeOfRepoart().equals("comment"))
				{
					Topic topic = SubforumService.findTopic(report.getSubforum(), report.getTopic());
					if(topic != null)
					{
						Comment c=SubforumService.findComment(topic.getComments(), report.getCommentId() ); 
						
						if(c != null)
						{
							sendMessage(c.getAuthor().getUsername(), "You should chande content of coment : ( topic: "+c.getParentTopic()+" ) Content:" +  c.getText()      );
						}
					}
				}
				else if(report.getTypeOfRepoart().equals("topic"))
				{
					Topic topic = SubforumService.findTopic(report.getSubforum(), report.getTopic());
					if(topic != null)
					{	
						
						sendMessage(topic.getAuthor().getUsername(), "You should chande content of Topic : ( topic: "+topic.getName()+" ) "   );
						
					}
				}
				else if(report.getTypeOfRepoart().equals("subforum"))
				{
					
					Subforum subforum = SubforumService.findSubforum(report.getSubforum());
					if( subforum != null)
						sendMessage(subforum.getResponsibleModerator().getUsername(), "You should chande content of your subforum : ( subforum: "+subforum.getName()+" ) "   );
					
				}
			}
		}
		
	}
	
	@POST
	@Path("/allRead")
	@Produces(MediaType.TEXT_PLAIN)
	public void allRead() {
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(user != null) {
			
			for(Message message:user.getMessages()){
				message.setSeen(true);
			}
			
		} 
	}
	
	
	@POST
	@Path("/sendMessage")
	@Produces(MediaType.TEXT_PLAIN)
	public String sendMessage(@FormParam("username") String receiverId, @FormParam("content") String content) {
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(user != null) {
			
			User receiver =null;
			for(User u : model.getUsers())
			{
				if(u.getUsername().equals(receiverId))
					receiver=u;
			}
			
			if(receiver != null) {
				Message message = new Message(user.getUsername(), receiver.getUsername(), content,user.getUsername());
				receiver.addMessage(message);
				DataBase.getInstance().saveDatabase();
				
				return "Message sent!";
			} else {
				return "Receiver doesn't exists!";
			}
		} else {
			return "Need to login in order to send a message!";
		}	
	}
	
	@GET
	@Path("/getMessages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Message> getMessages() {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if(user != null) {
			return user.getMessages();
		} else {
			return null;
		}
	}
	
	

}

