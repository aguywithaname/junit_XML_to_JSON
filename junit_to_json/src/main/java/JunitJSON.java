import java.util.ArrayList;  

public class JunitJSON {
    private String title = "Junit";
    private ArrayList <FeedbackItems> feedbackItems;
    private ArrayList <Overview> overview;

    public JunitJSON(){
    
    }
    
    public JunitJSON(String title, ArrayList <FeedbackItems> feedbackItems, ArrayList <Overview> overview){
        this.title = title;
        this.feedbackItems = feedbackItems;
        this.overview = overview;
    }

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<FeedbackItems> getFeedbackItems() {
		return this.feedbackItems;
	}

	public void setFeedbackItems (ArrayList <FeedbackItems> feedbackItems) {
		this.feedbackItems = feedbackItems;
	}

	public ArrayList<Overview> getOverview() {
		return this.overview;
	}

	public void setOverview(ArrayList<Overview> overview) {
		this.overview = overview;
	}


}