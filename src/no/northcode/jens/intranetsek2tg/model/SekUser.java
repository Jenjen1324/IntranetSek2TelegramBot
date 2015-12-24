package no.northcode.jens.intranetsek2tg.model;

import java.io.IOException;
import java.util.ArrayList;

import de.raysha.lib.telegram.bot.api.model.Message;
import no.northcode.jens.intranetsek2.Login;
import no.northcode.jens.intranetsek2.exception.IntranetException;
import no.northcode.jens.intranetsek2.exception.InvalidCredentialsException;
import no.northcode.jens.intranetsek2.exception.LoginException;
import no.northcode.jens.intranetsek2tg.MysqlHelper;

@SuppressWarnings("unused")
public class SekUser {
	
	private int id_users;
	private int tg_userid;
	private String username;
	private String password;
	private String school;
	
	
	public int getId_users() {
		return id_users;
	}

	
	public int getTg_userid() {
		return tg_userid;
	}


	public String getUsername() {
		return username;
	}


	public String getPassword() {
		return password;
	}


	public String getSchool() {
		return school;
	}


	public SekUser(int id_users, int tg_userid, String username, String password, String school) {
		this.id_users = id_users;
		this.tg_userid = tg_userid;
		this.username = username;
		this.password = password;
		this.school = school;
	}
	
	public static SekUser getUserByMessage(Message msg, ArrayList<SekUser> users) throws Exception {
		for(SekUser user : users) {
			if(user.tg_userid == msg.getFrom().getId())
				return user;
		}
		throw new Exception("User not found!");
	}
	
	public Login getIntranetLogin() throws IOException, InvalidCredentialsException, LoginException, IntranetException {
		return new Login(username, password, school);
	}

}
