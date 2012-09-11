$(function(){
	Backbone.Collection.prototype.page = 0;
	Backbone.Collection.prototype.perPage = 20;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 	USERS ------------------------------------------------------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	function userRoleSelect(user){	
		var html = '';
		if(user && user.get('user') && user.get('role')){
			html = '<div class="ca_input_desc_text">User Role</div>\
						<select class="ca_input_select" id="user-role" data-user-id="'+user.get("user").id+'" >\
							<option value="ROLE_EXTERNAL" '+(user.get("role")=='ROLE_EXTERNAL'? 'selected' : '')+'>External</option>\
							<option value="ROLE_SALES" '+(user.get("role")=='ROLE_SALES'? 'selected' : '')+'>Internal</option>\
							<option value="ROLE_ADMIN" '+(user.get("role")=='ROLE_ADMIN'? 'selected' : '')+'>Admin</option>\
							<option value="ROLE_SUPERADMIN" '+(user.get("role")=='ROLE_SUPERADMIN'? 'selected' : '')+'>Super Admin</option>\
							<option value="ROLE_I24_ADMIN" '+(user.get("role")=='ROLE_I24_ADMIN'? 'selected' : '')+'>I24 Admin</option>\
						</select>';
		}else{
			html = '<div class="ca_input_desc_text">User Role</div>\
						<select class="ca_input_select" id="user-role"  >\
							<option value="ROLE_EXTERNAL" selected>External</option>\
							<option value="ROLE_SALES" >Internal</option>\
							<option value="ROLE_ADMIN" >Admin</option>\
							<option value="ROLE_SUPERADMIN" >Super Admin</option>\
							<option value="ROLE_I24_ADMIN" >I24 Admin</option>\
						</select>';		
		}
		return html;
	}	
	
	// Create new user
	$('#create-user-button').on('click',function(){
		$('html').trigger('click');
		var industry_list = '<option value="-1" selected></option>';
		Industries.each(function(industry){
			industry_list += '<option value="'+industry.get('id')+'">'+industry.get('name')+'</option>';
		});			
		var userRole = '';
		if($('#ur').val() === 'adm' || $('#ur').val() === 'sad'){
			userRole = userRoleSelect('');
		}		
		var button = $(this);
		var html = 	'\
			<div id="cloud_admin">\
				<div id="cloud_admin_header" style="color:#fff;">Create User<span class="admin_settings_title_async_msg">User Saved</span></div>\
				<div id="cloud_admin_content" style="height:311px;">\
					<div class="ca_col ca_col_one">\
						<div class="ca_input_desc_text"><span class="required">*</span><span style="float:left;">First Name</span></div>\
						<input type="text" value="" id="first_name" class="ca_input"  />\
						<div class="ca_input_desc_text">Last Name<span class="required">*</span></div>\
						<input type="text" value="" id="last_name" class="ca_input"  />\
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
					<div class="ca_col ca_col_three" id="comp-name">\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.6')+'<span class="required">*</span></div>\
						<input type="text" value="" id="company_name" class="ca_input" />\
						<input type="hidden" value="" id="company_id" class="ca_input" />\
						<div class="ca_input_desc_text">Industry<span class="required">*</span></div>\
						<select name="test" class="ca_input_select" id="industry_id">'+industry_list+'</select>\
						<div class="ca_input_desc_text">InforID</div>\
						<input type="text" value="" id="infor_id" class="ca_input" />\
						'+userRole+'\
					</div>\
				</div>\
				<div id="cloud_admin_button_container">\
					<span class="cloud_admin_button" id="create-user-save" style="width:60px;text-align:center;">'+$.i18n.prop('account.12')+'</span>\
					<span class="cloud_admin_button" id="create-user-cancel" style="float:left;">'+$.i18n.prop('account.13')+'</span>\
				</div>\
			</div>\
		';
		
		openBigDialog(429,755,20,20,429,755,html,true);		
		
		$('#first_name').focus();
		
		$('#create-user-cancel').on('click',function(){
			closeBigDialog();
		});			
		
		$('#company_name').autocomplete(companyAutoCompleteSetup);	
		$('#industry_id').on('change',function(){$('#company_id').val(-1);});	
		
		$('#create-user-save').on('click',function(){
			// Update settings
			var button = $(this);
			button.text($.i18n.prop('account.18'));
			
			var post_data = {
				firstName			: $.trim($('#first_name').val()),
				lastName			: $.trim($('#last_name').val()),
				email				: $.trim($('#account_email').val()),
				language	 		: $.trim($('#account_language').val()),
				address1 	 		: $.trim($('#user_address_1').val()),
				address2 	 		: $.trim($('#user_address_2').val()),
				country 	 		: $.trim($('#user_country').val()),
				phone				: $.trim($('#user_phone').val()),				
				companyName 	 	: $.trim($('#company_name').val()),
				companyId			: $.trim($('#company_id').val()),
				industryId			: $.trim($('#industry_id').val()),
				inforId				: $.trim($('#infor_id').val())
			}			
			
			var validatedRequired = post_data.firstName && post_data.lastName && post_data.email && post_data.language && post_data.address1 && post_data.address2 && post_data.country
									&& post_data.phone && post_data.companyName && post_data.industryId && (post_data.industryId > 0);		
			
			if($('#ur').val() === 'adm' || $('#ur').val() === 'sad'){
				post_data.role = $.trim($('#user-role').val());
			}			
			
			// Validation
			if(validatedRequired){
				$.ajax({ 
					url: "services/useradmin/createNewUser", 
					type: "POST",
					cache: false,
					data : JSON.stringify(post_data),
					dataType: "json",
					processData : true,
					contentType: "application/json; charset=utf-8",
					success: function(resp){
					
						Users.add(resp);		
						refreshUsersView(Users,{ pageNum : 0 , perPage : Users.perPage });				
						var newUser = Users.get(resp.id);
						
						button.remove();
						$('#create-user-cancel').text($.i18n.prop('account.19'));
						
						$('#first_name').val(newUser.get('user').firstName).attr('disabled',true);
						$('#last_name').val(newUser.get('user').lastName).attr('disabled',true);	
						$('#account_email').val(newUser.get('user').username).attr('disabled',true);
						$('#account_language').val(newUser.get('user').language).attr('disabled',true);
						
						$('#user_address_1').val(newUser.get('user').address1).attr('disabled',true);
						$('#user_address_2').val(newUser.get('user').address2).attr('disabled',true);
						$('#user_country').val(newUser.get('user').country).attr('disabled',true);	
						$('#user_phone').val(newUser.get('user').phone).attr('disabled',true);							
						
						$('#company_name').val(newUser.get('company').name).attr('disabled',true);
						$('#company_id').val(newUser.get('company').id);
						$('#industry_id').val(newUser.get('company').industryId).attr('disabled',true);
						$('#infor_id').val(newUser.get('company').inforId).attr('disabled',true);
						if($('#ur').val() === 'adm' || $('#ur').val() === 'sad'){
							$('#user-role').val(newUser.get('role')).attr('disabled',true);
						}							
					
						$('.admin_settings_title_async_msg').show();
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
	
	// Edit user dialog
	$('body').on('click','.user-edit-button',function(){
		var button = $(this);
		$('html').trigger('click');
		var industry_list = '<option value="-1" selected></option>';
		Industries.each(function(industry){
			industry_list += '<option value="'+industry.get('id')+'">'+industry.get('name')+'</option>';
		});			
		var button = $(this);
		var user = Users.get(button.attr('data-user-id'));
		var userRole = '';
		if($('#ur').val() === 'adm' || $('#ur').val() === 'sad'){
			userRole = userRoleSelect(user);
		}
		
		var html = 	'\
			<div id="cloud_admin">\
				<div id="cloud_admin_header" style="color:#fff;">Edit User | '+user.get('user').firstName+' '+user.get('user').lastName+'<span class="admin_settings_title_async_msg">User Saved</span></div>\
				<div id="cloud_admin_content" style="height:311px;">\
					<div class="ca_col ca_col_one">\
						<div class="ca_input_desc_text"><span class="required">*</span><span style="float:left;">First Name</span></div>\
						<input type="text" value="" id="first_name" class="ca_input" disabled />\
						<div class="ca_input_desc_text">Last Name<span class="required">*</span></div>\
						<input type="text" value="" id="last_name" class="ca_input" disabled />\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.1')+'<span class="required">*</span></div>\
						<input type="email" value="" id="account_email" class="ca_input" disabled />\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.4')+'<span class="required">*</span></div>\
						<select name="test" class="ca_input_select" id="account_language" disabled>\
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
					<div class="ca_col ca_col_three" id="comp-name">\
						<div class="ca_input_desc_text">'+$.i18n.prop('account.6')+'<span class="required">*</span></div>\
						<input type="text" value="" id="company_name" class="ca_input" />\
						<input type="hidden" value="" id="company_id" class="ca_input" />\
						<div class="ca_input_desc_text">Industry<span class="required">*</span></div>\
						<select class="ca_input_select" id="industry_id">'+industry_list+'</select>\
						<div class="ca_input_desc_text">InforID</div>\
						<input type="text" value="" id="infor_id" class="ca_input" />\
						'+userRole+'\
					</div>\
				</div>\
				<div id="cloud_admin_button_container">\
					<span class="cloud_admin_button" id="update-user-save" style="width:60px;text-align:center;">'+$.i18n.prop('account.12')+'</span>\
					<span class="cloud_admin_button" id="update-user-cancel" style="float:left;">'+$.i18n.prop('account.13')+'</span>\
				</div>\
			</div>';
		
		openBigDialog(429,755,20,20,429,755,html,true);		
		
		$('#update-user-cancel').on('click',function(){
			closeBigDialog();
		});			
		
		$('#first_name').val(user.get('user').firstName).attr('disabled',true);
		$('#last_name').val(user.get('user').lastName).attr('disabled',true);	
		$('#account_email').val(user.get('user').username).attr('disabled',true);
		$('#account_language').val(user.get('user').language).attr('disabled',true);
		
		$('#user_address_1').val(user.get('user').address1).attr('disabled',false);
		$('#user_address_2').val(user.get('user').address2).attr('disabled',false);
		$('#user_country').val(user.get('user').country).attr('disabled',false);	
		$('#user_phone').val(user.get('user').phone).attr('disabled',false);							
		
		$('#company_name').val(user.get('company').name).attr('disabled',false).autocomplete(companyAutoCompleteSetup);
		$('#company_id').val(user.get('company').id);
		$('#industry_id').val(user.get('company').industryId).attr('disabled',false).on('change',function(){$('#company_id').val(-1);});	
		$('#infor_id').val(user.get('company').inforId).attr('disabled',false);		
		
		$('#update-user-save').on('click',function(){
			// Update settings
			var button = $(this);
			button.text($.i18n.prop('account.18'));
			
			var post_data = {
				id					: user.id,
				firstName			: $.trim($('#first_name').val()),
				lastName			: $.trim($('#last_name').val()),
				email				: $.trim($('#account_email').val()),
				language	 		: $.trim($('#account_language').val()),
				address1 	 		: $.trim($('#user_address_1').val()),
				address2 	 		: $.trim($('#user_address_2').val()),
				country 	 		: $.trim($('#user_country').val()),
				phone				: $.trim($('#user_phone').val()),				
				companyName 	 	: $.trim($('#company_name').val()),
				companyId			: $.trim($('#company_id').val()),
				industryId			: $.trim($('#industry_id').val()),
				inforId				: $.trim($('#infor_id').val())
			}			
			
			var validatedRequired = post_data.firstName && post_data.lastName && post_data.email && post_data.language && post_data.address1 && post_data.address2 && post_data.country
									&& post_data.phone && post_data.companyName && post_data.industryId && (post_data.industryId > 0);		
			
			if(($.trim(user.get('company').name) != post_data.companyName) || 
				($.trim(user.get('company').inforId) != post_data.inforId) ||
				($.trim(user.get('company').industryId) != post_data.industryId)){
					// Next, add caching to company results and look there for this data before setting companyId to -1.
					post_data.companyId = -1;
			}		
									
			if($('#ur').val() === 'adm' || $('#ur').val() === 'sad'){
				post_data.role = $.trim($('#user-role').val());
			}
			
			// Validation
			if(validatedRequired){
				$.ajax({ 
					url: "services/useradmin/updateUser", 
					type: "POST",
					cache: false,
					data : JSON.stringify(post_data),
					dataType: "json",
					processData : true,
					contentType: "application/json; charset=utf-8",
					success: function(resp){
											
						button.text('Save')
						var updatedUser = Users.get(resp.id);
						updatedUser.set(resp);
						
						$('#create-user-cancel').text($.i18n.prop('account.19'));
						
						$('#first_name').val(updatedUser.get('user').firstName).attr('disabled',true);
						$('#last_name').val(updatedUser.get('user').lastName).attr('disabled',true);	
						$('#account_email').val(updatedUser.get('user').username).attr('disabled',true);
						$('#account_language').val(updatedUser.get('user').language).attr('disabled',true);
						
						$('#user_address_1').val(updatedUser.get('user').address1).attr('disabled',false);
						$('#user_address_2').val(updatedUser.get('user').address2).attr('disabled',false);
						$('#user_country').val(updatedUser.get('user').country).attr('disabled',false);	
						$('#user_phone').val(updatedUser.get('user').phone).attr('disabled',false);							
						
						$('#company_name').val(updatedUser.get('company').name).attr('disabled',false);
						$('#company_id').val(updatedUser.get('company').id);
						$('#industry_id').val(updatedUser.get('company').industryId).attr('disabled',false);
						$('#infor_id').val(updatedUser.get('company').inforId).attr('disabled',false);
						if($('#ur').val() === 'adm' || $('#ur').val() === 'sad'){
							$('#user-role').val(updatedUser.get('role')).attr('disabled',false);
						}						
					
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
	
	window.displaySecurityProductList = function(type,user){
		var products = '';
		var infor24 = user.get('trialAvailable');
		var aws = user.get('deployAvailable');
		UserProducts.each(function(product){
			var available = false;
			var security = false;
			if(type ==='INFOR24'){
				available = product.get('availability').trial;
				security = $.inArray(product.get('id'),infor24);
			}else if(type === 'AWS'){
				available = product.get('availability').deployment;		
				security = $.inArray(product.get('id'),aws);
			}
			if(available){
				var productAllowed = security >= 0 ? 'display-product-row-selected' : '';
				var subName = product.get('displayName2') ? '<span class="display-product-name-small">'+product.get('displayName2')+'</span>' : '';
				var version = product.get('versions')[0]; // by default, get the latest version
				products += '<div class="display-product-row '+productAllowed+'" data-deploy-type="'+type+'" data-product-id="'+product.get('id')+'" data-product-version-id="'+version.id+'">\
												<div class="display-product-checkbox"></div>\
												<div class="display-product-name">'+product.get('displayName1')+' '+subName+'</div>\
												<div class="display-product-version">'+version.description+'</div>\
											</div>';
			}
		});
		return products;
	}
	
	$('body').on('click','.user-security-button',function(){
		var userId = $(this).attr('data-user-id');
		var user = Users.get(userId);		
		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage">\
					<span style="color: #fff;font-size:18px;font-weight: bold;">Security | '+user.get('user').firstName+' '+user.get('user').lastName+'</span>\
				</div>\
				<div id="admin-tabs" style="width: 460px;height:30px;padding: 10px 20px 10px 20px;overflow: hidden;float: left;position: relative;">\
					<div id="infor24-security-tab" class="admin-pop-up-tab open-tab">Infor24</div>\
					<div id="aws-security-tab" class="admin-pop-up-tab">AWS</div>\
				</div>\
				<div id="admin-content" class="cloud_deploy_singlepage_content">\
					<div id="infor24-security-content" class="admin-pop-up-content"  style="width:460px;height:330px;overflow:auto;">\
						'+displaySecurityProductList('INFOR24',user)+'\
					</div>\
					<div id="aws-security-content" class="admin-pop-up-content"  style="width:460px;height:330px;overflow:auto;display:none;">\
						'+displaySecurityProductList('AWS',user)+'\
					</div>\
			    </div>\
				<div class="cloud_deploy_button_container_singlepage">\
					<span class="cloud_deploy_button" id="cloud_deploy_single_cancel" style="float:right;">Close</span>\
				</div>\
			</div>';
		
		openBigDialog(500,500,20,20,500,500,html,true);
		
		$('#cloud_deploy_single_cancel').on('click',function(){
			closeBigDialog();
		});	
				
		$('#admin-tabs').on('click','#infor24-security-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #infor24-security-content').show();
		});
		
		$('#admin-tabs').on('click','#aws-security-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #aws-security-content').show();
		});		
		
		// Click/select behavior for products
		$('#admin-content').on('click','.display-product-row',function(){
			var row = $(this);
			var deployType= row.attr('data-deploy-type') == 'INFOR24' ? 'trialType' : 'deployType';
			var activeState = false;
			if(row.hasClass('display-product-row-selected')){
				activeState = false;
			}else{
				activeState = true;
			}

			var post_data = {
				userId:userId,
				productId:row.attr('data-product-id'),
				type: deployType,
				active: activeState
			}
			
			$.ajax({ 
				url: "services/admin/updateUserProduct", 
				type: "POST",
				cache: false,
				data : JSON.stringify(post_data),
				dataType: "json",
				processData : true,
				contentType: "application/json; charset=utf-8",
				success: function(response){
					if(deployType == 'deployType'){
						var aws = user.get('deployAvailable');
						if(response.active){
							aws.insert(parseInt(post_data.productId));
							user.set('deployAvailable',aws);	
							row.addClass('display-product-row-selected');						
						}else{
							aws.remove(parseInt(post_data.productId));
							user.set('deployAvailable',aws);
							row.removeClass('display-product-row-selected');	
						}
					}else{
						var infor24 = user.get('trialAvailable');
						if(response.active){
							infor24.insert(parseInt(post_data.productId));
							user.set('trialAvailable',infor24);		
							row.addClass('display-product-row-selected');
						}else{
							infor24.remove(parseInt(post_data.productId));
							user.set('trialAvailable',infor24);
							row.removeClass('display-product-row-selected');								
						}
					}
				},
				error: function(response){
					alert('Update did not work.  Please try again.');
				}
			});		
		});		
	});
	
	$('body').on('click','.user-requests-button',function(){
		var userId = $(this).attr('data-user-id');
		var user = Users.get(userId);		
		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage">\
					<span style="color: #fff;font-size:18px;font-weight: bold;">Security | '+user.get('user').firstName+' '+user.get('user').lastName+'</span>\
				</div>\
				<div id="admin-tabs" style="width: 460px;height:30px;padding: 10px 20px 10px 20px;overflow: hidden;float: left;position: relative;">\
					<div id="user-requests-tab" class="admin-pop-up-tab open-tab">Requests</div>\
					<div id="user-security-tab" class="admin-pop-up-tab">Security</div>\
				</div>\
				<div id="admin-content" class="cloud_deploy_singlepage_content">\
					<div id="user-requests-content" class="admin-pop-up-content" style="display:none;">\
						\
					</div>\
					<div id="user-security-content" class="admin-pop-up-content" >\
						\
					</div>\
			    </div>\
				<div class="cloud_deploy_button_container_singlepage">\
					<span class="cloud_deploy_button" id="cloud_deploy_single_cancel" style="float:right;">Close</span>\
				</div>\
			</div>';
		
		openBigDialog(500,500,20,20,500,500,html,true);
		
		$('#cloud_deploy_single_cancel').on('click',function(){
			closeBigDialog();
		});	
				
		$('#admin-tabs').on('click','#user-security-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-security-content').show();
		});
		
		$('#admin-tabs').on('click','#user-requests-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-requests-content').show();
		});		
		
	});	
	
	$('body').on('click','.user-deployments-button',function(){
		var userId = $(this).attr('data-user-id');
		var up = Users.get(userId);
		
		var container = $('#user-running-content'),
		instanceContent = '',
		ieOnly = true;
		var deployments =up.get('deployments');
		if(deployments.length){
			for(var i=0;i<deployments.length;i++){
				var deployment = new UserDeployment(deployments[i]);
				if(deployment.get('deploymentState')!='Deleted'){
					instanceContent += buildDeploymentRow(deployment);
				}
			}
		}else{
			instanceContent += '<div class="app-content-header" style="border:0px;margin-top:30px;">User does not have deployments.</div>';
		}		
		
		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage" style="width:720px;">\
					<span style="color: #fff;font-size:18px;font-weight: bold;">Deployments | '+up.get('user').firstName+' '+up.get('user').lastName+'</span>\
				</div>\
				<div id="admin-tabs" style="width: 740px;height:30px;padding: 10px 20px 10px 20px;overflow: hidden;float: left;position: relative;">\
					<div id="user-running-tab" class="admin-pop-up-tab open-tab">All</div>\
					<div id="user-stopped-tab" class="admin-pop-up-tab" style="display:none;">Stopped</div>\
					<div id="user-terminated-tab" class="admin-pop-up-tab" style="display:none;">Terminated</div>\
				</div>\
				<div id="admin-content" class="cloud_deploy_singlepage_content" style="width: 720px;overflow:auto;">\
					<div id="user-running-content" class="admin-pop-up-content">'+instanceContent+'</div>\
					<div id="user-stopped-content" class="admin-pop-up-content" style="display:none;">\
						\
					</div>\
					<div id="user-terminated-content" class="admin-pop-up-content" style="display:none;">\
						\
					</div>\
					<div style="clear:both;"></div>\
				</div>\
				<div class="cloud_deploy_button_container_singlepage" style="width: 720px;">\
					<span class="cloud_deploy_button" id="cloud_deploy_single_cancel" style="float:right;">Close</span>\
				</div>\
			</div>\
		';
		
		openBigDialog(500,760,20,20,500,760,html,true);
		
		$('#cloud_deploy_single_cancel').on('click',function(){
			closeBigDialog();
		});	
				
		$('#admin-tabs').on('click','#user-running-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-running-content').show();
		});
		
		$('#admin-tabs').on('click','#user-stopped-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-stopped-content').show();
		});		
		
		$('#admin-tabs').on('click','#user-terminated-tab',function(){
			$('#admin-tabs .open-tab').removeClass('open-tab');
			$(this).addClass('open-tab');
			$('#admin-content .admin-pop-up-content').hide();
			$('#admin-content #user-terminated-content').show();
		});		
		
	});	
	
	$('#admin_user_product_data_container').on('click','.adminProductNext',function(){
		var nextPageNum = UsersList.page + 1;	
		var numPages = Math.ceil(Users.length / Users.perPage);
		if(nextPageNum < numPages){
			var options = {
				pageNum : nextPageNum,
				perPage : Users.perPage
			}				
			refreshUsersView(UsersList,options);
		}
	});
	
	$('#admin_user_product_data_container').on('click','.adminProductPrevious',function(){
		var previousPageNum = UsersList.page - 1;	
		var numPages = Math.ceil(Users.length / Users.perPage);
		if(previousPageNum >= 0){
			var options = {
				pageNum : previousPageNum,
				perPage : Users.perPage
			}				
			refreshUsersView(UsersList,options);
		}
	});
		
		
		
	function refreshUsersView(collection,options){
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
		var isInt = $('#ur').val() === 'int';
		for(var i = startIndex;i<endIndex;i++){
			var user = collection.at(i);
			var myId = 'user_row_'+i;
			var lastLoginDate =((new Date(parseInt(user.get('loginAgg').lastLogin))).toString());
			var lastLoginDateString = lastLoginDate.substring(0,lastLoginDate.indexOf('(',0));
			var loginCount = user.get('loginAgg').loginCnt;	
			var compName = user.get('company') ? user.get('company').name : 'n/a';
			var accountStatus = user.get('user').active ? 'Active' : 'Disabled';
			if(isInt && (user.get("role")=='ROLE_SALES' || user.get("role")=='ROLE_ADMIN' || user.get("role")=='ROLE_SUPERADMIN')){
				
			}else{
				var statusColor = 'green';
				var infoHTML ='	<div class="app-instance-detail-color-box status-'+statusColor+'"></div>\
								<div class="app-instance-detail-info-element" style="margin-left:10px;width:104px;">\
									<div class="metric-header" style="width:104px;">Account</div>\
									<div class="metric-row-1" style="width:104px;">Created Date:</div>\
									<div class="metric-row-2" style="width:104px;">'+lastLoginDateString+'</div>\
									<div class="metric-row-1" style="width:104px;">Total Logins:</div>\
									<div class="metric-row-2" style="width:104px;">'+loginCount+'</div>\
									<div class="metric-row-1" style="width:104px;">Last Login:</div>\
									<div class="metric-row-2" style="width:104px;">'+lastLoginDateString+'</div>\
									<div class="metric-row-1" style="width:104px;">Account:</div>\
									<div class="metric-row-2" style="width:104px;">'+accountStatus+'</div>\
									<div class="metric-row-1" style="width:104px;">Status:</div>\
									<div class="metric-row-2" style="width:104px;">Logged In</div>\
								</div>\
								<div class="app-instance-detail-info-element a-detail-small" style="width:166px;">\
									<div class="metric-header" style="width:166px;">Contact</div>\
									<div class="metric-row-1" style="width:166px;">Address</div>\
									<div class="metric-row-2" style="text-align:left;width:166px;">'+user.get('user').address1+'</div>\
									<div class="metric-row-2" style="text-align:left;width:166px;">'+user.get('user').address2+'</div>\
									<div class="metric-row-2" style="text-align:left;width:166px;">'+user.get('user').country+'</div>\
									<div class="metric-row-1" style="width:166px;">Phone</div>\
									<div class="metric-row-2" style="width:166px;">'+user.get('user').phone+'</div>\
								</div>\
								<div class="app-instance-detail-info-element a-detail-small">\
									<div class="metric-header">Deployments</div>\
									<div class="metric-row-1">Number:</div>\
									<div class="metric-row-2">'+user.get('deployments').length+'</div>\
									<div class="metric-row-1">Last:</div>\
									<div class="metric-row-2">'+lastLoginDateString+'</div>\
									<div class="metric-row-1" style="display:none;">Tickets:</div>\
									<div class="metric-row-2" style="display:none;">0</div>\
								</div>\
								<div class="app-instance-detail-info-element a-detail-small" style="display:none;">\
									<div class="metric-header">Total Usage</div>\
									<div class="metric-row-1">Users:</div>\
									<div class="metric-row-2">n/a</div>\
									<div class="metric-row-1">Logins:</div>\
									<div class="metric-row-2">n/a</div>\
									<div class="metric-row-1">I/O:</div>\
									<div class="metric-row-2">n/a</div>\
								</div>\
								<div class="app-instance-detail-info-element a-detail-small" style="margin:0;display:none;">\
									<div class="metric-header">Cost (est.)</div>\
									<div class="metric-row-1">YTD:</div>\
									<div class="metric-row-2">n/a</div>\
									<div class="metric-row-1">Monthly:</div>\
									<div class="metric-row-2">n/a</div>\
									<div class="metric-row-1">Daily:</div>\
									<div class="metric-row-2">n/a</div>\
								</div>\
								<div class="app-instance-detail-buttons" style="width:503px;">\
									<div class="button blue-style user-chat" data-user-id="'+user.get('user').id+'" style="display:none;">Chat</div>\
									<div class="button blue-style user-edit-button" data-user-id="'+user.get('user').id+'">Edit</div>\
									<div class="button blue-style user-security-button" data-user-id="'+user.get('user').id+'">Security</div>\
									<div class="button blue-style user-requests-button" data-user-id="'+user.get('user').id+'" style="display:none;">Requests</div>\
									<div class="button blue-style user-deployments-button" data-user-id="'+user.get('user').id+'" style="">Deployments</div>\
									<div class="button blue-style user-deploy" data-user-id="'+user.get('user').id+'">Deploy</div>\
								</div>';								
				// New user row
				tempStr += '<div class="app-box-list-element">\
								<div class="app-box-list-header">\
									<div class="list-app-status status-'+statusColor+'"></div>\
									<div class="list-app-icon"></div>\
									<div class="list-app-name">\
										<div style="width:1200px;height:22px;line-height:22px;font-size:14px;"><span style="float:left;">'+user.get("user").firstName+' '+user.get("user").lastName+'</span></div>\
										<div style="width:1200px;color:#9c9c9c;"><span style="float:left;">'+compName+'</span><span style="float:left;margin-left:5px;border-left:1px dotted #777;padding-left:5px;color:#777;font-weight:normal;">'+user.get('user').username+'</span></div>\
									</div>\
									<div class="list-app-buttons">\
										<div class="button blue-style	view-button"	>Details</div>\
									</div>\
								</div>\
								<div class="app-box-list-content">'+infoHTML+'</div>\
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
	
	var file_search_box_text = 'Search Names';
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
		UsersList = Users;
		var options = {
			pageNum : 0,
			perPage : Users.perPage
		}				
		refreshUsersView(UsersList,options);		
	}).autocomplete({
		minLength: 0,
		delay: 150,
		source: function( request, response ) {
			UsersList = searchUsersByName(request.term,Users);
			var options = {
				pageNum : 0,
				perPage : Users.perPage
			}				
			refreshUsersView(UsersList,options);
		},
		focus: function() {
			// prevent value inserted on focus
			return false;
		}
	});		
	
	$('#user-list-clear-search-button').on('click',function(){
		$('#user-list-search-box').trigger('clear');
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
	
	// Product Collection 
	window.ProductCollection = Backbone.Collection.extend({
		model : Product,
		url: function(){
			var dr = new Date();
			return 'services/admin/getProductInfo?_='+dr.getTime();
		}
	});	
	
	// Get Instances of Products
	window.Products = new ProductCollection;		
	Products.fetch();
	
	window.Company = Backbone.Model.extend({
		defaults:{	
			id:'',
			customerId:'',
			name:'',
			notes:'',
			industryId:''
		}	
	});
	
	// Company Collection 
	window.CompanyCollection = Backbone.Collection.extend({
		model : Company,
		url: function(){
			var dr = new Date();
			return 'services/company/getCompanies?_='+dr.getTime();
		}
	});	
	
	// Get Instances of Companies
	window.Companies = new CompanyCollection;		
	Companies.fetch();		

	//Users
	window.User = Backbone.Model.extend({
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
			company	: {
				id: 1011,
				industryId: 1002,
				inforId: "",
				name: "Infor Global",
				notes: ""
			},
			loginAgg: {
				lastLogin: 1332442970000,
				loginCnt: 2,
				userId: 1010		
			},
			deployments: [{
				amazonCredentialsId: -1,
				createdAt: 1339521270000,
				createdByUser: {
					id:-1, 
					username:"admin@infor.com", 
					firstName:"Admin", 
					lastName:"User"
				},
				deployedProducts: [{
					id: 1002,
					longName: "DEMO Product",
					shortName: "DEMO Product"
				}],
				deploymentUrl: "NA",
				deploymentName : "NA",
				deploymentState: "NA",
				deploymentStatus: "NA",
				elasticIp: "0.0.0.0",
				id: 1000,
				lastStartedAt: 1339521270000,
				numServers: 0,
				regionId: 1,
				updatedAt: 1339521270000,
				user: {
					id:-1, 
					username:"admin@infor.com", 
					firstName:"Admin", 
					lastName:"User"
				},
				vpcId: "VPC-0",
				vpcPassword: null,
				vpcUsername: null		
			}]
		}	
	});
	
	// Users Collection 
	window.UserCollection = Backbone.Collection.extend({
		model : User,
		url: function(){
			var dr = new Date();
			var post_params = {
				numPerPage:10000,
				pageNum: 0
			}
			return 'services/admin/getUsers?'+$.param(post_params)+'&_='+dr.getTime();
		}
	});
	
	// Get Instances ofUserCollection
	window.Users = new UserCollection;	
	window.UsersList = new UserCollection;	
	Users.fetch({
		success:function(collection){
			var options = {
				pageNum : 0,
				perPage : Users.perPage
			}
			UsersList = collection;
			refreshUsersView(UsersList,options);
		}
	});
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 	TRIAL REQUESTS ---------------------------------------------------------------------------------------------///////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
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
								<span style="float:left;width:200px;overflow:hidden;display:none;">'+request.get('user').companyName+'</span>\
								<span class="approve-request" data-request-id="'+request.get('id')+'" data-request-key="'+request.get('requestKey')+'" style="float:right;width:auto;text-align:right;height:20px;line-height:20px;padding:5px;cursor:pointer;background-color:#44cc00;color:#fff;margin-left:5px;">Approve</span>\
								<span class="decline-request" data-request-id="'+request.get('id')+'" data-request-key="'+request.get('requestKey')+'" style="float:right;width:auto;text-align:right;height:20px;line-height:20px;padding:5px;cursor:pointer;background-color:#ff2200;color:#fff;">Decline</span>\
							</div>\
						</div>';	
		};	
		// Appending a long string is much faster than appending individual rows
		con.append(tempStr);	
	}


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
	

	//TrialRequest  
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
	
	//TrialRequest Collection 
	window.TrialRequestCollection = Backbone.Collection.extend({
		model : TrialRequest,
		url: function(){
			var dr = new Date();
			return 'services/trialService/getTrialRequests?_='+dr.getTime();
		}
	});	
	
	//Get Instances of TrialRequestCollection
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
	*/

});
	