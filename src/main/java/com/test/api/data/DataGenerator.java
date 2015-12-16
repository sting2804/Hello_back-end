package com.test.api.data;

import com.test.api.domain.Contact;
import com.test.api.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataGenerator {

    private final ContactRepository contactRepository;

    @Autowired
    public DataGenerator(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public void generateDataIntoDB() {
        for (Integer i = 0; i < 1000; i++) {
            contactRepository.save(new Contact("name" + i));
        }
    }
}
