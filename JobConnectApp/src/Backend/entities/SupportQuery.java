package Backend.entities;

import java.util.Date;

public class SupportQuery {
    private int queryId;       // Unique query ID
    private int userId;        // User who submitted the query
    private String queryText;  // The content of the query
    private Date queryDate;    // Date the query was submitted
    private boolean resolved;  // Whether the query has been resolved

    // Constructor for new queries (without queryId, as it will be auto-generated in the database)
    public SupportQuery(int userId, String queryText) {
        this.userId = userId;
        this.queryText = queryText;
        this.queryDate = new Date(); // Automatically set the current timestamp
        this.resolved = false;      // New queries are unresolved by default
    }

    // Constructor for existing queries fetched from the database
    public SupportQuery(int queryId, int userId, String queryText, Date queryDate, boolean resolved) {
        this.queryId = queryId;
        this.userId = userId;
        this.queryText = queryText;
        this.queryDate = queryDate;
        this.resolved = resolved;
    }

    // Getters and Setters
    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public String toString() {
        return "SupportQuery{" +
                "queryId=" + queryId +
                ", userId=" + userId +
                ", queryText='" + queryText + '\'' +
                ", queryDate=" + queryDate +
                ", resolved=" + resolved +
                '}';
    }
}
