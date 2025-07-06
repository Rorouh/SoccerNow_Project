package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.repository.RefereeRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class RefereeService {

    private final RefereeRepository refereeRepository;

    public RefereeService(RefereeRepository refereeRepository) {
        this.refereeRepository = refereeRepository;
    }

    @Transactional(readOnly = true)
    public List<Referee> findAllReferees() {
        return refereeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Referee> findByName(String name) {
        return refereeRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Referee> findByMinGames(long minGames) {
        if (minGames < 0) {
            throw new ApplicationException("minGames no puede ser negativo.");
        }
        return refereeRepository.findRefereesWithMinGames(minGames);
    }

    public Optional<Referee> getRefereeById(Long id) {
        return refereeRepository.findById(id);
    }

    public Set<Referee> findAllByIds(Set<Long> ids) {
        return refereeRepository.findAllById(ids).stream().collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<Referee> filterReferees(String name, Integer minGames, Integer minCards) {
        List<Referee> all = refereeRepository.findAll();
        return all.stream()
            .filter(r -> name == null || r.getName().toLowerCase().contains(name.toLowerCase()))
            .filter(r -> minGames == null
                         || (r.getGamesAsPrimary().size()
                             + r.getGamesAsAssistant().size()) >= minGames)
            // de momento ignoramos minCards
            .toList();
    }
}
