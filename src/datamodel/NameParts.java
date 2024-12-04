package datamodel;

import java.util.Optional;

/**
 * NameParts
 */
public record NameParts(String first, String last) {
	public static Optional<NameParts> validateSplitName(String name) {
		String firstName = "";
		String lastName = "";
		name = name.replaceAll("\"", "");
		name = name.replaceAll("\t", "");
		
		if (name.contains(", ")) {
			var split = name.split(", ");
			firstName = split[1];
			lastName = split[0];
			return Optional.of(new NameParts(firstName, lastName));
		}
		else if (name.contains("; ")) {
			var split = name.split("; ");
			firstName = split[1];
			lastName = split[0];
			return Optional.of(new NameParts(firstName, lastName));
		}
		
		if (name == null || name.isBlank()) {
			return Optional.empty();
		}
		int index = name.lastIndexOf(" ");
		if (index > 0) {
			firstName = name.substring(0, index);
			lastName = name.substring(index).trim();
		}
		else {
			lastName = name;
		}
		return Optional.of(new NameParts(firstName, lastName));
	}
}
