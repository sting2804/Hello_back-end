package com.test.api.service

import spock.lang.Specification

/**
 * Created by sting on 12/18/15.
 */
class ContactServiceTest extends Specification {
    def "get page count by count"(){
        given:
        int contactCount = 1002;
        int limit = 500;
        expect:
        ContactService.getPageCount(contactCount,limit) == 3
    }
}
