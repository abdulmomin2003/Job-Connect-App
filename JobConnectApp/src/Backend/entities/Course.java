package Backend.entities;

public class Course{
    public String courseName;
    public String Instructor;
    public String link;
    public String courseDescription;
    
	public Course(String courseName, String Instructor, String link, String courseDescription) {
		this.courseName = courseName;
		this.Instructor = Instructor;
		this.link = link;
	    this.courseDescription = courseDescription;
	}
	
	public Course(Course course) {
		this.courseName = course.courseName;
		this.Instructor = course.Instructor;
		this.link = course.link;
		this.courseDescription = course.courseDescription;
	}
	

	public String getCourseName() {
		return courseName;
	}
	
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
	public String getInstructor() {
		return Instructor;
	}
	
	public void setInstructor(String Instructor) {
		this.Instructor = Instructor;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	
	public String toString() {
		return "Course [courseName=" + courseName + ", Instructor=" + Instructor + ", link=" + link + "]";
	}
}
