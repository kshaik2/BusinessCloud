<#setting time_zone="US/Eastern">
A trial instance has been requested.

    Date: ${.now?string("MM-dd-yyyy hh:mm a")} US/Eastern time
    Products: ${productList}
    Region: ${region.name}
    User: ${user.username} (${user.lastName}, ${user.firstName})
    address: ${user.address1!"--no address1--"}, ${user.address2!"--no address2--"}
    Company: ${user.companyName!"--No company--"}
    Comment: ${comment!"--None--"}

To approve the trial click the following link:
${approveLink}

To disapprove the trial and delete the request click the following link:
${deleteLink}