package com.test.api.controller;

import com.test.api.domain.Contact;
import com.test.api.repository.ContactRepository;
import com.test.api.validator.ContactParamsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactRepository contactRepository;
    @Autowired
    public ContactParamsValidator validator;

    @Autowired
    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Map getContacts(@RequestParam(value = "nameFilter", required = false) String nameFilter, HttpServletResponse response) {
        Map answer = new HashMap<>();
        List<Contact> contactList = (List<Contact>) contactRepository.findAll();
        if (contactList == null || contactList.size() == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            if (nameFilter == null) {
                answer.put("contacts", contactList);
            } else {
                String validatingError = validator.validateRegex(nameFilter);
                if (validatingError != null)
                    try {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, validatingError);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                List<Contact> filteredList = findAllContactsByRegex(contactList, nameFilter);
                if (filteredList.size() == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else
                    response.setStatus(HttpServletResponse.SC_OK);
                answer.put("contacts", filteredList);
            }
            return answer;
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return null;
    }

    /**
     * поиск всех контактов, не соответствующих фильтру поиска
     *
     * @param contactList
     * @param nameFilter
     * @return
     */
    protected List<Contact> findAllContactsByRegex(List<Contact> contactList, String nameFilter) {
        List<Contact> filteredContactList = new ArrayList<>();
        synchronized (this) {
            for (Contact contact : contactList) {
                if (contact.getName().matches(nameFilter))
                    filteredContactList.add(contact);
            }
            contactList.removeAll(filteredContactList);
        }
        return contactList;
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
        Contact contact = contactRepository.findOne(id);
        Map result = null;
        if (contact == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result = getErrorMapWithMessage("Contact is not found.");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            result = BeanMap.create(contact);
        }
        return result;
    }

    public Map getErrorMapWithMessage(String errorMessage) {
        Map errorMap = new HashMap<>();
        errorMap.put("error", errorMessage);
        return errorMap;
    }

}
