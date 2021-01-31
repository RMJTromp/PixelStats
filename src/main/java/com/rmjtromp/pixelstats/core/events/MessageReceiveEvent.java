package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Cancellable;
import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.util.IChatComponent;

public class MessageReceiveEvent extends Event implements Cancellable {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private boolean cancelled = false;
	private IChatComponent component = null;
	
	public MessageReceiveEvent(IChatComponent component) {
		this.component = component;
	}
	
	public IChatComponent getMessage() {
		return this.component;
	}
	
	public void setMessage(IChatComponent component) {
		if(component != null) this.component = component;
		else this.cancelled = true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
