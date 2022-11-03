package com.swp.hr_backend.repository;

import com.swp.hr_backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    public Room findByRoomName(String roomName);
}
