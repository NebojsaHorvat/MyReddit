package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



@SuppressWarnings("serial")
public class Model implements Serializable {
	
	private List<Subforum> subforums;
	private List<User> users;
	private List<Report> reports;
	
	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public Model() {
		super();
		this.subforums = new ArrayList<Subforum>();
		this.users = new ArrayList<User>();
		this.reports = new ArrayList<Report>();
		loadTestData();
	}
	
	private void loadTestData() {
		User admin = new User("admin", "admin", "email", "name", "surname", "phoneNumber");
		admin.setRole("ADMIN");
		User temp = new User("user", "user", "email", "name", "surname", "phoneNumber");
		
		users.add(admin);
		users.add(temp);
		
		Subforum subforum1 = new Subforum("test1", "test", "test", "test", admin);
		subforums.add(subforum1);
		subforums.add(new Subforum("test2", "test", "test", "test", admin));
		subforums.add(new Subforum("test3", "test", "test", "test", admin));
		
		admin.followForum(subforum1);
	}

	public List<Subforum> getSubforums() {
		return subforums;
	}

	public void setSubforums(List<Subforum> subforums) {
		this.subforums = subforums;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public void changeUserRole(String username, String string) {
		for(User u :users){
			
			if(u.getUsername().equals(username))
			{
				u.setRole(string);
			}
		}
		
	}
	
}
