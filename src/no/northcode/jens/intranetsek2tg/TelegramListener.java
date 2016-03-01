package no.northcode.jens.intranetsek2tg;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

public class TelegramListener implements Listener {
	
	private TelegramBot bot;
	private HashMap<Integer, UserData> users;
	private HashMap<String, GroupData> groups;
	private IntranetHandler intranet;

	public HashMap<Integer, UserData> getUsers() {
		return users;
	}
	
	public HashMap<String, GroupData> getGroups() {
		return groups;
	}
	
	public TelegramListener(TelegramBot bot) throws IOException {
		this.bot = bot;
		this.users = DataHandler.loadUsers();
		this.groups = DataHandler.loadGroups();
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
				intranet.handleTimetable(event, users.get(group.user));
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
			switch(event.getCommand()) {
			case "auth":
				UserData user = users.get(event.getMessage().getSender().getId());
				if(user == null || user.success == false) {
					event.getChat().sendMessage(Strings.msg_no_login, bot);
				} else {
					group.user = event.getMessage().getSender().getId();
					group.active = true;
					event.getChat().sendMessage(Strings.msg_authenticated, bot);
				}
				break;
			case "heute":
				intranet.handleTimetable(event, users.get(group.user));
				break;
			case "morgen":
				intranet.handleTimetable(event, users.get(group.user), LocalDate.now().plusDays(1));
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
		event.getChat().sendMessage(Strings.msg_welcome, bot);
		getUserData(event).state = UserState.WELCOME;
	}
	
	private void handleGroupWelcome(MessageEvent event) {
		event.getChat().sendMessage(Strings.msg_welcome_group, bot);
	}
	
	private void showHelp(MessageEvent event) {
		
	}

	public IntranetHandler getIntranetHandler() {
		return intranet;
	}

	public TelegramBot getBot() {
		return bot;
	}
	

}
