package echipa13.calatorii.service;

import echipa13.calatorii.models.Trip;

import java.util.List;

public interface TripService {
    List<Trip> listForUser(Long userId);
    Trip getOwned(Long tripId, Long userId);
    Trip create(Long userId, Trip trip);
    Trip update(Long userId, Long tripId, Trip updated);
    void delete(Long userId, Long tripId);
    List<Trip> listForUserByCategory(Long userId, String category);
    List<String> listCategoriesForUser(Long userId);


}
