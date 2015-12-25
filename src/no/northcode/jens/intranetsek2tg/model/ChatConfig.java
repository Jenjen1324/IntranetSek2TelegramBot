package no.northcode.jens.intranetsek2tg.model;

public class ChatConfig {

	private int chat_id;
	private int chatId;
	public int getChatId() {
		return chatId;
	}

	private SekUser login;
	
	public SekUser getLogin() {
		return login;
	}

	public ChatConfig(int chat_id, int chatId, SekUser login) {
		this.chat_id = chat_id;
		this.chatId = chatId;
		this.login = login;
	}
	
	
}
