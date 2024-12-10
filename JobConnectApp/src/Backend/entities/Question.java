package Backend.entities;
import java.util.List;


public class Question {
    private String text;
    private List<String> options;
    private String correct;
    
	public Question(String text, List<String> options, String correct) {
		this.text = text;
		this.options = options;
		this.correct = correct;
	}
	
	public Question() {
		
		this.text = "";
		this.options = null;
		this.correct = "";
	}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrect() {
        return correct;
    }

	public String getAnswer() {
		return correct;
	}

    public void setCorrect(String correct) {
        this.correct = correct;
    }
    
    
}