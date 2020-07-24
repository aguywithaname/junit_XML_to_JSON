/*
Filename: Testcase.java
Author: Jason Chow
Date: 22/07/2020
*/

public class Testcase {

    private String name = null;
    private String classname = null;
    private float time = 0;
    private Failures failure = null;

    public Testcase() {}

    public Testcase(String name, String classname, float time, Failures failure){
        this.name = name;
        this.classname = classname;
        this.time = time;
        this.failure = failure;
    }

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassname() {
		return this.classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public float getTime() {
		return this.time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public Failures getFailure() {
		return this.failure;
	}

	public void setFailure(Failures failure) {
		this.failure = failure;
    }
}