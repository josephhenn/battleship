import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class Globals {
	
	public static String USERS_INPUT_FILE 					= "resources/Users.txt";
	
	public static CopyOnWriteArrayList<User> readUsersFromFile(String fileName) {

		CopyOnWriteArrayList<User> userList = new CopyOnWriteArrayList<User>();
		String line;

		try (InputStream fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis,
						Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);) {

			int i = 0;
			String firstName = null, lastName = null, middleName = null, userName = null, password = null, sUserID = null;
			Integer iUserID = 0;
			while ((line = br.readLine()) != null) {
				// Deal with the line

				if (i % 4 == 0) {
					iUserID = Integer.parseInt(line.trim());

				}

				else if (i % 4 == 1) {
					String nameTokens[] = line.split("\\s+"); // split the
																// fullName into
																// first,
																// middle, last
					firstName = nameTokens[0]; // always the in location 0;
					// System.out.println(firstName);
					lastName = nameTokens[nameTokens.length - 1]; // always in
																	// location
																	// length -1

					if (nameTokens.length > 2) { // if there is a middle name,
													// get what is between first
													// and last
						middleName = line.substring(firstName.length() + 1,
								line.length() - lastName.length() - 2);
						// System.out.println(middleName);

					}

					// System.out.println(lastName);

				} else if (i % 4 == 2) {
					userName = line;
					// System.out.println(userName);
				} else if (i % 4 == 3) {

					password = line;
					// System.out.println(password);

					// Now make a user by instantiating a user with the
					// information read from the line in the file;
					User u = new User(firstName, middleName, lastName,
							userName, password, iUserID);
					userList.add(u);

				}
				i++; // increment line number

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userList;

	}

	public static User authenticateUser(CopyOnWriteArrayList<User> userList,
			Login loginObject) {

		for (User u : userList) {
			if (u.getUserName().equals(loginObject.getLoginUserName())
					&& u.getPasswd().equals(loginObject.getLoginPassword())) {
				u.setLoginStatus(true);
				return u;
			}
		}

		return null;
	}

}
