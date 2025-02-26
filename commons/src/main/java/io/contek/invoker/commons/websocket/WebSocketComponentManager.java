package io.contek.invoker.commons.websocket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.contek.invoker.commons.websocket.ConsumerState.TERMINATED;

final class WebSocketComponentManager {

  private final List<IWebSocketComponent> active = new ArrayList<>();
  private final List<IWebSocketComponent> idle = new ArrayList<>();

  void attach(IWebSocketComponent component) {
    switch (component.getState()) {
      case ACTIVE -> active.add(component);
      case IDLE -> idle.add(component);
      default -> throw new IllegalStateException(component.getState().name());
    }
  }

  void refresh() {
    Iterator<IWebSocketComponent> idleIt = idle.iterator();
    while (idleIt.hasNext()) {
      IWebSocketComponent next = idleIt.next();
      switch (next.getState()) {
        case ACTIVE:
          active.add(next);
        case TERMINATED:
          idleIt.remove();
          break;
      }
    }
    Iterator<IWebSocketComponent> activeIt = active.iterator();
    while (activeIt.hasNext()) {
      IWebSocketComponent next = activeIt.next();
      switch (next.getState()) {
        case IDLE:
          idle.add(next);
        case TERMINATED:
          activeIt.remove();
          break;
      }
    }
  }

  void heartbeat(WebSocketSession session) {
    active.forEach(c -> c.heartbeat(session));
  }

  boolean hasActiveComponent() {
    return !active.isEmpty();
  }

  boolean hasComponent() {
    return !active.isEmpty() || !idle.isEmpty();
  }

  void onMessage(AnyWebSocketMessage message, WebSocketSession session) {
    active.forEach(component -> component.onMessage(message, session));
  }

  void afterDisconnect() {
    active.forEach(IWebSocketListener::afterDisconnect);
    active.removeIf(component -> component.getState() == TERMINATED);

    idle.forEach(IWebSocketListener::afterDisconnect);
    idle.removeIf(component -> component.getState() == TERMINATED);
  }
}
