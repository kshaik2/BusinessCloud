$(document).ready(function () {
	var R = Backbone.Router.extend({
		routes: {
			"/login"	:	"login",
			""			:	"icsDefault",
			"/"			:	"icsDefault", 
			"*actions"	: 	"icsDefault"			
		},
		login: function(sub){
			doLogin(true);
		},
		icsDefault: function(sub){
			closeBigDialog();
		}
	});
	
	// Init routing bindings created above
	var app_router = new R;
	Backbone.history.start();	

	$('#lang_select').val($('#language').val()).change(function(){
		window.location = './index.jsp?l='+$(this).val();
	});
	
	function doLogin(fromURL){
		var errorMSG = $.i18n.prop('login.8'), 
			displayError = 'none', 
			dialogHeight = 280,
			contentHeight = 160;
		if(fromURL){
			displayError = 'block';
			dialogHeight = 305;		
			contentHeight = 190;
		}

		var html = 	'\
			<div id="cloud_deploy">\
				<form method="post" action="j_spring_security_check">\
					<div class="cloud_deploy_header_container_singlepage" style="width:340px;">\
						<span style="color:#fff;">Login to Infor Business Cloud</span>\
					</div>\
					<div class="cloud_deploy_singlepage_content" style="height:'+contentHeight+'px;width:340px;">\
						<div class="cdc_input_desc_text" id="password_reset_title" style="line-height:30px;margin-bottom:0px;margin-top:0px;font-weight:bold;font-size:14px;">'+$.i18n.prop('login.2')+'</div>\
						<input type="text" value="" class="cdc_input" style="margin-top:0px;margin-left:0px;width:290px;" name="j_username" id="j_username"/><br>\
						<div class="cdc_input_desc_text" id="password_reset_title" style="line-height:30px;margin-bottom:0px;margin-top:0px;font-weight:bold;font-size:14px;">'+$.i18n.prop('login.3')+'</div>\
						<input type="password" value="" class="cdc_input" style="margin-top:0px;margin-left:0px;width:290px;" name="j_password" id="j_password"/><br>\
						<div class="login_input_desc_small" style="margin-left:0;margin-top:8px;margin-bottom:0px;"><span style="cursor:pointer;color:#545454;font-size:12px;" id="forgot_password_link">'+$.i18n.prop('login.5')+'</span></div>\
						<div class="login_input_desc_small" style="margin-left:0;margin-top:12px;margin-bottom:0px;color:#CC121B;font-weight:bold;display:'+displayError+';">'+errorMSG+'</div>\
					</div>\
					<div class="cloud_deploy_button_container_singlepage"  style="width:340px;">\
						<input type="submit" value="'+$.i18n.prop('login.1')+'" class="cloud_deploy_button" id="cloud_login_submit" style="width:100px;height:33px;padding:0;line-height:33px;text-align:center;" />\
						<span class="cloud_deploy_button" id="cloud_login_cancel" style="float:left;">'+$.i18n.prop('login.6')+'</span>\
					</div>\
				</form>\
			</div>\
		';
		
		openBigDialog(dialogHeight,380,20,20,dialogHeight,380,html,true);		
		
		$('#j_username').focus();
		
		$('#cloud_login_cancel').bind('click',function(){
			// 1. Close Dialog
			if(fromURL){
				window.location = './';
			}else{
				closeBigDialog();
			}
			
		});		
	
	}

	
	$('#login-button-top').bind('click',function(){
		doLogin(false);
	});
	
	$('#forgot_password_link').live('click', function(){
		closeBigDialog();
		var button = $(this);
		
		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage">\
					<span style="color:#fff;">'+$.i18n.prop('password.dialog.1')+'</span>\
				</div>\
				<div class="cloud_deploy_singlepage_content" style="height:115px;">\
				    <div class="cdc_input_desc_text" id="password_reset_title" style="line-height:30px;margin-bottom:0px;margin-top:15px;font-weight:bold;font-size:12px;">'+$.i18n.prop('password.dialog.2')+'</div>\
					<input type="text" value="" class="cdc_input" style="margin-top:0px;margin-left:0px;width:208px;" id="reset_password_email_addy"/><br>\
					<div class="cdc_info_container" id="reset_password_insct" style="left:255px;top:20px;">\
						<div class="cdc_arrow" style="margin-top:11px;"></div>\
						<span class="cdc_info_text">Give us your email address and we\'ll send you instructions for resetting your password.</span>\
					</div>\
			    </div>\
				<div class="cloud_deploy_button_container_singlepage">\
					<span class="cloud_deploy_button" id="cloud_reset_password_submit" style="width:120px;text-align:center;">'+$.i18n.prop('password.dialog.1')+'</span>\
					<span class="cloud_deploy_button" id="cloud_reset_password_cancel" style="float:left;">'+$.i18n.prop('password.dialog.3')+'</span>\
				</div>\
			</div>\
		';
		
		openBigDialog(235,500,20,20,235,500,html,true);
		
		$('#cloud_reset_password_cancel').bind('click',function(){
			// 1. Close Dialog
			closeBigDialog();
		});	

		$('#cloud_reset_password_submit').bind('click',function(){
			var button = $(this);
			
			button.text($.i18n.prop('password.dialog.6'));
			
			var post_data = {
				email : $('#reset_password_email_addy').val()
			}
			
			if(post_data.email != '' && isEmail(post_data.email)){
				$.ajax({ 
					url: "services/passwordReset/passwordReset", 
					type: "POST",
					cache: false,
					data : JSON.stringify(post_data),
					dataType: "json",
					processData : true,
					contentType: "application/json; charset=utf-8",
					success: function(resp){	
						$('#reset_password_email_addy').prop('disabled',true);
						$('#password_reset_title').css({color:'#CC121B'}).text($.i18n.prop('password.dialog.7'));
						$('#reset_password_insct .cdc_info_text').text($.i18n.prop('password.dialog.5'));
						button.hide().text($.i18n.prop('password.dialog.8'));
						$('#cloud_reset_password_cancel').text($.i18n.prop('password.dialog.9'));
					},
					error: function(){
						alert($.i18n.prop('password.dialog.15'));
						button.text($.i18n.prop('password.dialog.1'));
					}
				});	
			}else{
				alert($.i18n.prop('password.dialog.16'));
				button.text($.i18n.prop('password.dialog.1'));
			}

		});	

	});
	
	function notNull(v){
		return v && $.trim(v)!='';
	}
	
	$('#get-started-button-top').on('click',function(){
		var errorMSG = $.i18n.prop('login.8'), 
			displayError = 'none', 
			dialogHeight = 380,
			contentHeight = 260;

		var html = 	'\
			<div id="cloud_deploy">\
				<form>\
					<div class="cloud_deploy_header_container_singlepage" style="width:340px;">\
						<span style="color:#fff;">Register for Infor Business Cloud</span>\
					</div>\
					<div class="cloud_deploy_singlepage_content" style="height:'+contentHeight+'px;width:340px;">\
						<div class="cdc_input_desc_text" id="password_reset_title" style="line-height:30px;margin-bottom:0px;margin-top:0px;font-weight:bold;font-size:14px;">First Name</div>\
						<input type="text" value="" class="cdc_input" style="margin-top:0px;margin-left:0px;width:290px;" name="reg_first_name" id="reg_first_name"/><br>\
						<div class="cdc_input_desc_text" id="password_reset_title" style="line-height:30px;margin-bottom:0px;margin-top:0px;font-weight:bold;font-size:14px;">Last Name</div>\
						<input type="text" value="" class="cdc_input" style="margin-top:0px;margin-left:0px;width:290px;" name="reg_last_name" id="reg_last_name"/><br>\
						<div class="cdc_input_desc_text" id="password_reset_title" style="line-height:30px;margin-bottom:0px;margin-top:0px;font-weight:bold;font-size:14px;">Company</div>\
						<input type="text" value="" class="cdc_input" style="margin-top:0px;margin-left:0px;width:290px;" name="reg_company" id="reg_company"/><br>\
						<div class="cdc_input_desc_text" id="password_reset_title" style="line-height:30px;margin-bottom:0px;margin-top:0px;font-weight:bold;font-size:14px;">Email</div>\
						<input type="text" value="" class="cdc_input" style="margin-top:0px;margin-left:0px;width:290px;" name="reg_email" id="reg_email"/><br>\
					</div>\
					<div class="cloud_deploy_button_container_singlepage"  style="width:340px;">\
						<input type="button" value="Register" class="cloud_deploy_button" id="register_button" style="width:100px;height:33px;padding:0;line-height:33px;text-align:center;" />\
						<span class="cloud_deploy_button" id="cloud_register_cancel" style="float:left;">'+$.i18n.prop('login.6')+'</span>\
					</div>\
				</form>\
			</div>\
		';
		
		openBigDialog(dialogHeight,380,20,20,dialogHeight,380,html,true);		
		
		$('#reg_first_name').focus();
		
		$('#cloud_register_cancel').bind('click',function(){
			// 1. Close Dialog
			closeBigDialog();
		});		

	
		$('#register_button').live('click', function(){			
			var button = $(this);

			var post_data = { 
				firstName 	: $('#reg_first_name').val(),
				lastName 	: $('#reg_last_name').val(),
				company 	: $('#reg_company').val(),
				email 		: $('#reg_email').val(),
				language	: 'en_US'
			}

			if(notNull(post_data.firstName) && notNull(post_data.lastName) && notNull(post_data.company) && notNull(post_data.email)){
				// Hide existing dialogs
				closeBigDialog();
				
				var html = 	'\
					<div id="cloud_deploy">\
						<div class="cloud_deploy_header_container_singlepage" id="registration_confirmation_header">\
							<span style="color:#fff;">'+$.i18n.prop('reg.dialog.1')+'</span>\
						</div>\
						<div class="cloud_deploy_singlepage_content" style="height:165px;">\
							<div class="cdc_input_desc_text" style="line-height:30px;margin-bottom:12px;margin-top:0px;font-weight:bold;width:auto;font-size:12px;">Thank you for registering for an Infor Business Cloud account</div>\
							<div class="cdc_input_desc_text" style="margin-bottom:15px;margin-top:7px;width:auto;height:auto;font-size:12px;line-height:14px;">An email has been sent to the address you provided. Please open it and click the activation link to activate your Infor Business Cloud account.</div>\
							<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:0px;margin-left:30px;height:auto;font-size:12px;line-height:14px;"><span style="width:100px;float:left;">'+$.i18n.prop('reg.dialog.4')+':</span><span style="color:#5468DC;font-weight:bold;"  id="register_verify_name">'+$.i18n.prop('reg.dialog.9')+'</span></div>\
							<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:0px;margin-left:30px;height:auto;font-size:12px;line-height:14px;"><span style="width:100px;float:left;">'+$.i18n.prop('reg.dialog.5')+':</span><span style="color:#5468DC;font-weight:bold;" 	id="register_verify_company">'+$.i18n.prop('reg.dialog.10')+'</span></div>\
							<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:0px;margin-left:30px;height:auto;font-size:12px;line-height:14px;"><span style="width:100px;float:left;">'+$.i18n.prop('reg.dialog.6')+':</span><span style="color:#5468DC;font-weight:bold;"  id="register_verify_email">'+$.i18n.prop('reg.dialog.11')+'</span></div>\
						</div>\
						<div class="cloud_deploy_button_container_singlepage">\
							<span class="cloud_deploy_button" id="cloud_deployed_close" style="width:120px;text-align:center;">'+$.i18n.prop('reg.dialog.12')+'</span>\
						</div>\
					</div>\
				';
				
				openBigDialog(285,500,20,20,285,500,html,true);		
				
				$('#cloud_deployed_close').bind('click',function(){
					// 1. Close Dialog
					closeBigDialog();
					//window.location = './cloud.jsp#/about/complete_registration';
				});			
			
				$.ajax({ 
					url: "services/registration/register", 
					type: "POST",
					cache: false,
					data : JSON.stringify(post_data),
					dataType: "json",
					processData : true,
					contentType: "application/json; charset=utf-8",
					success: function(resp){
						var header_div = $('#registration_confirmation_header');
						var name_div = $('#register_verify_name');
						var comp_div = $('#register_verify_company');
						var email_div = $('#register_verify_email');
						
						header_div.html('<span style="color:#fff;">'+$.i18n.prop('reg.dialog.13')+'</span>');
						name_div.text(resp.firstName + ' ' + resp.lastName);
						comp_div.text(resp.company);
						email_div.text(resp.email);
						
						$('#reg_first_name').val('');
						$('#reg_last_name').val('');
						$('#reg_company').val('');
						$('#reg_email').val('');
					},
					error: function(){
						var header_div = $('#registration_confirmation_header');
						var name_div = $('#register_verify_name');
						var comp_div = $('#register_verify_company');
						var email_div = $('#register_verify_email');
						
						header_div.html('<span style="color:#fff;">'+$.i18n.prop('reg.dialog.12')+'</span>');
						name_div.text('');
						comp_div.text('');
						email_div.text('');
					}
				});				
			}else{
				$('#reg_first_name').focus();
				alert($.i18n.prop('reg.dialog.15'));
			}
		});		
	});
	
});
/*
function tumblr(resp) {
	var posts = resp.posts;
	$('#blog .loading').replaceWith('<div />');
	$ul = $('#blog div');
	for (var i=0; i<posts.length; i++) {
		var p = posts[i];
				var title = p['regular-title'] || p['link-text'] || null;
		var pBody = p['regular-body'];
		pBody = pBody.replace("<img","<input type=hidden");
		pBody = pBody.replace("src=","value=");
		var cad = new Date(p['date-gmt']);
		var postDate = (cad.getUTCMonth()+1) +'/'+cad.getUTCDate()+'/'+(cad.getFullYear()+'').substr(2,3);	
		title =postDate + ' - ' + title;
		if (title){
			$ul.append('<div class="post"><a href="'+p['url']+'" class="post-link" >'+title+'</a><div class="post-body">'+pBody+'</div></div>');
		}
	}
}	*/