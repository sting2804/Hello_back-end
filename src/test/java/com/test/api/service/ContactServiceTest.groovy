package com.test.api.service

import com.test.api.domain.Contact
import spock.lang.Specification

/**
 * Created by sting on 12/18/15.
 */
class ContactServiceTest extends Specification {
    static List<Contact> contactList = new ArrayList<>()

    def "FindAllContactsByRegex"() {
        setup:
        ContactService controller = Spy(ContactService)
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

    def "get page count by count"() {
        given:
        int contactCount = 1002;
        int limit = 500;
        expect:
        ContactService.getPageCount(contactCount, limit) == 3
    }
}
