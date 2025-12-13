package echipa13.calatorii.service.impl;

import echipa13.calatorii.models.Tour;
import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.TourRepository;
import echipa13.calatorii.repository.TripRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository trips;
    private final UserRepository users;

    public TripServiceImpl(TripRepository trips, UserRepository users) {
        this.trips = trips;
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> listForUser(Long userId) {
        return trips.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Trip getOwned(Long tripId, Long userId) {
        return trips.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Trip inexistent sau nu aparține utilizatorului."));
    }

    @Override
    public Trip create(Long userId, Trip trip) {
        UserEntity owner = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator inexistent."));
        trip.setUser(owner);
        return trips.save(trip);
    }

    @Override
    public Trip update(Long userId, Long tripId, Trip updated) {
        Trip existing = getOwned(tripId, userId);
        existing.setTitle(updated.getTitle());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        return trips.save(existing);
    }

    @Override
    public void delete(Long userId, Long tripId) {
        Trip existing = getOwned(tripId, userId);
        trips.delete(existing);
    }
}
