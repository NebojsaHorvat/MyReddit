package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@SuppressWarnings("serial")

public class Comment implements Serializable {

	private User author;
	private String date;
	private String text;
	private int likes;
	private int dislikes;
	private boolean modified;
	private String id;
	private boolean deleted=false;

	private String parentSubforum;
	private String parentTopic;
	private String parentCommentId;
	private List<Comment> childComments;
	
	public Comment(User author, String text, String parentTopic, String parentCommentiD,String parentSubforum) {
		this.author = author;
		this.date = "";
		this.text = text;
		this.likes = 0;
		this.dislikes = 0;
		this.modified = false;
		this.parentTopic = parentTopic;
		this.parentCommentId = parentCommentiD;
		this.childComments = new ArrayList<Comment>();
		this.parentSubforum = parentSubforum;
		id= UUID.randomUUID().toString();
	}

	public String getParentSubforum() {
		return parentSubforum;
	}

	public void setParentSubforum(String parentSubforum) {
		this.parentSubforum = parentSubforum;
	}
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public String getParentCommentId() {
		return parentCommentId;
	}

	public void setParentCommentId(String parentCommentId) {
		this.parentCommentId = parentCommentId;
	}



	

	public String getParentTopic() {
		return parentTopic;
	}

	public void setParentTopic(String parentTopic) {
		this.parentTopic = parentTopic;
	}

	public List<Comment> getChildComments() {
		return childComments;
	}

	public void setChildComments(List<Comment> childComments) {
		this.childComments = childComments;
	}

	@Override
	public String toString() {
		return "Comment [author=" + author + ", date=" + date + ", text=" + text + ", likes=" + likes + ", dislikes="
				+ dislikes + ", modified=" + modified + "]";
	}
	
}