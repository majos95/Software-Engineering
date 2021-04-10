package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class ResubmitQuestionTest extends Specification {
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String NEW_QUESTION_TITLE = 'new question title'
    public static final String NEW_QUESTION_CONTENT = 'new question content'
    public static final String NEW_OPTION_CONTENT = "new optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    ImageRepository imageRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def question
    def optionOK
    def optionKO

    def setup() {
        given: "create a question"
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.REJECTED)
        question.setNumberOfCorrect(1)
        and: 'an image'
        def image = new Image()
        image.setUrl(URL)
        image.setWidth(20)
        imageRepository.save(image)
        question.setImage(image)
        and: 'two options'
        optionOK = new Option()
        optionOK.setContent(OPTION_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestion(question)
        optionRepository.save(optionOK)
        optionKO = new Option()
        optionKO.setContent(OPTION_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)
        questionRepository.save(question)
    }

    def "resubmit a question"(){
        given: "create a question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(NEW_QUESTION_TITLE)
        questionDto.setContent(NEW_QUESTION_CONTENT)
        and: 'a optionId'
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(NEW_OPTION_CONTENT)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        options.add(optionDto)

        questionDto.setOptions(options)

        when:
        questionService.resubmitQuestion(question.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == NEW_QUESTION_TITLE
        result.getContent() == NEW_QUESTION_CONTENT
        result.getStatus() == Question.Status.PENDING
        and: 'are not changed'
        result.getImage() != null
        and: 'an option is changed'
        result.getOptions().size() == 2
        def resOptionOne = result.getOptions().stream().filter({option -> option.getId() == optionOK.getId()}).findAny().orElse(null)
        resOptionOne.getContent() == NEW_OPTION_CONTENT
        def resOptionTwo = result.getOptions().stream().filter({option -> option.getId() == optionKO.getId()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_CONTENT
    }

    def "resubmitted question with no alterations"(){
        given: "create a question"
        def questionDto = new QuestionDto(question)
        and: 'a optionId'
        def optionDto = new OptionDto(optionOK)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.resubmitQuestion(question.getId(), questionDto)

        then:  "a TutorException is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_NOT_ALTERED
    }



    @Unroll("invalid arguments: #newTitle | #newContent | #optionContent | #correctOption || errorMessage ")
    def "invalid input values"() {
        given: "a questionDto"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(newTitle)
        questionDto.setContent(newContent)

        and: 'an option'
        def optionDto1 = new OptionDto(optionOK)
        optionDto1.setContent(optionContent)
        optionDto1.setCorrect(correctOption)
        def options1 = new ArrayList<OptionDto>()
        options1.add(optionDto1)
        questionDto.setOptions(options1)

        when:
        questionService.resubmitQuestion(question.getId(), questionDto)

        then: "a TutorException is thrown"
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        newTitle            | newContent            | optionContent      | correctOption  || errorMessage
        null                | NEW_QUESTION_CONTENT  | NEW_OPTION_CONTENT |      true      || INVALID_TITLE_FOR_QUESTION
        ""                  | NEW_QUESTION_CONTENT  | NEW_OPTION_CONTENT |      true      || INVALID_TITLE_FOR_QUESTION
        NEW_QUESTION_TITLE  |       null            | NEW_OPTION_CONTENT |      true      || INVALID_CONTENT_FOR_QUESTION
        NEW_QUESTION_TITLE  |        ""             | NEW_OPTION_CONTENT |      true      || INVALID_CONTENT_FOR_QUESTION
        NEW_QUESTION_TITLE  | NEW_QUESTION_CONTENT  |       ""           |      true      || INVALID_CONTENT_FOR_OPTION
        NEW_QUESTION_TITLE  | NEW_QUESTION_CONTENT  | NEW_OPTION_CONTENT |      false     || ONE_CORRECT_OPTION_NEEDED
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
