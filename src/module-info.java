/**
 * Module {@code se1.play} demonstrates Java project setup, sourcing the project
 * and the project build process for the <i>Software Engineering-I</i> course.
 * 
 * The module's main {@link application} package has a simple {@link Runtime} that
 * scans for classes that are assignable from interfaces such as from the
 * {@link application.Runtime.Runnable} interface. It selects the class with the
 * highest {@link application.Runtime.Bean} priority, creates an instance of that
 * class and invokes the {@code run(properties, args)} - method to start the
 * application.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
module se1.play {
    opens application;     // open: package is accessible by JavaVM at runtime
    exports application;   // export: package is accessible to compile other modules

    requires org.junit.jupiter.api;     // JUnit-5 module for JUnit testing
    requires transitive org.slf4j;      // slf4j/log4j2 logging modules
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
}