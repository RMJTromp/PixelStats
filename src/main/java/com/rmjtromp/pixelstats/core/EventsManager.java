package com.rmjtromp.pixelstats.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.events.RegisteredListener;

import net.minecraftforge.common.MinecraftForge;

public final class EventsManager {

	private static EventsManager manager = null;
	private static final List<Listener> queue = new ArrayList<Listener>();

	private EventsManager() {}
	
	public static void init() {
		if(manager == null) manager = new EventsManager();
		for(Listener listener : queue) _registerEvents(listener);
		if(!queue.isEmpty()) queue.clear();
		
		MinecraftForge.EVENT_BUS.register(new ForgeEventListener());
	}
	
	public static void registerEvents(Listener listener) {
		if(manager != null) _registerEvents(listener);
		else queue.add(listener);
	}
	
	public static void callEvent(Event event) {
		_callEvent(event);
	}
	
    private static void _callEvent(Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners) {
            try {
                registration.callEvent(event);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
    }
    
    private static HandlerList _getEventListeners(Class<? extends Event> type) {
        try {
            Method method = _getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
        	try {
        		throw new Exception(e.toString());
        	} catch(Exception e1) {}
        }
        return null;
    }
    
    private static Class<? extends Event> _getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return _getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
            	try {
            		throw new Exception("Unable to find handler list for event " + clazz.getName());
            	} catch(Exception e1) {}
            }
        }
        return null;
    }
    
    private static void _registerEvents(Listener listener) {
    	for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : RegisteredListener.createRegisteredListeners(listener).entrySet()) {
    		Class<? extends Event> clazz = entry.getKey();
    		Set<RegisteredListener> value = entry.getValue();
    		Class<? extends Event> registrationClass = _getRegistrationClass(clazz);
    		HandlerList list = _getEventListeners(registrationClass);
            list.registerAll(value);
        }
    }
	
}
