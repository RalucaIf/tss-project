package echipa13.calatorii.service;

import echipa13.calatorii.models.FavoriteDestination;
import echipa13.calatorii.models.Tour;

import java.util.List;
import java.util.Set;

public interface FavoriteDestinationService {

    boolean isFavorite(Long userId, Long destinationId);

    List<FavoriteDestination> getFavoritesForUser(Long userId);

    /**
     * @return true dacă DUPĂ apel destinația este favorită, false dacă a fost scoasă din favorite
     */
    boolean toggleFavorite(Long userId, Long destinationId);

    void removeFavorite(Long userId, Long destinationId);

    Set<Long> getFavoriteDestinationIds(Long userId);

}
