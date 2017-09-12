package beans;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class Report implements Serializable {

	private String text;
	private String date;
	private String typeOfRepoart;
	private User user;
	private String data;
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	private String subforum;
	private String topic;
	private String commentId;
	private String reportId;
	
	
	public Report(String text, String date, String typeOfRepoart, User user, String subforum, String topic,
			String commentId) {
		super();
		this.text = text;
		this.date = date;
		this.typeOfRepoart = typeOfRepoart;
		this.user = user;
		this.subforum = subforum;
		this.topic = topic;
		this.commentId = commentId;
		this.reportId = UUID.randomUUID().toString();
	}
	
	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTypeOfRepoart() {
		return typeOfRepoart;
	}
	public void setTypeOfRepoart(String typeOfRepoart) {
		this.typeOfRepoart = typeOfRepoart;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getSubforum() {
		return subforum;
	}
	public void setSubforum(String subforum) {
		this.subforum = subforum;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	



	
}