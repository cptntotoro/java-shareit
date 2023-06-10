package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "requests")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "requesting_user_id")
    private User requestingUser;
}
