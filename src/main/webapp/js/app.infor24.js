$(function(){

	function keepAliveSession(){
		$('body').stopTime();
		$('body').oneTime(1800000, 'timeout', function(){
			window.location = './j_spring_security_logout';
		});
	}
	
	$.ajaxSetup({
		complete: function(jqXHR, textStatus){
			keepAliveSession();
		}
	});
	
	var userNameText 	= $('.user-name-text');
	if(userNameText && userNameText.text() && userNameText.text().length > 20){
		var shorterUserName = (userNameText.text()).substr(0,18);
		userNameText.text(shorterUserName+'...');
	}
	
	function slideTo(tempNum,topText){
		var actNum = tempNum;
		$('#top-slide-1');
		$('#top-slide-1 .top-content-main .header-icon-block').removeClass().addClass('header-icon-block infor24-'+(topText.toLowerCase())+'-icon');
		$('#top-slide-1 .top-content-main .header-text').text(topText);
		$('.slide').hide().css({'z-index':1});
		$('#slide-'+actNum).css({'z-index':2}).show('slide', { direction: "right",  queue: false });		
		$(window).scrollTop(0);
	}
	
	// For user with user dropdown
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
			$('.left-mini-container').append('<div id="dropdown-menu" class="dropdown-menu" data-button-id="'+opts.id+'"></div>');
			menu = $('#dropdown-menu');
			menu.css({top:(41),left:offset.left,'z-index':(zIndex+10),height:'38px'}).html(html);

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
	
	$('#account-settings').on('click', function(event){
		var html = '<div class="setting-menu-row" id="global-logout">Logout</div>';
		// Update username style at top for dialog. Create callback to reset this style
		$(this).css({'border-color':'#777','background-color':'#777','color':'#fff'});
		function backToNormal(){
			$('#account-settings').css({'border-color':'transparent','background-color':'transparent','color':'#222'});
		}
		createMenuDropdown(event,$(this),{id:'account-settings-dropdown-menu'},html,backToNormal);
		// Logout
		$('#global-logout').on('click',function(){
			window.location = './j_spring_security_logout';
		});			
	});	
	
	Backbone.Collection.prototype.page = 0;
	Backbone.Collection.prototype.perPage = 20;	
	
	window.buildRow = function(opts){	
		var row = '\
				<div class="app-box-list-element" id="type-id">\
					<div class="app-box-list-header">\
						<div class="list-app-status status-gray"></div>\
						<div class="list-app-icon"></div>\
						<div class="list-app-name">\
							<div style="width:1200px;height:22px;line-height:22px;font-size:14px;"><span style="float:left;">test</span><span style="font-size:12px;float:left;margin-left:5px;border-left:1px dotted #777;padding-left:5px;color:#777;font-weight:normal;">test</span></div>\
							<div style="width:1200px;color:#9c9c9c;"><span style="color:#555;font-weight:normal;">Username:</span> <span style="color:#777;">test</span> <span style="color:#555;font-weight:normal;">Password:</span> <span style="color:#777;">test</span></div>\
						</div>\
						<div class="list-app-buttons">\
							<div class="button blue-style	view-button"	data-list-type="type" data-type-id="id">Details</div>\
						</div>\
					</div>\
					<div class="app-box-list-content" style="display:none;">inside text</div>\
				</div>';	
		return row;
	}
	
	$('body').on('click','.view-button',function(){
		var viewButton = $(this);
		var listType = viewButton.attr('data-list-type');
		var headerButtonContainer = viewButton.parent();
		var configButton = $('.config-button',headerButtonContainer);
		var headerRow = viewButton.parent().parent();
		var rowElement = viewButton.parent().parent().parent();
		var content = $('.app-box-list-content',rowElement);
		if(viewButton.text() === 'Details'){
			headerRow.addClass('open-header-row');
			$('.list-app-icon',headerRow).css('background-color',$('.list-app-status',headerRow).css('background-color'));
			content.show('blind',225);
			$('.app-box-list-element',content).each(function(i,rower){
				var row = $(rower);
				var timer = (300+(i*45))
				row.show('slide',timer);
			});
			viewButton.addClass('blue-style-open');		
			viewButton.text('Hide');
			configButton.show();
		}else{
			$('.list-app-icon',headerRow).css('background-color','#ccc');
			headerRow.removeClass('open-header-row');
			content.hide('blind',225);
			viewButton.removeClass('blue-style-open');			
			viewButton.text('Details');
			configButton.hide();
		}
	});	
	
	$('body').on('click','.open-button',function(){
		var viewButton = $(this);
		var listType = viewButton.attr('data-list-type');
		if(listType == 'customer'){
			app_router.navigate("customer",false);
			slideTo(26,'Customer');
			var viewButton =$('#i24-customers-container .customer-environments .view-button');
			if(viewButton.text() !== 'Hide'){
				//viewButton.trigger('click');
			}
		}
	});		
	
	$('body').on('click','#go-back-customers-button',function(){
		app_router.navigate("customers",false);
		slideTo(20,'Customers');
	});
	
	window.getDialogHTML = function(opts){		
		return	'\
			<div class="cloud_deploy">\
				<div class="header" style="width:700px;">\
					<div class="list-app-icon   infor24-'+opts.type+'-icon" style="background-color:'+opts.color+';margin:0;"></div>\
					<div class="header-text">'+opts.title+'</div>\
				</div>\
				<div class="content" style="width:720px;height:450px;">\
					\
				</div>\
				<div class="footer" style="width:680px">\
					<span class="button" id="close-dialog"  style="width:80px;"	>Save</span>\
					<span class="button" id="close-dialog"  style="float:left;"		>Cancel</span>\
				</div>\
			</div>';		
	}
	
	$('body').on('click','.open-environment',function(){
	
		var options = {
			type : 'environments',
			color: '#5080D8', //blue
			title : 'Environment'
		}

		openBigDialog(550,720,20,20,550,720,getDialogHTML(options),true);			
		
		$('.cloud_deploy').on('click','#close-dialog',function(){
			closeBigDialog();
		});	
		
	});
	
	$('body').on('click','.open-instance',function(){
	
		var options = {
			type : 'instances',
			color: '#67BF00', //green
			title : 'Instance'
		}

		openBigDialog(550,720,20,20,550,720,getDialogHTML(options),true);			
		
		$('.cloud_deploy').on('click','#close-dialog',function(){
			closeBigDialog();
		});	
		
	});	

	$('body').on('click','.open-job',function(){
	
		var options = {
			type : 'jobs',
			color: '#9A43D7', //purple
			title : 'Job'
		}

		openBigDialog(550,720,20,20,550,720,getDialogHTML(options),true);			
		
		$('.cloud_deploy').on('click','#close-dialog',function(){
			closeBigDialog();
		});	
		
	});

	$('body').on('click','.open-ticket',function(){
	
		var options = {
			type : 'tickets',
			color: '#D17D1D', //orange
			title : 'Ticket'
		}

		openBigDialog(550,720,20,20,550,720,getDialogHTML(options),true);			
		
		$('.cloud_deploy').on('click','#close-dialog',function(){
			closeBigDialog();
		});	
		
	});	
	
	// Routing and Navigation
	var R = Backbone.Router.extend({
		routes: {
			""	    				:	"customers",
			"customers"		:	"customers",
			"environments"	:	"environments",
			"instances"		:	"instances",
			"jobs"				:	"jobs",
			"tickets"			:	"tickets",
			"config"				:	"config",
			"customer"		:	"customer"
		},
		customers: function(){
			slideTo(20,'Customers');
		},
		environments: function(){
			slideTo(21,'Environments');
		},
		instances: function(){
			slideTo(22,'Instances');
		},
		jobs: function(){
			slideTo(23,'Jobs');
		},
		tickets: function(){
			slideTo(24,'Tickets');
		},
		config: function(){
			slideTo(25,'Config');
		},
		customer: function(){
			slideTo(26,'Customer');
		}
		
	});
	
	// Init routing bindings created above
	var app_router = new R;
	Backbone.history.start();

	// MENU LINKS ///////////////////////////////////////////////////////////////////////////////////
	//Links
	var i24Customers  	= $('#i24-customers-link');
	var i24Environments  = $('#i24-environments-link');
	var i24Instances  		= $('#i24-instances-link');
	var i24Jobs  				= $('#i24-jobs-link');
	var i24Tickets  			= $('#i24-tickets-link');
	var i24Config  			= $('#i24-config-link');
	
	i24Customers.on('click',function(){
		app_router.navigate("customers",false);
		slideTo(20,'Customers');
	});
	i24Environments.on('click',function(){
		app_router.navigate("environments",false);
		slideTo(21,'Environments');	
	});
	i24Instances.on('click',function(){
		app_router.navigate("instances",false);
		slideTo(22,'Instances');	
	});
	i24Jobs.on('click',function(){
		app_router.navigate("jobs",false);
		slideTo(23,'Jobs');	
	});
	i24Tickets.on('click',function(){
		app_router.navigate("tickets",false);
		slideTo(24,'Tickets');	
	});
	i24Config.on('click',function(){
		app_router.navigate("config",false);
		slideTo(25,'Config');	
	});			
		
});