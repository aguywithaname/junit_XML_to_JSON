public class FeedbackItems {
    private String name = null;
    private String result = null;

    public FeedbackItems(){
        
    }
    
    public FeedbackItems(String name, String result){
        this.name = name;
        this.result = result;
    }

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}