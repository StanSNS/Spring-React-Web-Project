package fxibBackend.constants;

public class EmailTemplateConst {

    public final static String EMAIL_FORGOT_PASSWORD_SUBJECT = "FXIB Reset Password";
    public static final String EMAIL_FORGOT_PASSWORD_HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset</title>
            </head>
            <body>
                <h1>Hello %s,</h1>
                <p>We received a request to reset your password. To reset your password, please click the link below:</p>
                <p><a href="http://localhost:3000/reset-password-update?token=%s">Reset My Password</a></p>
                <p>If you did not request a password reset, you can ignore this email.</p>
                <p>Thank you,</p>
                <p>FXIB</p>
            </body>
            </html>""";
    public final static String TWO_FACTOR_AUTH_SUBJECT_EMAIL = "FXIB - Two Authentication 6-digit Code";
    public static final String TWO_FACTOR_AUTH_HTML_TEMPLATE_EMAIL = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Authentication Code</title>
            </head>
            <body>
                <h1>Hello %s,</h1>
                <p>Your authentication code is: <strong>%s</strong></p>
                <p>If you did not tried to login recently, please change your password.</p>
                <p>Thank you,</p>
                <p>FXIB</p>
            </body>
            </html>""";
    public final static String DIFFERENT_LOCATION_AUTH_SUBJECT_EMAIL = "FXIB - Login from a different location!";
    public static final String DIFFERENT_LOCATION_HTML_TEMPLATE_EMAIL = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Login from a different location!</title>
            </head>
            <body>
                <h1>Hello %s,</h1>
                <p>If you have knowledge of someone else logging into your account from a different location, please take action immediately. Here are the details of the unauthorized login:
                <ul>
                    <li>Continent: %s</li>
                    <li><img src="%s" alt="Country Flag" width=16 height=10> Country: %s</li>
                    <li>City: %s</li>
                    <li>IP Address: %s</li>
                </ul>
                </p>
                       <p>Your registration was made at this location:
                <ul>
                    <li>Continent: %s</li>
                    <li><img src="%s" alt="Country Flag" width=16 height=10> Country: %s</li>
                    <li>City: %s</li>
                    <li>IP Address: %s</li>
                </ul>
                </p>
                
                <p>If you are aware of this login, you can ignore this message.</p>
                <p>You can change your location from the settings.</p>
                <p>Thank you,</p>
                <p>FXIB</p>
            </body>
            </html>""";

    public final static String REGISTRATION_SUCCESS_SUBJECT_EMAIL = "FXIB - Successful Registration";

    public static final String REGISTRATION_SUCCESS_HTML_TEMPLATE_EMAIL = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Successful Registration</title>
                </head>
                    <body>
                <h1>Hello %s,</h1>
                <p>Congratulations! You have successfully registered with FXIB.</p>
                <p>Thank you for choosing FXIB!</p>
            </body>
                </html>""";


    public static final String SUBSCRIPTION_SUCCESS_SUBJECT_EMAIL = "FXIB - Subscription Successful!";
    public static final String SUBSCRIPTION_SUCCESS_HTML_TEMPLATE_EMAIL = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Subscription Successful!</title>
            </head>
            <body>
                <h1>Hello %s,</h1>
                <p>Thank you for subscribing to FXIB services. Your subscription details are as follows:
                <ul>
                    <li>Billing Date: %s</li>
                    <li>Duration: %s</li>
                    <li>End of Billing Date: %s</li>
                    <li>Amount: %s</li>
                    <li>Card: %s</li>
                    <li>Status: %s</li>
                    <li>Receipt: %s</li>
                    <li>Description: %s</li>
                </ul>
                </p>
                <p>If you have any questions or concerns, please contact our support team.</p>
                <p>Thank you for choosing FXIB!</p>
            </body>
            </html>""";

    public static final String USER_BAN_SUBJECT_EMAIL = "FXIB - Account suspended";
    public static final String USER_BAN_HTML_TEMPLATE_EMAIL = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Information:</title>
            </head>
            <body>
                <h1>Hello %s,</h1>
                <p>We regret to inform you that your account has been banned from FXIB services.</p>
                <p>Details of the ban are as follows:
                <ul>
                    <li>Ban Date: %s</li>
                    <li>Reason: Violating Terms of Service</li>
                </ul>
                </p>
                <p>If you believe this ban is a mistake or have any concerns, please contact our support team.</p>
                <p>Thank you for your understanding.</p>
                <p>Sincerely,<br>FXIB Team</p>
            </body>
            </html>""";

    public static final String USER_UNBAN_SUBJECT_EMAIL = "FXIB - Account no longer suspended.";
    public static final String USER_UNBAN_HTML_TEMPLATE_EMAIL = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Information:</title>
            </head>
            <body>
                <h1>Hello %s,</h1>
                <p>We are pleased to inform you that your account has been unbanned from FXIB services.</p>
                <p>The details of the unbanning are as follows:
                <ul>
                    <li>Unban Date: %s</li>
                </ul>
                </p>
                <p>If you have any questions or concerns, please contact our support team.</p>
                <p>Thank you for your understanding and cooperation.</p>
                <p>Sincerely,<br>FXIB Team</p>
            </body>
            </html>""";

    public static final String INQUIRY_EMAIL_HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
            </head>
            <body>
                <p>%s</p>
                <p>Sent on: %s by %s</p>
            </body>
            </html>""";

    public static final String REPORT_EMAIL_SUBJECT = "Customer Support Report - %s";
    public static final String REPORT_EMAIL_HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
            </head>
            <body>
                <p>Sent on: %s by %s</p>
                
                <!-- Report Content -->
                <div>
                    <h2>Report Content:</h2>
                    <p>%s</p>
                </div>
                
                <!-- Image -->
                <div>
                     <p>Image url: %s</p>
                </div>
            </body>
            </html>""";
}
