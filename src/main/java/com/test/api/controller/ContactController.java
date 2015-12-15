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

    //@RequestMapping(method = RequestMethod.GET)
    public Resources<Contact> getContacts(@RequestParam(value = "nameFilter", required = false) String nameFilter){
        if(nameFilter == null) {
            List<Contact> contactList = ((List<Contact>) contactRepository.findAll())
                    .stream()
                    .map(contact -> {
                        contact.add(linkTo(methodOn(ContactController.class).getContactById(contact.getObjectId())).withSelfRel());
                        return contact;
                    })
                    .collect(Collectors.toList());
            Link categoryLink = linkTo(ContactController.class).withSelfRel();
            return new Resources<>(contactList, categoryLink);
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Contact> getContactsList(@RequestParam(value = "nameFilter", required = false) String nameFilter){
        if(nameFilter == null) {
            return (Collection<Contact>) contactRepository.findAll();
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
