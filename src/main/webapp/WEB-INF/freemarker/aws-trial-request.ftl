<#setting time_zone="US/Eastern">
A custom trial environment deployment has been requested.

    Date: ${.now?string("MM-dd-yyyy hh:mm a")} US/Eastern time
    Product(s) requested: ${products}
    Region: ${region.name}
    User: ${user.username} (${user.lastName}, ${user.firstName})
    address: ${user.address1!"--no address1--"}, ${user.address2!"--no address2--"}
    Company: ${user.companyName!"--No company--"}
    Comment: ${comment!"--None--"}

To view the trial request, click:
${siteLink}