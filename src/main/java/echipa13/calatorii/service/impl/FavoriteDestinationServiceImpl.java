package echipa13.calatorii.service.impl;

import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.FavoriteDestination;
import echipa13.calatorii.models.UserEntity; // <-- schimbă dacă e alt nume
import echipa13.calatorii.repository.DestinationsRepository;
import echipa13.calatorii.repository.FavoriteDestinationRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.FavoriteDestinationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoriteDestinationServiceImpl implements FavoriteDestinationService {

    private final FavoriteDestinationRepository favoritesRepo;
    private final UserRepository userRepo;
    private final DestinationsRepository destinationRepo;

    public FavoriteDestinationServiceImpl(FavoriteDestinationRepository favoritesRepo,
                                          UserRepository userRepo,
                                          DestinationsRepository destinationRepo) {
        this.favoritesRepo = favoritesRepo;
        this.userRepo = userRepo;
        this.destinationRepo = destinationRepo;
    }

    @Override
    public boolean isFavorite(Long userId, Long destinationId) {
        return favoritesRepo.existsByUser_IdAndDestination_Id(userId, destinationId);
    }

    @Override
    public List<FavoriteDestination> getFavoritesForUser(Long userId) {
        return favoritesRepo.findAllByUser_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long userId, Long destinationId) {
        var existing = favoritesRepo.findByUser_IdAndDestination_Id(userId, destinationId);
        if (existing.isPresent()) {
            favoritesRepo.delete(existing.get());
            return false;
        }

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Destinations dest = destinationRepo.findById(destinationId)
                .orElseThrow(() -> new IllegalArgumentException("Destination not found: " + destinationId));

        favoritesRepo.save(new FavoriteDestination(user, dest));
        return true;
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long destinationId) {
        favoritesRepo.deleteByUser_IdAndDestination_Id(userId, destinationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getFavoriteDestinationIds(Long userId) {
        if (userId == null) return Set.of();
        return new HashSet<>(favoritesRepo.findDestinationIdsByUserId(userId));
    }
}
