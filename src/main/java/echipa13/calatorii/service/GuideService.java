package echipa13.calatorii.service;

import echipa13.calatorii.Dto.RegistrationGuideDto;

public interface GuideService {
    void saveGuide(Long userId, RegistrationGuideDto registrationGuideDto);
}
