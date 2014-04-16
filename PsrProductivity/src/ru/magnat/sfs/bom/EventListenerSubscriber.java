package ru.magnat.sfs.bom;

import java.util.Set;

public class EventListenerSubscriber {
	public static void addListener(Set<IEventListener> listeners,
			IEventListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (listeners.contains(listener))
			return;
		listeners.add(listener);

	}

	public static void setListener(Set<IEventListener> listeners, IEventListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (listeners.contains(listener))
			return;
		listeners.clear();
		listeners.add(listener);
	}

	public static void removeListener(Set<IEventListener> listeners,
			IEventListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (!listeners.contains(listener))
			return;
		listeners.remove(listener);

	}

}
