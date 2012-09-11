<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <title>Infor Business Cloud</title>
</head>
<body style="padding:0; margin:0; background:#fefefe">

<table width="650" border="1"
       style="font-family: Verdana, Arial,serif; font-size:12px; line-height:16px; text-align:left; color:#000;">
    <tr>
        <td>

            <table width="650" cellspacing="0" cellpadding="0">
                <tr>
                    <td><img src="https://s3.amazonaws.com/emailAssets/email-header.png" border="0"/></td>
                </tr>
                <tr>
                    <td style="padding:20px;">

                        Creation of your Infor application stack has failed!
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <br/>
                                    <div style="font-weight:bold;">Applications</div>
                                    <ul style="padding:0 15px; margin:0; color:#d0000a; line-height:20px; ">
                                        <#list APPLICATIONS as app>
                                            <div style="text-indent: 20px;">${app}</div>
                                        </#list>
                                    </ul>
                                    <br/>
                                    <table>
                                        <tr>
                                            <td>
                                                <div style="font-weight:bold;">Server Count</div>
                                            </td>
                                            <td>
                                                ${SERVER_COUNT}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <div style="font-weight:bold;">Region</div>
                                            </td>
                                            <td>
                                                ${REGION}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <div style="font-weight:bold;">Exception Message</div>
                                            </td>
                                            <td>
                                                ${EXC_MESSAGE}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <div style="font-weight:bold;">Rolled back</div>
                                            </td>
                                            <td>
                                                ${ROLLBACK}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <div style="font-weight:bold;">Rollback Message</div>
                                            </td>
                                            <td>
                                                ${ROLLBACK_EXC}
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td style="font-size:14px; line-height:16px; text-align:left; color:#333;">
                                    <br/>
                                    Support has been notified.
                                    <br/><br/>
                                    <div style="font-weight:bold;">The BusinessCloud Team</div>
                                </td>
                            </tr>
                        </table>

                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>


