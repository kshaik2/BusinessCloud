$(document).ready(function() {

	function notNull(v){
		return v && $.trim(v)!='';
	}

	$('#lang').val($('#locale_val').val());
	
	window.Industry = Backbone.Model.extend({
		defaults:{	
			id: -1,
			name: "",
			description: ""
		}	
	});
	
	// Industry Collection 
	window.IndustryCollection = Backbone.Collection.extend({
		model : Industry,
		url: function(){
			var dr = new Date();
			return 'services/company/getIndustries?&_='+dr.getTime();
		}
	});	
	
	// Get Instances of Industries
	window.Industries = new IndustryCollection;		
	Industries.fetch({
		success:function(){
			var industry_list = '<option value="-1" selected></option>';
			Industries.each(function(industry){
				industry_list += '<option value="'+industry.get('id')+'">'+industry.get('name')+'</option>';
			});
			$('#industry_id').append(industry_list);
		},
		error:function(){
			var industry_list = '<option value="-1" selected></option>';
			$('#industry_id').append(industry_list);		
		}
	});	
			
	$('#complete_registration_save').bind('click',function(){
	
		var post_data = {
			validationId 		: $.trim($('#validationId').val()),
			password 	 		: $.trim($('#password').val()),
			password2 	 		: $.trim($('#password2').val()),
			language	 		: $.trim($('#lang').val()),
			companyName 	 	: $.trim($('#company_name').val()),
			companyId			: $.trim($('#company_id').val()),
			industryId			: $.trim($('#industry_id').val()),
			inforId				: $.trim($('#infor_id').val()),
			address1 	 		: $.trim($('#user_address_1').val()),
			address2 	 		: $.trim($('#user_address_2').val()),
			country 	 		: $.trim($('#user_country').val()),
			phone				: $.trim($('#user_phone').val())
		}
		
		var passNotNull = notNull(post_data.password) && notNull(post_data.password2);
		var passSame =  post_data.password == post_data.password2;
		var validatedPass = passNotNull && passSame;
		var validatedRequired = post_data.address1 && post_data.address2 && post_data.companyName && post_data.industryId &&
								post_data.country && post_data.phone;	
								
		if(validatedPass && validatedRequired){
			$.ajax({ 
				url: "services/registration/completeRegistration", 
				type: "POST",
				cache: false,
				data : JSON.stringify(post_data),
				dataType: "json",
				processData : true,
				contentType: "application/json; charset=utf-8",
				success: function(resp){	
					$.cookie('locale', post_data.language);
					window.location = './cloud.jsp';
				},
				error: function(){
					alert($.i18n.prop('reg.dialog.7'));
				}
			});	
		}else{
			if(!passNotNull){
				alert('Please enter a password.');
			}else if(!passSame){
				alert('Please make sure both passwords match.');
			}else if(!validatedRequired){
				alert('Please make sure all fields are filled out.');
			}
		}		
	});

	$('#reset_password_save').bind('click',function(){
		var post_data = {
			password 	 		: $('#password').val(),
			password2 	 		: $('#password2').val()
		}
		if(notNull(post_data.password) && notNull(post_data.password2)){
			$.ajax({ 
				url: "services/passwordReset/completePasswordReset", 
				type: "POST",
				cache: false,
				data : JSON.stringify(post_data),
				dataType: "json",
				processData : true,
				contentType: "application/json; charset=utf-8",
				success: function(resp){	
					window.location = '.'+resp.redirectUrl;
				},
				error: function(){
					alert($.i18n.prop('reg.dialog.7'));
				}
			});	
		}else{
			alert($.i18n.prop('reg.dialog.8'));
		}		
	});	


});