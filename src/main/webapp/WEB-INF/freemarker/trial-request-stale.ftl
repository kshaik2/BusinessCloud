<#setting time_zone="US/Eastern">
A trial request is waiting for approval.

    Date Requested: ${created?string("MM-dd-yyyy hh:mm a")} US/Eastern time
    Products: ${productList}
    Region: ${region.name}
    User: ${user.username} (${user.lastName}, ${user.firstName})
    address: ${user.address1!"--no address1--"}, ${user.address2!"--no address2--"}
    Company: ${user.companyName!"--No company--"}
    Comment: ${comment!"--None--"}