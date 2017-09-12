package Data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import beans.Model;




public class DataBase {
	
	@Context
	ServletContext context;
	
	private static DataBase instance;
	private static Model model;
	private String path;
	private DataBase()
	{
		super();
		model = new Model();
		
		path=System.getProperty("user.home") + "/" + "model.bin";
		//path = context.getRealPath("/") + "/" + "model.bin";
		//path="model.bin";
		
		String nesto="";
		File databaseFile = new File(path);
		if (!databaseFile.exists()) {
			model = new Model();
			saveDatabase();
		}
		else {
			loadDatabase();
		}
	}
	
	
	
	public static DataBase  getInstance() 
	{
		if(instance == null)
		{
			instance = new DataBase();
		}
		
		return instance;
	}
	
	public Model getModel()
	{
		return model;
	}
	
	public void saveDatabase() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(
					new BufferedOutputStream(
						new FileOutputStream(path)));
			
			out.writeObject(model);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	public void loadDatabase() {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(
					new BufferedInputStream(
						new FileInputStream(path)));
			
			model = ((Model) in.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
}
