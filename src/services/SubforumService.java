package services;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;


import Data.DataBase;
import beans.Comment;
import beans.Consts;
import beans.Model;
import beans.Report;
import beans.Subforum;
import beans.Topic;
import beans.TopicContent;
import beans.User;
import sun.misc.IOUtils;


@Path("/subforumService")


public class SubforumService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext context;
	
	private Model model = DataBase.getInstance().getModel();
	
	
	
	@GET
	@Path("/getComment/{commentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Comment getComment( @PathParam("commentId") String commentId) {
		
		
		
		return findCommentById(commentId);
		
	}
	
	
	@POST
	@Path("/addImage/{topicId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void dodajSliku(InputStream inputStream, @PathParam("topicId") String topicId,
			@Context ServletContext ctx) throws Exception
	{
		
		Topic topic=findTopicById(topicId);
		
		if(topic == null)
		{
			return;
		}
		if(topic.getType().equals("Image")){
			
			
			OutputStream outputStream = null;
			try {
				
				String fileName= ctx.getRealPath(topic.getImage());
				//String fileName="C:\\Users\\horva\\workspaceEE\\radi\\kocka.jpg";
				//String fileName = topic.getImage();
				File imgFile = new File(fileName);
				outputStream = new FileOutputStream(imgFile);
	
				int read = 0;
				byte[] bytes = new byte[1024];
	
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				
				topic.setImage(topic.getImage());
				
			} catch (IOException e) {
				e.printStackTrace();
				
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						
					}
				}
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						
					}
	
				}
			}
			
			
		}
		
		
	}
	
	
	
	@POST
	@Path("/addTopic")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String dodajPodforum(TopicContent topicContent, @Context ServletContext ctx) {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if(topicContent.getName().isEmpty() || topicContent.getSubforumName().isEmpty() || topicContent.getTypeOfTopic().isEmpty())
		{
			return "Topic not sent properly";
		}
		
		Topic newTopic=null;
		if(topicContent.getTypeOfTopic().equals("Text")){
			
		newTopic = new Topic(topicContent.getName(), topicContent.getContent(), user, topicContent.getSubforumName(),
				topicContent.getTypeOfTopic(), topicContent.getImageName());
		}
		else if (topicContent.getTypeOfTopic().equals("Link")){
			newTopic = new Topic(topicContent.getName(), topicContent.getLink(), user, topicContent.getSubforumName(),
					topicContent.getTypeOfTopic(), topicContent.getImageName());
		}
		else {
			newTopic = new Topic(topicContent.getName(), topicContent.getContent(), user, topicContent.getSubforumName(),
					topicContent.getTypeOfTopic(), topicContent.getImageName());
		}
		
		Subforum subforum = findSubforum(topicContent.getSubforumName());
		
		if(subforum == null)
			return "Subforum doesn't exit";
		subforum.addTopic(newTopic);
		DataBase.getInstance().saveDatabase();
		
		return newTopic.getTopicId();
	}
	
	
	public static Topic findTopicById(String topicId)
	{
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		
		for(Subforum subforum : subforums){
			for(Topic topic: subforum.getTopics())
			{
				if (topic.getTopicId().equals(topicId))
				{
					return topic;
				}
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	


	
	
	

	
	
	
	@DELETE
	@Path("/deleteEntity/{reportId}")
	@Produces(MediaType.TEXT_PLAIN)
	public void deleteEntity(@PathParam("reportId") String reportId) {
		
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Report rep=null;
		for(Report report: DataBase.getInstance().getModel().getReports() ){
			
			if(report.getReportId().equals(reportId))
			{
				rep=report;
			}
		}
		
		if(rep !=null)
		{
			if(rep.getTypeOfRepoart().equals("subforum"))
			{
				Subforum subforum = findSubforum(rep.getSubforum());
				if(subforum != null)
					model.getSubforums().remove(subforum);
			}
			else if(rep.getTypeOfRepoart().equals("topic"))
			{
				Topic topic = findTopic(rep.getSubforum(),rep.getTopic());
				if(topic != null)
				{
					Subforum subf= findSubforum(rep.getSubforum());
					if(subf != null)
						subf.getTopics().remove(topic);
				}
				
			}
			else if(rep.getTypeOfRepoart().equals("comment"))
			{
				List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
				
				for(Subforum s:subforums){
					if(s.getName().equals(rep.getSubforum())){
						for(Topic t : s.getTopics()){
							
							if(t.getName().equals(rep.getTopic()))
							{		
								Comment c = findComment(t.getComments(),rep.getCommentId());
								if( c != null)
								{
									deleteComment(t.getComments(),rep.getCommentId());
									//System.out.println(t.getComments().remove(c));
								}			
							}
						}
					}
					
				}
			}
			
		}
			
		
		DataBase.getInstance().saveDatabase();
		
	}
	
	
	@DELETE
	@Path("/deleteReport/{reportId}")
	@Produces(MediaType.TEXT_PLAIN)
	public void deleteReport(@PathParam("reportId") String reportId) {
		
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Report rem=null;
		for(Report report: DataBase.getInstance().getModel().getReports() ){
			
			if(report.getReportId().equals(reportId))
			{
				rem=report;
			}
		}
		
		if(rem!=null)
			DataBase.getInstance().getModel().getReports().remove(rem);
		
		DataBase.getInstance().saveDatabase();
		
	}
	
	
	public static Topic findTopic(String subforum,String topic)
	{
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{		
						return t;											
					}
				}
			}
			
		}
		return null;
	}
	
	public static Subforum findSubforum(String subforum)
	{
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				return s;
			}
			
		}
		return null;
	}
	
	
	@GET
	@Path("/getReports")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Report> users() {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Model model = DataBase.getInstance().getModel();
		
		if(user != null) {
			if( user.getRole().equals("ADMIN")) {
				return model.getReports();
			}
			
			if( user.getRole().equals("MODERATOR")) {
				
				List<Report> ret = new ArrayList<Report>();
				
				for(Report report: model.getReports()){
					
					if(report.getTypeOfRepoart().equals("subforum"))
						continue;
					else{
						
						Subforum subforum=findSubforum(report.getSubforum());
						
						if(subforum.getResponsibleModerator().getUsername().equals(user.getUsername()))
						{
							ret.add(report);
						}
						else{
							
							for(User moderator:subforum.getModerators()){
								if(moderator.getUsername().equals(user.getUsername()))
								{
									ret.add(report);
								}
							}
						}
					}
					
				}
				return ret;
			}
		}
		
		return null;
	}
	
	@POST
	@Path("/reportComment/{subforum}/{topic}/{commentId}/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public String reportComment(@PathParam("subforum") String subforum,@PathParam("topic") String topic,@PathParam("commentId") String commentId,@PathParam("text") String text) {
		
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		Report report= new Report(text, dtf.format(now), "comment", user, subforum, topic, "");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{		
						Comment c = findComment(t.getComments(),commentId);
						if( c != null)
						{
							report.setData(c.getText());
							report.setCommentId(c.getId());
						}											
					}
				}
			}
			
		}
		
		
		
		DataBase.getInstance().getModel().getReports().add(report);
		
		DataBase.getInstance().saveDatabase();
		
		return "Comment report sent";
	}
	
	@POST
	@Path("/reportTopic/{subforum}/{topic}/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public String reportTopic(@PathParam("subforum") String subforum,@PathParam("topic") String topic,@PathParam("text") String text) {
		
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		DataBase.getInstance().getModel().getReports().add(new Report(text, dtf.format(now), "topic", user, subforum, topic, ""));
		
		DataBase.getInstance().saveDatabase();
		
		return "Topic report sent";
	}
	
	@POST
	@Path("/reportSubforum/{subforum}/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public String reportSubforum(@PathParam("subforum") String subforum,@PathParam("text") String text) {
		
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		DataBase.getInstance().getModel().getReports().add(new Report(text, dtf.format(now), "subforum", user, subforum, "", ""));
		
		DataBase.getInstance().saveDatabase();
		
		return "Subforum report sent";
	}
	
	@POST
	@Path("/followSubforum/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String followSubforum( @PathParam("name") String name) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(String subforum: user.getFollowedSubforums())
		{
			if(subforum.equals(name))
			{
				return "Subforum already followed!";
			}
		}
		user.getFollowedSubforums().add(name);
		DataBase.getInstance().saveDatabase();
		
		return "Subforum followed";
		
		
	}
	
	
	@POST
	@Path("/searchSubforum")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Subforum> searchSubrofum(@FormParam("typeOfSearch") String typeOfSearch, @FormParam("searchText") String searchText ) {
	  
		
		Model model = DataBase.getInstance().getModel();
		List<Subforum> subforms= model.getSubforums();
		List<Subforum> ret = new ArrayList<Subforum>();
		
		for(Subforum subform :subforms){
			
			if(subform.getName().contains(searchText) )
			{
				ret.add(subform);
			}
			else if(subform.getResponsibleModerator().getUsername().contains(searchText)){
				ret.add(subform);
			}
			else if( subform.getDescription().contains(searchText) )
			{
				ret.add(subform);
			}
		}
		
		DataBase.getInstance().saveDatabase();
		
		return ret;
	}
	
	@POST
	@Path("/searchTopic")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Topic> searchTopic(@FormParam("typeOfSearch") String typeOfSearch, @FormParam("searchText") String searchText ) {
	  
		
		Model model = DataBase.getInstance().getModel();
		List<Subforum> subforms= model.getSubforums();
		List<Topic> ret = new ArrayList<Topic>();
		
		for(Subforum subforum :subforms){
			for(Topic t:subforum.getTopics())
				if(t.getName().contains(searchText) )
				{
					ret.add(t);
				}
				else if(t.getAuthor().getUsername().contains(searchText)){
					ret.add(t);
				}
				else if( t.getParentSubforumName().contains(searchText) )
				{
					ret.add(t);
				}
				else if( t.getContent().contains(searchText) )
				{
					ret.add(t);
				}
		}
		
		DataBase.getInstance().saveDatabase();
		return ret;
	}
	
	
	@DELETE
	@Path("/deleteTopic/{subforum}/{topic}")
	@Produces(MediaType.TEXT_PLAIN)
	public void deleteTopic(@PathParam("subforum") String subforum, @PathParam("topic") String topic) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					if(t.getName().equals(topic))
					{
						s.getTopics().remove(t);
						break;
					}
				}
			}
			
		}
		
		DataBase.getInstance().saveDatabase();
		
	}
	
	@POST
	@Path("/saveTopic/{subforum}/{topic}")
	@Produces(MediaType.TEXT_PLAIN)
	public String saveTopic(@PathParam("subforum") String subforum, @PathParam("topic") String topic) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					if(t.getName().equals(topic))
					{
						
						for(String savedtopic:user.getSavedTopics()){
							if(savedtopic.equals(t.getTopicId())){
								return "Topic alrady saved";
							}
							
						}
						
						user.getSavedTopics().add(t.getTopicId());
						DataBase.getInstance().saveDatabase();
						return "Topic saved successfuly";
					}
				}
			}
			
		}
		return "Topic not saved";
		
	}
	
	
	
	
	@POST
	@Path("/saveComment/{subforum}/{topic}/{commentId}")
	@Produces(MediaType.TEXT_PLAIN)
	public void saveComment(@PathParam("subforum") String subforum, @PathParam("topic") String topic,@PathParam("commentId") String commentId) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{
						
						Comment c = findComment(t.getComments(),commentId);
						if( c != null)
						{
							c.setParentSubforum(subforum);
							boolean found = false;
							for(String cc : user.getSavedComments())
							{
								if(cc.equals(c.getId()))
								{
									found=true;
									break;
								}
									
							}
							if(!found)
							{
								
								
								user.getSavedComments().add(c.getId());
						
							}
						}
						DataBase.getInstance().saveDatabase();
						return ;
						
					}
				}
			}
			
		}
		
		DataBase.getInstance().saveDatabase();
	}
	
	
	@POST
	@Path("/editComment/{subforum}/{topic}/{commentId}/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public void editComment(@PathParam("subforum") String subforum, @PathParam("topic") String topic,@PathParam("commentId") String commentId,@PathParam("text") String text) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{
						
						Comment c = findComment(t.getComments(),commentId);
						if( c != null)
						{
							c.setText(text);
						}
						DataBase.getInstance().saveDatabase();
						
						return ;
						
					}
				}
			}
			
		}
		DataBase.getInstance().saveDatabase();
		
	}
	@DELETE
	@Path("/deleteComment/{subforum}/{topic}/{commentId}")
	@Produces(MediaType.TEXT_PLAIN)
	public void deleteComment(@PathParam("subforum") String subforum, @PathParam("topic") String topic,@PathParam("commentId") String commentId) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{
						
						Comment c = findComment(t.getComments(),commentId);
						if( c != null)
						{
							c.setDeleted(true);
						}
						DataBase.getInstance().saveDatabase();
						return ;
						
					}
				}
			}
			
		}
		DataBase.getInstance().saveDatabase();
		
	}
	
	@POST
	@Path("/addComment/{subforum}/{topic}/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public void addCommnetToTopic(@PathParam("subforum") String subforum, @PathParam("topic") String topic,@PathParam("text") String text) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{
						
						t.getComments().add(new Comment(user, text, topic, "",s.getName()));
						
					}
				}
			}
			
		}
		
		DataBase.getInstance().saveDatabase();
	}
	
	@POST
	@Path("/updateComment/{subforum}/{topic}/{commentId}/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public void addCommnet(@PathParam("subforum") String subforum, @PathParam("topic") String topic,@PathParam("commentId") String commentId,@PathParam("text") String text) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{
						Comment c = findComment(t.getComments(),commentId);
						if( c != null)
						{
							c.getChildComments().add(new Comment(user, text, topic, commentId,s.getName()));
						}
						DataBase.getInstance().saveDatabase();
						return ;
					}
				}
			}
			
		}
		
		DataBase.getInstance().saveDatabase();
	}
	
	
	@POST
	@Path("/updateComment/{subforum}/{topic}/{commentId}/{l_dl}/{command}")
	@Produces(MediaType.TEXT_PLAIN)
	public void updateCommnet(@PathParam("subforum") String subforum, @PathParam("topic") String topic,@PathParam("commentId") String commentId,@PathParam("l_dl") String l_dl,@PathParam("command") String command) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					
					if(t.getName().equals(topic))
					{
						Comment c = findComment(t.getComments(),commentId);
						if( c != null)
						{
							if(l_dl.equals("likes"))
							{
								if(command.equals("incl"))
									c.setLikes(c.getLikes()+1);
								else 
									c.setLikes(c.getLikes()-1);
							}else{
								if(command.equals("incl"))
									c.setDislikes(c.getDislikes()+1);
								else 
									c.setDislikes(c.getDislikes()-1);
							}	
						}
						DataBase.getInstance().saveDatabase();
						return ;
					}
				}
			}
			
		}
		DataBase.getInstance().saveDatabase();
		
	}
	



	


	@GET
	@Path("/getTopic/{subforum}/{topic}")
	@Produces(MediaType.APPLICATION_JSON)
	public Topic update(@PathParam("subforum") String subforum, @PathParam("topic") String topic) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					if(t.getName().equals(topic)){
						
						return t;
					}
				}
			}
			
		}
		
		return null;
	}
	
	@GET
	@Path("/getTopic/{topicId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Topic update( @PathParam("topicId") String topicId) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		
		return findTopicById(topicId);
		
		
	}
	
	@POST
	@Path("/updateTopic/{subforum}/{topic}/{l_dl}/{command}")
	@Produces(MediaType.TEXT_PLAIN)
	public String update(@PathParam("subforum") String subforum, @PathParam("topic") String topic,@PathParam("l_dl") String l_dl,@PathParam("command") String command) {
		
		List<Subforum> subforums = DataBase.getInstance().getModel().getSubforums();
		
		for(Subforum s:subforums){
			if(s.getName().equals(subforum)){
				for(Topic t : s.getTopics()){
					if(t.getName().equals(topic)){
						
						if(l_dl.equals("likes"))
						{
							if(command.equals("incl"))
								t.setLikes(t.getLikes()+1);
							else 
								t.setLikes(t.getLikes()-1);
						}else{
							if(command.equals("incl"))
								t.setDislikes(t.getDislikes()+1);
							else 
								t.setDislikes(t.getDislikes()-1);
						}		
						
					}
				}
			}
			
		}
		
		DataBase.getInstance().saveDatabase();
		return "idk" ;
	}
	
	@GET
	@Path("/getSubforum/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Subforum getSubrofum(@PathParam("name") String name ) {
	  
		
		Model model = DataBase.getInstance().getModel();
		List<Subforum> subforms= model.getSubforums();
		
		for(Subforum subform :subforms){
			
			if(subform.getName().equals(name))
			{
				return subform;
			}
		}
		
		
		return null;
	}
	
	
	@POST
	@Path("/delete/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteSubrofum(@PathParam("name") String name ) {
	  
		
		Model model = DataBase.getInstance().getModel();
		List<Subforum> subforms= model.getSubforums();
		
		for(Subforum subform :subforms){
			
			if(subform.getName().equals(name))
			{
				subforms.remove(subform);
				DataBase.getInstance().saveDatabase();
				return "Subform successfuly deleted";
			}
		}
		
		return "Subform NOT removed";
	}
	
	@POST
	@Path("/createSubforum")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String createSubform(@FormParam("name") String name, @FormParam("description") String description, @FormParam("rules") String rules ) {
	  
		
		Model model = DataBase.getInstance().getModel();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Subforum subform = new Subforum(name, description, rules,"", user);
		
		for(Subforum subforum:model.getSubforums()){
			if(subforum.getName().equals(name))
				return "Subforum name already exists";
		}
		
		model.getSubforums().add(subform);
		
		DataBase.getInstance().saveDatabase();
		
		return "Subform successfuly created";
	}	
	
	
	
	
	@GET
	@Path("/getSubforums")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Subforum> getSubforums() {
			
		Model model = DataBase.getInstance().getModel();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		List<Subforum> ret= new ArrayList<Subforum>();
		
		if(user != null)
		{
			for(Subforum subf : model.getSubforums()){
				if(user.getUsername().equals(subf.getResponsibleModerator().getUsername()) && user.getRole().equals(Consts.moderator)){
					ret.add(subf);
				}
				else if(user.getRole().equals(Consts.admin))
				{
					ret.add(subf);
				}
			}
		}
		DataBase.getInstance().saveDatabase();
		return ret;
	
	}	
	@GET
	@Path("/getAllSubforums")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Subforum> getAllSubforums() {
			
		Model model = DataBase.getInstance().getModel();
		
		return model.getSubforums();
	
	}	
	
	public static Comment findCommentById(String commentId)
	{
		Model model = DataBase.getInstance().getModel();
		List<Subforum> subforums= model.getSubforums();
		
		for(Subforum s:subforums){
			
				for(Topic t : s.getTopics()){
					
					Comment c=findComment(t.getComments(), commentId);
					
					if(c != null)
						return c;
				}
			
			
		}
		return null;
	}

	public static Comment findComment(List<Comment> comments, String commentId) {
		Comment ret=null;
		for( Comment c : comments){
			if(c.getId().equals(commentId))
			{
				return c;
			}
			if(c.getChildComments().size() != 0)
			{
				Comment nesto= findComment(c.getChildComments(), commentId);
				if(nesto != null)
					ret=nesto;
			}
				
			
		}
		return ret;
	}
	
	private void deleteComment(List<Comment> comments, String commentId) {
		Comment rem=null;
		for( Comment c : comments){
			if(c.getId().equals(commentId))
			{
				rem=c;
			}
			if(c.getChildComments().size() != 0)
				 deleteComment(c.getChildComments(), commentId);
			
		}
		if(rem != null)
			comments.remove(rem);
	}
}



