package com.test.api.controller

import com.test.api.domain.Contact
import spock.lang.Specification

class ContactControllerTest extends Specification {

    static List<Contact> contactList = new ArrayList<>()

    def "FindAllContactsByRegex"() {
        setup:
        ContactController controller = Spy(ContactController)
        for (int i = 0; i < 10; i++) {
            contactList.add(new Contact("name$i"))
        }

        when:
        def res = controller.findAllContactsByRegex(contactList, regex)

        then:
        res == expectedResult

        where:
        regex << [
                /.*[n].*/,
                /.*[v].*/,
        ]
        expectedResult << [
                [], contactList
        ]
    }
}
