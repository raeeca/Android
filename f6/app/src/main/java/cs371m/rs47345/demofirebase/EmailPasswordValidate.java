package cs371m.rs47345.demofirebase;

import android.content.res.Resources;

/**
 * Created by witchel on 11/6/16.
 */

public class EmailPasswordValidate
{
    // Locally check login or account creation credentials
    // Empty string means all is ok
    static String validate(Resources resources, String username, String password, String passwordAgain)
    {
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(resources.getString(R.string.error_intro));

        if (username.trim().length() == 0)
        {
            validationError = true;
            validationErrorMessage.append(resources.getString(R.string.error_blank_username));
        }
        else if (!username.contains("@"))
        {
            validationError = true;
            validationErrorMessage.append("use a valid email address");
        }
        if (password.trim().length() == 0)
        {
            if (validationError) {
                validationErrorMessage.append(resources.getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(resources.getString(R.string.error_blank_password));

        }
        else if( password.trim().length() < 6 )
        {
            if (validationError) {
                validationErrorMessage.append(resources.getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(resources.getString(R.string.error_password_too_short));
        }
        if (passwordAgain != null && !password.equals(passwordAgain))
        {
            if (validationError) {
                validationErrorMessage.append(resources.getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(resources.getString(
                    R.string.error_mismatched_passwords));
        }

        validationErrorMessage.append(resources.getString(R.string.error_end));

        String returnString = "";
        if (validationError) {
            returnString = validationErrorMessage.toString();
        }
        return returnString;
    }
}
