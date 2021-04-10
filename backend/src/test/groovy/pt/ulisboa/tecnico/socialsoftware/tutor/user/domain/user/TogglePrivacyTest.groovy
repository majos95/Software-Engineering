package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class TogglePrivacyTest extends Specification {

    static final USERNAME = 'username'
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"


    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    def user


    def setup() {

        user = new User('name', USERNAME, 1, User.Role.STUDENT)

        userRepository.save(user)
    }


    def "student with private information toggles to public"() {
        given:  "a current privacy"
        def currentPrivacy = userService.getPrivacy(user.getId())
        and: "a studentId"
        def studentId = user.getId()

        when:
        userService.togglePrivacy(studentId)

        then:
        def alteredPrivacy = userService.getPrivacy(studentId)
        alteredPrivacy != currentPrivacy
    }


    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {
        @Bean
        UserService userService() {
            return new UserService()
        }
    }

}