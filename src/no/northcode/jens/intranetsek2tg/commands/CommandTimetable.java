package no.northcode.jens.intranetsek2tg.commands;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import de.raysha.lib.telegram.bot.api.exception.BotException;
import de.raysha.lib.telegram.bot.api.model.Message;
import no.northcode.jens.intranetsek2.Lesson;
import no.northcode.jens.intranetsek2.Login;
import no.northcode.jens.intranetsek2tg.MysqlHelper;
import no.northcode.jens.intranetsek2tg.model.SekUser;
import no.northcode.jens.plustgbot.ICommandHandler;
import no.northcode.jens.plustgbot.PlusBot;

public class CommandTimetable implements ICommandHandler {

	private static final String invalidDate = "Kein gültiges datum! Bitte benutze `YYYY-MM-DD`";
	private static final String invalidLogin = "Du hast dich noch nicht eingeloggt! Bitte log dich mit /login ein";
	private static final String invalidCred = "Oops! Entweder hat dein Passwort geändert oder bei uns ist ein Fehler aufgetreten...";
	private MysqlHelper mysql;
	
	public CommandTimetable(MysqlHelper mysql) {
		this.mysql = mysql;
	}
	
	@Override
	public ArrayList<String> getCommands() {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("timetable");
		cmds.add("stundenplan");
		return cmds;
	}

	@Override
	public void handleMessage(PlusBot bot, Message message) throws BotException {
		String[] split = message.getText().split(" ");
		LocalDate ld;
		if(split.length == 1) {
			ld = LocalDate.now();
		} else if(split[1].equalsIgnoreCase("morgen")) {
			ld = LocalDate.now().plusDays(1);
		} else if(split[1].equalsIgnoreCase("heute")) {
			ld = LocalDate.now();
		} else {
			try {
				ld = LocalDate.parse(split[1]);
			} catch (DateTimeParseException ex) {
				bot.normalReply(message, invalidDate);
				return;
			}
		}
		
		SekUser user = null; 
		try {
			user = SekUser.getUserByMessage(message, mysql.getLogins());
		} catch (Exception ex) {
			bot.normalReply(message, invalidLogin);
			return;
		}
		
		Login login = null;
		ArrayList<Lesson> lessons = null;
		try {
			login = user.getIntranetLogin();
			lessons = Lesson.getLessonByDay(login, ld);
		} catch (Exception ex) {
			bot.normalReply(message, invalidCred);
			return;
		}
		
		String timetable = prettyTimetable(lessons);
		bot.normalReply(message, timetable);
	}

	@Override
	public void handleReply(PlusBot bot, Message message) throws BotException, Exception {
		// TODO Auto-generated method stub
		
	}
	
	private String prettyTimetable(ArrayList<Lesson> lessons) {
		LocalDate date = lessons.get(0).getStartTime().toLocalDate();
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("*Stundenplan vom %s*\n\n",date.toString()));
		for(Lesson les : lessons) {
			String type = les.getType();
			switch(les.getType()) {
			case "lesson":
				type = "\u2705";
				break;
			case "holiday":
				type = "\u2708";
				break;
			case "cancel":
				type = "\u274c";
				break;
			case "modlesson":
				type = "\u2734";
				break;
			case "block":
				type = "\uD83D\uDEBB";
				break;
			}
			String room = "";
			if(les.getRoomName() != null && les.getRoomName() != "null")
				room = String.format("Zi: %s", les.getRoomName());
			
			sb.append(String.format("%s %02d:%02d - %02d:%02d %s %s\n", type, les.getStartTime().getHour(), les.getStartTime().getMinute(), les.getEndTime().getHour(), les.getEndTime().getMinute(), les.getTitle(), room));
			if(les.getMessage() != null && les.getMessage() != "") {
				sb.append(String.format(" \uD83D\uDD16 %s\n", les.getMessage()));
			}
		}
		return sb.toString();
	}

}
