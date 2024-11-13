package application;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.lang.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link Runtime} initializes the Java-environment before the application
 * runs. This includes tasks of:
 * <ol type="1">
 *  <li>Load the application configuration file: {@code application.properties}
 *      from a {@code CLASSPATH} folder in the filesystem or from a
 *      {@code .jar} file.</li>
 *  <li>Load the logger configuration file: {@code log4j2.properties} from a
 *      {@code CLASSPATH} folder in the filesystem or from a {@code .jar} file
 *      and initialize the logging system.</li>
 *  <li>Scan classes of the application to create <i>bean</i> objects using
 *      the {@code getBean()} method. <i>Beans</i> are objects that are created
 *      and managed by {@link Runtime}.</li>
 * </ol>
 * An example is a <i>bean</i> that implements the {@link Runnable} interface.
 * An implementation class is selected during class-scan and instantiated.
 * {@link Runtime} then invokes {@code run(Properties properties, String[] args)}
 * passing {@code properties} and {@code args} as entry point to the application.
 * <p>
 * If multiple implementation classes exist matching a requested interface,
 * {@link Runtime} applies a selection policy:
 * <ol type="1">
 *  <li>Select by the highest {@link Bean} priority annotation provided with
 *      an implementation class.</li>
 *  <li>Select the deepest class in an inheritance path.</li>
 * </ol>
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public class Runtime {

    /**
     * Interface an implementation class must implement for creating
     * the {@link Runnable} bean as entry point for the application.
     */
    public static interface Runnable {
        /**
         * Method invoked by the {@link Runtime} system on the
         * created {@link Runnable} bean.
         * @param properties properties extracted from the
         *          {@code application.properties} file
         * @param args arguments passed from command line
         */
        void run(Properties properties, String[] args);
    }

    /**
     * The {@code @Bean(priority=value)} annotation prioritizes
     * the selection of <i>Bean</i> implementation classes.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Bean {
        /**
         * Return specified priority value.
         * @return priority value of the annotation
         */
        public int priority() default 0;
    }

    /*
     * Private reference to the {@code Runtime} singleton object.
     */
    private static Runtime singleton = null;

    /*
     * {@code Runtime} lifecycle states.
     */
    private enum State {notStarted, starting, started, shuttingDown, shutDown};
    private State state = State.notStarted;

    /*
     * Logger instance used by {@code Runtime}, see configuration in:
     * {@code log4j2.properties}.
     */
    private final Logger log = LoggerFactory.getLogger(Runtime.class.getSimpleName());

    /*
     * Properties obtained from the {@code application.properties} file.
     */
    private final Properties properties = new Properties();

    /*
     * Classes found during class scan (only classes of the application are included).
     */
    private final List<Class<?>> scannedClasses = new ArrayList<>();

    /*
     * List of classes that can be assigned from the class used as key.
     */
    private final Map<Class<?>, List<Class<?>>> assignables = new HashMap<>();

    /**
     * <i>Bean</i> objects created from scanned classes through {@link getBean()}.
     */
    private final Map<Class<?>, Object> beans = new HashMap<>();

    /**
     * Lowest bean priority, inheritance depth increases priority with negative values,
     * {@code @Bean} annotation priorities overrule with positive values.
     */
    private final int lowBound = -100;


    /**
     * Private constructor according to the (lazy) singleton pattern.
     * The {@link Runtime} class itself is instantiated as a singleton
     * <i>bean</i> object following the <a href=
     * "https://www.digitalocean.com/community/tutorials/java-singleton-design-pattern-best-practices-examples#3-lazy-initialization">
     * (lazy) singleton pattern</a>.
     */
    private Runtime() { }

    /**
     * Public getter for the {@link Runtime} singleton instance
     * (part of the singleton pattern).
     * @return Runtime singleton instance ({@link Runtime} may not be started)
     */
    public static Runtime getInstance() {
        if(singleton==null) {
            singleton = new Runtime();
        }
        return singleton;
    }

    /**
     * Main entry point for the Java VM, launches the {@link Runnable} <i>Bean</i>,
     * which is an object of a class that implements the {@link Runnable} interface.
     * @param args arguments passed from the command line
     */
    public static void main(String[] args) {
        run(args);  // run() launches Runnable <i>Bean</i>
    }

    /**
     * Return <i>Bean</i> object as instance of a class found during class scan
     * that implements {@code <T>}.
     * @param <T> generic type of returned <i>Bean</i> object
     * @param clazz interface or class the <i>Bean</i> implements
     * @return <i>Bean</i> object that implements the requested interface or class
     */
    public static <T> Optional<T> getBean(Class<T> clazz) {
        var runtime = getInstance();
        if(runtime.state==State.notStarted) {
            runtime.start(null);
        }
        return runtime.getBean(clazz, null);
    }

    /**
     * Start {@link Runtime} and launch the {@link Runnable} <i>Bean</i>.
     * @param args arguments passed from the command line
     * @return chainable self-reference
     */
    public static Runtime run(String[] args) {
        var rt = getInstance().start(args);
        var rbean = rt.getBean(Runtime.Runnable.class, args);
        if(rbean.isPresent()) {
            var runnable = rbean.get();
            // call run(properties, args) method at Runnable bean
            runnable.run(rt.properties, args);
        } else {
            rt.log.warn(String.format("no runnable instance found"));
        }
        return rt.shutdown();
    }

    /**
     * Initialize and start {@link Runtime}.
     * @param args arguments passed from the command line
     * @return chainable self-reference
     */
    private Runtime start(String[] args) {
        if(state != State.notStarted) {
            return this;
        }
        args = args != null? args : new String[] { };
        state = State.starting;
        log.info(String.format("------------ starting: %s", this.getClass().getName()));
        // String classpath = System.getProperty("java.class.path");
        String classpath = System.getenv("CLASSPATH");
        String[] classpathEntries = classpath.split(System.getProperty("path.separator"));
        boolean resourcesFromJar = classpathEntries.length==1;

        String appPropertiesFile = "application.properties";
        // var paths = List.of(fileName, "resources/" + fileName, "config/" + fileName);
        List<String> paths = List.of("", "resources/", "config/").stream()
            .map(p -> p + appPropertiesFile).toList();
        // load properties file from filesystem (priority) or from class loader
        loadProperties(properties, paths, resourcesFromJar? classpathEntries[0] : "");

        String loggerPropertiesFile = "log4j2.properties";
        String from = "from";
        if(resourcesFromJar) {
            boolean loggerInit = false;
            for(String fn : List.of("resources/").stream().map(p -> p + loggerPropertiesFile).toList()) {
                LoggerContext ctx = Configurator.initialize(null, /* "classpath:" + */ fn);
                loggerInit = ctx.getConfiguration().getAppender("console.appender") != null;
                if(loggerInit) {
                    from = "from jar";
                    break;
                }
            }
        }
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        if(ctx.getConfiguration().getAppender("console.appender") != null) {
            log.info(String.format("loaded logger configuration %s: \"%s%s\"", from, "resources/", loggerPropertiesFile));
        }
        // loading 'application.properties' and 'log4j2.properties' complete
        List<String> resources = findResources(classpathEntries);
        buildAssignableClasses(resources);
        resources.clear();
        // 
        state = State.started;
        log.info(String.format("%s.%s", this.getClass().getSimpleName(), state));
        return this;
    }

    /**
     * Shutdown {@link Runtime}.
     * @return chainable self-reference
     */
    private Runtime shutdown() {
        if(state==State.starting || state==State.started) {
            state = State.shuttingDown;
            log.info(String.format("%s.%s", this.getClass().getSimpleName(), state));
            properties.clear();
            scannedClasses.clear();
            assignables.clear();
            beans.clear();
            state = State.shutDown;
            log.info(String.format("%s.%s ------------", this.getClass().getSimpleName(), state));
        }
        return this;
    }

    /**
     * Load {@code application.properties} file from a {@code CLASSPATH}
     * folder in the filesystem or from a {@code .jar} file.
     * @param properties collect properties
     * @param paths {@code CLASSPATH} folders
     * @param jar name of a {@code .jar} file
     * @return number of properties found
     */
    private int loadProperties(Properties properties, List<String> paths, String jar) {
        int before = properties.size();
        // attempt to read from filesystem paths
        for(String fn : paths) {
            try (FileInputStream fis = new FileInputStream(new File(fn))) {
                properties.load(fis);
                int loaded = properties.size() - before;
                log.info(String.format("loaded %d properties from: \"%s\"", loaded, fn));
                return loaded;
            } catch (IOException e) { }
        }
        // if no properties file was found in the filesystem, attempt to read from class loader (CLASSPATH)
        for(String fn : paths) {
            try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fn)) {
                if(is==null)
                    throw new IOException("no InputStream from SystemClassLoader");
                //
                properties.load(is);
                int loaded = properties.size() - before;
                jar = jar.length() > 0? String.format("%s: ", jar) : "";
                log.info(String.format("loaded %d properties from class loader resource \"%s%s\"", loaded, jar, fn));
                return loaded;
            } catch(IOException e) { }
        }
        log.warn("no \"application.properties\" file, no properties loaded");
        return -1;
    }

    /**
     * <i>Resources</i> are names of loadable assets found on the {@code CLASSPATH}
     * or in a {@code .jar} file. Method returns names of <i>resources</i> found,
     * e.g. {@code .class} files from a {@code bin} directory.
     * @param classpathEntries entries from {@code CLASSPATH} split
     *          by {@code "path.separator"}
     * @return list of <i>resource</i> names found at {@code classpathEntries}
     */
    private List<String> findResources(String[] classpathEntries) {
        List<String> resources = new ArrayList<>();
        if(classpathEntries.length==1) {   // retrieve resources from jar
            String jar = classpathEntries[0];
            try (JarFile jarFile = new JarFile(new File(jar))) {
                Collections.list(jarFile.entries()).stream()
                    .map(e -> e.getName())
                    .filter(r -> r.endsWith(".class") || r.endsWith(".properties"))
                    .map(r -> r.endsWith(".class")? r.replace("\\", "/").replace("/", ".") : r)
                    .forEach(r -> resources.add(r));
            //
            } catch(IOException ex) {
                log.error("jar", ex);
            }
            log.info(String.format("found %d resources in: \"%s\"", resources.size(), jar));
        // 
        } else {
            for(String loc : classpathEntries) {
                if( ! loc.endsWith(".jar")) {
                    findResourcesFromFilesystem(resources, loc, loc);
                }
            }
            log.info(String.format("found %d resources in filesystem during class scan", classpathEntries.length));
        }
        return resources;
    }

    /**
     * Recursively traverse filesystem and collect names of {@code .class}
     * files as <i>"resources"</i>.
     * @param collect container where <i>resource</i> names are collected
     * @param prefix part removed from {@code path} to obtain Java package names
     * @param path starting path for traversal
     */
    private void findResourcesFromFilesystem(List<String> collect, String prefix, String path) {
        try (DirectoryStream<Path> dis = Files.newDirectoryStream(Paths.get(path))) {
            // replace '\' or '/' with '.' as used for package paths
            String pregex = prefix.replace("/", ".").replace("\\", ".") + ".";
            for(Path p : dis) {
                if (Files.isDirectory(p)) {
                    findResourcesFromFilesystem(collect, prefix, p.toString());
                } else {
                    String r = p.toString().replace("\\", "/");
                    if(r.endsWith(".class")) {
                        // replace '/' with '.' and remove prefix-path from resource
                        r = r.replace("/", ".").replaceFirst(Pattern.quote(pregex), "");
                    }
                    collect.add(r);
                }
            }
        } catch(IOException ex) { }
    }

    /**
     * <i>Assignable</i> classes can be assigned from a <i>asignee</i> class or
     * interface, which means they implement an interface or are derived from
     * a base class.
     * <p>
     * Method builds structures maintained in {@link Runtime}: {@code scannedClasses}
     * and {@code assignables} from {@code resources} found during class scan.
     * @param resources names of resources found during class scan
     */
    private void buildAssignableClasses(List<String> resources) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        resources.stream()
            // only '.class' excluding 'module-info.class' and 'package_info.class'
            // must exclude 'org' resources included by VSCode built-in test runner
            .filter(r -> r.endsWith(".class") && ! r.contains("-info") && ! r.contains("_info") && ! r.startsWith("org"))
            // exclude test classes ending with 'Test' or 'Tests'
            .filter(r -> ! r.matches(".*Test[s]?(|\\$.*).class$"))
            // remove trailing '.class'
            .map(r -> r.substring(0, r.length() - ".class".length()))
            // load class from resource name and pass class downstream
            .map(res -> {
                Class<?> cls = null;
                try {
                    cls = classLoader.loadClass(res);
                } catch (ClassNotFoundException e) {
                    log.warn(String.format("%s: attempting to load \"%s\"", e.getClass().getSimpleName(), res));
                }
                return Optional.ofNullable(cls);
            })
            .filter(opt -> opt.isPresent())
            .map(opt -> opt.get())
            //
            // find and collect assignable classes
            .forEach(cls -> {
                scannedClasses.clear();
                collectAssignables(scannedClasses, cls, 0).stream()
                    .forEach(assignee -> {
                        var assignableClasses = Optional.ofNullable(assignables.get(assignee))
                            .orElseGet(() -> {
                                var l2=new ArrayList<Class<?>>();
                                assignables.put(assignee, l2);
                                return l2;
                        });
                        if( ! assignableClasses.contains(cls) && cls.toString().startsWith("class")) {
                            assignableClasses.add(cls);    // for assignee: add cls to list of assignables
                        }
                    });
            });
        // 
        long count = assignables.keySet().size();
        if(count > 0) {
            log.info(String.format("found %d assignable interfaces or classes:", count));
            assignables.forEach((assignee, assignableClasses) -> {
                log.info(String.format("-- \"%s\" with assignable classes: \"%s\"", assignee, assignableClasses));
            });
        } else {
            log.warn("no assignable classes found");
        }
    }

    /**
     * Return interfaces and super-classes of {@code clazz} from application
     * packages, excluding interfaces and super-classes from libraries (e.g.
     * "java.util.*", "org.").
     * @param collector container to collect results
     * @param clazz starting class for traversal
     * @param depth distance of a super-class from {@ code clazz}
     * @return interfaces and super-classes to which {@code clazz} is assignable
     */
    private List<Class<?>> collectAssignables(List<Class<?>> collector, Class<?> clazz, int depth) {
        for(var intfc : clazz.getInterfaces()) {
            if(collect(collector, intfc)) {
                collectAssignables(collector, intfc, depth + 1);
            }
        }
        Class<?> superclazz = clazz.getSuperclass();
        if(superclazz != null && superclazz != java.lang.Object.class) {
            if(collect(collector, superclazz)) {
                collectAssignables(collector, superclazz, depth + 1);
            }
        }
        return collector;
    }

    /**
     * Probe and collect {@code clazz} as assignable class.
     * @param collector container to collect {@code clazz}
     * @param clazz class to probe and collect
     * @return interfaces and super-classes to which {@code clazz} is assignable
     */
    private boolean collect(List<Class<?>> collector, Class<?> clazz) {
        if(clazz.isAssignableFrom(clazz)) {
            String n = clazz.getName();
            if( ! (n.startsWith("java") || n.startsWith("org"))) {
                collector.add(clazz);
                return true;
            }
        }
        return false;
    }

    /**
     * Create and return <i>bean</i> for {@code clazz}. <i>Beans</i> are created
     * as singleton objects for the first (highest priority) assignable class
     * found in the {@code assignables} map.
     * <p>
     * When a <i>bean</i> is created, it is cached in the {@code beans} map.
     * @param <T> generic bean type
     * @param clazz class for which bean is requested
     * @param args command line arguments passed to an {@code args[]} constructor
     * @return requested bean or empty if bean could not be created
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<T> getBean(Class<T> clazz, String[] args) {
        if(clazz==null)
            throw new IllegalArgumentException("getBean(clazz) failed, clazz is null");
        //
        T bean = (T)beans.get(clazz);
        if(bean==null) {
            Optional<T> b = createBean(clazz, args);
            if(b.isPresent()) {
                bean = b.get();
                beans.put(clazz, bean);
                log.info(String.format("bean object \"%s\" created for: \"%s\"", bean.getClass(), clazz));
            } else {
                log.warn(String.format("no bean object created for: \"%s\"", clazz));
            }
        }
        if(bean==null) {
            log.warn(String.format("no bean object for: \"%s\"", clazz));
        }
        return Optional.ofNullable(bean);
    }

    /**
     * Create <i>bean</i> object for the first (highest priority) assignable class
     * found in the {@code assignables} map for {@code clazz}.
     * <p>
     * <i>Bean</i> object is created by invoking a series of constructors for the
     * highest priority assignable class.
     * @param <T> generic bean type
     * @param clazz class for which bean is requested
     * @param args command line arguments passed to an {@code args[]} constructor
     * @return requested bean or empty if bean could not be created
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<T> createBean(Class<T> clazz, String[] args) {
        var assignableClasses = Optional.ofNullable(assignables.get(clazz)).orElse(List.of());
        var prioritized = new LinkedList<Class<?>>();
        int max = lowBound;
        //
        // prioritize assignable classes for clazz
        for(var cl2 : assignableClasses) {
            // 
            // use {@code @Bean(priority=p)} to prioritize assignable classes
            int p = beanAnnotationPriority(cl2);
            if(p < 0) {
                // 
                // no annotation: use deepest inherited class (p < 0, starting from lowBound)
                p = inheritanceDepth(clazz, cl2, lowBound);
            }
            if(p > max) {
                max = p;
                prioritized.add(0, cl2);    // higher-priority assignable class to front
            } else {
                prioritized.add(cl2);       // append lower-priority class at the end
            }
        }
        // iterate through prioritized assignable classes invoking constructors,
        // the first successful constructor creates the bean
        // 
        var bean = prioritized.stream()
            .map(cls -> create(cls, () -> {
                    var ctor = cls.getConstructor(String[].class);
                    return (T)ctor.newInstance(new Object[] {args});
                })
                .or(() -> create(cls, () -> {
                    var ctor = cls.getConstructor(Properties.class);
                    return (T)ctor.newInstance(properties);
                }))
                .or(() -> create(cls, () -> {
                    var ctor = cls.getConstructor(Properties.class, String[].class);
                    return (T)ctor.newInstance(properties, args);
                }))
                .or(() -> create(cls, () -> {
                    var ctor = cls.getConstructor(String[].class, Properties.class);
                    return (T)ctor.newInstance(args, properties);
                }))
                .or(() -> create(cls, () -> {
                    var ctor = cls.getConstructor();
                    ctor.setAccessible(true);
                    return (T)ctor.newInstance();
                }))
                .orElse(null)
            )
            .filter(i -> i != null)
            .findFirst();
        //
        prioritized.clear();
        return bean;
    }

    /**
     * Return inheritance depth between two classes.
     * @param cls1 first class (super class)
     * @param cls2 second class (derived class)
     * @param depth value incremented with distance
     * @return inheritance depth between the two classes
     */
    private int inheritanceDepth(Class<?> cls1, Class<?> cls2, int depth) {
        if(cls1 != null && cls2 != null && cls1.isAssignableFrom(cls2)) {
            depth = inheritanceDepth(cls1, cls2.getSuperclass(), depth + 1);
        }
        return depth;
    }

    /**
     * Return value of {@link Bean} priority annotation of a class.
     * @param clazz class with {@link Bean} priority annotation
     * @return priority value or value of {@code lowBound} if no annotation is present
     */
    private int beanAnnotationPriority(Class<?> clazz) {
        for(var anno : clazz.getAnnotationsByType(Bean.class)) {
            return anno.priority();
        }
        return lowBound;
    }

    /**
     * Functional {@link Supplier<T>} interface that allows exceptions
     * used by the {@link create()} method.
     * @param <T> generic result type obtained from supplier
     */
    @FunctionalInterface
    private interface SupplierWithExceptions<T> {
        /**
         * {@link Supplier<T>} method to obtain result from supplier.
         * @return result from supplier
         * @throws Exception potentially thrown by {@code T get()}
         */
        T get() throws Exception;
    }

    /**
     * Wrapper method that absorbs <i>bean</i> creation exceptions returning
     * an empty Optional instead: NoSuchMethodException, SecurityException,
     * InstantiationException, IllegalAccessException and InvocationTargetException.
     * @param <T> generic result type obtained from supplier
     * @param supplier supplier that creates instance and may throw exception
     * @return created instance or empty Optional
     */
    private <T> Optional<T> create(Class<?> cls, SupplierWithExceptions<T> supplier) {
        try {
            return Optional.ofNullable((T)supplier.get());
        } catch (Exception e) { }
        return Optional.empty();
    }
}