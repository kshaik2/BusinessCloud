$(function(){
	Backbone.Collection.prototype.page = 0;
	Backbone.Collection.prototype.perPage = 20;

		//Links
	var userListLink = $('.user-list-link'),
		trialRequestLink = $('#trial-request-link'),
		userNameText = $('.user-name-text'),
		
		mainContainer = $('#main-app-container'),
		tileContainer = $('#tile-container'),
		
		//Apps
		allAppBoxes = $('.large-app-box,.small-app-box'),
		
		//Settings
		smallScreen = $('#small-screen-link'),
		largeScreen = $('#large-screen-link');	
	
	if(userNameText && userNameText.text() && userNameText.text().length > 20){
		var shorterUserName = (userNameText.text()).substr(0,18);
		userNameText.text(shorterUserName+'...');
	}	
	
	function slideTo(tempNum){
		$(window).scrollTop(0);
		var num = 3 + tempNum; // used because all numbers were 0, for home screen, now 3 is home screen. so use relative values
		var slideMultiplier = $('#main-app-container').hasClass('large-screen') ? 960 : 720;
		var slideNumberOptions = { 'left':(0-(slideMultiplier*num))+'px' };
		$('#content-slider')	.animate(slideNumberOptions, 250);
		$('#top-content-slider').animate(slideNumberOptions, 325);
	}
	
	function createMenuDropdown(e,el,opts,html,callback){
		e.stopPropagation();
		var offset = el.offset();
		var height = el.outerHeight();
		var zIndex = parseInt(el.css('z-index'));
		var menu = $('#dropdown-menu');
		var docBody = $('html');
		$('.dropdown-menu').remove();	
		docBody.off('click');		
		if(!menu.length || menu.attr('data-button-id')!=opts.id){
			$('body').append('<div id="dropdown-menu" class="dropdown-menu" data-button-id="'+opts.id+'"></div>');
			menu = $('#dropdown-menu');
			menu.css({top:(offset.top+height),left:offset.left,'z-index':(zIndex-1)}).html(html);

			docBody.on('click',function() {
				$('.dropdown-menu').remove();
				$(this).off('click');
				callback();
			});		
			menu.on('click',function(event){
				event.stopPropagation();
			});			
		}else{
			callback();
		}
	}	
	
	//  UserProduct  
	window.UserAWS = Backbone.Model.extend({
		defaults:{
			id: -1,
			name: "",			
			awsKey: "",
			secretKey: ""
		}	
	});

	// UserProduct Collection 
	window.UserAWSCollection = Backbone.Collection.extend({
		model : UserAWS,
		url: function(){
			var dr = new Date();
			return 'services/user/getAmazonCredentials?_='+dr.getTime();
		}
	});	
	
	// Get Instances of UserProduct Collection
	window.UserAWSList = new UserAWSCollection;		
	
	UserAWSList.fetch({
		success:function(){},
		error:function(collection,response){
			alert('Error getting AWS Credentials. Please refresh to try again.');
		}	
	});	
	
	function buildAWSCredList(){
		var aws_options_list = '';
		UserAWSList.each(function(awscred){
			aws_options_list += '<option value="'+awscred.get('id')+'">'+awscred.get('name')+'</option>';
		});	
		return	'	<select name="aws-credentials" class="ca_input_select" id="aws-credentials">\
						<option value="-1"></option>\
						'+aws_options_list+'\
					</select>';
	}
	
	$('#account-settings').on('click', function(event){
		
		var html = '\
			<div class="setting-menu-row" id="global-aws-credentials">AWS Credentials</div>\
			<div class="setting-menu-row" id="global-account-settings">Account Settings</div>\
			<div class="setting-menu-row" id="global-logout">Logout</div>\
		';
	
		// Update username style at top for dialog. Create callback to reset this style
		$(this).css({'border-color':'#777','background-color':'#777','color':'#fff'});
		function backToNormal(){
			$('#account-settings').css({'border-color':'transparent','background-color':'transparent','color':'#222'});
		}
	
		createMenuDropdown(event,$(this),{id:'account-settings-dropdown-menu'},html,backToNormal);
	
		// AWS Credentials
		$('#global-aws-credentials').on('click',function(){
			$('html').trigger('click');
		
			var html = 	'\
				<div id="cloud_admin">\
					<div id="cloud_admin_header"  style="width:480px;color:#33b5e5;">AWS Credentials</div>\
					<div id="cloud_admin_content" style="width:480px;height: 271px">\
						<div class="ca_col ca_col_one" style="height: 271px;">\
							<div class="ca_input_desc_text" style="text-align:center;font-size:12px;font-weight:bold;margin-bottom:15px;">Manage your AWS Credentials</div>\
							<div class="ca_input_desc_text" style="text-align:center;height:43px;position:relative;"><span class="cloud_admin_button" style="width:190px;margin-left:15px;height:25px;position:relative;text-align:center;margin:0;" id="create-new-aws-credential">Create New Credentials</span></div>\
							<div class="ca_input_desc_text" style="text-align:center;margin-bottom:17px;font-weight:bold;">or</div>\
							<div class="ca_input_desc_text" style="text-align:center;font-weight:bold;">View Existing Credentials</div>\
							<span id="aws-cred-list-select-container">\
								'+buildAWSCredList()+'\
							</span>\
							<div class="ca_input_desc_text" style="text-align:center;height:43px;padding-top:0px;padding-bottom:0px;"><span class="cloud_admin_button" style="margin:0 0 0 4px;width:auto;height:25px;text-align:center;float:right;" id="delete-aws-credential">Delete</span></div>\
						</div>\
						<div class="ca_col ca_col_two" style="border:0px solid #fff;width:222px;height: 271px;">\
							<div class="ca_input_desc_text" style="margin-top:5px;">AWS Credential Name<span class="required">*</span></div>\
							<input type="text" value="" id="aws-name" class="ca_input" />\
							<div class="ca_input_desc_text">AWS Key<span class="required">*</span></div>\
							<input type="text" value="" id="aws-key" class="ca_input" />\
							<div class="ca_input_desc_text">AWS Secret Key<span class="required">*</span></div>\
							<input type="text" value="" id="aws-secret-key" class="ca_input" />\
							<div class="ca_input_desc_text">\
								<span class="cloud_admin_button" id="aws-credentials-save" style="width:60px;width:auto;height:25px;text-align:center;float:right;margin:0 0 0 4px;">Save</span>\
								<span class="cloud_admin_button" id="aws-credentials-clear" style="width:60px;width:auto;height:25px;text-align:center;float:right;margin:0 0 0 0;">Clear</span>\
							</div>\
						</div>\
					</div>\
					<div id="cloud_admin_button_container"  style="width:480px;">\
						<span class="cloud_admin_button" id="aws-credentials-cancel" style="float:left;">Close</span>\
					</div>\
				</div>\
			';
			
			openBigDialog(390,500,20,20,390,500,html,true);
	
			$('#aws-credentials-cancel').on('click',function(){
				closeBigDialog();
			});		
			
			var awsName = $('#aws-name'),
				awsKey = $('#aws-key'),
				awsSecretKey = $('#aws-secret-key'),
				awsCredentialsClearButton = $('#aws-credentials-clear'),
				awsCredentialsSaveButton = $('#aws-credentials-save');			
			
			awsName.attr('disabled',true);
			awsKey.attr('disabled',true);
			awsSecretKey.attr('disabled',true);
			
			$('#create-new-aws-credential').on('click',function(){
				$('#aws-credentials').val(-1);
				awsName.attr('disabled',false).val('').focus();
				awsKey.attr('disabled',false).val('');
				awsSecretKey.attr('disabled',false).val('');
				awsCredentialsClearButton.show();
				awsCredentialsSaveButton.show();
			});
			
			$('#delete-aws-credential').on('click',function(){
				var currentId = $('#aws-credentials').val();
				var currentKey = UserAWSList.get(currentId);
				if(confirm('Are you sure you want to delete the following key: \n\n\t '+currentKey.get('name'))){
					$.ajax({ 
						url: "services/user/deleteAmazonCredentialsById", 
						type: "POST",
						cache: false,
						data : JSON.stringify({id:currentId}),
						dataType: "json",
						processData : true,
						contentType: "application/json; charset=utf-8",
						success: function(resp){
							UserAWSList.remove(currentId);
							$('#aws-cred-list-select-container').html(buildAWSCredList());
							$('#aws-credentials').val(-1);
							awsName.val('').attr('disabled',true);
							awsKey.val('').attr('disabled',true);
							awsSecretKey.val('').attr('disabled',true);	
							awsCredentialsClearButton.hide();
							awsCredentialsSaveButton.hide();
						},
						error: function(resp){					
							alert('Unable to delete AWS Credential.  Please try again');
						}
					});		
				}
			});				
			
			awsCredentialsClearButton.hide().on('click',function(){
				$('#aws-credentials').val(-1);
				awsName.val('').attr('disabled',true);
				awsKey.val('').attr('disabled',true);
				awsSecretKey.attr('disabled',true).val('');	
				awsCredentialsClearButton.hide();
				awsCredentialsSaveButton.hide();				
			});		

			awsCredentialsSaveButton.hide().on('click',function(){
				var post_data = {
					name: awsName.val() ,			
					awsKey: awsKey.val() ,
					secretKey: awsSecretKey.val()
				}	
				
				if($.trim(post_data.name) != '' && $.trim(post_data.awsKey) != '' && $.trim(post_data.secretKey) != '' ){
					$.ajax({ 
						url: "services/user/updateAmazonCredentials", 
						type: "POST",
						cache: false,
						data : JSON.stringify(post_data),
						dataType: "json",
						processData : true,
						contentType: "application/json; charset=utf-8",
						success: function(newAWS){
							UserAWSList.add(newAWS);
							$('#aws-cred-list-select-container').html(buildAWSCredList());
							$('#aws-credentials').val(-1);
							awsName.attr('disabled',true).val('');
							awsKey.attr('disabled',true).val('');
							awsSecretKey.attr('disabled',true).val('');	
							awsCredentialsClearButton.hide();
							awsCredentialsSaveButton.hide();
						},
						error: function(resp){					
							alert('Unable to save AWS Credential.  Please try again');
						}
					});		
				}else{
					alert('Please fill in all three fields.');
				}				
			});
	
			$('#aws-cred-list-select-container').on('change','#aws-credentials',function(){
				var currentId = $('#aws-credentials').val();
				if(currentId != -1){
					var currentKey = UserAWSList.get(currentId);
					awsName.attr('disabled',true).val(currentKey.get('name'));
					awsKey.attr('disabled',true).val(currentKey.get('awsKey'));
					awsSecretKey.attr('disabled',true).val(currentKey.get('secretKey'));	
					awsCredentialsClearButton.hide();
					awsCredentialsSaveButton.hide();
				}else{
					awsName.attr('disabled',true).val('');
					awsKey.attr('disabled',true).val('');
					awsSecretKey.attr('disabled',true).val('');
					awsCredentialsClearButton.hide();
					awsCredentialsSaveButton.hide();
				}
			});
		});
	
	
		// Account Settings
		$('#global-account-settings').on('click',function(){
			$('html').trigger('click');
			var button = $(this);
			var iam = button.attr('data-link-page');
			var myId = button.attr('id');
			var myEmail = $('#user-email-address').val();

			var html = 	'\
				<div id="cloud_admin">\
					<div id="cloud_admin_header" style="color:#33b5e5;">'+$.i18n.prop('super.4')+' <span class="admin_settings_title_name" style="color:#fff;">'+myEmail+'</span><span class="admin_settings_title_async_msg">'+$.i18n.prop('account.14')+'</span></div>\
					<div id="cloud_admin_content">\
						<div class="ca_col ca_col_one">\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.1')+'<span class="required">*</span></div>\
							<input type="email" value="'+myEmail+'" id="account_email" class="ca_input" disabled />\
							<div class="ca_input_desc_text"><span class="required">*</span><span style="float:left;">'+$.i18n.prop('account.2')+'</span><span id="change_password">'+$.i18n.prop('account.15')+'</span></div>\
							<input type="password" value="********************" id="password_once" class="ca_input" disabled />\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.3')+'<span class="required">*</span></div>\
							<input type="password" value="********************"  id="password_twice" class="ca_input" disabled />\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.4')+'<span class="required">*</span></div>\
							<select name="test" class="ca_input_select" id="account_language">\
								<option value="en_US">'+$.i18n.prop('account.16')+'</option>\
								<option value="pt_BR">'+$.i18n.prop('account.17')+'</option>\
							</select>\
						</div>\
						<div class="ca_col ca_col_two">\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.30')+'<span class="required">*</span></div>\
							<input type="text" value="" id="user_phone" class="ca_input" />\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.24')+'<span class="required">*</span></div>\
							<input type="text" value="" id="user_address_1" class="ca_input" />\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.25')+'<span class="required">*</span></div>\
							<input type="text" value="" id="user_address_2" class="ca_input" />\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.26')+'<span class="required">*</span></div>\
							<input type="text" value="" id="user_country" class="ca_input" />\
						</div>\
						<div class="ca_col ca_col_three">\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.6')+'<span class="required">*</span></div>\
							<input type="text" value="" id="company_name" class="ca_input" />\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.27')+'<span class="required">*</span></div>\
							<select name="test" class="ca_input_select" id="account_is_customer">\
								<option value="false">'+$.i18n.prop('account.29')+'</option>\
								<option value="true">'+$.i18n.prop('account.28')+'</option>\
							</select>\
						</div>\
					</div>\
					<div id="cloud_admin_button_container">\
						<span class="cloud_admin_button" id="cloud_admin_save" style="width:60px;text-align:center;">'+$.i18n.prop('account.12')+'</span>\
						<span class="cloud_admin_button" id="cloud_admin_cancel" style="float:left;">'+$.i18n.prop('account.13')+'</span>\
					</div>\
				</div>\
			';
			
			openBigDialog(450,755,20,20,450,755,html,true);
			
			$('#change_password').on('click',function(){
				$('#password_once').prop('disabled',false).val('').focus();
				$('#password_twice').prop('disabled',false).val('');
			});
			
			$('#cloud_admin_cancel').on('click',function(){
				// 1. Close Dialog
				closeBigDialog();
			});				
			
			$('#cloud_admin_save').on('click',function(){
				// Update settings
				var button = $(this);
				button.text($.i18n.prop('account.18'));
				
				var post_data = {
					language	 		: $('#account_language').val(),
					companyName 	 	: $('#company_name').val(),
					inforCustomer 	 	: $('#account_is_customer').val(),
					address1 	 		: $('#user_address_1').val(),
					address2 	 		: $('#user_address_2').val(),
					country 	 		: $('#user_country').val(),
					phone				: $('#user_phone').val()
				}			
				
				var validatedPass = true;
				if(!$('#password_once').prop('disabled')){
					post_data.password = $('#password_once').val();
					post_data.password2 = $('#password_twice').val();		
					validated = (post_data.password === post_data.password2 && post_data.password.length >= 6);				
				}
				
				var validatedRequired = $.trim($('#company_name').val()) && $.trim($('#user_address_1').val()) && $.trim($('#user_address_2').val()) && 
										$.trim($('#user_country').val()) && $.trim($('#user_phone').val());			
				
				// Validation
				if(validatedPass && validatedRequired){
					$.ajax({ 
						url: "services/user/settings", 
						type: "POST",
						cache: false,
						data : JSON.stringify(post_data),
						dataType: "json",
						processData : true,
						contentType: "application/json; charset=utf-8",
						success: function(resp){
						
							button.text($.i18n.prop('account.12'));
							$('#cloud_admin_cancel').text($.i18n.prop('account.19'));
							
							$('#password_once').val('********************').prop('disabled',true);
							$('#password_twice').val('********************').prop('disabled',true);						
							$('#account_language').val(resp.language);
							$('#company_name').val(resp.companyName);
							
							$('#account_is_customer').val(resp.inforCustomer+'');
							$('#user_address_1').val(resp.address1);
							$('#user_address_2').val(resp.address2);
							$('#user_country').val(resp.country);	
							$('#user_phone').val(resp.phone);						

							$('.admin_settings_title_async_msg').show();
							setTimeout("$('.admin_settings_title_async_msg').hide();",5000);
						},
						error: function(resp){					
							button.text($.i18n.prop('account.12'));
							alert($.i18n.prop('account.20'));
						}
					});
				}else{
					button.text($.i18n.prop('account.12'));
					if(!validatedRequired){
						alert($.i18n.prop('account.31'));
					}else{
						alert($.i18n.prop('account.20'));
					}
				}
				
			}).text($.i18n.prop('account.22'));			
			
			$.ajax({ 
				url: "services/user/settings", 
				type: "GET",
				cache: false,
				dataType: "json",
				processData : true,
				contentType: "application/json; charset=utf-8",
				success: function(resp){
					$('#cloud_admin_save').text('Save');
					$('#account_language').val(resp.language);
					$('#company_name').val(resp.companyName);
					$('#account_is_customer').val(resp.inforCustomer+'');
					$('#user_address_1').val(resp.address1);
					$('#user_address_2').val(resp.address2);
					$('#user_country').val(resp.country);	
					$('#user_phone').val(resp.phone);					
				},
				error: function(){
					alert($.i18n.prop('account.23'));
				}
			});					
			
		});		
		
		// Logout
		$('#global-logout').on('click',function(){
			window.location = './j_spring_security_logout';
		});			
		
	});
	
	
	$('#create-user-button').on('click',function(){
		$('html').trigger('click');
		var button = $(this);
		var html = 	'\
			<div id="cloud_admin">\
				<div id="cloud_admin_header" style="color:#fff;">Create User<span class="admin_settings_title_async_msg">User Saved</span></div>\
				<div id="cloud_admin_content">\
					<div class="ca_col ca_col_one">\
						<div class="ca_input_desc_text"><span class="required">*</span><span style="float:left;">First Name</span></div>\
						<input type="password" value="" id="first_name" class="ca_input"  />\
						<div class="ca_input_desc_text">Last Name<span class="required">*</span></div>\
						<input type="password" value="" id="last_name" class="ca_input"  />\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.1')+'<span class="required">*</span></div>\
						<input type="email" value="" id="account_email" class="ca_input"  />\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.4')+'<span class="required">*</span></div>\
						<select name="test" class="ca_input_select" id="account_language">\
							<option value="en_US">'+$.i18n.prop('account.16')+'</option>\
							<option value="pt_BR">'+$.i18n.prop('account.17')+'</option>\
						</select>\
					</div>\
					<div class="ca_col ca_col_two">\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.30')+'<span class="required">*</span></div>\
						<input type="text" value="" id="user_phone" class="ca_input" />\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.24')+'<span class="required">*</span></div>\
						<input type="text" value="" id="user_address_1" class="ca_input" />\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.25')+'<span class="required">*</span></div>\
						<input type="text" value="" id="user_address_2" class="ca_input" />\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.26')+'<span class="required">*</span></div>\
						<input type="text" value="" id="user_country" class="ca_input" />\
					</div>\
					<div class="ca_col ca_col_three">\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.6')+'<span class="required">*</span></div>\
						<input type="text" value="" id="company_name" class="ca_input" />\
						<div class="ca_input_desc_text">Company ID</div>\
						<input type="text" value="" id="company_id" class="ca_input" />\
						<div class="ca_input_desc_text">Infor Customer<span class="required">*</span></div>\
						<select name="test" class="ca_input_select" id="account_is_customer">\
							<option value="false" selected >No</option>\
							<option value="true">Yes</option>\
						</select>\
						<span id="infor_id_input_container" style="display:none;">\
							<div class="ca_input_desc_text">Infor ID<span class="required">*</span></div>\
							<input type="text" value="" id="infor_id" class="ca_input" />\
						<span>\
					</div>\
				</div>\
				<div id="cloud_admin_button_container">\
					<span class="cloud_admin_button" id="cloud_admin_save" style="width:60px;text-align:center;">'+$.i18n.prop('account.12')+'</span>\
					<span class="cloud_admin_button" id="cloud_admin_cancel" style="float:left;">'+$.i18n.prop('account.13')+'</span>\
				</div>\
			</div>\
		';
		
		openBigDialog(450,755,20,20,450,755,html,true);		
		
		$('#cloud_admin_cancel').on('click',function(){
			// 1. Close Dialog
			closeBigDialog();
		});			
		
		$('#account_is_customer').on('change',function(){
			var val = $(this).val();
			var icc = $('#infor_id_input_container');
			if(val === 'true'){
				icc.show();
			}else{
				icc.hide();
			}
		});
		
		$('#cloud_admin_save').on('click',function(){
			// Update settings
			var button = $(this);
			button.text($.i18n.prop('account.18'));
			
			var post_data = {
				firstName			: $('#first_name').val(),
				lastName			: $('#last_name').val(),
				email				: $('#account_email').val(),
				language	 		: $('#account_language').val(),
				address1 	 		: $('#user_address_1').val(),
				address2 	 		: $('#user_address_2').val(),
				country 	 		: $('#user_country').val(),
				phone				: $('#user_phone').val(),				
				companyName 	 	: $('#company_name').val(),
				companyId			: $('#company_id').val(),
				inforCustomer 	 	: $('#account_is_customer').val(),
				inforId				: $('#infor_id').val()
			}			
			
			var validatedRequired = $.trim($('#company_name').val()) && $.trim($('#user_address_1').val()) && $.trim($('#user_address_2').val()) && 
									$.trim($('#user_country').val()) && $.trim($('#user_phone').val());			
			
			// Validation
			if(validatedRequired){
				$.ajax({ 
					url: "services/user/settings", 
					type: "POST",
					cache: false,
					data : JSON.stringify(post_data),
					dataType: "json",
					processData : true,
					contentType: "application/json; charset=utf-8",
					success: function(resp){
					
						button.text('Save');
						$('#cloud_admin_cancel').text($.i18n.prop('account.19'));
						
						$('#first_name').val(resp.firstName);//new
						$('#last_name').val(resp.lastName);//new		
						$('#account_email').val(resp.email);//new
						$('#account_language').val(resp.language);
						
						$('#user_address_1').val(resp.address1);
						$('#user_address_2').val(resp.address2);
						$('#user_country').val(resp.country);	
						$('#user_phone').val(resp.phone);							
						
						$('#company_name').val(resp.companyName);
						$('#company_id').val(resp.companyName);//new
						$('#account_is_customer').val(resp.inforCustomer+'');
						$('#infor_id').val(resp.companyName);//new
					

						$('.admin_settings_title_async_msg').show();
						setTimeout("$('.admin_settings_title_async_msg').hide();",5000);
					},
					error: function(resp){					
						button.text($.i18n.prop('account.12'));
						alert($.i18n.prop('account.20'));
					}
				});
			}else{
				button.text($.i18n.prop('account.12'));
				if(!validatedRequired){
					alert($.i18n.prop('account.31'));
				}else{
					alert($.i18n.prop('account.20'));
				}
			}
			
		}).text('Save');			
		
	});
	
	
	
	var R = Backbone.Router.extend({
		routes: {
			""	    			:	"home"	,
			"trialRequests/"	:	"trialRequest" 
		},
		
		home: function(){
			slideTo(0);
		},
		trialRequest: function(){
			slideTo(1);
		}
	});
	
	// Init routing bindings created above
	var app_router = new R;
	Backbone.history.start();
	
	// MENU LINKS ///////////////////////////////////////////////////////////////////////////////////
	userListLink.on('click',function(){
		slideTo(0);
		$('#top-slide-1 .top-content-main .header-text').html('User List');	
		app_router.navigate("",false);
	});
	
	trialRequestLink.on('click',function(){
		slideTo(1);
		$('#top-slide-2 .top-content-main .header-text').html('Trial Requests');	
		app_router.navigate("trialRequests/",false);
	});
	
	function refreshTrialRequestView(collection,options){
		var pageNum = options.pageNum ? options.pageNum : 0;
		var numPerPage = options.perPage ? options.perPage : collection.perPage;
		var numPages = Math.ceil(collection.length / numPerPage);	
		var startIndex = pageNum * numPerPage;
		var endIndex = (pageNum + 1) * numPerPage;
		endIndex = endIndex > collection.length ? collection.length : endIndex;
		collection.page = pageNum;	
	
		var con = $('#trial-requests-details-body');
		// Clear existing list of products
		con.html('');
		$('.trial-requests-header-page-text')	.text( 'Page '+(pageNum+1)+' of '+numPages);
		$('#trial-requests-footer-num-details')	.text( 'Showing Trial Requests '+(startIndex+1)+'-'+endIndex+' of '+collection.length);	
		if(collection.length == 0){
			$('.trial-requests-header-page-text')	.text('');
			$('#trial-requests-footer-num-details')	.text('No Trial Requests Available');			
		}
		var tempStr = '';
		// Create user rows by adding to HTML string
		for(var i = startIndex;i<endIndex;i++){
			var request = collection.at(i);
			var cad = new Date(parseInt(request.get('createdAt')));
			var createdAt = (cad.getUTCMonth()+1) +'/'+cad.getUTCDate()+'/'+(cad.getFullYear()+'').substr(2,3);
			var myId = 'user_row_'+i;
			tempStr += '<div class="ul_row_request">\
							<div class="ul_row_col_full">\
								<span class="row_username" data-user-id="'+request.get('user').id+'" style="float:left;width:200px;overflow:hidden;">'+request.get('user').firstName+' '+request.get('user').lastName+'</span>\
								<span style="float:left;width:80px;overflow:hidden;">'+createdAt+'</span>\
								<span style="float:left;width:250px;overflow:hidden;">'+request.get('region').shortName+'-'+request.get('product').shortName+'</span>\
								<span style="float:left;width:200px;overflow:hidden;">'+request.get('user').companyName+'</span>\
								<span class="approve-request" data-request-id="'+request.get('id')+'" data-request-key="'+request.get('requestKey')+'" style="float:right;width:auto;text-align:right;height:20px;line-height:20px;padding:5px;cursor:pointer;background-color:#44cc00;color:#fff;margin-left:5px;">Approve</span>\
								<span class="decline-request" data-request-id="'+request.get('id')+'" data-request-key="'+request.get('requestKey')+'" style="float:right;width:auto;text-align:right;height:20px;line-height:20px;padding:5px;cursor:pointer;background-color:#ff2200;color:#fff;">Decline</span>\
							</div>\
						</div>';	
		};	
		// Appending a long string is much faster than appending individual rows
		con.append(tempStr);	
	}

	var file_search_box_text = 'Search Names';
	$('#trial-requests-search-box').val(file_search_box_text)
	  .on('focus',function(){
		var sb = $(this);
		if($.trim(sb.val())==file_search_box_text){
			sb.val('').css({'color':'#545454'});
		}
	}).on('blur',function(){
		var sb = $(this);
		if(!$.trim(sb.val())){
			sb.val(file_search_box_text).css({'color':'#cbcbcb'});
		}
	}).on('clear',function(){
		var sb = $(this);
		sb.val(file_search_box_text).css({'color':'#cbcbcb'});
		TrialRequestsList = TrialRequests;
		var options = {
			pageNum : 0,
			perPage : TrialRequests.perPage
		}				
		refreshTrialRequestView(TrialRequestsList,options);		
	}).autocomplete({
		minLength: 0,
		delay: 150,
		source: function( request, response ) {
			TrialRequestsList = searchUsersByName(request.term,TrialRequests);
			var options = {
				pageNum : 0,
				perPage : TrialRequests.perPage
			}				
			refreshTrialRequestView(TrialRequestsList,options);
		},
		focus: function() {
			// prevent value inserted on focus
			return false;
		}
	});		
	
	$('#trial-requests-clear-search-button').on('click',function(){
		$('#trial-requests-search-box').trigger('clear');
	});	
	
	$('#admin_user_trial_requests_container').on('click','.trialRequestsNext',function(){
		var nextPageNum = TrialRequestsList.page + 1;	
		var numPages = Math.ceil(TrialRequests.length / TrialRequests.perPage);
		if(nextPageNum < numPages){
			var options = {
				pageNum : nextPageNum,
				perPage : TrialRequests.perPage
			}				
			refreshTrialRequestView(TrialRequestsList,options);
		}
	});
	
	$('#admin_user_trial_requests_container').on('click','.trialRequestsPrevious',function(){
		var previousPageNum = TrialRequestsList.page - 1;	
		var numPages = Math.ceil(TrialRequests.length / TrialRequests.perPage);
		if(previousPageNum >= 0){
			var options = {
				pageNum : previousPageNum,
				perPage : TrialRequests.perPage
			}				
			refreshTrialRequestView(TrialRequestsList,options);
		}
	});		
	
	$('#admin_user_trial_requests_container').on('click','.approve-request',function(){
		var button = $(this);
		var requestId = button.attr('data-request-id');
		var requestKey = button.attr('data-request-key');
		
		$.ajax({ 
			url: "services/trialService/approveRequest/"+requestKey, 
			type: "GET",
			cache: true,
			data : null,
			dataType: "text",
			processData : true,
			success: function(resp){
				TrialRequests.remove(requestId);
				TrialRequestsList.remove(requestId);
				var options = {
					pageNum : TrialRequestsList.page,
					perPage : TrialRequestsList.perPage
				}				
				refreshTrialRequestView(TrialRequestsList,options);
			},
			error: function(resp){
				alert('Approve did not work, try again.');
			}
		});		
	});	
	
	$('#admin_user_trial_requests_container').on('click','.decline-request',function(){
		var button = $(this);
		var requestId = button.attr('data-request-id');
		var requestKey = button.attr('data-request-key');
		
		$.ajax({ 
			url: "services/trialService/deleteRequest/"+requestKey, 
			type: "GET",
			cache: true,
			data : null,
			dataType: "text",
			processData : true,
			success: function(resp){
				TrialRequests.remove(requestId);
				TrialRequestsList.remove(requestId);
				var options = {
					pageNum : TrialRequestsList.page,
					perPage : TrialRequestsList.perPage
				}				
				refreshTrialRequestView(TrialRequestsList,options);
			},
			error: function(resp){
				alert('Decline did not work, try again.');
			}
		});	
	});		
	
	function createProductSecurityRow(aup,userId){
		var product = Products.get(aup.productId);
		var toggleTrialClass = product.get('availability').trial ? 'toggle_trial' : '';
		var toggleDeploymentClass = product.get('availability').deployment ? 'toggle_aws' : '';
		return '<div class="admin-info-row">\
					<span class="admin-info-row-key" style="width:240px;">'+aup.productShortName+'</span>\
					<span class="admin-info-row-value">\
						<span class="'+toggleTrialClass+' '+getButtonStyleByStatus(aup.trialAvailable, product.get('availability').trial )+'" data-user-id="'+userId+'" data-product-id="'+aup.productId+'">Trial</span>\
						<span class="'+toggleDeploymentClass+' '+getButtonStyleByStatus(aup.launchAvailable, product.get('availability').deployment )+'" 	data-user-id="'+userId+'" data-product-id="'+aup.productId+'">AWS</span>\
					</span>\
				</div>';
	}
	
	function createProductDeployRow(aup,userId){
		var product = Products.get(aup.productId);
		if(product.get('availability').trial && product.get('availability').deployment){
			var toggleTrialClass = product.get('availability').trial ? 'toggle_trial' : '';
			var toggleDeploymentClass = product.get('availability').deployment ? 'toggle_aws' : '';
			return '<div class="admin-info-row">\
						<span class="admin-info-row-key" style="width:240px;">'+aup.productShortName+'</span>\
						<span class="admin-info-row-value" style="width:120px;">\
							<span class=" allow_button" data-user-id="'+userId+'" data-product-id="'+aup.productId+'">Trial</span>\
							<span class=" allow_button" 	data-user-id="'+userId+'" data-product-id="'+aup.productId+'">AWS</span>\
						</span>\
					</div>';
		}else{
			return '';
		}
	}	
	
	$('#content-slider').on('click','.row_username',function(){
		var userId = $(this).attr('data-user-id');
		var up = AdminUserProducts.get(userId);
		var createdDate =((new Date(parseInt(up.get('user').createdAt))).toString());
		var createdDateString = createdDate.substring(0,createdDate.indexOf('(',0));
		var lastLoginDate =((new Date(parseInt(up.get('loginAgg').lastLogin))).toString());
		var lastLoginDateString = lastLoginDate.substring(0,lastLoginDate.indexOf('(',0));	
		var inforCustomer = up.get('user').inforCustomer ? 'Yes' : 'No';
		var appSecString = '';var appDeployString = '';
		$.each(up.get('adminUserProducts'),function(i,aup){
			appSecString += createProductSecurityRow(aup,userId);
		});
		$.each(up.get('adminUserProducts'),function(i,aup){
			appDeployString += createProductDeployRow(aup,userId);
		});		
		
		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage">\
					<span style="color: #fff;font-size:18px;font-weight: bold;">'+up.get('user').firstName+' '+up.get('user').lastName+'</span>\
				</div>\
				<div id="admin-tabs" style="width: 460px;height:30px;padding: 10px 20px 0px 20px;overflow: hidden;float: left;position: relative;">\
					<div id="user-information-tab" class="admin-pop-up-tab open-tab">Info</div>\
					<div id="user-app-security-tab" class="admin-pop-up-tab">Security</div>\
					<div id="user-environments-tab" class="admin-pop-up-tab">Environments</div>\
					<div id="user-deploy-tab" class="admin-pop-up-tab">Launch</div>\
				</div>\
				<div id="admin-content" class="cloud_deploy_singlepage_content">\
					<div id="user-information-content" class="admin-pop-up-content">\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">email</span>\
							<span class="admin-info-row-value">'+up.get('user').username+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">phone</span>\
							<span class="admin-info-row-value">'+up.get('user').phone+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">company</span>\
							<span class="admin-info-row-value">'+up.get('user').companyName+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">address 1</span>\
							<span class="admin-info-row-value">'+up.get('user').address1+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">address 2</span>\
							<span class="admin-info-row-value">'+up.get('user').address2+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">country</span>\
							<span class="admin-info-row-value">'+up.get('user').country+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">infor customer</span>\
							<span class="admin-info-row-value">'+inforCustomer+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">account created</span>\
							<span class="admin-info-row-value">'+createdDateString+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">total logins</span>\
							<span class="admin-info-row-value">'+up.get('loginAgg').loginCnt+'</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key">last login</span>\
							<span class="admin-info-row-value">'+lastLoginDateString+'</span>\
						</div>\
					</div>\
					<div id="user-app-security-content" class="admin-pop-up-content" style="display:none;">'+appSecString+'</div>\
					<div id="user-environments-content" class="admin-pop-up-content" style="display:none;">\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:240px;">\
								<span class="allow_button" data-user-id="1010" data-product-id="1001">Trial</span> EAM \
							</span>\
							<span class="admin-info-row-value" style="width:120px;font-weight:bold;">5/12/12</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:390px;color:#555;font-weight:normal;font-size:10px;padding-left:15px;border-right:0px;">\
								 <strong>logins:</strong> 25 | <strong>last login:</strong> 5/12/12 @ 5:45pm | <strong>expires:</strong> 6/11/12  \
							</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:390px;color:#555;font-weight:normal;font-size:10px;padding-left:15px;border-right:0px;">\
								 <strong>days left:</strong> 28 | <strong>last contacted:</strong> 5/12/12 | <strong>Phone:</strong> 401-552-6985\
							</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:290px;color:#555;font-weight:normal;font-size:10px;padding-left:15px;border-right:0px;">\
								 <strong>url:</strong> http://EAM.12.infor24.infor.com/login.user.jsp \
							</span>\
							<span class="admin-info-row-value" style="width:85px;float:right;">\
								<span class="allow_button" style="margin-right:3px;display:none;">Go</span>\
								<span class="disable_button" style="display:none;" >End</span>\
							</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:240px;">\
								<span class="allow_button" data-user-id="1010" data-product-id="1001">Trial</span> Infor10 SoftBrands HMS \
							</span>\
							<span class="admin-info-row-value" style="width:120px;font-weight:bold;">4/07/12</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:390px;color:#555;font-weight:normal;font-size:10px;padding-left:15px;border-right:0px;">\
								 <strong>logins:</strong> 19 | <strong>last login:</strong> 4/07/12 @ 2:36pm | <strong>expires:</strong> 5/08/12  \
							</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:390px;color:#555;font-weight:normal;font-size:10px;padding-left:15px;border-right:0px;">\
								 <strong>days left:</strong> 8 | <strong>last contacted:</strong> 4/07/12 | <strong>Phone:</strong> 401-552-6985\
							</span>\
						</div>\
						<div class="admin-info-row">\
							<span class="admin-info-row-key" style="width:290px;color:#555;font-weight:normal;font-size:10px;padding-left:15px;border-right:0px;">\
								 <strong>url:</strong> http://hms18.infor24.infor.com/login.user.jsp \
							</span>\
							<span class="admin-info-row-value" style="width:85px;float:right;">\
								<span class="allow_button" style="margin-right:3px;display:none;">Go</span>\
								<span class="disable_button" style="display:none;" >End</span>\
							</span>\
						</div>\
					</div>\
					<div id="user-deploy-content" class="admin-pop-up-content" style="display:none;">'+appDeployString+'</div>\
			    </div>\
				<div class="cloud_deploy_button_container_singlepage">\
					<span class="cloud_deploy_button" id="cloud_deploy_single_cancel" style="float:right;">Close</span>\
				</div>\
			</div>\
		';
		
		openBigDialog(490,500,20,20,490,500,html,true);
		
		$('#cloud_deploy_single_cancel').on('click',function(){
			// 1. Close Dialog
			closeBigDialog();
		});	
				
		$('#admin-tabs').on('click','#user-information-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-information-content').show();
		});
		
		$('#admin-tabs').on('click','#user-app-security-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-app-security-content').show();
		});		
		
		$('#admin-tabs').on('click','#user-environments-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-environments-content').show();
		});	

		$('#admin-tabs').on('click','#user-deploy-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-deploy-content').show();
		});	
		
	});
	
	$('#admin_user_product_data_container').on('click','.adminProductNext',function(){
		var nextPageNum = AdminUserProductsList.page + 1;	
		var numPages = Math.ceil(AdminUserProducts.length / AdminUserProducts.perPage);
		if(nextPageNum < numPages){
			var options = {
				pageNum : nextPageNum,
				perPage : AdminUserProducts.perPage
			}				
			refreshAdminUserProductsView(AdminUserProductsList,options);
		}
	});
	
	$('#admin_user_product_data_container').on('click','.adminProductPrevious',function(){
		var previousPageNum = AdminUserProductsList.page - 1;	
		var numPages = Math.ceil(AdminUserProducts.length / AdminUserProducts.perPage);
		if(previousPageNum >= 0){
			var options = {
				pageNum : previousPageNum,
				perPage : AdminUserProducts.perPage
			}				
			refreshAdminUserProductsView(AdminUserProductsList,options);
		}
	});
	
	function sortCollection(collection){
		//TODO: Implement Sort
	}
	
	function searchCollection(collection){
		//TODO: Implement Search
	}	
	
	function getRoleSelector(userproduct){
		var html = '<select class="role_selector" id="rs_'+userproduct.get("user").id+'" data-user-id="'+userproduct.get("user").id+'" >\
						<option value="ROLE_USER" '+(userproduct.get("role")=='ROLE_USER'? 'selected' : '')+'>Customer</option>\
						<option value="ROLE_SALES_REP" '+(userproduct.get("role")=='ROLE_SALES_REP'? 'selected' : '')+'>Sales Rep</option>\
						<option value="ROLE_BDR_ADMIN" '+(userproduct.get("role")=='ROLE_BDR_ADMIN'? 'selected' : '')+'>Sales BDR</option>\
						<option value="ROLE_SALES_OPS" '+(userproduct.get("role")=='ROLE_SALES_OPS'? 'selected' : '')+'>Sales Opts</option>\
						<option value="ROLE_ADMIN" '+(userproduct.get("role")=='ROLE_ADMIN'? 'selected' : '')+'>BC Admin</option>\
					</select>';
		return html;
	}
	
	function refreshAdminUserProductsView(collection,options){
		var pageNum = options.pageNum ? options.pageNum : 0;
		var numPerPage = options.perPage ? options.perPage : collection.perPage;
		var numPages = Math.ceil(collection.length / numPerPage);	
		var startIndex = pageNum * numPerPage;
		var endIndex = (pageNum + 1) * numPerPage;
		endIndex = endIndex > collection.length ? collection.length : endIndex;
		collection.page = pageNum;

		var con = $('#user-list-details-body');
		// Clear existing list of products
		con.html('');
		$('.user-list-header-page-text')	.text( 'Page '+(pageNum+1)+' of '+numPages);
		$('#user-list-footer-num-details')	.text( 'Showing Users '+(startIndex+1)+'-'+endIndex+' of '+collection.length);
		if(collection.length == 0){
			$('.user-list-header-page-text')	.text('');
			$('#user-list-footer-num-details')	.text('No Users Available');			
		}
		
		var tempStr = '';
		// Create user rows by adding to HTML string
		var isBDR = $('#bdr').text() == 'true';
		for(var i = startIndex;i<endIndex;i++){
			var userproduct = collection.at(i);
			var myId = 'user_row_'+i;
			var lastLoginDate =((new Date(parseInt(userproduct.get('loginAgg').lastLogin))).toString());
			var lastLoginDateString = lastLoginDate.substring(0,lastLoginDate.indexOf('(',0));
			var loginCount = userproduct.get('loginAgg').loginCnt;	
			var roleSelector = isBDR ? '' : getRoleSelector(userproduct);
			if(isBDR && (userproduct.get("role")=='ROLE_BDR_ADMIN' || userproduct.get("role")=='ROLE_ADMIN')){
				// BDRs can't see other BDRS or CSAdmins
			}else{
				tempStr += '<div id="'+myId+'" class="ul_row" data-user-id="'+userproduct.get("id")+'">\
								<div class="ul_row_col_1"><span style="float:left;">'+roleSelector+'</span><span class="row_username" style="width:240px;" data-user-id="'+userproduct.get('id')+'">'+userproduct.get("user").firstName+' '+userproduct.get("user").lastName+'</span></div>\
								<div class="ul_row_col_2">\
									'+lastLoginDateString+'\
								</div>\
								<div class="ul_row_col_3">\
									'+loginCount+'\
								</div>\
								<div class="ul_row_col_4">\</div>\
								<div class="ul_row_col_5"><span class="toggle_account '+getAccountButtonStyleByStatus(userproduct.get("user").active)+'"  data-user-id="'+userproduct.get("id")+'">'+getAccountButtonTextByStatus(userproduct.get("user").active)+'</span></div>\
							</div>';	
			}
		};	
		// Appending a long string is much faster than appending individual rows
		con.append(tempStr);										
	}
		
	function searchUsersByName(val,collection){
		var searchPattern = new RegExp(val,"i");
		var filteredList = new collection.constructor(collection.filter(function(elem){
			var userName = elem.get("user").firstName + ' ' + elem.get("user").lastName;
			return searchPattern.test(userName);
		}));
		return filteredList;
	}	
	

	$('#user-list-search-box').val(file_search_box_text)
	  .on('focus',function(){
		var sb = $(this);
		if($.trim(sb.val())==file_search_box_text){
			sb.val('').css({'color':'#545454'});
		}
	}).on('blur',function(){
		var sb = $(this);
		if(!$.trim(sb.val())){
			sb.val(file_search_box_text).css({'color':'#cbcbcb'});
		}
	}).on('clear',function(){
		var sb = $(this);
		sb.val(file_search_box_text).css({'color':'#cbcbcb'});
		AdminUserProductsList = AdminUserProducts;
		var options = {
			pageNum : 0,
			perPage : AdminUserProducts.perPage
		}				
		refreshAdminUserProductsView(AdminUserProductsList,options);		
	}).autocomplete({
		minLength: 0,
		delay: 150,
		source: function( request, response ) {
			AdminUserProductsList = searchUsersByName(request.term,AdminUserProducts);
			var options = {
				pageNum : 0,
				perPage : AdminUserProducts.perPage
			}				
			refreshAdminUserProductsView(AdminUserProductsList,options);
		},
		focus: function() {
			// prevent value inserted on focus
			return false;
		}
	});		
	
	$('#user-list-clear-search-button').on('click',function(){
		$('#user-list-search-box').trigger('clear');
	});
		
	function getButtonStyleByStatus(security,available){
		if(available){
			if(security){
				return 'allow_button';
			}else{
				return 'disable_button';
			}
		}else{
			return 'disallowed_button';
		}
		
	}		
	function getAccountButtonTextByStatus(bool){if(bool){return 'Active';}else{return 'Disabled';}}	
	function getAccountButtonStyleByStatus(bool){if(bool){return 'allow_button';}else{return 'disable_button';}}	

	$('.toggle_trial').live('click',function(){
		var button = $(this);
		var allowTrial = button.hasClass('allow_button');
		
		var post_data = {
			userId:button.attr('data-user-id'),
			productId:button.attr('data-product-id'),
			type:'trialType',
			active: (!allowTrial)
		}
		
		$.ajax({ 
			url: "services/admin/updateUserProduct", 
			type: "POST",
			cache: false,
			data : JSON.stringify(post_data),
			dataType: "json",
			processData : true,
			contentType: "application/json; charset=utf-8",
			success: function(resp){
				var up = AdminUserProducts.get(resp.userId);
				var aups = up.get('adminUserProducts');
				var aup = _.find(aups,function(p,i){return p.productId == resp.productId;});
				aup.trialAvailable = resp.active;
				// Update button style/text
				if(!resp.active)
					button.removeClass('allow_button').addClass('disable_button');
				else
					button.removeClass('disable_button').addClass('allow_button');			

			},
			error: function(resp){
				alert('Update did not work.  Please try again.');
			}
		});
		
	});	
	
	$('.toggle_aws').live('click',function(){
		var button = $(this);
		var allowAWS = button.hasClass('allow_button');
		
		var post_data = {
			userId:button.attr('data-user-id'),
			productId:button.attr('data-product-id'),
			type:'deployType',
			active: (!allowAWS)
		}
		
		$.ajax({ 
			url: "services/admin/updateUserProduct", 
			type: "POST",
			cache: false,
			data : JSON.stringify(post_data),
			dataType: "json",
			processData : true,
			contentType: "application/json; charset=utf-8",
			success: function(resp){
				var up = AdminUserProducts.get(resp.userId);
				var aups = up.get('adminUserProducts');
				var aup = _.find(aups,function(p,i){return p.productId == resp.productId;});
				aup.launchAvailable = resp.active;			
				// Update button style/text
				if(!resp.active)
					button.removeClass('allow_button').addClass('disable_button');
				else
					button.removeClass('disable_button').addClass('allow_button');			

			},
			error: function(resp){
				alert('Update did not work.  Please try again.');
			}
		});
		
	});
	
	$('.toggle_account').live('click',function(){
		var button = $(this);
		var accActive = button.hasClass('allow_button');
		
		var post_data = {
			userId:button.attr('data-user-id'),
			status: (!accActive)
		}
		
		$.ajax({ 
			url: "services/admin/setUserActive", 
			type: "POST",
			cache: false,
			data : JSON.stringify(post_data),
			dataType: "json",
			processData : true,
			contentType: "application/json; charset=utf-8",
			success: function(resp){
				// Update collection

				// Update button style/text
				if(accActive)
					button.removeClass('allow_button').addClass('disable_button').text(getAccountButtonTextByStatus(false));
				else
					button.removeClass('disable_button').addClass('allow_button').text(getAccountButtonTextByStatus(true));			

			},
			error: function(resp){
				alert('Update did not work.  Please try again.');
			}
		});	
	});
	
	$('.role_selector').live('change',function(){
		var selector = $(this);
		
		var post_data = {
			userId:selector.attr('data-user-id'),
			newRole: selector.val()
		}
		
		$.ajax({ 
			url: "services/admin/setUserRole", 
			type: "POST",
			cache: false,
			data : JSON.stringify(post_data),
			dataType: "json",
			processData : true,
			contentType: "application/json; charset=utf-8",
			success: function(resp){		
				
			},
			error: function(resp){
				selector.val('ERROR');
				alert('Update did not work.  Please try again.');
			}
		});		
	});	
	
	window.Product = Backbone.Model.extend({
		defaults:{	
			availability: {
				trial:false, 
				deployment:false
			},
			id: -1,
			longName: "",
			shortName: "",
			tileOrder: -1
		}	
	});
	
	// UserProduct Collection 
	window.ProductCollection = Backbone.Collection.extend({
		model : Product,
		url: function(){
			var dr = new Date();
			return 'services/admin/getProductInfo?&_='+dr.getTime();
		}
	});	
	
	// Get Instances ofAdminUserProductCollection
	window.Products = new ProductCollection;		
	Products.fetch();

	//  UserProduct  
	window.AdminUserProduct = Backbone.Model.extend({
		defaults:{	
			id: -1,
			role: "ROLE_USER",
			user: {			
				active: true,
				address1: "test",
				address2: "test",
				companyName: "test",
				country: "test",
				createdAt: 1331147072000,
				firstName: "test",
				id: 1000,
				inforCustomer: true,
				language: "en_US",
				lastName: "test",
				loginAttempts: null,
				phone: "test",
				updatedAt: 1331644632000,
				username: "test"
			},			
			loginAgg: {
				lastLogin: 1332442970000,
				loginCnt: 2,
				userId: 1010		
			},
			adminUserProducts: [
				{
					productId: -1,
					trialAvailable: false,
					launchAvailable: false,
					productShortName: "EAM"
				},
				{
					productId: -1,
					trialAvailable: false,
					launchAvailable: false,
					productShortName: "XM"
				},
				{
					productId: -1,
					trialAvailable: false,
					launchAvailable: false,
					productShortName: "Syteline"
				}
			]
		}	
	});
	
	// UserProduct Collection 
	window.AdminUserProductCollection = Backbone.Collection.extend({
		model : AdminUserProduct,
		url: function(){
			var dr = new Date();
			var post_params = {
				numPerPage:10000,
				pageNum: 0
			}
			return 'services/admin/getAllProducts?'+$.param(post_params)+'&_='+dr.getTime();
		}
	});
	
	// Get Instances ofAdminUserProductCollection
	window.AdminUserProducts = new AdminUserProductCollection;	
	window.AdminUserProductsList = new AdminUserProductCollection;	
	AdminUserProducts.fetch({
		success:function(collection){
			var options = {
				pageNum : 0,
				perPage : AdminUserProducts.perPage
			}
			AdminUserProductsList = collection;
			refreshAdminUserProductsView(AdminUserProductsList,options);
		}
	});
	
	//  TrialRequest  
	window.TrialRequest = Backbone.Model.extend({
		defaults:{	
			comment: "",
			createdAt: 1332185543000,
			id: 1000,
			language: "en_US",
			product: {
				createdAt: null,
				id: 1001,
				name: "test",
				shortName: "test",
				updatedAt: null
			},
			region: {
				id: 1,
				name: "North America",
				shortName: "NAM"
			},
			requestKey: "f9eec970acbc502c304973b0ce79c05d94ed1643",
			updatedAt: null,
			user: {			
				active: true,
				address1: "test",
				address2: "test",
				companyName: "test",
				country: "test",
				createdAt: 1331147072000,
				firstName: "test",
				id: 1000,
				inforCustomer: true,
				language: "en_US",
				lastName: "test",
				loginAttempts: null,
				phone: "test",
				updatedAt: 1331644632000,
				username: "test"
			}
		}	
	});
	
	// TrialRequest Collection 
	window.TrialRequestCollection = Backbone.Collection.extend({
		model : TrialRequest,
		url: function(){
			var dr = new Date();
			return 'services/trialService/getTrialRequests?_='+dr.getTime();
		}
	});	
	
	// Get Instances of TrialRequestCollection
	window.TrialRequests = new TrialRequestCollection;		
	window.TrialRequestsList = new TrialRequestCollection;	
	TrialRequests.fetch({success:function(collection){
			var options = {
				pageNum : 0,
				perPage : TrialRequests.perPage
			}
			TrialRequestsList = collection;
			refreshTrialRequestView(TrialRequestsList,options);
		}
	});	

});
	