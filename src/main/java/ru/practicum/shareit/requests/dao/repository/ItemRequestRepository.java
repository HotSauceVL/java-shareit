package ru.practicum.shareit.requests.dao.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Optional<List<ItemRequest>> findAllByRequestor_Id(long requestorId);

    Optional<List<ItemRequest>> findAllByRequestor_IdIsNot(long requestorId, Pageable pageable);

}
