/* 
Filename: Failures.java
Author: Jason Chow
Date: 22/07/2020
*/ 

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Failures {
    
    private String message = null;
    private String type = null;

    @JacksonXmlText
    private String text = null;

    public Failures(){
    
    }
    
    public Failures(String message, String type, String text){
        this.message = message;
        this.type = type;
        this.text = text;
    }

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

}