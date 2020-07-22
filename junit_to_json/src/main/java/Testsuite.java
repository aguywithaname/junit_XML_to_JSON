import java.util.ArrayList;  
import javax.xml.bind.annotation.XmlAttribute;  
import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement; 
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.*;


@JsonIgnoreProperties({"system-out", "system-err"})

// Legacy JAXB annotations for XML are here
// To do: Replace annotations with Jackson equivalents.
@XmlRootElement
public class Testsuite implements Serializable{
    
    private String name = null;
    private int tests = 0;
    private int skipped = 0;
    private int failures = 0;
    private int errors = 0;
    private String timestamp = null;
    private String hostname = null;
    private float time = 0;
    private String properties = null;

    // Junit XML results do no put testcases in a <list> </list> wrapper so we need to disable the wrapping
    @JacksonXmlElementWrapper(useWrapping = false)
    private ArrayList<Testcase> testcase;

    public Testsuite(){
        
    }
    
    public Testsuite(String name, int tests, int skipped, int failures, int errors, String timestamp, String hostname, float time, ArrayList<Testcase> testcase){
        this.name = name;
        this.tests = tests;
        this. skipped = skipped;
        this.failures = failures;
        this.errors = errors;
        this.timestamp = timestamp;
        this.hostname = hostname;
        this.time = time;
        this.properties = properties;
        this.testcase = testcase;
    }
    
    @XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
    }
    
    @XmlAttribute
	public int getTests() {
		return this.tests;
	}

	public void setTests(int tests) {
		this.tests = tests;
	}

    @XmlAttribute
	public int getSkipped() {
		return this.skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

    @XmlAttribute
	public int getFailures() {
		return this.failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

    @XmlAttribute
	public int getErrors() {
		return this.errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

    @XmlAttribute
	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

    @XmlAttribute
	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

    @XmlAttribute
	public float getTime() {
		return this.time;
	}

	public void setTime(float time) {
		this.time = time;
    }
    
    @XmlAttribute
    public String getProperties() {
		return this.properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

    @XmlElement
	public ArrayList<Testcase> getTestcase() {
		return this.testcase;
	}

	public void setTestcase(ArrayList<Testcase> testcase) {
		this.testcase = testcase;
	}

}