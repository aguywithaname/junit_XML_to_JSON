/* 
Filename: junit_to_json.java
Author: Jason Chow
Date: 09/07/2020
Purpose: Accepts a path to a java file on the CL, executes Junit on the repo using Gradle and converts the Junit 
         output to JSON.
*/ 

/*  How to run this using Gradle 4.4:
    Gradle 4.4 doesn't support passing command line arguments. So you need to pass it like this:
    
    $ gradle run -PappArgs="['path/to/project/dir']"

    Gradle 4.9+ accepts command line arugments 
    
    $ gradle run --args='arg1 arg2'

    The end "junit.json" file is stored in your current working directory.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.io.FilenameFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.FileInputStream; 
import java.io.FileNotFoundException;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.ArrayList; 


public class junit_to_json {
    private static String PATH_TO_GRADLE_PROJECT = "./";
    private static String GRADLE_EXECUTABLE = "gradle";
    private static String BLANK = " ";
    private static String GRADLE_TASK = "test";

    public static void main(String args[] ) {
        if(args.length == 0) {
            System.out.println("No argument was passed! Usage: java junit_to_json [file path]."); 
        }
        else {
            // Set path to gradle project from cmd argument
            PATH_TO_GRADLE_PROJECT = args[0];

            try {
                // Code is from here: https://mkyong.com/java/how-to-execute-shell-command-from-java/
                // About executing a bash command in a different dir from a Java program https://stackoverflow.com/questions/26697916/running-a-bash-command-in-different-directory-from-a-java-program
                StringBuffer output = new StringBuffer();

                // Run the executable
                Process process = Runtime.getRuntime().exec(GRADLE_EXECUTABLE + BLANK + GRADLE_TASK, null, new File(PATH_TO_GRADLE_PROJECT));
        
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
        
                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                // Print out program output
                System.out.println("\nProgram output:");
                System.out.println(output.toString());
            }
            catch (IOException e) {
                System.out.println("Error. Printing StackTrace: \n");
                e.printStackTrace();
                System.out.println("\nEnd of StackTrace.");
            }

            // Find all test result XML files 
            File test_results_path = new File(PATH_TO_GRADLE_PROJECT + "/build/test-results/test");
            File[] files_found = test_results_path.listFiles(new FilenameFilter(){
                public boolean accept(File test_results_path, String name){
                    // Return any XML file that starts with "TEST-" and is an XML file
                    return name.startsWith("TEST-") && name.endsWith(".xml");
                }
            }); 
            
            // Print out all test files found (XML)
            System.out.println("Test Result file(s) found: ");
            System.out.println(Arrays.toString(files_found));
            
            // Loop through each XML file, read the contents
            int fileCount = 0;

            try{
                for(File file : files_found){
                    System.out.println("\nConverting file: ");
                    System.out.println(file);
    
                    try {
                        // Read the XML file and store it as a String
                        XmlMapper xmlMapper = new XmlMapper();
                        String xml = inputStreamToString(new FileInputStream(file));
                        
                        // Store as a Java Object
                        Testsuite obj =  xmlMapper.readValue(xml, Testsuite.class);
                        
                        System.out.println("\nTests ran:");
                        
                        // Reformat Java Object to match desired JSON style
                        // eg. Extract failed tests from Java Object
                        JunitJSON junitJSON = createJunitJSON(obj);
                        
                        Gson gson = new Gson();
    
                        // If statement exists in case there is more than 1 junit XML file
                        // Prevents overwriting JSON files.
                        if(fileCount == 0){
                            // Write to a JSON file
                            try (FileWriter writer = new FileWriter("junit.json")){
                                gson.toJson(junitJSON, writer);
    
                                System.out.println("\n");
                                System.out.println("Success!");
                                System.out.println("'junit.json' written to: " + System.getProperty("user.dir"));
    
                            }
                            catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            // Write to a JSON file
                            try (FileWriter writer = new FileWriter("junit(" + fileCount + ").json")){
                                gson.toJson(junitJSON, writer);
    
                                System.out.println("\n");
                                System.out.println("Success!");
                                System.out.println("'junit("+ fileCount +").json' written to: " + System.getProperty("user.dir"));
    
                            }
                            catch (IOException e){
                                e.printStackTrace();
                            }
                        }
    
                        
                    }
                    catch(FileNotFoundException e) {
                        System.out.println(e);
                    }
                    catch(IOException e) {
                        System.out.println(e);
                    }
    
                    fileCount++;
    
                }

            }
            catch(NullPointerException e){
                System.out.println("\n");
                System.out.println("Error: " + e);
                System.out.println("Could not find Junit XML file(s).");
            }
            

        }

    }


    // From: https://www.baeldung.com/jackson-xml-serialization-and-deserialization
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

    
    // Creates a new JunitJSON object that will be coverted to JSON
    public static JunitJSON createJunitJSON(Testsuite obj){
        JunitJSON junitJSON = new JunitJSON();
        ArrayList <FeedbackItems> feedbackItemsList = new ArrayList <FeedbackItems>();
        ArrayList <Overview> overviewList = new ArrayList <Overview>();

        // Loop through the arraylist of testcases inside the object
        for(int i = 0; i < obj.getTestcase().size(); i++){
            System.out.println(obj.getTestcase().get(i).getName());

            // Check if the testcase contains a failure
            if(obj.getTestcase().get(i).getFailure() != null){
                FeedbackItems feedback = new FeedbackItems();
                Overview overview = new Overview();

                // Get path to filename and name of test (eg. adventure.Roomtest.setNameWithValidInput)
                feedback.setName(obj.getName() + "." + obj.getTestcase().get(i).getName());
                feedback.setResult(obj.getTestcase().get(i).getFailure().getType());
                overview.setName(obj.getName() + "." + obj.getTestcase().get(i).getName());

                feedbackItemsList.add(feedback);
                overviewList.add(overview);
            }

        }

        // Set FeedbackItems and Overview Arraylists inside of junitJSON object
        junitJSON.setFeedbackItems(feedbackItemsList);
        junitJSON.setOverview(overviewList);

        return junitJSON;

    }

}