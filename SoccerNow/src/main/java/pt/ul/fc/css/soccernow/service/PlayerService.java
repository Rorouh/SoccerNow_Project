// src/main/java/pt/ul/fc/css/soccernow/service/PlayerService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.PlayerCreateDTO;
import pt.ul.fc.css.soccernow.dto.PlayerUpdateDTO;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.domain.Player.PreferredPosition;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player createPlayer(PlayerCreateDTO dto) {
        if (playerRepository.existsByEmail(dto.getEmail())) {
            throw new ApplicationException("Ya existe un jugador con ese email.");
        }
        if (dto.getPreferredPosition() == null || dto.getPreferredPosition().isBlank()) {
            throw new ApplicationException("El campo 'preferredPosition' es obligatorio para un PLAYER.");
        }

        // Convertir String → enum
        PreferredPosition enumPos;
        try {
            enumPos = PreferredPosition.valueOf(dto.getPreferredPosition());
        } catch (IllegalArgumentException ex) {
            throw new ApplicationException(
                "PreferredPosition inválido. Valores válidos: PORTERO, DEFENSA, CENTROCAMPISTA, DELANTERO."
            );
        }

        Player jugador = new Player();
        jugador.setName(dto.getName());
        jugador.setEmail(dto.getEmail());
        jugador.setPassword(dto.getPassword());
        jugador.setRole("PLAYER");  // heredado de User
        jugador.setPreferredPosition(enumPos);
        jugador.setGoals(dto.getGoals());
        jugador.setCards(dto.getCards());

        return playerRepository.save(jugador);
    }

    @Transactional(readOnly = true)
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    @Transactional
    public Optional<Player> updatePlayer(Long id, PlayerUpdateDTO dto) {
        return playerRepository.findById(id).map(existing -> {
            if (dto.getName() != null && !dto.getName().isBlank()) {
                existing.setName(dto.getName());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                existing.setEmail(dto.getEmail());
            }
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existing.setPassword(dto.getPassword());
            }
            if (dto.getPreferredPosition() != null) {
                try {
                    existing.setPreferredPosition(
                        User.PreferredPosition.valueOf(dto.getPreferredPosition()));
                } catch (IllegalArgumentException ex) {
                    throw new ApplicationException("PreferredPosition inválido. Valores válidos: PORTERO, DEFENSA, CENTROCAMPISTA, DELANTERO.");
                }
            }
            if (dto.getGoals() != null) {
                existing.setGoals(dto.getGoals());
            }
            if (dto.getCards() != null) {
                existing.setCards(dto.getCards());
            }
            return playerRepository.save(existing);
        });
    }

    @Transactional
    public boolean deletePlayer(Long id) {
        Optional<Player> opt = playerRepository.findById(id);
        if (opt.isEmpty()) return false;
        playerRepository.delete(opt.get());
        return true;
    }
    
    // -------------------------------------------------------
    // Estos métodos se añaden para que los tests de PlayerServiceTest compilen:
    @Transactional(readOnly = true)
    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Player> findPlayersByName(String name) {
        return playerRepository.findByNameContainingIgnoreCase(name);
    }
    // -------------------------------------------------------

    public List<Player> findByPosition(Player.PreferredPosition pos) {
        return playerRepository.findByPreferredPosition(pos);
    }

    public List<Player> findByMinGames(long minGames) {
        return playerRepository.findPlayersWithMinGames(minGames);
    }

    public List<Player> findByMinGoals(long minGoals) {
        return playerRepository.findPlayersWithMinGoals(minGoals);
    }

    public List<Player> findByMinRedCards(long minRedCards) {
        return playerRepository.findPlayersWithMinRedCards(minRedCards);
    }

    /**
     * Filtro avançado de jogadores: nome, posição, minGoals, minCards
     */
    @Transactional(readOnly = true)
    public List<Player> filterPlayers(String name, String preferredPosition, Integer minGoals, Integer minCards, Integer minGames) {
        List<Player> all = playerRepository.findAll();
        return all.stream()
            .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
            .filter(p -> preferredPosition == null || p.getPreferredPosition().name().equalsIgnoreCase(preferredPosition))
            .filter(p -> minGoals == null || p.getGoals() >= minGoals)
            .filter(p -> minCards == null || p.getCards() >= minCards)
            .filter(p -> minGames == null || Integer.valueOf(p.getGames()) >= minGames)
            .toList();
    }
}
