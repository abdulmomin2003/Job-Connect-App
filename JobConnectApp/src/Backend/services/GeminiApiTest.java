					package Backend.services;
					
					import com.fasterxml.jackson.databind.ObjectMapper;

import Backend.entities.Course;
import Backend.entities.Question;

import com.fasterxml.jackson.core.type.TypeReference;
					
					import java.io.*;
					import java.net.HttpURLConnection;
					import java.net.URL;
					import java.util.*;
					
					public class GeminiApiTest {
					
			            static String apiKey = System.getenv("API_KEY2");
			            static // API URL
			            String apiUrl = System.getenv("API_URL") + apiKey;
			
//			            public static void main(String[] args) {
//			                try {
//			                    List<String> jobRequirements = List.of("Java", "Spring", "Microservices");
//			                    GeminiApiTest geminiApiTest = new GeminiApiTest();
//			                    List<Course> courses = geminiApiTest.fetchCourses(jobRequirements);
//
//			                    if (courses.isEmpty()) {
//			                        System.out.println("No courses were recommended.");
//			                    } else {
//			                        System.out.println("Recommended Courses:");
//			                        for (Course course : courses) {
//			                            System.out.println(course);
//			                        }
//			                    }
//			                } catch (Exception e) {
//			                    e.printStackTrace();
//			                }
//			            }
			            private String generateCoursePrompt(List<String> jobRequirements) {
					        String joinedRequirements = String.join(", ", jobRequirements);
					        return "Based on the skills: " + joinedRequirements + 
					               ", recommend 5 online courses. Provide the following details for each course:\n" +
					               "- Course Name\n" +
					               "- Instructor Name\n" +
					               "- Course URL\n" +
					               "- A brief description of the course.\n" +
					               "Format the output as a JSON array of objects with fields: courseName, instructor, link, description.";
					    }
					    public List<Course> fetchCourses(List<String> jobRequirements) {
					        List<Course> courses = new ArrayList<>();
					        try {
					            // Generate course prompt
					            String coursePrompt = generateCoursePrompt(jobRequirements);
					            String response = sendPostRequest(apiUrl, buildRequestBody(coursePrompt));

					            // Check for errors in the response
					            if (response.contains("\"error\":")) {
					                System.err.println("Error fetching courses: " + response);
					                return Collections.emptyList();
					            }

					            // Parse the course response
					            courses = parseCourses(response);
					            
					            System.out.println(response);
					            
					        } catch (Exception e) {
					            System.err.println("Error fetching courses: " + e.getMessage());
					        }

					        return courses;
					    }
					    private List<Course> parseCourses(String response) {
					        List<Course> courses = new ArrayList<>();
					        try {
					            ObjectMapper objectMapper = new ObjectMapper();

					            // Parse the response JSON
					            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
					            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");

					            if (candidates == null || candidates.isEmpty()) {
					                System.err.println("No candidates found in the response.");
					                return courses;
					            }

					            // Extract content from the response
					            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
					            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
					            String jsonText = (String) parts.get(0).get("text");

					            // Clean and parse JSON content
					            jsonText = jsonText.replaceAll("```json|```", "").trim();
					            List<Map<String, Object>> courseList = objectMapper.readValue(jsonText, new TypeReference<>() {});

					            // Convert course data into Course objects
					            for (Map<String, Object> courseMap : courseList) {
					                String courseName = (String) courseMap.get("courseName");
					                String instructor = (String) courseMap.get("instructor");
					                String link = (String) courseMap.get("link");
					                String description = (String) courseMap.get("description");

					                courses.add(new Course(courseName, instructor, link, description));
					            }
					        } catch (Exception e) {
					            System.err.println("Error parsing courses: " + e.getMessage());
					        }
					        return courses;
					    }
					    private String buildRequestBody(String prompt) {
					        return "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}";
					    }

					
					    public static List<Question> getMCQsFromGemini(String apiUrl, String prompt) throws Exception {
					    	
					    	
					    	System.out.println("Prompt: "+prompt);
					        // Request body for generating MCQs
					        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}";
					        // Send request and parse response
					        String response = sendPostRequest(apiUrl, requestBody);
					        
					        System.out.println(response);
					
					        // **Check for error code in response**
					        if (response.contains("\"error\":")) {
					            System.out.println("MCQ Generation Error: " + response);
					            return Collections.emptyList(); // Return empty list on error
					        }
					
					        return parseMCQs(response);
					    }
					
					    private static String sendPostRequest(String apiUrl, String requestBody) throws Exception {
					        // Create and configure connection
					        URL url = new URL(apiUrl);
					        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					        connection.setRequestMethod("POST");
					        connection.setRequestProperty("Content-Type", "application/json");
					        connection.setDoOutput(true);
					
					        // Send request body
					        try (OutputStream os = connection.getOutputStream()) {
					            byte[] input = requestBody.getBytes("utf-8");
					            os.write(input, 0, input.length);
					        }
					
					        // Read response
					        int status = connection.getResponseCode();
					        BufferedReader reader;
					        if (status > 299) {
					            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					        } else {
					            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					        }
					
					        StringBuilder response = new StringBuilder();
					        String line;
					        while ((line = reader.readLine()) != null) {
					            response.append(line);
					        }
					        reader.close();
					
					        return response.toString();
					    }
					
					    private static List<Question> parseMCQs(String response) {
					        List<Question> mcqs = new ArrayList<>();
					        try {
					            ObjectMapper objectMapper = new ObjectMapper();
					            
					            // Parse the main response JSON
					            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
					            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");

					            if (candidates == null || candidates.isEmpty()) {
					                System.err.println("No candidates found in the response.");
					                return mcqs;
					            }

					            // Extract the text from the first candidate's content
					            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
					            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
					            String jsonText = (String) parts.get(0).get("text");

					            // Remove the code block formatting (```json ... ```) if present
					            jsonText = jsonText.replaceAll("```json|```", "").trim();

					            // Parse the JSON text containing questions
					            Map<String, Object> questionsData = objectMapper.readValue(jsonText, new TypeReference<>() {});
					            List<Map<String, Object>> questions = (List<Map<String, Object>>) questionsData.get("questions");

					            // Process each question
					            if (questions != null) {
					                for (Map<String, Object> questionMap : questions) {
					                    Question mcq = new Question();

					                    // Extract the question text
					                    mcq.setText((String) questionMap.get("question"));

					                    // Extract the options
					                    List<String> options = (List<String>) questionMap.get("options");
					                    mcq.setOptions(options);

					                    // Extract the correct answer
					                    mcq.setCorrect((String) questionMap.get("answer"));

					                    // Add to the list of MCQs
					                    mcqs.add(mcq);
					                }
					            }
					        } catch (Exception e) {
					            System.err.println("Error parsing MCQs: " + e.getMessage());
					            e.printStackTrace();
					        }
					        return mcqs;
					    }

					

					    


					    public List<Question> fetchQuestions(String jobRequirement) {
					        List<Question> questions = new ArrayList<>();

					        try {
					            
					            // User input for relevant skills or requirements
					            System.out.println("Enter relevant skills or requirements (comma-separated): ");

					            // Generate prompt for question generation
					            String mcqPrompt = "Generate 10 multiple-choice questions on " + jobRequirement + ". The questions should be of easy difficulty. Format the output as JSON.";

					            // Get questions from Gemini API
					            List<Question> mcqQuestions = getMCQsFromGemini(apiUrl, mcqPrompt);

					            // Check if questions are generated
					            if (mcqQuestions.isEmpty()) {
					                System.out.println("No questions were generated. Please try again with a different input.");
					                return questions;
					            }
					            else {
					            	return mcqQuestions;
					            }

					           					        } catch (Exception e) {
					            e.printStackTrace();
					        }
					        

					        return questions;
					    }
					}
				
					