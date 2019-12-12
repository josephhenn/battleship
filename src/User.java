import java.util.Date;


public class User extends Person {
	
	
	private String 			userName;
	private String 			passwd;
	private Integer 		userID;
	private Boolean 		mustChangePasswd;
	private Date			dateCreated;
	private Date			dateChanged;
	private Boolean			loginStatus;	
	
	private static Integer 	userIDGenerator; // class variable - we may use later
	
	
	//Constructor with parameters
	
	public User(String firstName, String middleName, String lastName,
			String userName, String passwd, Integer userID) {
		
		super(firstName, middleName, lastName);
		this.userName = userName;
		this.passwd = passwd;				//initial password
		this.userID = userID;
		
		
	}


	


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPasswd() {
		return passwd;
	}


	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}


	public Integer getUserID() {
		return userID;
	}


	public void setUserID(Integer userID) {
		this.userID = userID;
	}


	public Boolean getMustChangePasswd() {
		return mustChangePasswd;
	}


	public void setMustChangePasswd(Boolean mustChangePasswd) {
		this.mustChangePasswd = mustChangePasswd;
	}


	public Date getDateCreated() {
		return dateCreated;
	}


	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}


	public Date getDateChanged() {
		return dateChanged;
	}


	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}


	public static Integer getUserIDGenerator() {
		return userIDGenerator;
	}


	public static void setUserIDGenerator(Integer userIDGenerator) {
		User.userIDGenerator = userIDGenerator;
	}
	
	public Boolean getLoginStatus() {
		return loginStatus;
	}


	public void setLoginStatus(Boolean loginStatus) {
		this.loginStatus = loginStatus;
	}
	
	
	public String toString(){
		String space =" ";
		return "UserID:" + userID + "\t" + String.format("%-50s", super.getFirstName()+ 
				space + super.getMiddleName()+ space + super.getLastName())+ "\tusername: " + userName;
		
	}
	
	public void print(){
		System.out.println(this.toString());
	}
	
	
	
	

}
