package pt.ulisboa.tecnico.socialsoftware.tutor.doubt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DoubtRepositor extends JpaRepository<Doubt, Integer> {
    @Query( value = "SELECT * FROM doubts d WHERE d.user_id = :userid", nativeQuery = true)
    List<Doubt> findUserDoubts(Integer userid);

    @Query( value = "SELECT * FROM doubts d WHERE d.question_answer_id = :questionAnswerId", nativeQuery = true)
    List<Doubt> findQuestionAnswerDoubts(Integer questionAnswerId);

    @Query( value = "SELECT * FROM doubts", nativeQuery = true)
    List<Doubt> getDoubts();

}
