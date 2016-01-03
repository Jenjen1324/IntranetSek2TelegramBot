package no.northcode.jens.intranetsek2tg;

import no.northcode.jens.intranetsek2.Login;

public class UserData {

	public UserState state;
	
	public String username;
	public String password;
	public String school;
	
	public boolean success;
	
	public UserData() {
		success = false;
	}
	
	public Login getIntranetLogin() {
		if(school != null && username != null && password != null) {
			return new Login(username, password, school);
		}
		return null;
	}
}
