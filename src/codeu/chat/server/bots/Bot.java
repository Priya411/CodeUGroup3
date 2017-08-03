package codeu.chat.server.bots;

import com.fasterxml.jackson.annotation.JsonValue;

import codeu.chat.util.Uuid;

public abstract class Bot {
	public abstract String reactTo(String message, Uuid sender);

	public abstract String onAdd();

	@JsonValue
	public String name() {
		return this.getClass().getName();
	}
}
