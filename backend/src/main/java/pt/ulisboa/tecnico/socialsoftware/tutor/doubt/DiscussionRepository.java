package pt.ulisboa.tecnico.socialsoftware.tutor.doubt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    @Query( value = "SELECT * FROM discussions d WHERE d.user_id = :userid", nativeQuery = true)
    List<Discussion> findUserDiscussions(Integer userid);

    @Query( value = "SELECT * FROM discussions", nativeQuery = true)
    List<Discussion> getDiscussions();

    @Query( value = "SELECT * FROM discussions d WHERE d.question_answer_id = :questionAnswerId", nativeQuery = true)
    List<Discussion>findQuestionAnswerDiscussions(Integer questionAnswerId);
}
