// src/main/java/pt/ul/fc/css/soccernow/service/CampeonatoService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Campeonato;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.CampeonatoDTO;
import pt.ul.fc.css.soccernow.repository.CampeonatoRepository;
import pt.ul.fc.css.soccernow.repository.TeamRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CampeonatoService {

    private final CampeonatoRepository campeonatoRepository;
    private final TeamRepository teamRepository;

    public CampeonatoService(CampeonatoRepository campeonatoRepository,
                             TeamRepository teamRepository) {
        this.campeonatoRepository = campeonatoRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Campeonato createCampeonato(CampeonatoDTO dto) {
        // 1) Validar nombre único (opcional)
        List<Campeonato> existentes = campeonatoRepository.findByNomeContainingIgnoreCase(dto.getNome());
        for (Campeonato c : existentes) {
            if (c.getNome().equalsIgnoreCase(dto.getNome())) {
                throw new ApplicationException("Ya existe un campeonato con ese nombre.");
            }
        }

        // 2) Buscar los equipos participantes por sus IDs
        Set<Team> participantes = new HashSet<>();
        for (Long teamId : dto.getParticipanteIds()) {
            Team t = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ApplicationException("No existe el equipo con id=" + teamId));
            participantes.add(t);
        }

        // 3) Construir la entidad y persistir
        Campeonato campeonato = new Campeonato();
        campeonato.setNome(dto.getNome());
        campeonato.setModalidade(dto.getModalidade());
        campeonato.setFormato(dto.getFormato());
        campeonato.setParticipantes(participantes);

        return campeonatoRepository.save(campeonato);
    }

    @Transactional(readOnly = true)
    public Optional<Campeonato> getCampeonatoById(Long id) {
        return campeonatoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Campeonato> findCampeonatosByNome(String nome) {
        return campeonatoRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Optional<Campeonato> updateCampeonato(Long id, CampeonatoDTO dto) {
        return campeonatoRepository.findById(id).map(existing -> {
            // 1) Actualizar nombre, modalidad o formato si vienen no nulos
            if (dto.getNome() != null && !dto.getNome().isBlank()) {
                existing.setNome(dto.getNome());
            }
            if (dto.getModalidade() != null && !dto.getModalidade().isBlank()) {
                existing.setModalidade(dto.getModalidade());
            }
            if (dto.getFormato() != null && !dto.getFormato().isBlank()) {
                existing.setFormato(dto.getFormato());
            }

            // 2) Actualizar lista de participantes (reemplazar) si viene no vacío
            if (dto.getParticipanteIds() != null && !dto.getParticipanteIds().isEmpty()) {
                Set<Team> nuevos = new HashSet<>();
                for (Long teamId : dto.getParticipanteIds()) {
                    Team t = teamRepository.findById(teamId)
                            .orElseThrow(() -> new ApplicationException("No existe el equipo con id=" + teamId));
                    nuevos.add(t);
                }
                existing.setParticipantes(nuevos);
            }

            // No tocamos “jogos”: esos se asocian al campeonato cuando creas/actualizas un Jogo.
            return campeonatoRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteCampeonato(Long id) {
        Optional<Campeonato> opt = campeonatoRepository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }
        Campeonato c = opt.get();
        // Regla de negocio: si ya hay partidos asociados, no borrar
        if (c.getJogos() != null && !c.getJogos().isEmpty()) {
            throw new ApplicationException("No se puede eliminar un campeonato con juegos asociados.");
        }
        campeonatoRepository.delete(c);
        return true;
    }

    public List<Campeonato> findByNome(String nome) {
        return campeonatoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Campeonato> findByMinGamesPlayed(long minPlayed) {
        return campeonatoRepository.findByMinGamesPlayed(minPlayed);
    }

    public List<Campeonato> findByMinGamesPending(long minPending) {
        return campeonatoRepository.findByMinGamesPending(minPending);
    }

    @Transactional(readOnly = true)
    public List<Campeonato> getAllCampeonatos() {
        return campeonatoRepository.findAll();
    }

}   
