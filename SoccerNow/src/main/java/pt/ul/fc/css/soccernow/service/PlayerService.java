// src/main/java/pt/ul/fc/css/soccernow/service/PlayerService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player createPlayer(PlayerDTO dto) {
        // 1) Validar que no exista otro jugador con el mismo email
        if (playerRepository.existsByEmail(dto.getEmail())) {
            throw new ApplicationException("Ya existe un jugador con ese email.");
        }

        // 2) Validar preferredPosition no nulo ni vacío
        if (dto.getPreferredPosition() == null || dto.getPreferredPosition().isBlank()) {
            throw new ApplicationException("El campo 'preferredPosition' es obligatorio para un PLAYER.");
        }

        // 3) Convertir el String a enum PreferredPosition
        User.PreferredPosition enumPos;
        try {
            enumPos = User.PreferredPosition.valueOf(dto.getPreferredPosition());
        } catch (IllegalArgumentException ex) {
            throw new ApplicationException("PreferredPosition inválido. Valores válidos: PORTERO, DEFENSA, CENTROCAMPISTA, DELANTERO.");
        }

        // 4) Construir la entidad Player
        Player jugador = new Player();
        jugador.setName(dto.getName());
        jugador.setEmail(dto.getEmail());
        jugador.setPassword(dto.getPassword());
        // En User el campo "role" también se guarda, pero PlayerService asume role = "PLAYER"
        jugador.setRole("PLAYER");
        jugador.setPreferredPosition(enumPos);

        // 5) Guardar en BD
        return playerRepository.save(jugador);
    }

    @Transactional(readOnly = true)
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    @Transactional
    public Optional<Player> updatePlayer(Long id, PlayerDTO dto) {
        return playerRepository.findById(id).map(existing -> {
            // 1) Campos básicos “fusión”
            if (dto.getName() != null && !dto.getName().isBlank()) {
                existing.setName(dto.getName());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                existing.setEmail(dto.getEmail());
            }
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existing.setPassword(dto.getPassword());
            }

            // 2) preferredPosition: si viene, convertir a enum y asignar
            if (dto.getPreferredPosition() != null) {
                User.PreferredPosition enumPos;
                try {
                    enumPos = User.PreferredPosition.valueOf(dto.getPreferredPosition());
                } catch (IllegalArgumentException ex) {
                    throw new ApplicationException("PreferredPosition inválido. Valores válidos: PORTERO, DEFENSA, CENTROCAMPISTA, DELANTERO.");
                }
                existing.setPreferredPosition(enumPos);
            }

            // 3) Guardar cambios
            return playerRepository.save(existing);
        });
    }

    @Transactional
    public boolean deletePlayer(Long id) {
        Optional<Player> opt = playerRepository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }
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
}
