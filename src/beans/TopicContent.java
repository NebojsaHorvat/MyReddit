package beans;

import java.io.Serializable;

public class TopicContent implements Serializable {

	
	
	public TopicContent() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String name;
	private String subforumName;
	private String typeOfTopic;
	private String content;
	private String imageName;
	private String link;
	
	


	public TopicContent(String name, String subforumName, String typeOfTopic, String content, String imageName,
			String link) {
		super();
		this.name = name;
		this.subforumName = subforumName;
		this.typeOfTopic = typeOfTopic;
		this.content = content;
		this.imageName = imageName;
		this.link = link;
	}


	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}


	public String getImageName() {
		return imageName;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubforumName() {
		return subforumName;
	}

	public void setSubforumName(String subforumName) {
		this.subforumName = subforumName;
	}

	public String getTypeOfTopic() {
		return typeOfTopic;
	}

	public void setTypeOfTopic(String typeOfTopic) {
		this.typeOfTopic = typeOfTopic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	

}
