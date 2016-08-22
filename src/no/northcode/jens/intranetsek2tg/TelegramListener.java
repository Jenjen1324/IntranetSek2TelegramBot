package no.northcode.jens.intranetsek2tg;

import java.io.IOException;
import java.util.HashMap;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.user.User;

public class TelegramListener implements Listener {
	
	private TelegramBot bot;
	private HashMap<Long, UserData> users;
	private HashMap<String, GroupData> groups;
	private IntranetHandler intranet;

	public TelegramListener(TelegramBot bot) throws IOException {
		this.bot = bot;
		this.users = DataHandler.loadUsers();
		this.groups = DataHandler.loadGroups(users);
		intranet = new IntranetHandler(bot);
	}
	
	@Override
	public void onTextMessageReceived(TextMessageReceivedEvent event) {
		check(event);
		if(event.getChat().getType() == ChatType.PRIVATE) {
			UserData user = getUserData(event);
			switch(user.state) {
			case SELECT_SCHOOL_CAT:
				intranet.handleSelectSchoolCat(event, user);
				break;
			case PROMPT_PASSWORD:
				intranet.handlePassword(event, user);
				break;
			case PROMPT_USERNAME:
				intranet.handleUsername(event, user);
				break;
			case SELECT_SCHOOL:
				intranet.handleSelectSchool(event, user);
				break;
			case AUTHENTICATED:
				intranet.handleTimetable(event, user);
				break;
			case AUTHENTICATED_DATEPROMPT:
				intranet.handleDate(event, user);
				break;
			default:
				break;
				
			}
		} else if(event.getChat().getType() == ChatType.GROUP || event.getChat().getType() == ChatType.SUPERGROUP) {
			GroupData group = groups.get(event.getChat().getId());
			if(group.active) {
				intranet.handleTimetable(event, group.user);
			}
		}
	}
	
	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		check(event);
		if(event.getChat().getType() == ChatType.PRIVATE) {
			UserData user = getUserData(event);
			switch(event.getCommand()) {
			case "login":
				intranet.selectSchoolCat(event, user);
				return;
			case "timetable":
				if(user.success) {
					intranet.defaultKeyboard(event);
				} else {
					intranet.selectSchoolCat(event, user);
				}
			default:
				showHelp(event);
				return;
			}
		} else if (event.getChat().getType() == ChatType.GROUP || event.getChat().getType() == ChatType.SUPERGROUP) {
			GroupData group = groups.get(event.getChat().getId());
			if(event.getCommand().equals("auth")) {
				UserData user = users.get(event.getMessage().getSender().getId());
				if(user == null || user.success == false) {
					event.getChat().sendMessage(Strings.msg_no_login);
				} else {
					group.user = user;
					group.active = true;
					event.getChat().sendMessage(Strings.msg_authenticated);
				}
			} else if (event.getCommand().equals("timetable")) {
				if(group.active) {
					intranet.defaultKeyboard(event);
				} else {
					event.getChat().sendMessage(Strings.msg_no_login);
				}
			}
		}
	}
	
	@Override
	public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
		User usr = event.getParticipant();
		if(usr.getId() == bot.getBotID()) {
			check(event);
		}
	}
	
	private void check(MessageEvent event) {
		System.out.println(event.toString());
		if(event.getChat().getType() == ChatType.PRIVATE) {
			if(!users.containsKey(event.getMessage().getSender().getId())) {
				UserData data = new UserData();
				users.put(event.getMessage().getSender().getId(), data);
				handleWelcome(event);
			}
		} else if (event.getChat().getType() == ChatType.GROUP || event.getChat().getType() == ChatType.SUPERGROUP) {
			if(!groups.containsKey(event.getMessage().getChat().getId())) {
				GroupData data = new GroupData();
				groups.put(event.getChat().getId(), data);
				handleGroupWelcome(event);
			}
		}
		try {
			DataHandler.saveData(users, groups);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private UserData getUserData(MessageEvent event) {
		return this.users.get(event.getMessage().getSender().getId());
	}
	
	private void handleWelcome(MessageEvent event) {
		event.getChat().sendMessage(Strings.msg_welcome);
		getUserData(event).state = UserState.WELCOME;
	}
	
	private void handleGroupWelcome(MessageEvent event) {
		event.getChat().sendMessage(Strings.msg_welcome_group);
	}
	
	private void showHelp(MessageEvent event) {
		event.getChat().sendMessage(SendableTextMessage.builder()
				.message("HALP")
				.replyMarkup(ReplyKeyboardHide.builder().build()).build());
	}
	

}
