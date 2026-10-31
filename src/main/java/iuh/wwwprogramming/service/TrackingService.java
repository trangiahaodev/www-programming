package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.TrackingEventDTO;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.entity.UserActivityLog;

import java.util.List;

public interface TrackingService {

    UserActivityLog recordTrackingEvent(String sessionId, TrackingEventDTO eventDto, User user);

    UserActivityLog recordCartEvent(String sessionId, String productId, int quantity, boolean isBuyNow, User user);

    List<UserActivityLog> getSessionActivities(String sessionId);
}
