package com.swp.hr_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swp.hr_backend.entity.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note,Integer>{
	
	@Query(value = "SELECT * FROM note WHERE cv_id = ?1" , nativeQuery = true)
	public List<Note> findNotesByCvId(int cvId);
}
