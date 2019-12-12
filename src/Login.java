import java.io.Serializable;


public class Login implements Serializable{
	
	private String loginUserName;
	private String loginPassword;
	
	
	
	public Login(String loginUserName, String loginPassword) {
		super();
		this.loginUserName = loginUserName;
		this.loginPassword = loginPassword;
	}



	public String getLoginUserName() {
		return loginUserName;
	}



	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}



	public String getLoginPassword() {
		return loginPassword;
	}



	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	
	
	
	

}
