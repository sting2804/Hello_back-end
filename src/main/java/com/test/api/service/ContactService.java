package com.test.api.service;

import com.test.api.data.DataGenerator;
import com.test.api.domain.Contact;
import com.test.api.repository.ContactRepository;
import com.test.api.validator.ContactParamsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContactService {

    @Autowired
    protected ContactRepository contactRepository;
    @Autowired
    protected DataGenerator dataGenerator;
    @Autowired
    protected ContactParamsValidator validator;

    @Transactional
    public Map getContacts(String nameFilter, HttpServletResponse response) {
        if (contactRepository.count() == 0) {
            dataGenerator.generateDataIntoDB();
        }
        Map<String, Object> answer = new HashMap<>();
        if (nameFilter != null) {
            String validatingError = validator.validateRegex(nameFilter);
            if (validatingError != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                answer = getErrorMapWithMessage(validatingError);
                return answer;
            }
        }

        final int limit = 500;
        List<Contact> contactList;
        final long contactCount = contactRepository.count();
        final long contactPageCount = getPageCount(contactCount, limit);
        for (int offset = 0; offset < contactPageCount; offset++) {
            contactList = new ArrayList<>();
            Page<Contact> contactPage = contactRepository.findAll(createPageRequest(offset, limit));
            contactList.addAll(contactPage.getContent());
            if (contactList.size() == 0 && offset == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                answer = getErrorMapWithMessage("Contacts are not found.");
                break;
            } else if (contactList.size() == 0) {
                break;
            } else {
                if (nameFilter == null) {
                    if (answer.containsKey("contacts")) {
                        List<Contact> contacts = (List<Contact>) answer.get("contacts");
                        contacts.addAll(contactList);
                    } else
                        answer.put("contacts", contactList);
                } else {
                    String validatingError = validator.validateRegex(nameFilter);
                    if (validatingError != null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        answer = getErrorMapWithMessage(validatingError);
                        break;
                    } else {
                        List<Contact> filteredList = findAllContactsByRegex(contactList, nameFilter);
                        if (filteredList.size() == 0 && offset == 0) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        } else
                            response.setStatus(HttpServletResponse.SC_OK);
                        answer.put("contacts", filteredList);
                    }
                }
            }
        }
        return answer;
    }

    /**
     * определение количества страниц с учётом размера пачки (limit)
     *
     * @param contactCount
     * @param limit
     * @return
     */
    protected static long getPageCount(long contactCount, int limit) {
        long pageCount = contactCount / limit;
        long remainder = contactCount % limit;
        do {
            pageCount++;
            remainder = Math.abs(remainder - limit);
        } while (remainder > limit);

        return pageCount;
    }

    private Pageable createPageRequest(int offset, int limit) {
        return new PageRequest(offset, limit);
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
                if (checkWithRegExp(contact.getName(), nameFilter))
                    filteredContactList.add(contact);
            }
            contactList.removeAll(filteredContactList);
        }
        return contactList;
    }

    public static boolean checkWithRegExp(String regExp, String userNameString) {
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(userNameString);
        return m.matches();
    }

    /**
     * поиск контакта по id
     *
     * @param id
     * @param response
     * @return
     */
    public Map getContactById(Long id, HttpServletResponse response) {
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
