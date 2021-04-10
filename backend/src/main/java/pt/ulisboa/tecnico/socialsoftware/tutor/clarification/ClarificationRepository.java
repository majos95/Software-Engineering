package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt;

import java.util.List;

@Repository
@Transactional
public interface ClarificationRepository extends JpaRepository<Clarification, Integer> {

    @Query( value = "SELECT * FROM clarifications c WHERE c.doubt_id = :doubtId", nativeQuery = true)
    List<Clarification> findByDoubtID(Integer doubtId);
}