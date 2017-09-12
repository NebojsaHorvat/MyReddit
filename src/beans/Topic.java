package beans;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@SuppressWarnings("serial")

public class Topic implements Serializable {

	private String name;
	private User author;
	private String content;
	private String type;
	private String creationDate;
	private int likes;
	private int dislikes;
	private String image;
	private String topicId;
	
	private String parentSubforumName;
	
	@JsonManagedReference
	private List<Comment> comments;

	public Topic(String name, String content, User author, String parentSubforumName,String type, String image) {
		super();
		this.name = name;
		this.author = author;
		this.creationDate = (new Date()).toString();
		this.likes = 0;
		this.dislikes = 0;
		this.parentSubforumName = parentSubforumName;
		this.comments = new ArrayList<Comment>();
		this.content = content;
		this.type = type;
		this.image = image;
		this.topicId=UUID.randomUUID().toString();
		likes=0;
		dislikes=0;
		
		initTopic();
	}
	
	private void initTopic()
	{
		User u = new User("autorOfComment","123","email","name","suername","234");
		Comment c1=new Comment(u, "Nesto u komentaru1", name, "",parentSubforumName);
		Comment c2=new Comment(u, "Nesto u komentaru2", name, "",parentSubforumName);
		Comment c2_1=new Comment(u, "Nesto u PODkomentaru", name,c2.getId(),parentSubforumName);
		Comment c2_1_1=new Comment(u, "Nesto u PODPODkomentaru", name,c2_1.getId(),parentSubforumName);
		Comment c2_1_1_1=new Comment(u, "Nesto u PODPODPODkomentaru", name,c2_1.getId(),parentSubforumName);
		Comment c2_2=new Comment(u, "Nesto u PODkomentaru2", name,c2.getId(),parentSubforumName);
		
		c2_1.getChildComments().add(c2_1_1);
		c2.getChildComments().add(c2_1);
		c2.getChildComments().add(c2_2);
		
		comments.add(c1);
		comments.add(c2);
		
	}

	

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getImage() {
		return image;
	}



	public void setImage(String image) {
		this.image = image;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setParentSubforumName(String parentSubforumName) {
		this.parentSubforumName = parentSubforumName;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

	public String getParentSubforumName() {
		return parentSubforumName;
	}

	public void setParentSubforum(String parentSubforumName) {
		this.parentSubforumName = parentSubforumName;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void like() {
		this.likes++;
	}

	public void dislike() {
		this.dislikes++;
	}

	@Override
	public String toString() {
		return "Topic [name=" + name + ", author=" + author;
	}

	public void removeLike() {
		this.likes--;
	}
	
	public void removeDislike() {
		this.dislikes--;
	}
	
}