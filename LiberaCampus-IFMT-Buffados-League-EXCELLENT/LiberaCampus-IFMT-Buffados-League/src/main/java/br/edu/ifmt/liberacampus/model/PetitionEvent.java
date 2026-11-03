package br.edu.ifmt.liberacampus.model;

import br.edu.ifmt.liberacampus.util.CampusTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "peticao_eventos")
public class PetitionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "peticao_id", nullable = false)
    private Petition petition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ator_id")
    private UserAccount actor;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1800)
    private String description;

    @Column(nullable = false, length = 140)
    private String actorName;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    protected PetitionEvent() {
    }

    public PetitionEvent(Petition petition, String title, String description, UserAccount actor) {
        this.petition = petition;
        this.actor = actor;
        this.title = title;
        this.description = description;
        this.actorName = actor == null ? "Sistema" : actor.getFullName();
        this.occurredAt = CampusTime.now();
    }

    public Long getId() {
        return id;
    }
    public UserAccount getActor() {
        return actor;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getActorName() {
        return actorName;
    }
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
