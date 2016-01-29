package no.northcode.jens.intranetsek2tg;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.northcode.jens.intranetsek2.Lesson;
import no.northcode.jens.intranetsek2.Login;
import no.northcode.jens.intranetsek2.School;
import no.northcode.jens.intranetsek2.exception.IntranetException;
import no.northcode.jens.intranetsek2.exception.InvalidCredentialsException;
import no.northcode.jens.intranetsek2.exception.LoginException;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.ForceReply;
import pro.zackpollard.telegrambot.api.chat.message.content.Content;
import pro.zackpollard.telegrambot.api.chat.message.content.TextContent;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder;

public class IntranetHandler {

	private List<School> schools;
	private HashMap<String, List<School>> schools_str;
	private TelegramBot bot;
	
	public IntranetHandler(TelegramBot bot) throws IOException {
		schools = School.getSchoolList();
		schools_str = School.getStructuredList(schools);
		this.bot = bot;
	}
	
	public void selectSchoolCat(MessageEvent event, UserData user) {
		ReplyKeyboardMarkupBuilder keyboard = ReplyKeyboardMarkup.builder()
				.selective(true)
				.oneTime(true);
		for(String cat : schools_str.keySet()) {
			keyboard.addRow(cat);
		}
		event.getChat().sendMessage(SendableTextMessage.builder()
				.message(Strings.msg_select_school_cat)
				.replyMarkup(keyboard.build())
				.build()
				, bot);
		user.state = UserState.SELECT_SCHOOL_CAT;
		user.success = false;
	}
	
	public void handleSelectSchoolCat(TextMessageReceivedEvent event, UserData user) {
		Content content = event.getMessage().getContent();
		if(content instanceof TextContent) {
			TextContent msg = (TextContent) content;
			
			if(schools_str.containsKey(msg.getContent())) {
				ReplyKeyboardMarkupBuilder keyboard = ReplyKeyboardMarkup.builder()
						.selective(true)
						.oneTime(true);
				for(School school : schools_str.get(msg.getContent())) {
					keyboard.addRow(school.getName());
				}
				event.getChat().sendMessage(SendableTextMessage.builder()
						.message(Strings.msg_select_school)
						.replyMarkup(keyboard.build())
						.build()
						, bot);
				user.state = UserState.SELECT_SCHOOL;
				return;
			}
		}
		
		event.getChat().sendMessage(Strings.msg_invalid_school_cat, bot);
		selectSchoolCat(event, user);
	}
	
	public void handleSelectSchool(TextMessageReceivedEvent event, UserData user) {
		String msg = ((TextContent)event.getMessage().getContent()).getContent();
		School s = School.findSchoolByName(msg, schools);
		if(s == null) {
			event.getChat().sendMessage(Strings.msg_invalid_school, bot);
			selectSchoolCat(event, user);
			return;
		}
		
		user.school = s.getId();
		
		// Username
		event.getChat().sendMessage(SendableTextMessage.builder()
				.message(Strings.msg_enter_username)
				.replyMarkup(new ForceReply(true))
				.build()
				, bot);
		user.state = UserState.PROMPT_USERNAME;
	}

	public void handleUsername(TextMessageReceivedEvent event, UserData user) {
		String msg = ((TextContent)event.getMessage().getContent()).getContent();
		user.username = msg;
		
		event.getChat().sendMessage(SendableTextMessage.builder()
				.message(Strings.msg_enter_password)
				.replyMarkup(new ForceReply(true))
				.build()
				, bot);
		user.state = UserState.PROMPT_PASSWORD;
	}

