package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@SuppressWarnings("serial")
public class User implements Serializable {

	private String username;
	private String password;
	private String email;
	private String name;
	private String surname;
	private String phoneNumber;
	private String registrationDate;
	
	private String role;

	private List<String> followedSubforums;
	private List<String> savedTopics;
	private List<String> savedComments;
	private List<String> likedTopics;
	private List<String> dislikedTopics;
	private List<String> likedComments;
	private List<String> dislikedComments;
	private List<Comment> comments;
	private List<Message> messages;
	

	public User(String username, String password, String email, String name, String surname, String phoneNumber) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phoneNumber;
		this.registrationDate = (new Date()).toString();
		this.role = "USER";
		this.followedSubforums = new ArrayList<String>();
		this.savedTopics = new ArrayList<String>();
		this.savedComments = new ArrayList<String>();
		this.likedTopics = new ArrayList<String>();
		this.dislikedTopics = new ArrayList<String>();
		this.likedComments = new ArrayList<String>();
		this.dislikedComments = new ArrayList<String>();
		this.comments = new ArrayList<Comment>();
		this.messages = new ArrayList<Message>();
		this.role= "USER";
	}
	
	

	

	


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	

	

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getFollowedSubforums() {
		return followedSubforums;
	}

	public void setFollowedSubforums(List<String> followedSubforums) {
		this.followedSubforums = followedSubforums;
	}



	
	

	public List<String> getSavedComments() {
		return savedComments;
	}








	public void setSavedComments(List<String> savedComments) {
		this.savedComments = savedComments;
	}








	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", email=" + email + ", name=" + name
				+ ", surname=" + surname + ", phoneNumber=" + phoneNumber + ", registrationDate=" + registrationDate
				+ ", followedSubforums=" + followedSubforums + ", followedTopics=" + savedTopics + ", comments="
				+ comments + "]";
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	

	public List<String> getLikedTopics() {
		return likedTopics;
	}








	public void setLikedTopics(List<String> likedTopics) {
		this.likedTopics = likedTopics;
	}








	public List<String> getDislikedTopics() {
		return dislikedTopics;
	}








	public void setDislikedTopics(List<String> dislikedTopics) {
		this.dislikedTopics = dislikedTopics;
	}








	public List<String> getLikedComments() {
		return likedComments;
	}








	public void setLikedComments(List<String> likedComments) {
		this.likedComments = likedComments;
	}








	public List<String> getDislikedComments() {
		return dislikedComments;
	}








	public void setDislikedComments(List<String> dislikedComments) {
		this.dislikedComments = dislikedComments;
	}








	
	public List<String> getSavedTopics() {
		return savedTopics;
	}








	public void setSavedTopics(List<String> savedTopics) {
		this.savedTopics = savedTopics;
	}









	public void followForum(Subforum subforum) {
		followedSubforums.add(subforum.getName());
	}

	public boolean followsSubforum(String subforumId) {
		for(String followedSubforum: followedSubforums) {
			if(followedSubforum.equals(subforumId)) {
				return true;
			}
		}
		return false;
	}
	
}