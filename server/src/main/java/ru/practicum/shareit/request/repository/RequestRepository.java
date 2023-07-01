package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequestingUserIdOrderByCreatedDesc(Integer id);

    List<Request> findByRequestingUserIdNotOrderByCreatedDesc(Integer id);
}
