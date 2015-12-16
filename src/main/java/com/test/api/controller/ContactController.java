package com.test.api.controller;

import com.test.api.data.DataGenerator;
import com.test.api.domain.Contact;
import com.test.api.repository.ContactRepository;
import com.test.api.validator.ContactParamsValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    public static Logger logger = Logger.getLogger(ContactController.class);
    private final ContactRepository contactRepository;
    @Autowired
    public ContactParamsValidator validator;
    @Autowired
    public DataGenerator dataGenerator;

    @Autowired
    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * получение всех контактов с фильтром или без него
     *
     * @param nameFilter
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Map getContacts(@RequestParam(value = "nameFilter", required = false) String nameFilter, HttpServletResponse response) {
        if (contactRepository.count() == 0) {
            dataGenerator.generateDataIntoDB();
        }
        Map<String, Object> answer = new HashMap<>();
        List<Contact> contactList = (List<Contact>) contactRepository.findAll();
        if (contactList == null || contactList.size() == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            answer = getErrorMapWithMessage("Contacts are not found.");
        } else {
            if (nameFilter == null) {
                answer.put("contacts", contactList);
            } else {
                String validatingError = validator.validateRegex(nameFilter);
                if (validatingError != null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    answer = getErrorMapWithMessage(validatingError);
                } else {
                    List<Contact> filteredList = findAllContactsByRegex(contactList, nameFilter);
                    if (filteredList.size() == 0) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    } else
                        response.setStatus(HttpServletResponse.SC_OK);
                    answer.put("contacts", filteredList);
                }
            }
        }
        return answer;
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
            result = getErrorMapWithMessage("Contact with id=" + id + " is not found.");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            result = BeanMap.create(contact);
        }
        return result;
    }

    /**
     * обёртывание сообщения об ошибке в словарь с ключём error и сообщением в качестве значения
     *
     * @param errorMessage
     * @return
     */
    public Map<String, Object> getErrorMapWithMessage(String errorMessage) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("error", errorMessage);
        return errorMap;
    }

}
