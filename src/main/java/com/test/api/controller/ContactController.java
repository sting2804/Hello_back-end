package com.test.api.controller;

import com.test.api.service.ContactService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    public static Logger logger = Logger.getLogger(ContactController.class);

    @Autowired
    public ContactService contactService;

    /**
     * получение всех контактов с фильтром или без него
     *
     * @param nameFilter
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Map getContacts(@RequestParam(value = "nameFilter", required = false) String nameFilter, HttpServletResponse response) {
        return contactService.getContacts(nameFilter, response);
    }


    /**
     * поиск контакта по id
     *
     * @param id
     * @param response
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Map getContactById(@PathVariable Long id, HttpServletResponse response) {
        return contactService.getContactById(id, response);
    }


}
