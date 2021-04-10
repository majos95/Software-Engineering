package pt.ulisboa.tecnico.socialsoftware.tutor.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
//trying to merge

@Repository
@Transactional
public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {
    @Query(value = "SELECT * FROM evaluations e WHERE e.key = :key", nativeQuery = true)
    Optional<Evaluation> findByKey(Integer key);
}
