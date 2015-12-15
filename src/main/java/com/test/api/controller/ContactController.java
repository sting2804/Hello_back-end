package com.test.api.controller;

import com.test.api.domain.Contact;
import com.test.api.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Contact> getContacts(@RequestParam(value = "nameFilter", required = false) String nameFilter){
        if(nameFilter == null) {
            List<Contact> contactList = (List<Contact>) contactRepository.findAll();
            return contactList;
        }
        return null;
    }


    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Contact getContactById(@PathVariable Long id){
        Contact contact = contactRepository.findOne(id);
        contact.add(linkTo(methodOn(ContactController.class).getContactById(id)).withSelfRel());
        return contact;
    }
}