	public void handlePassword(TextMessageReceivedEvent event, UserData user) {
		String msg = ((TextContent)event.getMessage().getContent()).getContent();
		user.password = msg;
		
		Login l = user.getIntranetLogin();
		if(l != null) {
			try {
				l.login();
				event.getChat().sendMessage(Strings.msg_success, bot);
				user.state = UserState.AUTHENTICATED;
				user.success = true;
				defaultKeyboard(event);
				return;
			} catch (InvalidCredentialsException ex) {
				event.getChat().sendMessage(Strings.msg_invalid_logindata, bot);
				selectSchoolCat(event, user);
				return;
			} catch (Exception ex) {
				event.getChat().sendMessage(Strings.msg_failed_login, bot);
				user.state = UserState.WELCOME;
				return;
			}
		}	
	}
	
	public void defaultKeyboard(MessageReceivedEvent event) {
		ReplyKeyboardMarkupBuilder keyboard = ReplyKeyboardMarkup.builder();
		keyboard.addRow(Strings.keyboard_today, Strings.keyboard_tomorrow);
		if(event.getChat().getType() == ChatType.PRIVATE) 
			keyboard.addRow(Strings.keyboard_followingweek, Strings.keyboard_date);
		else
			keyboard.addRow(Strings.keyboard_followingweek);
		
		
		event.getChat().sendMessage(
				SendableTextMessage.builder()
				.message(Strings.msg_show_keyboard)
				.replyMarkup(keyboard.build())
				.build(), bot);
	}

	public void handleTimetable(TextMessageReceivedEvent event, UserData user) {
		handleTimetable(event, user, null);
	}
	
	public void sendTimetable(Chat chat, UserData user, LocalDate day) {
		Login login = user.getIntranetLogin();
		try {
			login.login();
			ArrayList<Lesson> lessons = Lesson.getLessonByDay(login, day);
			String timetable = prettyTimetable(lessons);
			chat.sendMessage(timetable, bot);
		}catch(Exception ex) {
			
		}
		
	}
	
	
	public void handleTimetable(TextMessageReceivedEvent event, UserData user, LocalDate day) {
		String msg = ((TextContent)event.getMessage().getContent()).getContent();
		if(msg.equals(Strings.keyboard_date)) {
			promptDate(event, user);
			return;
		}
		
		Login login = user.getIntranetLogin();
		try {
			login.login();
			
			ArrayList<Lesson> lessons;
			
			if(day != null) {
				lessons = Lesson.getLessonByDay(login, day);
			} else {
				switch(msg) {
				case Strings.keyboard_today:
					lessons = Lesson.getLessonByDay(login, LocalDate.now());
					break;
				case Strings.keyboard_tomorrow:
					lessons = Lesson.getLessonByDay(login, LocalDate.now().plusDays(1));
					break;
				case Strings.keyboard_followingweek:
					lessons = Lesson.getLessonByWeek(login, LocalDate.now());
					break;
				default:
					lessons = Lesson.getLessonByDay(login, LocalDate.now());
					break;
				}
			}
			
			String timetable = prettyTimetable(lessons);
			
			event.getChat().sendMessage(SendableTextMessage.builder()
					.message(timetable)
					.parseMode(ParseMode.MARKDOWN)
					.build()
					, bot);
			user.state = UserState.AUTHENTICATED;
			
		} catch (InvalidCredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IntranetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String prettyTimetable(ArrayList<Lesson> lessons) {
		StringBuilder sb = new StringBuilder();
		String date = "";
		for(Lesson les : lessons) {
			String newDate = les.getStartTime().toLocalDate().toString();
			if(!date.equals(newDate)) {
				sb.append(String.format("*%s*\n\n", newDate));
				date = newDate;	
			}
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
	
	public void promptDate(TextMessageReceivedEvent event, UserData user) {
		user.state = UserState.AUTHENTICATED_DATEPROMPT;
		event.getChat().sendMessage(SendableTextMessage.builder()
				.message(Strings.msg_enter_date)
				.replyTo(event.getMessage())
				.replyMarkup(new ForceReply())
				.build(), bot);
	}
	
	public void handleDate(TextMessageReceivedEvent event, UserData user) {
		String msg = ((TextContent)event.getMessage().getContent()).getContent();
		handleTimetable(event, user, LocalDate.parse(msg));
	}
}
