package application;

import application.Runtime.Bean;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class that implements the {@link Runtime.Runnable} interface that
 * is called by the {@link Runtime}.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
@Bean(priority=1)
public class Application implements Runtime.Runnable {

    /*
     * Logger instance for this class.
     */
    final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * None-public default constructor (avoid javadoc warning).
     */
    public Application() { }


    /**
     * Method of the {@link Runtime.Runnable} interface called on an instance
     * created by the {@link Runtime}. Program execution starts here.
     * @param properties properties from the {@code application.properties} file
     * @param args arguments passed from the command line
     */
    @Override
    public void run(Properties properties, String[] args) {
        /*
         * 1.) print application name fetched from 'application.name' property
         *     from the 'resources/application.properties' file:
         * //
         * Hello, "SE-1 Play" (application.Application)
         */
        String key = "application.name";
        String name = (String)properties.get(key);
        if(name==null) {
            name = "(undefined)";
            log.warn(String.format("property: \"%s\" not found, used \"%s\" instead", key, name));
        }
        System.out.println(String.format("Hello, \"%s\" (%s)", name, this.getClass().getName()));

        /*
         * 2.) print args[] from command line when called with:
         *     java application.Application AB BC CD
         * //
         * Hello, "SE-1 Play" (application.Application)
         *   - arg: AB
         *   - arg: BC
         *   - arg: CD
         */
        for(String arg : args) {
            String fmt = String.format("- arg: %s", arg);
            System.out.println(fmt);    // print formatted line
        }
    }

    /**
     * JavaVM entry method that calls the {@link Runtime}, which creates
     * an instance of this class that implements the {@link Runnable}
     * interface and invokes its {@code run(properties, args)} - method.
     * @param args arguments passed from command line
     */
    public static void main(String[] args) {
        Runtime.run(args);
    }
}