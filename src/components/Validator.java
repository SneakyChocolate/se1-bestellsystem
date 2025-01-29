package components;

import datamodel.Customer;
import datamodel.NameParts;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Validator {
    Optional<LocalDateTime> validateOrderCreationDate(LocalDateTime date);

    Optional<String> validateName(String name, boolean acceptEmptyName);

    Optional<NameParts> validateSplitName(String name);

    public Optional<String> validateContact(String contact);
}

