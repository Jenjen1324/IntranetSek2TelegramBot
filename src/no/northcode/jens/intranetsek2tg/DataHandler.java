package no.northcode.jens.intranetsek2tg;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataHandler {

	private static final String CONFIG_FILE = "config.json";
	
	public static HashMap<Long, UserData> loadUsers() {
		HashMap<Long, UserData> users = new HashMap<Long, UserData>();
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(new FileReader(CONFIG_FILE));
			JSONArray uarr = (JSONArray) obj.get("users");
			for(Object user : uarr) {
				JSONObject juser = (JSONObject) user;
				UserData u = new UserData();
				u.username = (String) juser.get("username");
				u.school = (String) juser.get("school");
				u.password = (String) juser.get("password");
				u.state = UserState.valueOf((String) juser.get("state"));
				users.put((Long) juser.get("userid"), u); 
			}
		} catch (Exception ex) { 
			System.out.println("Config file not found!");
			ex.printStackTrace();
			
		}
		
		return users;
	}
	
	public static HashMap<String, GroupData> loadGroups(HashMap<Long, UserData> users) {
		HashMap<String, GroupData> groups = new HashMap<String, GroupData>();
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(new FileReader(CONFIG_FILE));
			JSONArray garr = (JSONArray) obj.get("groups");
			for(Object group : garr) {
				JSONObject jgroup = (JSONObject) group;
				GroupData g = new GroupData();
				g.user = users.get((String) jgroup.get("user"));
				g.active = (Boolean) jgroup.get("active");
				groups.put((String) jgroup.get("groupid"), g); 
			}
		} catch (Exception ex) { }
		
		return groups;
	}
	
	@SuppressWarnings("unchecked")
	public static void saveData(HashMap<Long, UserData> users, HashMap<String, GroupData> groups) throws IOException {
		JSONObject root = new JSONObject();
		JSONArray userlist = new JSONArray();
		// Users
		for(Long userid : users.keySet()) {
			UserData user = users.get(userid);
			JSONObject udata = new JSONObject();
			udata.put("userid", userid);
			udata.put("username", user.username);
			udata.put("school", user.school);
			udata.put("password", user.password);
			udata.put("state", user.state.toString());
			userlist.add(udata);
		}
		root.put("users", userlist);
		
		JSONArray grouplist = new JSONArray();
		for(String groupid : groups.keySet()) {
			GroupData group = groups.get(groupid);
			JSONObject gdata = new JSONObject();
			gdata.put("groupid", groupid);
			gdata.put("user", group.user);
			gdata.put("active", group.active);
			grouplist.add(gdata);
		}
		root.put("groups", grouplist);
		
		String json = root.toJSONString();
		FileWriter file = new FileWriter(CONFIG_FILE);
		file.write(json);
		file.flush();
		file.close();
	}
	
}
