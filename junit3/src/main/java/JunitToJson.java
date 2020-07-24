/*
Filename: JunitToJson.java
*/

/*  How to run this using Gradle 4.4:
    Gradle 4.4 doesn't support passing command line arguments. So you need to pass it like this:

    $ gradle run -PappArgs="['path/to/project/dir']"

    Gradle 4.9+ accepts command line arugments

    $ gradle run --args='arg1 arg2'

    You can run the jar using:

    java -jar <file.jar> <path/to/file>

    The final "junit.json" file is stored in your current working directory.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.FileInputStream;
import com.google.gson.Gson;
import java.util.ArrayList;

// The latest versions of ShadowJar (v5.0.0+) do not support Gradle version less than 5.0, we are using ShadowJar 4.0.4.

/**
* This program accepts a path to a java file on the CL, executes Junit on the repo using Gradle and converts the Junit
* output to JSON.
* 
* @author Jason Chow
* @since 2020-07-09
*/
public class JunitToJson {

    private static final String GRADLE_EXECUTABLE = "gradle";
    private static final String GRADLE_TASK = "test";

    /**
    * The main method of the program reads the runs the gradle "test" command,
    * finds the Junit XML results, reads the XML file(s), then stores them as 
    * Java objects. The XML Java objects are then passed to the createJunitJSON()
    * method to be converted to JunitJSON Java objects. The JunitJSON objects are
    * then converted and written to a JSON file called 'junit.json'in your current
    * working directory.
    * @param args Takes in a command line argument. The file path to your gradle project.
    * @return none
    */
    public static void main(String[] args ) {
        if (args.length == 0) {
            System.out.println("No argument was passed! Usage: execute <file path>.");
            return;
        }

        // Set path to gradle project from cmd argument
        final String PATH_TO_GRADLE_PROJECT = args[0];

        try {
            /* 
               Code is from here: 
               https://mkyong.com/java/how-to-execute-shell-command-from-java/
               About executing a bash command in a different dir from a Java program:
               https://stackoverflow.com/questions/26697916/running-a-bash-command-in-different-directory-from-a-java-program
            */

            StringBuilder output = new StringBuilder();

            // Run the executable
            Process process = Runtime.getRuntime().exec(GRADLE_EXECUTABLE + " " + GRADLE_TASK, null, new File(PATH_TO_GRADLE_PROJECT));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Print program output
            System.out.println("\nProgram output:\n" + output.toString());

            // Find all test result XML files
            final File TEST_RESULTS_PATH = new File(PATH_TO_GRADLE_PROJECT + "/build/test-results/test");

            // Return any XML file that starts with "TEST-" and is an XML file
            File[] filesFound = TEST_RESULTS_PATH.listFiles((test_results_path, name) ->
                    name.startsWith("TEST-") && name.endsWith(".xml"));

            if (filesFound == null) {
                System.err.println("FATAL: Path to Gradle project not found.");
                return;
            }

            // Print out all test files found (XML)
            System.out.println("Test Result file(s) found:\n" + Arrays.toString(filesFound));

            ArrayList<Testsuite> testsuiteLists = new ArrayList<>();

            // Loop through each XML file, read the contents
            for (File file : filesFound) {
                System.out.println("\nConverting file:\n" + file);
                try {
                    // Read the XML file and store it as a String
                    XmlMapper xmlMapper = new XmlMapper();
                    String xml = inputStreamToString(new FileInputStream(file));

                    // Store as a Java Object and add it to the testsuiteLists ArrayList
                    testsuiteLists.add(xmlMapper.readValue(xml, Testsuite.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Reformat Java Object to match desired JSON style
            // eg. Extract failed tests from Java Object
            System.out.println("\nTests ran:");

            JunitJSON junitJSON = createJunitJSON(testsuiteLists);
            Gson gson = new Gson();

            // Write to a JSON file
            try (FileWriter writer = new FileWriter("junit.json")) {
                gson.toJson(junitJSON, writer);
                System.out.println("\nSuccess!\n'junit.json' written to: " + System.getProperty("user.dir"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException | IOException e) {
            System.err.println("\nError: " + e);
        }
    }

    // From: https://www.baeldung.com/jackson-xml-serialization-and-deserialization
    /**
    * This function converts a file input stream into a String.
    * @param is The input stream object (eg. The XML file input stream)
    * @return String The contents of the XML file are returned as a String.
    * @exception IOException IOException if file can't be read.
    */
    public static String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    /**
    * This function creates a new JunitJSON object that will be converted to a JSON file in main().
    * @param testsuiteList An ArrayList of Testuite objects.
    * @return junitJSON A junitJSON object that is filled with failures from the Testcases.
    */
    private static JunitJSON createJunitJSON(ArrayList<Testsuite> testsuiteList) {
        JunitJSON junitJSON = new JunitJSON();
        ArrayList<FeedbackItems> feedbackItemsList = new ArrayList<>();
        ArrayList<Overview> overviewList = new ArrayList<>();

        // Loop through ArrayList of Testsuites
        for (Testsuite testsuite : testsuiteList) {

            // Loop through the ArrayList of Testcases inside the object
            for (Testcase testcase : testsuite.getTestcase()) {
                System.out.println(testcase.getName());

                // Check if the testcase contains a failure
                if (testcase.getFailure() != null) {
                    FeedbackItems feedback = new FeedbackItems();
                    Overview overview = new Overview();

                    // Get path to filename and name of test (eg. adventure.RoomTest.setNameWithValidInput)
                    feedback.setName(testsuite.getName() + "." + testcase.getName());
                    feedback.setResult(testcase.getFailure().getType());
                    overview.setName(testsuite.getName() + "." + testcase.getName());

                    feedbackItemsList.add(feedback);
                    overviewList.add(overview);
                }
            }
        }

        // Set FeedbackItems and Overview ArrayLists inside of junitJSON object
        junitJSON.setFeedbackItems(feedbackItemsList);
        junitJSON.setOverview(overviewList);

        return junitJSON;
    }

}