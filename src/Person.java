import java.io.Serializable;


public class Person implements Serializable{
	
	private Integer personID;
	private static Integer personIDGenerator=499999;
	private String firstName, middleName, lastName;
	
	
	public Person (String firstName, String middleName, String lastName) {
		super();
		personID = ++personIDGenerator;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}


	public Integer getPersonID() {
		return personID;
	}


	public void setPersonID(Integer personID) {
		this.personID = personID;
	}

	public static Integer getPersonIDGenerator() {
		return personIDGenerator;
	}


	public static void setPersonIDGenerator(Integer personIDGenerator) {
		Person.personIDGenerator = personIDGenerator;
	}

	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getMiddleName() {
		return middleName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	
	
}
