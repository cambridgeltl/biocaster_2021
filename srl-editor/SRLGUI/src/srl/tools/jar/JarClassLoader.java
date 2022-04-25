package srl.tools.jar;

import java.io.IOException;

/**
 * A class for loading a class file from a JAR
 * @author John McCrae
 */
public class JarClassLoader extends MultiClassLoader {
    private JarResources	jarResources;

    /** Create a new instance
     *
     * @param jarName The file name of the JAR file
     * @throws java.io.IOException
     */
    public JarClassLoader (String jarName) throws IOException {
        // Create the JarResource and suck in the .jar file.
        jarResources = new JarResources (jarName);
	}

    protected byte[] loadClassBytes (String className) {
        // Support the MultiClassLoader's class name munging facility.
        className = formatClassName (className);

        // Attempt to get the class data from the JarResource.
        return (jarResources.getResource (className));
	}
}
