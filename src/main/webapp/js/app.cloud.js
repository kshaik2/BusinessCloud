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
	
	Backbone.Collection.prototype.page = 0;
	Backbone.Collection.prototype.perPage = 20;	
		//Links
	var mMarketplace 	= $('#marketplace-link'),
		mDeployments 	= $('#deployments-link'),
		mUsers 		 	= $('#users-link'),
		mTrialRequests 	= $('#trial-requests-link'),
		mInstances	 	= $('#instances-link'),
		mDashboard	 	= $('#dashboard-link'),
		userNameText 	= $('.user-name-text'),
		mainContainer 	= $('#main-app-container'),
		tileContainer 	= $('#tile-container'),
		
		//Apps
		allAppBoxes 	= $('.large-app-box,.small-app-box'),
		
		//Settings
		smallScreen 	= $('#small-screen-link'),
		largeScreen 	= $('#large-screen-link');	
	
	
	if(userNameText && userNameText.text() && userNameText.text().length > 20){
		var shorterUserName = (userNameText.text()).substr(0,18);
		userNameText.text(shorterUserName+'...');
	}
	
	function slideTo(tempNum,topText){
		var actNum = tempNum;
		$('#top-slide-1');
		$('#top-slide-1 .top-content-main .header-text').text(topText);
		$('.slide').hide().css({'z-index':1});
		$('#slide-'+actNum).css({'z-index':2}).show('slide', { direction: "right",  queue: false });		
		$(window).scrollTop(0);
	}
	
	function getURLFromHash(h,full){
		var scheme = $('#scheme').val() + '://',
			serverName = $('#servName').val(),
			contextRoot = $('#conRoot').val();
			
		return full ? scheme + serverName + contextRoot + "/t/?g=" + h : contextRoot + "/t/?g=" + h ;
	}	
	
	function getDuration(t,type){
		var upTime = new Date(parseInt(t));
		var days = 1000*60*60*24; var d = new Date(); 
		var t = 1;
		if(type=='up'){
			t = d.getTime() - upTime.getTime();
		}else{
			t = upTime.getTime() - d.getTime();
		}
		var daysRunning = Math.floor(t / days);	
		var hrsRunning = Math.floor((t / (days/24)) - (parseInt(daysRunning) * 24));
		var minsRunning = Math.floor(( t / ((days/24)/60)) - (parseInt(hrsRunning) * 60));
		var upTimeString = '';var daysRunningStr= '';var hrsRunningStr= '';var minsRunningStr = '';
		if(daysRunning>0){
			var dayStr = (daysRunning === 1) ? 'd' : 'd';
			daysRunningStr = daysRunning + dayStr;
		}
		if(hrsRunning>0){	
			var hrStr = (hrsRunning === 1) ? 'h' : 'h';
			hrsRunningStr = hrsRunning + hrStr;
		}
		if(minsRunning>0){
			var minStr = (minsRunning === 1) ? 'm' : 'm';
			minsRunningStr = minsRunning + minStr;
		}else{
			minsRunningStr = '0m';
		}
		if(daysRunning>0){
			upTimeString = daysRunningStr + ' ' + hrsRunningStr;
		}else if(hrsRunning>0){
			upTimeString = hrsRunningStr + ' ' + minsRunningStr;
		}else{
			upTimeString = minsRunningStr;
		}	
		return upTimeString;
	}

	function getDeploymentStateColor(state){
		var color = 'grey'; // was yellow
		if(state === 'Available'){
			color = 'green';
		}else if(state === 'Deleted'){
			color = 'grey'; // was red;
		}
		return color;
	}
	
	window.buildDeploymentRow = function(inst){
		var isAws = inst.get('deploymentType') == 'AWS' ? true : false;
		var displayGoLink = !isAws ? 'block' : 'none' ;
		var statusColor = getDeploymentStateColor(inst.get('deploymentState'));

		// Product List
		var numProducts = inst.get('deployedProducts') ? inst.get('deployedProducts').length : 0;
		var productLinkList = '';
		if(numProducts){
			$.each(inst.get('deployedProductVersions'),function(i,el){
				productLinkList += '<a href="#app/'+el.productId+'" class="instance-link instance-credentials" style=""><span class="instance-row-product-name">'+el.product.displayName1+'</span><span class="instance-row-product-version">('+el.description+')</span></a>';
			});
		}
		// Button Logic
		var allowStop = 'disabled-button';  var allowStart ='disabled-button';
		if(statusColor === 'grey' ){
			upTimeString = 'n/a';
			stopTimeString = 'n/a';
			if(inst.get('deploymentStatus')=='Stopped'){
				allowStart = '';				
			}
		}else{
			allowStop = '';	
		}
		var displayExtend 	= isAws ? 'none' : 'block';
		var displayEdit 		= isAws ? 'block' : 'none';
		var displayStop 		= isAws ? 'block' : 'none';
		var displayStart 		= isAws ? 'block' : 'none';
		
		// Info Titles and Values based on Type
		var deploymentType = inst.get('deploymentType') ;
		var regionName = ((Regions.get((inst.get('deployedProductVersions')[0]).region_id)).get('name')).toUpperCase();
		var locationTitle = isAws ? 'AWS Region' : 'Location';
		var location = isAws ? regionName : 'Infor24';
		var stopInTitle = isAws ? 'Stop In' : 'Expires In';
		var openStatus = inst.showNow ? 'block' : 'none';
		var elasticIpTitle = isAws ? 'ElasticIP' : 'Password';
		var elasticIpValue= isAws ? inst.get('elasticIp') : inst.get('vpcPassword');
		var awsKeyTitle = isAws ? 'AWS Key' : 'Username';
		var awsKey = UserAWSList.get(inst.get('amazonCredentialsId'));
		var awsKeyString = isAws ? (awsKey ? awsKey.get('name') : 'n/a') : inst.get('vpcUsername');
		//  Uptime and Stop/Expire Time
		var cad = new Date(parseInt(inst.get('createdAt')));
		var createdAt = (cad.getUTCMonth()+1) +'/'+cad.getUTCDate()+'/'+(cad.getFullYear()+'').substr(2,3);	
		var upTimeString = inst.get('lastStartedAt') ? getDuration(inst.get('lastStartedAt'),'up') : 'n/a' ;
		var stopTimeString = inst.get('scheduledStopAt') ? getDuration(inst.get('scheduledStopAt'),'down') : 'Never';	
		// Display Link Size
		var displayURL = inst.get('deploymentUrl'); 
		displayURL = displayURL.length > 65 ? displayURL.substr(0,62) + '...' : displayURL;	
		
		// HTML Row Builder
		var infoHTML = '';
		infoHTML ='	<div class="app-instance-detail-color-box status-'+statusColor+'"></div>\
					<div class="app-instance-detail-info-element" style="margin-left:10px;">\
						<div class="metric-header">Instance</div>\
						<div class="metric-row-1">Status:</div>\
						<div class="metric-row-2 deployment-state" >'+inst.get('deploymentStatus')+'</div>\
						<div class="metric-row-1">'+locationTitle+':</div>\
						<div class="metric-row-2 ">'+location+'</div>\
						<div class="metric-row-1">Deployed:</div>\
						<div class="metric-row-2">'+createdAt+'</div>\
						<div class="metric-row-1">Uptime:</div>\
						<div class="metric-row-2 uptime-value">'+upTimeString+'</div>\
						<div class="metric-row-1">'+stopInTitle+':</div>\
						<div class="metric-row-2 stoptime-value">'+stopTimeString+'</div>\
					</div>\
					<div class="app-instance-detail-info-element a-detail-small">\
						<div class="metric-header">Metrics</div>\
						<div class="metric-row-1">Num. Servers:</div>\
						<div class="metric-row-2">'+inst.get('numServers')+'</div>\
						<div class="metric-row-1">'+awsKeyTitle+'</div>\
						<div class="metric-row-2">'+awsKeyString+'</div>\
						<div class="metric-row-1">'+elasticIpTitle+'</div>\
						<div class="metric-row-2">'+elasticIpValue+'</div>\
					</div>\
					<div class="app-instance-detail-info-element-large">\
						<div class="metric-header">Products (<span style="color:#545454;">'+numProducts+'</span>)</div>\
						<div class="metric-row-1" style="height:auto;">'+productLinkList+'</div>\
					</div>\
					<div class="app-instance-detail-buttons">\
						<div class="button blue-style deployment-delete"  						 data-env-type="deployment" data-deployment-id="'+inst.get('id')+'" data-deployment-name="'+inst.get('deploymentName')+'" 	data-deployment-type="'+deploymentType+'"																>Delete</div>\
						<div class="button blue-style deployment-extend"      					 data-env-type="deployment" data-deployment-id="'+inst.get('id')+'" data-deployment-name="'+inst.get('deploymentName')+'"  	data-deployment-type="'+deploymentType+'" style="display:'+displayExtend+'"		>Extend</div>\
						<div class="button blue-style deployment-edit '+allowStop+' "   data-env-type="deployment" data-deployment-id="'+inst.get('id')+'" data-deployment-name="'+inst.get('deploymentName')+'"  	data-deployment-type="'+deploymentType+'" style="display:'+displayEdit+'"			>Edit</div>\
						<div class="button blue-style deployment-stop '+allowStop+' "  data-env-type="deployment" data-deployment-id="'+inst.get('id')+'" data-deployment-name="'+inst.get('deploymentName')+'"  	data-deployment-type="'+deploymentType+'" style="display:'+displayStop+'"			>Stop</div>\
						<div class="button blue-style deployment-start '+allowStart+' "  data-env-type="deployment" data-deployment-id="'+inst.get('id')+'" data-deployment-name="'+inst.get('deploymentName')+'" 	data-deployment-type="'+deploymentType+'" style="display:'+displayStart+'"		>Start</div>\
					</div>';

		return	'<div class="app-box-list-element" id="deployment-'+inst.get('id')+'">\
					<div class="app-box-list-header">\
						<div class="list-app-status status-'+statusColor+'"></div>\
						<div class="list-app-icon"></div>\
						<div class="list-app-name">\
							<div style="width:1200px;height:22px;line-height:22px;font-size:14px;"><span style="float:left;">'+inst.get('deploymentName')+'</span><span style="font-size:12px;float:left;margin-left:5px;border-left:1px dotted #777;padding-left:5px;color:#777;font-weight:normal;">'+displayURL+'</span></div>\
							<div style="width:1200px;color:#9c9c9c;"><span style="color:#555;font-weight:normal;">Username:</span> <span style="color:#777;">'+inst.get('vpcUsername')+'</span> <span style="color:#555;font-weight:normal;">Password:</span> <span style="color:#777;">'+inst.get('vpcPassword')+'</span></div>\
						</div>\
						<div class="list-app-buttons">\
							<a href="'+ inst.get('deploymentUrl')+'" target="_blank" class="button green-style go-button  " style="display:'+displayGoLink+';"	>Go</a>\
							<div class="button blue-style	view-button"	data-list-type="deployment" data-deployment-id="'+inst.get('id')+'">Details</div>\
						</div>\
					</div>\
					<div class="app-box-list-content" style="display:'+openStatus+'">'+infoHTML+'</div>\
				</div>';
	}
	
	window.listDeployments = function(collection,options){
		var container = $('#app-box-running-instances'),
			instanceContent = '';
		if(collection.length){
			for(var i = options.startIndex;i<options.endIndex;i++){		
				var deployment = collection.at(i);
				instanceContent += buildDeploymentRow(deployment);
			}			
		}else{
			instanceContent = '<div  style="width:700px;float:left;height:500px;line-height:500px;font-size:18px;font-weight:bold;color:#777;text-align:center;">\
											Click the Deploy button to get started.\
										</div>';
		}
		container.html(instanceContent);	
	}

	$('#refresh-user-deployments').on('click',function(){
		UserDeployments.fetch({
			success:function(collection){
				var options = {
					pageNum : 0,
					perPage : UserDeployments.perPage
				}
				refreshDeploymentsView(collection,options);
			},
			error:function(collection,response){
				alert('Error refreshing User Deployments.');
			}	
		});	
	});
	
	window.refreshDeploymentsView = function(collection,options){
		var pageNum = options.pageNum ? options.pageNum : 0;
		var numPerPage = options.perPage ? options.perPage : collection.perPage;
		var numPages = Math.ceil(collection.length / numPerPage);	
		var startIndex = pageNum * numPerPage;
		var endIndex = (pageNum + 1) * numPerPage;
		endIndex = endIndex > collection.length ? collection.length : endIndex;
		UserDeployments.page = pageNum;	
	
		var con = $('#deployments-list-details-body');
		// Clear existing list of products
		con.html('');
		$('.deployments-list-header-page-text')	.text( 'Page '+(pageNum+1)+' of '+numPages);
		$('#deployments-list-footer-num-details')	.text( 'Showing Deployments '+(startIndex+1)+'-'+endIndex+' of '+collection.length);	
		if(collection.length == 0){
			$('.deployments-list-header-page-text')	.text('');
			$('#deployments-list-footer-num-details')	.text('No Deloyments Found');			
		}	
		
		var options = {
			startIndex : startIndex,
			endIndex : endIndex
		}
		listDeployments(collection,options);
		openDeployments.forEach(function(did) {
			$('.app-box-list-content',$('#deployment-'+did)).show(); // Add animation so user knows Refresh button did something.
			$('.view-button',$('#deployment-'+did)).text('Hide');
		});		
	}
	
	$('#deployments_data_container').on('click','.deploymentPageNext',function(){
		var nextPageNum = UserDeployments.page + 1;	
		var numPages = Math.ceil(UserDeployments.length / UserDeployments.perPage);
		if(nextPageNum < numPages){
			var options = {
				pageNum : nextPageNum,
				perPage : UserDeployments.perPage
			}				
			refreshDeploymentsView(UserDeployments,options);
		}
	});
	
	$('#deployments_data_container').on('click','.deploymentPagePrevious',function(){
		var previousPageNum = UserDeployments.page - 1;	
		var numPages = Math.ceil(UserDeployments.length / UserDeployments.perPage);
		if(previousPageNum >= 0){
			var options = {
				pageNum : previousPageNum,
				perPage : UserDeployments.perPage
			}				
			refreshDeploymentsView(UserDeployments,options);
		}
	});	

	function searchDeploymentsByName(val,collection){
		var searchPattern = new RegExp(val,"i");
		var filteredList = new collection.constructor(collection.filter(function(elem){
			var userName = elem.get("deploymentName");
			return searchPattern.test(userName);
		}));
		return filteredList;
	}		
	
	var deployments_search_box_text = 'Search Deployments';
	$('#deployments-list-search-box').val( deployments_search_box_text)
	  .on('focus',function(){
		var sb = $(this);
		if($.trim(sb.val())== deployments_search_box_text){
			sb.val('').css({'color':'#545454'});
		}
	}).on('blur',function(){
		var sb = $(this);
		if(!$.trim(sb.val())){
			sb.val( deployments_search_box_text).css({'color':'#cbcbcb'});
		}
	}).on('clear',function(){
		var sb = $(this);
		sb.val( deployments_search_box_text).css({'color':'#cbcbcb'});
		//UsersList = Users;
		var options = {
			pageNum : 0,
			perPage : UserDeployments.perPage
		}				
		refreshDeploymentsView(UserDeployments,options);		
	}).autocomplete({
		minLength: 0,
		delay: 150,
		source: function( request, response ) {
			UserDeploymentsList = searchDeploymentsByName(request.term,UserDeployments);
			var options = {
				pageNum : 0,
				perPage : UserDeployments.perPage
			}				
			refreshDeploymentsView(UserDeploymentsList,options);
		},
		focus: function() {
			// prevent value inserted on focus
			return false;
		}
	});		
	
	$('#deployments-list-clear-search-button').on('click',function(){
		$('#deployments-list-search-box').trigger('clear');
	});	
	
	
	// up == UserProduct model for particular product
	function showProductSlide(up){
		app_router.navigate("app/"+up.get('id'),false);
		var appContent = $('#slide-2 .content-main');
		var appInnerFlag = $('.box-owned-flag',appContent);			
	
		slideTo(1,up.get('name'));		

		////////////////////////////////////////
		// ADD/REMOVE CSS style from main tile to internal page.
		// Have to add/remove these classes each, so keep track using jqdata
		var appPreviewBox = $('.app-box-preview',appContent);
		var currentAPBClass = $.data(document.body,'cl');
		if(currentAPBClass){appPreviewBox.removeClass(currentAPBClass);}
		var newAPBClass = 'tile-bg-'+up.get('mason');
		appPreviewBox.addClass(newAPBClass);
		$.data(document.body,'cl',newAPBClass);
		// end ADD/REMOVE CSS STYLE from main tile to internal page
		////////////////////////////////////////
		
		$('.app-box-preview-title',appContent).html(up.get('name'));
		$('.app-box-preview-version',appContent).html('');
		$('#deploy-buy',appContent).attr('data-app-id',up.get('id')).attr('data-app-name',up.get('name'));
		$('#deploy-trial',appContent).attr('data-app-id',up.get('id')).attr('data-app-name',up.get('name')).hide();
		$('#deploy-aws',appContent).attr('data-app-id',up.get('id')).attr('data-app-name',up.get('name')).hide();

		// Add bindings for tabs/content
		$('.app-box-tab').on('click',function(){
			$('.app-box-tab-clicked').removeClass('app-box-tab-clicked');
			$('.content-open').removeClass('content-open');
			$(this).addClass('app-box-tab-clicked');
			$('#'+$(this).attr('data-content-body-id')).addClass('content-open');
		});

		// Update product text
		var desc = up.get('descriptions');
		// Main top blurb size based on screen width
		if($('#main-app-container').hasClass('large-screen')){
			if(desc.header_large_desc_text){
				$('#header_desc_text',appContent).html(desc.header_large_desc_text);
			}else{
				$('#header_desc_text',appContent).html('');
			}		
		}else{
			if(desc.tile_large_desc_text){
				$('#header_desc_text',appContent).html(desc.tile_large_desc_text);		
			}else{
				$('#header_desc_text',appContent).html('');
			}			
		}
		
		// Product overview sections
		if(desc.overview_1 && desc.overview_header_1){
			$('#overview-header-section-1',appContent).html(desc.overview_header_1).show();
			$('#overview-section-1',appContent).html(desc.overview_1).show();
		}else{
			$('#overview-header-section-1',appContent).html('').hide();
			$('#overview-section-1',appContent).html('').hide();		
		}	
		if(desc.overview_2 && desc.overview_header_2){
			$('#overview-header-section-2',appContent).html(desc.overview_header_2).show();
			$('#overview-section-2',appContent).html(desc.overview_2).show();
		}else{
			$('#overview-header-section-2',appContent).html('').hide();
			$('#overview-section-2',appContent).html('').hide();		
		}	
		if(desc.overview_3 && desc.overview_header_3){
			$('#overview-header-section-3',appContent).html(desc.overview_header_3).show();
			$('#overview-section-3',appContent).html(desc.overview_3).show();
		}else{
			$('#overview-header-section-3',appContent).html('').hide();
			$('#overview-section-3',appContent).html('').hide();		
		}	
		if(desc.overview_4 && desc.overview_header_4){
			$('#overview-header-section-4',appContent).html(desc.overview_header_4).show();
			$('#overview-section-4',appContent).html(desc.overview_4).show();
		}else{
			$('#overview-header-section-4',appContent).html('').hide();
			$('#overview-section-4',appContent).html('').hide();		
		}
		if(desc.overview_5 && desc.overview_header_5){
			$('#overview-header-section-5',appContent).html(desc.overview_header_5).show();
			$('#overview-section-5',appContent).html(desc.overview_5).show();
		}else{
			$('#overview-header-section-5',appContent).html('').hide();
			$('#overview-section-5',appContent).html('').hide();		
		}		
		$('#overview-section-media-1',appContent).html('').hide(); // TODO: update to allow media 

		// HIDE running instances tab
		$('#tab-menu-overview',appContent).trigger('click');
		$('#tab-menu-running-instances',appContent).hide();
		$('.app-box-preview-version',appContent).hide();
	}
	
	function prepareAppBoxes(){
		var allAppBoxes = $('.large-app-box,.small-app-box',$('#tile-container'));
		// Define functionality for each tile
		$.each(allAppBoxes,function(i,elem){
			var app = $(elem);
			var appName = app.attr('data-app-name'),
				appOwned = app.attr('data-own')==='true',
				appVersion = app.attr('data-app-version'),
				appMason = app.attr('data-app-mason-order'),
				appTileFlag = $('.box-owned-flag',app),
				appTitleDiv = $('.app-box-title',app),			
				appDetailsDiv = $('.app-box-details',app),
				appVersionDiv = $('.app-box-version',app),
				appDetailsTitleDiv = $('.app-box-details-title',app),
				appOpenButton = $('.action-button',app),
				appMarkAsOwn = $('.mark-as-own',app),
				appMarkAsOwnTooltip = $('.markAsOwnTooltip',appMarkAsOwn),
				myAppNum = app.attr('id').split("-")[2];
			var product = UserProducts.get(myAppNum);
	
			app.addClass('tile-bg-' + appMason);
			appTitleDiv.text(appName);
			appDetailsTitleDiv.text(appName);
			appVersionDiv.text(appVersion);
	
			if(!appOwned){		
				appOpenButton.text('Learn More');
			}else{
				appOpenButton.text('Open');
			}
			app.hover(function(){
				appTitleDiv.animate({'margin-top':'-220px','background-color':'#fff','color':'#222'},{ duration:400, queue: false });			
				appDetailsDiv.animate({'margin-top':'0px'},{ duration: 400, queue: false });
			},function(){
				var marginVal = 35;
				if(app.hasClass('large-app-box')){marginVal=97;}
				appDetailsDiv.animate({'margin-top':'425px'},{ duration: 400, queue: false });
				appTitleDiv.animate({'margin-top': marginVal+'px','background-color':'#222','color':'#fff'},{ duration: 400, queue: false });
			});	
			appOpenButton.on('click',function(){
				var up = UserProducts.get(myAppNum);
				up.set('mason',appMason);
				showProductSlide(up);
			});
		});
	}
	
	function updateSmallLargeTitleDivMargin(app){
		var marginVal = 35;
		if($(app).hasClass('large-app-box')){marginVal=97;}
		$('.app-box-title',$(app)).css({'margin-top': marginVal+'px'});
	}
	
	function displayProducts(collection){
		var tilesContainer = $('#tile-container');
		var tiles = ''; 
		collection.each(function(product){
			var prod = product.attributes;
			// temp options
			prod.version = '8.0.1';
			prod.desc_text = product.get('descriptions').tile_desc_text;
			if(prod.tileSize == 'large'){
				prod.desc_text = product.get('descriptions').tile_large_desc_text;
			}
			var classList = prod.tileSize+'-app-box ';
			if(prod.owned){
				if(prod.owned){
					classList += 'my-app ';
				}			
			}else{
				classList += 'all-apps ';
			}
			prod.mason = (prod.tileOrder < 10) ? '0'+prod.tileOrder : prod.tileOrder;
			tiles += '	<div 	data-app-name="'+prod.name+'"\
								data-own="'+prod.owned+'"\
								data-app-version="available"\
								data-app-mason-order="'+prod.mason+'"\
								id="app-box-'+prod.id+'"\
								class="'+classList+'">\
								<div class="box-owned-flag"></div><div class="app-box-title"></div><div class="app-box-version"></div>\
								<div class="app-box-details"><div class="app-box-details-title"></div><div class="app-box-details-desc">'+prod.desc_text+'</div><div class="app-box-details-button-container"><div class="button mark-as-own"><span class="markAsOwnText">+</span><div class="markAsOwnTooltip"><div class="tt-arrow"></div><div class="tt-text"></div></div></div><div class="button action-button">Open</div></div></div>\
						</div>';
		});		
		tilesContainer.html(tiles+'<div style="clear:both;"></div>');
		prepareAppBoxes();
		// Masonry Tiles
		tilesContainer.isotope({
			masonry: {
			  columnWidth: 240
			},
			sortBy: 'name',
			filter: '.large-app-box,.small-app-box',
			getSortData: {
			  name: function( $elem ) {
				return $elem.attr('data-app-mason-order');
			  }
			}
		});		
	}
	
	window.companyAutoCompleteSetup = {
		minLength: 2,
		appendTo: "#comp-name",
		source: function( request, response ) {
			$.ajax({ 
				url: "services/company/getCompaniesMatchingString?match="+request.term, 
				type: "GET",
				cache: false,
				dataType: "json",
				processData : true,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					var comps=new Array();
					$.each(data,function(i,item){
						var industry = Industries.get(item.industryId);
						comps.push({ label: (item.name+' ('+industry.get('name')+')'), value: item.id, fieldLabel: item.name, industryId: item.industryId, inforId: item.inforId });
					});
					response( comps );
				}
			});
		},			
		focus: function( event, ui ) {
			return false;
		},
		select: function( event, ui ) {
			$('#company_name').val( ui.item.fieldLabel);
			$('#company_id').val( ui.item.value ).attr('disabled',false);
			$('#industry_id').val(ui.item.industryId).attr('disabled',false);
			$('#infor_id').val(ui.item.inforId).attr('disabled',false);					
			return false;
		},
		change: function( event, ui ) {
			if(ui && ui.item){
				$('#company_id').val( ui.item.value ).attr('disabled',false);
				$('#industry_id').val(ui.item.industryId).attr('disabled',false);
				$('#infor_id').val(ui.item.inforId).attr('disabled',false);			
			}else{
				$('#company_id').val(-1).attr('disabled',false);
				$('#industry_id').val(-1).attr('disabled',false);		
			}
			return false;
		}				
	}
	
	//  Region
	window.Region = Backbone.Model.extend({});

	// Region Collection 
	window.RegionCollection = Backbone.Collection.extend({
		model : Region,
		url: function(){
			var dr = new Date();
			return 'services/deploy/getRegions?_='+dr.getTime();
		}
	});	
	
	// Get Instances of RegionCollection
	window.Regions = new RegionCollection;	
	Regions.fetch({
		success:function(collection,response){
			initRegions();
		},
		error:function(collection,response){
			alert('Error getting AWS Regions. Please refresh to try again.');
		}	
	});	
	
	
	//  UserProduct  
	window.UserProduct = Backbone.Model.extend({});

	// UserProduct Collection 
	window.UserProductCollection = Backbone.Collection.extend({
		model : UserProduct,
		url: function(){
			var dr = new Date();
			return 'services/products?_='+dr.getTime();
		}
	});	
	
	// Get Instances of UserProduct Collection
	window.UserProducts = new UserProductCollection;		
	
	//  UserUserDeployment 
	window.UserDeployment = Backbone.Model.extend({
		defaults:{
			amazonCredentialsId: -1,
			createdAt: 1339521270000,
			createdByUser: {
				id:-1, 
				username:"admin@infor.com", 
				firstName:"Admin", 
				lastName:"User"
			},
			deployedProducts: [{
				accessKey: "null",
				amiDescriptors: [],
				description: "BusCloud 3 HMS Description",
				id: 1019,
				ieOnly: true,
				name: "HMS-BC-3",
				product: {
					id:1019, 
					shortName:"Demo Short",
					longName:"Demo Product Long Name"
				},
				productId: 1019,
				secretKey: "null"
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
		}	
	});

	// UserProduct Collection 
	window.UserDeploymentCollection = Backbone.Collection.extend({
		model : UserDeployment,
		comparator: function(ud){
			return 100000000 - parseInt(ud.id);
		},
		url: function(){
			var dr = new Date();
			return 'services/user/getDeployments?_='+dr.getTime();
		}
	});	
	
	// Get Instances of UserProduct Collection
	window.UserDeployments = new UserDeploymentCollection;	
	window.UserDeploymentsList = new UserDeploymentCollection;
	
	function deploymentActionFunc(post_data,el,deploymentId){
		$.ajax({ 
			url: "services/deploy/do", 
			type: "POST",
			cache: false,
			data : JSON.stringify(post_data),
			dataType: "json",
			processData : true,
			contentType: "application/json; charset=utf-8",
			success: function(deployment){
				UserDeployments.remove(UserDeployments.get(deploymentId));			
				UserDeployments.add(deployment);	
				var newDeployment = UserDeployments.get(deploymentId);
				newDeployment.showNow = true;
				var newRow = $(buildDeploymentRow(newDeployment));
				$('#deployment-'+deploymentId).replaceWith(newRow);
				$('.view-button',('#deployment-'+deploymentId)).text('Hide');
				
				// if open
				closeSmallDialog();				
			},
			error:function(e){
				alert('Unable to ' + post_data.type + ' your deployment. \n\n Please try again.');
			}
		});	
	}
	
	$('body').on('click','.deployment-extend',function(){
		var button = $(this);
		if(!button.hasClass('disabled-button')){
			var deploymentId = button.attr('data-deployment-id');
			var deploymentType = button.attr('data-deployment-type');
			var deployAction = 'EXTEND';
			
			var post_data = {
				deploymentStackId  : deploymentId,
				deploymentType : deploymentType,
				type: deployAction
			}

			if(confirm('Are you sure you wish to EXTEND the selected deployment?')){
				deploymentActionFunc(post_data,button,deploymentId);
			}
		}
	});	
	
	$('body').on('click','.deployment-stop',function(){
		var button = $(this);
		if(!button.hasClass('disabled-button')){
			var deploymentId = button.attr('data-deployment-id');
			var deployAction = 'STOP';
			
			var post_data = {
				deploymentStackId  : deploymentId,
				type: deployAction
			}

			if(confirm('Are you sure you wish to '+deployAction+' the selected deployment?')){
				deploymentActionFunc(post_data,button,deploymentId);
			}
		}
	});
	
	$('body').on('click','.deployment-start,.deployment-edit',function(){	
		var button = $(this);
		if(!button.hasClass('disabled-button')){
			var deploymentId = button.attr('data-deployment-id');	
			var deploymentName = button.attr('data-deployment-name');
			var isUpdate = button.hasClass('deployment-edit');
			var updateTitle = isUpdate ? 'Edit ' : '';
			var html = 	'\
				<div id="cloud_deploy">\
					<div class="cloud_deploy_header_container_singlepage" style="width:460px;">\
						<span style="color: #fff;font-size:16px;font-weight:bold;">'+updateTitle+'Start Options : "'+deploymentName+'"</span>\
					</div>\
					<div class="cloud_deploy_singlepage_content" id="start-deployment-type-container"  style="width:460px;height:135px;">\
						<div class="cdc_input_desc_text start-deployment-row" id="start-always" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
							<div class="bc-checkbox"></div>\
							Run until I click STOP or DELETE.\
						</div>\
						<div class="cdc_input_desc_text start-deployment-row" id="start-hours-config" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
							<div class="bc-checkbox"></div>\
							Stop after \
							<select id="deployment_stop_hours" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
								<option value="1">1</option><option value="2">2</option><option value="3" selected>3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option>\
							</select> hours.\
						</div>\
						<div class="cdc_input_desc_text start-deployment-row" id="start-date-config" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
							<div class="bc-checkbox"></div>\
							Stop on \
							<input type="text" id="deployment_stop_date" name="test" class="cdc_input_select" style="margin-top:0px;width:100px;padding:5px;" /> at \
							<select id="deployment_stop_time" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
								<option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5" selected>5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="0">12</option>\
							</select>\
							<select id="deployment_stop_ampm" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
								<option value="am">AM</option><option value="pm" selected>PM</option>\
							</select>\
						</div>\
					</div>\
					<div class="cloud_deploy_button_container_singlepage" style="bottom:0px;width:460px">\
						<span class="cloud_deploy_button" id="start_deployment"  style="width:80px;text-align:center;">Start</span>\
						<span class="cloud_deploy_button" id="start_cancel" style="float:left;">Cancel</span>\
					</div>\
				</div>\
			';
			
			openSmallDialog(255,500,20,20,255,500,'',function(){},html,true);		
			
			$('#start_cancel').on('click',function(){
				closeSmallDialog();
			});	
			
			// Set default date to tomorrow
			var tom = new Date();tom.setUTCDate(tom.getUTCDate());tom.setUTCHours(0);tom.setUTCMilliseconds(0);tom.setUTCMinutes(0);
			var tomorrowString = (tom.getUTCMonth()+1) +'/'+tom.getUTCDate()+'/'+(tom.getFullYear()+'');	
			$('#deployment_stop_date').val(tomorrowString).datepicker({ dateFormat: "m/d/yy" });
			
			// Click/select behavior for start types
			$('#start-deployment-type-container').on('click','.bc-checkbox',function(){
				var row = $(this).parent();
				if(row.hasClass('start-deployment-row-selected')){
					row.removeClass('start-deployment-row-selected');
				}else{
					$('.start-deployment-row-selected',$('#start-deployment-type-container')).removeClass('start-deployment-row-selected');
					row.addClass('start-deployment-row-selected');
				}
			});	

			$('#start_deployment').on('click',function(){
				var startType = isUpdate ? 'CHANGE_SCHEDULE' : 'START';
				var post_data = {
					type: startType,
					deploymentStackId : deploymentId,
					scheduleType: '',
					scheduleValue: ''
				}
			
				var validated = false;
				var validationMessage = 'Please select a start option.';
			
				var startRow = $('.start-deployment-row-selected',$('#start-deployment-type-container'));
				if(startRow.length){
					validationMessage = 'Please select the date at which to stop the deployment, and make sure it\'s at least one hour from now.';
					var startRowId = startRow.attr('id');
					if(startRowId === 'start-always'){
						post_data.scheduleType = 'INDEFINITE';
						post_data.scheduleValue = '';
						validated = true;
					}else if(startRowId === 'start-hours-config'){
						post_data.scheduleType = 'HOURLY';
						post_data.scheduleValue = $('#deployment_stop_hours').val();	
						validated = true;
					}else if(startRowId === 'start-date-config'){
						post_data.scheduleType = 'STOP_DATE';
						//var selDate = $('#deployment_stop_date').datepicker( "getDate" );
						var selHour = $('#deployment_stop_time').val();
						var selAMPM = $('#deployment_stop_ampm').val();
						var selectedDate = $('#deployment_stop_date').datepicker( "getDate" );
						// Insure date is in the future.
						var rightNow = new Date();
						var selectedHours = (selAMPM == 'am') ? selHour : (parseInt(selHour) + 12);
						selectedDate.setUTCHours(selectedHours);
						selectedDate.setUTCMinutes(selectedDate.getUTCMinutes() + (rightNow.getTimezoneOffset()));
						if(selectedDate && selectedDate.getTime() && selectedDate.getTime() >= rightNow.getTime()){
							post_data.scheduleValue = selectedDate.getTime();
							validated = true;
						}else{
							validated = false;
						}
					}
				}
			
				// Submit if validated
				if(validated){
					deploymentActionFunc(post_data,button,deploymentId);
				}else{
					alert(validationMessage);
				}
			});	
		}		
	});
	
	$('body').on('click','.deployment-delete',function(){	
		var button = $(this);
		var deploymentId = button.attr('data-deployment-id');	
		var deploymentName = button.attr('data-deployment-name');
		var deploymentType = button.attr('data-deployment-type');
		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage" style="width:460px;">\
					<span style="color: #fff;font-size:16px;font-weight:bold;">Delete : "'+deploymentName+'"</span>\
				</div>\
				<div class="cloud_deploy_singlepage_content" id="start-deployment-type-container"  style="width:460px;height:115px;">\
					<div class="cdc_input_desc_text start-deployment-row" id="start-hours-config" style="width:400px;font-size:14px;margin-bottom:0px;margin-top:25px;margin-left:33px;font-weight:normal;line-height:30px;">\
						Type "YES" to DELETE:  \
						<input type="text" id="delete-verify-string" name="test" class="cdc_input_select" style="margin-top:0px;margin-left:10px;width:100px;padding:5px;" />\
					</div>\
					<div class="cdc_input_desc_text start-deployment-row" id="start-hours-config" style="width:400px;font-size:10px;margin-bottom:0px;margin-top:12px;margin-left:33px;font-weight:normal;line-height:15px;">\
						Deleting a Deployment will destroy all associated instances and data and cannot be undone.\
					</div>\
				</div>\
				<div class="cloud_deploy_button_container_singlepage" style="bottom:0px;width:460px">\
					<span class="cloud_deploy_button" id="delete_deployment"  style="width:80px;text-align:center;">Delete</span>\
					<span class="cloud_deploy_button" id="delete_cancel" style="float:left;">Cancel</span>\
				</div>\
			</div>';
		
		openSmallDialog(235,500,20,20,235,500,'',function(){},html,true);		
		
		$('#delete_cancel').on('click',function(){
			closeSmallDialog();
		});	
		
		$('#delete_deployment').on('click',function(){
			var post_data = {
				type: 'TERMINATE',
				deploymentType : deploymentType,
				deploymentStackId : deploymentId
			}
		
			var validated = false;
			var validationMessage = 'Please type "YES" into the box.';
			var termString = $.trim($('#delete-verify-string').val());
			
			if(termString && termString === 'YES'){
				validated = true;
			}
		
			// Submit if validated
			if(validated){
				deploymentActionFunc(post_data,button,deploymentId);
			}else{
				alert(validationMessage);
			}
		});		
	});	
	
	window.openDeployments = Array.create();
	
	$('body').on('click','.view-button',function(){
		var viewButton = $(this);
		var listType = viewButton.attr('data-list-type');
		var headerButtonContainer = viewButton.parent();
		var contentButtonContainer = $('.app-instance-detail-buttons',viewButton.parent().parent().parent());
		$('.app-box-list-content',viewButton.parent().parent().parent()).toggle(225);
		if(viewButton.text() === 'Details'){
			viewButton.text('Hide');
			//var upgradeButton = $('.go-button:first',headerButtonContainer);
			//upgradeButton.detach();		
			// For possible error clean up 
			//$('.go-button',headerButtonContainer).remove();
			//upgradeButton.appendTo(contentButtonContainer);
			if(listType && listType == 'deployment'){
				var deployment = UserDeployments.get(viewButton.attr('data-deployment-id'));
				openDeployments.insert(deployment.get('id'));
				openDeployments.unique();
			}
		}else{
			viewButton.text('Details');
			//var upgradeButton = $('.go-button:first',contentButtonContainer);
			//upgradeButton.detach();
			// For possible error clean up 
			//	$('.go-button',contentButtonContainer).remove();
			//upgradeButton.appendTo(headerButtonContainer);
			if(listType && listType == 'deployment'){
				var deployment = UserDeployments.get(viewButton.attr('data-deployment-id'));
				openDeployments.unique();
				openDeployments.remove(deployment.get('id'));
			}			
		}
	});
	
	//  UserInstance 
	window.UserInstance = Backbone.Model.extend({
		defaults:{
			id : '',
			imageId : '',
			reservationId : '',
			instanceState : '',
			code : '',
			status : '',
			state : {
				stateCode: 0,
				stateName: "",
				stateReasonCode: null,
				stateReasonMessage: null,
				stateTransitionReason: ""			
			},
			publicDnsName : '',
			privateDnsName : '',
			publicIpAddress : '',
			privateIpAddress : '',
			architecture : '',
			type : '',
			lifecycle : '',
			subnetId : '',
			launchTime : '',
			vpcId : '',
			scheduledStopAt : ''
		}	
	});

	// UserInstances Collection 
	window.UserInstanceCollection = Backbone.Collection.extend({
		model : UserInstance,
		comparator: function(ui){
			return 100000000 - parseInt(ui.id);
		},
		awsId : 1001,
		regionId : 4,
		url: function(){
			var dr = new Date();
			return 'services/instances?id='+this.awsId+'&regionId='+this.regionId+'&_='+dr.getTime();
		}
	});	
	
	// Get Instances of UserInstances Collection
	window.UserInstances = new UserInstanceCollection;			
	
	function instanceActionFunc(post_data,el,instanceId){
		$.ajax({ 
			url: "services/instances", 
			type: "POST",
			cache: false,
			data : JSON.stringify(post_data),
			dataType: "json",
			processData : true,
			contentType: "application/json; charset=utf-8",
			success: function(instance){				
				UserInstances.remove(UserInstances.get(instanceId));			
				UserInstances.add(instance);	
				var newInstance = UserInstances.get(instanceId);
				newInstance.showNow = true;
				var newRow = $(buildInstanceRow(newInstance));
				$('#instance-'+instanceId).replaceWith(newRow);
				$('.view-button',('#instance-'+instanceId)).text('Hide');							
				// if open
				closeSmallDialog();				
			},
			error:function(e){
				alert('error');
			}
		});	
	}	

	$('body').on('click','.instance-stop',function(){
		var button = $(this);
		if(!button.hasClass('disabled-button')){
			var instanceId = button.attr('data-instance-id');
			var post_data = {
				credentials : { id: $('#aws_instance_credentials').val() } ,
				instanceIds  : [instanceId],
				regionId : $('#aws_instance_regions').val(),
				stateChange: 'STOP'
			}

			if(confirm('Are you sure you wish to STOP the selected instance?')){
				instanceActionFunc(post_data,button,instanceId);
			}
		}
	});
	
	$('body').on('click','.instance-start,.instance-edit',function(){	
		var button = $(this);
		if(!button.hasClass('disabled-button')){
			var instanceId = button.attr('data-instance-id');	
			var optUpdate = button.hasClass('instance-edit');
			var updateTitle = optUpdate ? 'Edit ' : '';
			var html = 	'\
				<div id="cloud_deploy">\
					<div class="cloud_deploy_header_container_singlepage" style="width:460px;">\
						<span style="color: #fff;font-size:16px;font-weight:bold;">'+updateTitle+'Start Options : "'+instanceId+'"</span>\
					</div>\
					<div class="cloud_deploy_singlepage_content" id="start-deployment-type-container"  style="width:460px;height:135px;">\
						<div class="cdc_input_desc_text start-deployment-row" id="start-always" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
							<div class="bc-checkbox"></div>\
							Run until I click STOP or DELETE.\
						</div>\
						<div class="cdc_input_desc_text start-deployment-row" id="start-hours-config" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
							<div class="bc-checkbox"></div>\
							Stop after \
							<select id="deployment_stop_hours" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
								<option value="1">1</option><option value="2">2</option><option value="3" selected>3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option>\
							</select> hours.\
						</div>\
						<div class="cdc_input_desc_text start-deployment-row" id="start-date-config" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
							<div class="bc-checkbox"></div>\
							Stop on \
							<input type="text" id="deployment_stop_date" name="test" class="cdc_input_select" style="margin-top:0px;width:100px;padding:5px;" /> at \
							<select id="deployment_stop_time" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
								<option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5" selected>5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="0">12</option>\
							</select>\
							<select id="deployment_stop_ampm" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
								<option value="am">AM</option><option value="pm" selected>PM</option>\
							</select>\
						</div>\
					</div>\
					<div class="cloud_deploy_button_container_singlepage" style="bottom:0px;width:460px">\
						<span class="cloud_deploy_button" id="start_deployment"  style="width:80px;text-align:center;">Start</span>\
						<span class="cloud_deploy_button" id="start_cancel" style="float:left;">Cancel</span>\
					</div>\
				</div>\
			';
			
			openSmallDialog(255,500,20,20,255,500,'',function(){},html,true);		
			
			$('#start_cancel').on('click',function(){
				closeSmallDialog();
			});	
			
			// Set default date to tomorrow
			var tom = new Date();tom.setUTCDate(tom.getUTCDate());tom.setUTCHours(0);tom.setUTCMilliseconds(0);tom.setUTCMinutes(0);
			var tomorrowString = (tom.getUTCMonth()+1) +'/'+tom.getUTCDate()+'/'+(tom.getFullYear()+'');	
			$('#deployment_stop_date').val(tomorrowString).datepicker({ dateFormat: "m/d/yy" });
			
			// Click/select behavior for start types
			$('#start-deployment-type-container').on('click','.bc-checkbox',function(){
				var row = $(this).parent();
				if(row.hasClass('start-deployment-row-selected')){
					row.removeClass('start-deployment-row-selected');
				}else{
					$('.start-deployment-row-selected',$('#start-deployment-type-container')).removeClass('start-deployment-row-selected');
					row.addClass('start-deployment-row-selected');
				}
			});	

			$('#start_deployment').on('click',function(){
				var stateChangeType = optUpdate ? 'EDIT' : 'START';
				var post_data = {
					credentials : { id: $('#aws_instance_credentials').val() } ,
					instanceIds  : [instanceId],
					regionId : $('#aws_instance_regions').val(),
					stateChange: stateChangeType,
					scheduleType : '',
					scheduleValue: ''
				}
			
				var validated = false;
				var validationMessage = 'Please select a start option.';
			
				var startRow = $('.start-deployment-row-selected',$('#start-deployment-type-container'));
				if(startRow.length){
					validationMessage = 'Please select the date at which to stop the deployment, and make sure it\'s at least one hour from now.';
					var startRowId = startRow.attr('id');
					if(startRowId === 'start-always'){
						post_data.scheduleType = 'INDEFINITE';
						post_data.scheduleValue = '';
						validated = true;
					}else if(startRowId === 'start-hours-config'){
						post_data.scheduleType = 'HOURLY';
						post_data.scheduleValue = $('#deployment_stop_hours').val();	
						validated = true;
					}else if(startRowId === 'start-date-config'){
						post_data.scheduleType = 'STOP_DATE';
						//var selDate = $('#deployment_stop_date').datepicker( "getDate" );
						var selHour = $('#deployment_stop_time').val();
						var selAMPM = $('#deployment_stop_ampm').val();
						var selectedDate = $('#deployment_stop_date').datepicker( "getDate" );
						// Insure date is in the future.
						var rightNow = new Date();
						var selectedHours = (selAMPM == 'am') ? selHour : (parseInt(selHour) + 12);
						selectedDate.setUTCHours(selectedHours);
						selectedDate.setUTCMinutes(selectedDate.getUTCMinutes() + (rightNow.getTimezoneOffset()));
						if(selectedDate && selectedDate.getTime() && selectedDate.getTime() >= rightNow.getTime()){
							post_data.scheduleValue = selectedDate.getTime();
							validated = true;
						}else{
							validated = false;
						}
					}
				}
			
				// Submit if validated
				if(validated){
					instanceActionFunc(post_data,button,instanceId);
				}else{
					alert(validationMessage);
				}
			});		
		}
	});
	
	$('body').on('click','.instance-delete',function(){	
		var button = $(this);
		var instanceId = button.attr('data-instance-id');	
		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage" style="width:460px;">\
					<span style="color: #fff;font-size:16px;font-weight:bold;">Delete : "'+ instanceId+'"</span>\
				</div>\
				<div class="cloud_deploy_singlepage_content" id="start-deployment-type-container"  style="width:460px;height:115px;">\
					<div class="cdc_input_desc_text start-deployment-row" id="start-hours-config" style="width:400px;font-size:14px;margin-bottom:0px;margin-top:25px;margin-left:33px;font-weight:normal;line-height:30px;">\
						Type "YES" to DELETE:  \
						<input type="text" id="delete-verify-string" name="test" class="cdc_input_select" style="margin-top:0px;margin-left:10px;width:100px;padding:5px;" />\
					</div>\
					<div class="cdc_input_desc_text start-deployment-row" id="start-hours-config" style="width:400px;font-size:10px;margin-bottom:0px;margin-top:12px;margin-left:33px;font-weight:normal;line-height:15px;">\
						Deleting an Instance will destroy all associated data and cannot be undone.\
					</div>\
				</div>\
				<div class="cloud_deploy_button_container_singlepage" style="bottom:0px;width:460px">\
					<span class="cloud_deploy_button" id="delete_deployment"  style="width:80px;text-align:center;">Delete</span>\
					<span class="cloud_deploy_button" id="delete_cancel" style="float:left;">Cancel</span>\
				</div>\
			</div>';
		
		openSmallDialog(235,500,20,20,235,500,'',function(){},html,true);		
		
		$('#delete_cancel').on('click',function(){
			closeSmallDialog();
		});	
		
		$('#delete_deployment').on('click',function(){
			var post_data = {
				credentials : { id: $('#aws_instance_credentials').val() } ,
				instanceIds  : [instanceId],
				regionId : $('#aws_instance_regions').val(),
				stateChange: 'TERMINATE'
			}
		
			var validated = false;
			var validationMessage = 'Please type "YES" into the box.';
			var termString = $.trim($('#delete-verify-string').val());
			
			if(termString && termString === 'YES'){
				validated = true;
			}
		
			// Submit if validated
			if(validated){
				instanceActionFunc(post_data,button,instanceId);
			}else{
				alert(validationMessage);
			}
		});		
	});	
	
	$('#user-aws-instance-list-container').on('click','#refresh-aws-instances',function(){
		var sel= $('#aws_instance_credentials');
		var ri = $('#aws_instance_regions');
		UserInstances.awsId = sel.val();
		UserInstances.regionId = ri.val();
		UserInstances.fetch({
			success: function(collection, response){
				var options = {
					pageNum : 0,
					perPage : collection.perPage
				}				
				refreshInstancesView(collection,options);
			},
			error : function(collection, response){
				alert('AWS Keys selected are not valid.  Please select or create a valid set of AWS Keys.');
			}
		});		
	});

	window.buildInstanceRow = function(inst){
		var statusColor = getDeploymentStateColor(inst.get('instanceState'));
		var publicIpAddress = inst.get('publicIpAddress') ? inst.get('publicIpAddress') : '';
		var lineTwo = '<span style="color:#777;float:left;border-right:0px solid #777;font-weight:normal;padding-right:5px;">'+publicIpAddress+'</span><span style="color:#555;float:left;padding-left:5px;">'+inst.get('publicDnsName')+'</span>';
		var instanceName =  inst.get('name')  ? inst.get('name') : '';
		var cad = new Date(parseInt(inst.get('launchTime')));
		var createdAt = (cad.getUTCMonth()+1) +'/'+cad.getUTCDate()+'/'+(cad.getFullYear()+'').substr(2,3);	
		var upTimeString = inst.get('launchTime') ? getDuration(inst.get('launchTime'),'up') : 'n/a' ;
		var stopTimeString = inst.get('scheduledStopAt') ? getDuration(inst.get('scheduledStopAt'),'down') : 'Never';		
		if(statusColor !== 'green'){
			upTimeString = 'n/a';
		}
		var instanceStatus = inst.get('state') && inst.get('state').stateName ? inst.get('state').stateName : 'n/a';
		var allowStop = 'disabled-button';  var allowStart ='disabled-button';
		if(statusColor === 'grey' ){
			upTimeString = 'n/a';
			stopTimeString = 'n/a';
			if(instanceStatus =='Stopped'){
				allowStart = '';				
			}
		}else{
			allowStop = '';	
		}
		var openStatus = inst.showNow ? 'block' : 'none';
		var infoHTML ='	<div class="app-instance-detail-color-box status-'+statusColor+'" style="height:92px;"></div>\
					<div class="app-instance-detail-info-element" style="margin-left:10px;height:80px;">\
						<div class="metric-header">Instance</div>\
						<div class="metric-row-1">Deployed:</div>\
						<div class="metric-row-2">'+createdAt+'</div>\
						<div class="metric-row-1">Status:</div>\
						<div class="metric-row-2 deployment-status" >'+instanceStatus+'</div>\
					</div>\
					<div class="app-instance-detail-info-element a-detail-small" style="height:80px;">\
						<div class="metric-header">Metrics</div>\
						<div class="metric-row-1">Uptime:</div>\
						<div class="metric-row-2 uptime-value">'+upTimeString+'</div>\
						<div class="metric-row-1">Stop In:</div>\
						<div class="metric-row-2 stoptime-value">'+stopTimeString+'</div>\
					</div>\
					<div class="app-instance-detail-buttons" style="width:350px;height:90px;margin-top:0px;border-bottom: 1px dotted #BDBDBD;">\
						<div class="button blue-style instance-delete"  data-instance-id="'+inst.get('id')+'" style="margin-top:28px;">Delete</div>\
						<div class="button blue-style instance-edit '+allowStop+' "  data-instance-id="'+inst.get('id')+'" style="margin-top:28px;">Edit</div>\
						<div class="button blue-style instance-stop '+allowStop+' "  data-instance-id="'+inst.get('id')+'" style="margin-top:28px;">Stop</div>\
						<div class="button blue-style instance-start '+allowStart+' "  data-instance-id="'+inst.get('id')+'" style="margin-top:28px;">Start</div>\
					</div>';
		// Header row with included detail information
		return	'<div class="app-box-list-element" id="instance-'+inst.get('id')+'">\
					<div class="app-box-list-header">\
						<div class="list-app-status status-'+statusColor+'"></div>\
						<div class="list-app-icon"></div>\
						<div class="list-app-name">\
							<div style="width:1200px;height:22px;line-height:22px;font-size:14px;"><span style="float:left;">'+inst.get('id')+'</span><span style="font-size:12px;float:left;margin-left:5px;border-left:1px dotted #777;padding-left:5px;color:#555;font-weight:normal;">'+instanceName+'</span></div>\
							<div style="width:1200px;color:#9c9c9c;">'+lineTwo+'</div>\
						</div>\
						<div class="list-app-buttons">\
							<div class="button blue-style	view-button"	>Details</div>\
						</div>\
					</div>\
					<div class="app-box-list-content" style="height:100px;display:'+openStatus+'">'+infoHTML+'</div>\
				</div>';
	}
	
	window.listInstances = function(collection,options){
		var container = $('#app-box-aws-instances'),
			instanceContent = '';
		if(collection.length){
			for(var i = options.startIndex;i<options.endIndex;i++){		
				var deployment = collection.at(i);
				instanceContent += buildInstanceRow(deployment);
			}			
		}else{
			instanceContent = '<div  style="width:700px;float:left;height:500px;line-height:500px;font-size:18px;font-weight:bold;color:#777;text-align:center;">\
											No Instances Running\
										</div>';
		}
		container.html(instanceContent);	
	}

	window.refreshInstancesView = function(collection,options){
		var pageNum = options.pageNum ? options.pageNum : 0;
		var numPerPage = options.perPage ? options.perPage : collection.perPage;
		var numPages = Math.ceil(collection.length / numPerPage);	
		var startIndex = pageNum * numPerPage;
		var endIndex = (pageNum + 1) * numPerPage;
		endIndex = endIndex > collection.length ? collection.length : endIndex;
		UserInstances.page = pageNum;	
	
		var con = $('#app-box-aws-instances');
		// Clear existing list of products
		con.html('');
		$('.aws-instances-list-header-page-text')	.text( 'Page '+(pageNum+1)+' of '+numPages);
		$('#aws-instances-list-footer-num-details')	.text( 'Showing Instances '+(startIndex+1)+'-'+endIndex+' of '+collection.length);	
		if(collection.length == 0){
			$('.aws-instances-list-header-page-text')	.text('');
			$('#aws-instances-list-footer-num-details')	.text('No Instances Found');			
		}	
		
		var options = {
			startIndex : startIndex,
			endIndex : endIndex
		}
		listInstances(collection,options);
	}	
	
	$('#user-aws-instance-list-container').on('click','.awsInstancesPageNext',function(){
		var nextPageNum = UserInstances.page + 1;	
		var numPages = Math.ceil(UserInstances.length / UserInstances.perPage);
		if(nextPageNum < numPages){
			var options = {
				pageNum : nextPageNum,
				perPage : UserInstances.perPage
			}				
			refreshInstancesView(UserInstances,options);
		}
	});
	
	$('#user-aws-instance-list-container').on('click','.awsInstancesPagePrevious',function(){
		var previousPageNum = UserInstances.page - 1;	
		var numPages = Math.ceil(UserInstances.length / UserInstances.perPage);
		if(previousPageNum >= 0){
			var options = {
				pageNum : previousPageNum,
				perPage : UserInstances.perPage
			}				
			refreshInstancesView(UserInstances,options);
		}
	});		
	
	
	
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
			menu.css({top:(41),left:offset.left,'z-index':(zIndex+10)}).html(html);

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
			return 'services/company/getIndustries?_='+dr.getTime();
		}
	});	
	
	// Get Instances of Industries
	window.Industries = new IndustryCollection;		
	Industries.fetch();		
	
	//  UserAWS
	window.UserAWS = Backbone.Model.extend({
		defaults:{
			id: -1,
			name: "",			
			awsKey: "",
			secretKey: ""
		}	
	});

	// UseAWSCollection 
	window.UserAWSCollection = Backbone.Collection.extend({
		model : UserAWS,
		url: function(){
			var dr = new Date();
			return 'services/user/getAmazonCredentials?_='+dr.getTime();
		}
	});	
	
	// Get Instances of UserAWSCollection
	window.UserAWSList = new UserAWSCollection;		
	
	UserAWSList.fetch({
		success:function(collection,response){
			initInstances();
		},
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
					<div id="cloud_admin_header"  style="width:480px;color:#fff;">AWS Credentials</div>\
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
			var industry_list = '<option value="-1" selected></option>';
			Industries.each(function(industry){
				industry_list += '<option value="'+industry.get('id')+'">'+industry.get('name')+'</option>';
			});	
			window.myVars = {}
			var html = 	'\
				<div id="cloud_admin">\
					<div id="cloud_admin_header" style="color:#fff;">'+$.i18n.prop('super.4')+' <span class="admin_settings_title_name" style="color:#fff;">'+myEmail+'</span><span class="admin_settings_title_async_msg">'+$.i18n.prop('account.14')+'</span></div>\
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
						<div class="ca_col ca_col_three" id="comp-name">\
							<div class="ca_input_desc_text">'+$.i18n.prop('account.6')+'<span class="required">*</span></div>\
							<input type="text" value="" id="company_name" class="ca_input" />\
							<input type="hidden" value="" id="company_id" class="ca_input" />\
							<div class="ca_input_desc_text">Industry<span class="required">*</span></div>\
							<select name="test" class="ca_input_select" id="industry_id">'+industry_list+'</select>\
							<div class="ca_input_desc_text">Infor ID</div>\
							<input type="text" value="" id="infor_id" class="ca_input" />\
						</div>\
					</div>\
					<div id="cloud_admin_button_container">\
						<span class="cloud_admin_button" id="cloud-admin-save" style="width:60px;text-align:center;">'+$.i18n.prop('account.12')+'</span>\
						<span class="cloud_admin_button" id="cloud-admin-cancel" style="float:left;">'+$.i18n.prop('account.13')+'</span>\
					</div>\
				</div>';
			
			openBigDialog(450,755,20,20,450,755,html,true);

			$('#company_name').autocomplete(companyAutoCompleteSetup);	
			$('#industry_id').on('change',function(){$('#company_id').val(-1);});	
			$('#infor_id').on('change',function(){$('#company_id').val(-1);});	
			
			$('#cloud-admin-cancel').on('click',function(){
				closeBigDialog();
			});			
			
			$('#change_password').on('click',function(){
				$('#password_once').prop('disabled',false).val('').focus();
				$('#password_twice').prop('disabled',false).val('');
			});
			
			$('#cloud-admin-save').on('click',function(){
				// Update settings
				var button = $(this);
				button.text($.i18n.prop('account.18'));
				
				var post_data = {
					language	 		: $.trim($('#account_language').val()),
					companyName 	 	: $.trim($('#company_name').val()),
					companyId			: $.trim($('#company_id').val()),
					industryId			: $.trim($('#industry_id').val()),
					inforId				: $.trim($('#infor_id').val()),
					address1 	 		: $.trim($('#user_address_1').val()),
					address2 	 		: $.trim($('#user_address_2').val()),
					country 	 		: $.trim($('#user_country').val()),
					phone				: $.trim($('#user_phone').val())
				}			
				
				var validatedPass = true;
				if(!$('#password_once').prop('disabled')){
					post_data.password = $.trim($('#password_once').val());
					post_data.password2 = $.trim($('#password_twice').val());		
					validatedPass = (post_data.password === post_data.password2);				
				}
				
				var validatedRequired = post_data.language && post_data.companyName && post_data.industryId && post_data.address1 &&
										post_data.address2 && post_data.country && post_data.phone;			
				
				
				if(($.trim(window.myVars.companyName) != post_data.companyName) || 
					($.trim(window.myVars.inforId) != post_data.inforId) ||
					($.trim(window.myVars.industryId) != post_data.industryId)){
						// Next, add caching to company results and look there for this data before setting companyId to -1.
						post_data.companyId = -1;
				}				
				
				
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
							window.myVars = resp;
							button.text($.i18n.prop('account.12'));
							$('#cloud_admin_cancel').text($.i18n.prop('account.19'));
							
							$('#password_once').val('********************').prop('disabled',true);
							$('#password_twice').val('********************').prop('disabled',true);						
							$('#account_language').val(resp.language);
							$('#company_name').val(resp.companyName);
							$('#company_id').val(resp.companyId);
							$('#industry_id').val(resp.industryId);
							$('#infor_id').val(resp.inforId);
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
					window.myVars = resp;
					$('#cloud-admin-save').text('Save');
					$('#account_language').val(resp.language);
					$('#company_name').val(resp.companyName);
					$('#company_id').val(resp.companyId);
					$('#industry_id').val(resp.industryId);
					$('#infor_id').val(resp.inforId);
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
	
	$('#deploy-buy').on('click', function(){
		var button = $(this);
		var iam = button.attr('data-app-name');
		var product_id = button.attr('data-app-id');

		var html = 	'\
			<div id="cloud_deploy">\
				<div class="cloud_deploy_header_container_singlepage">\
					<span style="color: #fff;font-size:16px;font-weight: bold;">'+$.i18n.prop('buy.dialog.1')+'</span>\
				</div>\
				<div class="cloud_deploy_singlepage_content">\
				    <div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:15px;font-weight:bold;font-size:12px;line-height:30px;">'+$.i18n.prop('consult.dialog.2')+'</div>\
					<input type="text" value="" class="cdc_input" id="request_info_email" style="margin-top:0px;margin-left:0px;width:208px;"/><br />\
					<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:35px;font-weight:bold;font-size:12px;line-height:30px;">'+$.i18n.prop('consult.dialog.3')+'</div>\
					<input type="text" value="" class="cdc_input" id="request_info_phone"style="margin-top:0px;margin-left:0px;width:208px;"/><br />\
					<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:35px;font-weight:bold;font-size:12px;line-height:30px;">'+$.i18n.prop('buy.dialog.4')+'</div>\
					<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:0px;font-size:12px;line-height:30px;">1 (800) 260-2640</div>\
					<div class="cdc_info_container" style="left:255px;top:20px;">\
						<div class="cdc_arrow" style="margin-top:11px;"></div>\
						<span class="cdc_info_text">'+$.i18n.prop('buy.dialog.6',iam)+'</span>\
					</div>\
					<div class="cdc_info_container" style="left:255px;top:117px;">\
						<div class="cdc_arrow" style="margin-top:11px;"></div>\
						<span class="cdc_info_text">'+$.i18n.prop('buy.dialog.7',iam)+'</span>\
					</div>\
					<div class="cdc_info_container" style="left:255px;top:217px;">\
						<div class="cdc_arrow" style="margin-top:11px;"></div>\
						<span class="cdc_info_text">'+$.i18n.prop('buy.dialog.8')+'</span>\
					</div>\
			    </div>\
				<div class="cloud_deploy_button_container_singlepage">\
					<span class="cloud_deploy_button" id="request_sales_contact_confirm" data-app-name="'+iam+'"style="width:120px;text-align:center;">'+$.i18n.prop('buy.dialog.9')+'</span>\
					<span class="cloud_deploy_button" id="cloud_purchase_cancel" style="float:left;">'+$.i18n.prop('buy.dialog.10')+'</span>\
				</div>\
			</div>\
		';
		
		openBigDialog(450,500,20,20,450,500,html,true);
		
		$('#cloud_purchase_cancel').on('click',function(){
			// 1. Close Dialog
			closeBigDialog();
		});		
		
		$('#request_sales_contact_confirm').on('click', function(){
			var button2 = $(this);	
			button2.text($.i18n.prop('buy.dialog.11'));

			var post_data = {
				productId : product_id,
				email: $('#request_info_email').val(),
				phone: $('#request_info_phone').val()
			}
		
			if(post_data.email != '' || post_data.phone != '' ){
				$.ajax({ 
					url: "services/buy", 
					type: "POST",
					cache: false,
					data : JSON.stringify(post_data),
					dataType: "json",
					processData : true,
					contentType: "application/json; charset=utf-8",		
					success: function(resp){	
						// Hide existing dialogs
						closeBigDialog();

						var iam = button2.attr('data-app-name');
						var prod = iam ? '<span style="color:#990000;">'+iam+'</span>' : $.i18n.prop('buy.dialog.13');
						var html = 	'\
							<div id="cloud_deploy">\
								<div class="cloud_deploy_header_container_singlepage">\
									<span style="font-weight:bold;color:#fff;font-size:16px;">'+$.i18n.prop('buy.dialog.14')+'</span>\
								</div>\
								<div class="cloud_deploy_singlepage_content" style="height:115px;">\
									<div class="cdc_input_desc_text" style="margin-bottom:12px;margin-top:0px;font-weight:bold;width:auto;">'+$.i18n.prop('buy.dialog.15', prod)+'</div>\
									<div class="cdc_input_desc_text" style="margin-bottom:15px;margin-top:7px;width:auto;height:auto;line-height:11px;">'+$.i18n.prop('buy.dialog.16')+'</div>\
									<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:0px;margin-left:30px;height:auto;line-height:11px;">'+$.i18n.prop('buy.dialog.17')+': <span style="color:#5468DC;font-weight:bold;">'+resp.email+'</span></div>\
									<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:0px;margin-left:30px;height:auto;line-height:11px;">'+$.i18n.prop('buy.dialog.18')+': <span style="color:#5468DC;font-weight:bold;">'+resp.phone+'</span></div>\
								</div>\
								<div class="cloud_deploy_button_container_singlepage">\
									<span class="cloud_deploy_button" id="cloud_deployed_close" style="width:120px;text-align:center;">'+$.i18n.prop('buy.dialog.19')+'</span>\
								</div>\
							</div>\
						';
						
						openBigDialog(235,500,20,20,235,500,html,true);
						
						$('#cloud_deployed_close').on('click',function(){
							// 1. Close Dialog
							closeBigDialog();
						});		
					},
					error: function(){
						button2.text($.i18n.prop('buy.dialog.9'));
						alert($.i18n.prop('buy.dialog.20'));
					}
				});
			}else{
				button2.text($.i18n.prop('buy.dialog.9'));
				alert($.i18n.prop('buy.dialog.12'));
			}
		});	
	});	
	
	window.availableInfor24Deploy = function(){
		var hasOptions = false;
		var infor24 = false;
		UserProducts.each(function(product){
			infor24 = product.get('availability').trial && product.get('security').trial;		
			if(infor24){
				hasOptions = true;
			}
		});
		return hasOptions;		
	}
	
	window.availableAWSDeploy = function(){
		var hasOptions = false;
		var aws = false;
		UserProducts.each(function(product){
			aws = product.get('availability').deployment && product.get('security').deployment;			
			if(aws){
				hasOptions = true;
			}
		});
		return hasOptions;	
	}
	
	window.availableDeployOptions = function(){
		var hasOptions = false;
		var infor24 = false;
		var aws = false;
		UserProducts.each(function(product){
			infor24 = product.get('availability').trial && product.get('security').trial;
			aws = product.get('availability').deployment && product.get('security').deployment;			
			if(infor24 || aws){
				hasOptions = true;
			}
		});
		return hasOptions;	
	}
	
	window.displayDeployProductList = function(type,regionId){
		var products = '';
		UserProducts.each(function(product){
			var available = false;
			var security = false;
			if(type ==='INFOR24'){
				available = product.get('availability').trial;
				security = product.get('security').trial;
			}else if(type === 'AWS'){
				available = product.get('availability').deployment;
				security = product.get('security').deployment;			
			}
			if(available && security){
				var subName = product.get('displayName2') ? '<span class="display-product-name-small">'+product.get('displayName2')+'</span>' : '';
				var version = product.get('versions')[0]; // by default, get the latest version
				if( type!=='AWS' || version.region_id == regionId){ 
					products += '\
						<div class="display-product-row" data-product-id="'+product.get('id')+'" data-product-version-id="'+version.id+'">\
							<div class="display-product-checkbox"></div>\
							<div class="display-product-name">'+product.get('displayName1')+' '+subName+'</div>\
							<div class="display-product-version">'+version.description+'</div>\
						</div>';
				}
			}
		});
		return products;
	}
	
	window.getAvailableAWSRegions = function(type){
		var regions = [];
		UserProducts.each(function(product){
			var available = false;
			var security = false;
			if(type ==='INFOR24'){
				available = product.get('availability').trial;
				security = product.get('security').trial;
			}else if(type === 'AWS'){
				available = product.get('availability').deployment;
				security = product.get('security').deployment;			
			}
			if(available && security){
				var subName = product.get('displayName2') ? '<span class="display-product-name-small">'+product.get('displayName2')+'</span>' : '';
				var version = product.get('versions')[0]; // by default, get the latest version
				regions.add(version.region_id);
			}
		});
		return regions.unique();		
	}
	
	$('body').on('click', '#global-deploy,.user-deploy',function(){
		var button = $(this);
		if(!button.hasClass('disabled')){
			var iam = 'your Infor10 suite';		
			var aws_options_list = '';
			UserAWSList.each(function(awscred){
				aws_options_list += '<option value="'+awscred.get('id')+'">'+awscred.get('name')+'</option>';
			});			
			var availableRegions = getAvailableAWSRegions('AWS')
			var aws_region_list = '';
			$.each(availableRegions,function(i,regionId){
				var region = Regions.get(regionId)
				aws_region_list +=  '<option value="'+region.get('id')+'">'+(region.get('name')).toUpperCase()+'</option>';
			});
			var forOtherUser = button.hasClass('user-deploy');
			var deployHeader = '';
			if(forOtherUser && Users.get(button.attr('data-user-id'))){
				var user = Users.get(button.attr('data-user-id'));
				deployHeader = ' on behalf of '+user.get('user').firstName+' '+user.get('user').lastName;
			}
			var infor24SelectButton = availableInfor24Deploy() ? 'select_button' : 'select_button_disabled';
			var awsSelectButton =  availableAWSDeploy() ? 'select_button' :'select_button_disabled';

			var html = 	'\
				<div id="cloud_deploy">\
					<div class="cloud_deploy_header_container_singlepage" style="width:560px;">\
						<span style="color: #fff;font-size:16px;font-weight:bold;">Deploy '+deployHeader+'</span>\
					</div>\
					<div class="cloud_deploy_singlepage_content"  style="width:560px;height:400px;">\
						<div class="deploy-section-header" id="deployment-text-header">Type</div>\
						<input type="hidden" id="deployment-type" value="" />\
						<div class="deploy-page" id="deploy-page-0" data-page-title="Type" style="display:block;">\
							<div class="deployment-type-box">\
								<div class="dtb-logo infor-24-logo"></div>\
								<div class="dtb-description">\
									<div class="desc-row">Zero cost EAM, XM and Syteline trial environments.</div>\
									<div class="desc-row">30 day expiration.</div>\
									<div class="desc-row">Ideal for short term, high level product evaluations.</div>\
								</div>\
								<div class="dtb-select">\
									<div class="'+infor24SelectButton+'" id="select-infor-24">Select</div>\
								</div>\
							</div>\
							<div class="deployment-type-box">\
								<div class="dtb-logo aws-logo"></div>\
								<div class="dtb-description">\
									<div class="desc-row">AWS account required. </div>\
									<div class="desc-row">All hosting fees are the responsibility of the account holder.</div>\
									<div class="desc-row">Intended for more involved pre-sales engagements.</div>\
								</div>\
								<div class="dtb-select">\
									<div class="'+awsSelectButton+'" id="select-aws">Select</div>\
								</div>\
							</div>\
						</div>\
						<div class="deploy-page" id="deploy-page-1" data-page-title="Details" style="display:none;">\
							<div class="cdc_input_desc_text" style="font-size:12px;margin-bottom:0px;margin-top:15px;margin-left:14px;font-weight:bold;line-height:30px;float:left;">Deployment Name</div>\
							<input type="text" id="aws_deployment_name" name="test" class="cdc_input_select" style="margin-top:0px;margin-left:14px;width:213px;margin-right:120px;padding:5px;" />\
							<div class="cdc_input_desc_text aws-type" style="font-size:12px;margin-bottom:0px;margin-top:15px;margin-left:14px;font-weight:bold;line-height:30px;">AWS Credentials</div>\
							<select id="aws_deployment_credentials" name="test" class="cdc_input_select aws-type" style="margin-top:0px;margin-left:14px;width:225px;">\
								<option value="-1"></option>\
								'+aws_options_list+'\
							</select>\
							<div class="cdc_input_desc_text aws-type" style="font-size:12px;margin-bottom:0px;margin-top:15px;margin-left:14px;font-weight:bold;line-height:30px;">AWS Region</div>\
							<select id="aws_deployment_region" name="test " class="cdc_input_select aws-type" style="margin-top:0px;margin-left:14px;width:225px;">\
								'+aws_region_list+'\
							</select>\
							<div class="cdc_info_container" style="left:269px;margin-top:12px;">\
								<div class="cdc_arrow" style="margin-top: 24px;"></div>\
								<span class="cdc_info_text">Please give the deployment a meaningful name, as this is the method you\'ll use to refer to this deployment in the future.</span>\
							</div>\
							<div class="cdc_info_container aws-type" style="left:269px;margin-top:88px;">\
								<div class="cdc_arrow" style="margin-top: 28px;"></div>\
								<span class="cdc_info_text">Please select which set of AWS Credentials to use for this deployment.  You may edit your list of available AWS credentials in the AWS Credentials settings menu.</span>\
							</div>\
							<div class="cdc_info_container aws-type" style="left:269px;margin-top:175px;">\
								<div class="cdc_arrow" style="margin-top: 22px;"></div>\
								<span class="cdc_info_text">Please select which AWS Region you would like to deploy to.  Selecting a region closer to your customer, will increase performance and create a better user experience.</span>\
							</div>\
						</div>\
						<div class="deploy-page" id="deploy-page-2" data-page-title="Products" style="display:none;">\
							<div class="display-product-list-container infor24-type">\
									'+displayDeployProductList('INFOR24')+'\
							</div>\
							<div class="display-product-list-container aws-type">\
									'+displayDeployProductList('AWS')+'\
							</div>\
						</div>\
						<div class="deploy-page" id="deploy-page-3" data-page-title="Start Options" style="display:none;">\
							<div class="cdc_input_desc_text start-deployment-row aws-type" id="start-always" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
								<div class="bc-checkbox"></div>\
								Run until I click STOP or DELETE.\
							</div>\
							<div class="cdc_input_desc_text start-deployment-row aws-type" id="start-hours-config" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
								<div class="bc-checkbox"></div>\
								Stop after \
								<select id="deployment_stop_hours" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
									<option value="1">1</option><option value="2">2</option><option value="3" selected>3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option>\
								</select> hours.\
							</div>\
							<div class="cdc_input_desc_text start-deployment-row aws-type" id="start-date-config" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
								<div class="bc-checkbox"></div>\
								Stop on \
								<input type="text" id="deployment_stop_date" name="test" class="cdc_input_select" style="margin-top:0px;width:100px;padding:5px;" /> at \
								<select id="deployment_stop_time" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
									<option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5" selected>5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="0">12</option>\
								</select>\
								<select id="deployment_stop_ampm" name="test" class="cdc_input_select" style="margin-top:0px;width:65px;">\
									<option value="am">AM</option><option value="pm" selected>PM</option>\
								</select>\
							</div>\
							<div class="cdc_input_desc_text start-deployment-row infor24-type" id="start-date-config" style="font-size:12px;margin-bottom:0px;margin-top:10px;margin-left:14px;font-weight:normal;line-height:30px;">\
								Infor24 deployments expire after 30 days. <br />This deployment will stop on <span id="deployment_stop_date_infor24" style="font-weight:bold;"></span> at <span id="deployment_stop_ampm_infor24" style="font-weight:bold;">5pm</span>.\
							</div>\
							<div class="cdc_input_desc_text" id="terms-and-cond-row" style="bottom:5px;cursor:pointer;float:left;line-height:30px;margin-left:15px;margin-top:10px;position:absolute;font-weight:bold;width:430px;clear:both;"><div class="terms-and-cond-checkbox"></div>I have read and agree to the <span style="color:#CC121B;text-decoration:none;">Terms &amp; Conditions</span> linked <a href="http://www.infor.com/content/LegalNotice/" target="_blank" style="color:#CC121B;text-decoration:none;">here</a>.</div>\
						</div>\
					</div>\
					<div class="cloud_deploy_button_container_singlepage" style="bottom:0px;width:560px">\
						<span class="cloud_deploy_button" id="deploy_to_amazon_confirmation" data-app-name="'+iam+'" style="width:120px;text-align:center;display:none;">'+$.i18n.prop('aws.dialog.12')+'</span>\
						<span class="cloud_deploy_button" id="deploy-next" style="width:120px;text-align:center;">Next</span>\
						<span class="cloud_deploy_button" id="deploy-previous" style="">Previous</span>\
						<span class="cloud_deploy_button" id="cloud_deploy_single_cancel" style="float:left;">'+$.i18n.prop('aws.dialog.13')+'</span>\
					</div>\
				</div>';
			
			openBigDialog(520,600,20,20,520,600,html,true);		
			
			$('#cloud_deploy_single_cancel').on('click',function(){
				closeBigDialog();
			});		
			
			// Click/select behavior for infor24 products (radio button style)
			$('.display-product-list-container.infor24-type').on('click','.display-product-row',function(){
				var row = $(this);
				$('.display-product-row-selected').removeClass('display-product-row-selected');
				row.addClass('display-product-row-selected');
			});
			
			// multiselect for AWS products
			$('.display-product-list-container.aws-type').on('click','.display-product-row',function(){
				var row = $(this);
					if(row.hasClass('display-product-row-selected')){
						row.removeClass('display-product-row-selected');
					}else{
						row.addClass('display-product-row-selected');
					}
			});		
			
			// Set default date to tomorrow
			var tom 	= new Date();tom.setUTCDate(tom.getUTCDate());tom.setUTCHours(0);tom.setUTCMilliseconds(0);tom.setUTCMinutes(0);
			var tomorrowString = (tom.getUTCMonth()+1) +'/'+tom.getUTCDate()+'/'+(tom.getFullYear()+'');	
			$('#deployment_stop_date').val(tomorrowString).datepicker({ dateFormat: "m/d/yy" });
			var td 	= new Date();td.setUTCDate(td.getUTCDate()+30);td.setUTCHours(0);td.setUTCMilliseconds(0);td.setUTCMinutes(0);
			var thirtyDayString = (td.getUTCMonth()+1) +'/'+td.getUTCDate()+'/'+(td.getFullYear()+'');
			$('#deployment_stop_date_infor24').text(thirtyDayString);
			
			// Click/select behavior for start types
			$('#cloud_deploy').on('click','.bc-checkbox',function(){
				var row = $(this).parent();
				if(row.hasClass('start-deployment-row-selected')){
					row.removeClass('start-deployment-row-selected');
				}else{
					$('.start-deployment-row-selected',$('#cloud_deploy')).removeClass('start-deployment-row-selected');
					row.addClass('start-deployment-row-selected');
				}
			});		
			
			// Click/select behavior for terms and conditions
			$('.cloud_deploy_singlepage_content').on('click','#terms-and-cond-row',function(){
				var row = $(this);
				if(row.hasClass('terms-and-cond-selected')){
					row.removeClass('terms-and-cond-selected');
				}else{
					row.addClass('terms-and-cond-selected');
				}
			});			
			
			$('#select-infor-24').on('click',function(){
				if(!$(this).hasClass('select_button_disabled')){
					$('#select-aws').removeClass('type_selected').text('Select');
					$(this).addClass('type_selected').text('Selected');		
					$('#deployment-type').val('INFOR24');
					selectedScheduleType = 'INDEFINITE';
				}
			});
			
			$('#select-aws').on('click',function(){
				if(!$(this).hasClass('select_button_disabled')){
					$('#select-infor-24').removeClass('type_selected').text('Select');
					$(this).addClass('type_selected').text('Selected');		
					$('#deployment-type').val('AWS');
					selectedScheduleType = 'INDEFINITE';
				}
			});			
			
			var selectedDeploymentType = '';
			var selectedAWSCrednetials = -1;
			var selectedAWSRegion = 1;
			var selectedDeploymentName = '';
			var selectedProducts = [];
			var selectedScheduleType = '';
			var selectedScheduleValue = '';
			
			var validateDeploymentType = function(){
				var hasSelectedDeploymentType = false;
				selectedDeploymentType = $('#deployment-type').val();
				if(selectedDeploymentType === 'INFOR24' || selectedDeploymentType === 'AWS'){
					hasSelectedDeploymentType = true;
				}else{
					hasSelectedDeploymentType = false;
				}
				var returnable={
					passed : hasSelectedDeploymentType,
					msg : 'You must select a deployment type.'
				}
				return returnable;			
			}
				
			var validateAWSCredentials = function(){
				var hasSelectedAWSCredentials = false;
				selectedAWSCrednetials = $('#aws_deployment_credentials').val();
				selectedAWSRegion = $('#aws_deployment_region').val();
				if(selectedAWSCrednetials >= 0){
					hasSelectedAWSCredentials = true;
				}else{
					hasSelectedAWSCredentials = false;
				}
				var returnable={
					passed : hasSelectedAWSCredentials,
					msg : 'You must select a set of AWS Credentials.'
				}
				return returnable;
			}
			
			var validateDeploymentName = function(){
				var hasSelectedDeploymentName = false;
				selectedDeploymentName = $.trim($('#aws_deployment_name').val());
				if(selectedDeploymentName){
					hasSelectedDeploymentName = true;
				}else{
					hasSelectedDeploymentName = false;
				}	
				var returnable={
					passed : hasSelectedDeploymentName,
					msg : 'You must input a deployment name.'
				}
				return returnable;
			}
			
			var validateProductsSelected = function(){
				var hasSelectedAProduct = false;
				var productsObj = $('.display-product-row-selected');
				if(productsObj.length > 0){
					$.each(productsObj, function(i,v){
						var prodArray = [$(v).attr('data-product-id'),$(v).attr('data-product-version-id')]; 
						selectedProducts.push(prodArray);
					});
					hasSelectedAProduct = true;
				}else{
					hasSelectedAProduct = false;
				}	
				var returnable={
					passed : hasSelectedAProduct ,
					msg : 'You must select at least one product.'
				}
				return returnable;
			}
			
			var validateStartOptions = function(){
				var hasSelectedSchedule = false;
				var scheduleSelectionMessage = 'Please select a start option.';
				var startRow = $('.start-deployment-row-selected',$('#cloud_deploy'));
				if(startRow.length){
					scheduleSelectionMessage = 'Please select the date at which to stop the deployment, and make sure it\'s after today.';
					var startRowId = startRow.attr('id');
					if(startRowId === 'start-always'){
						selectedScheduleType = 'INDEFINITE';
						selectedScheduleValue = '';
						hasSelectedSchedule = true;
					}else if(startRowId === 'start-hours-config'){
						selectedScheduleType = 'HOURLY';
						selectedScheduleValue = $('#deployment_stop_hours').val();	
						hasSelectedSchedule = true;
					}else if(startRowId === 'start-date-config'){
						selectedScheduleType = 'STOP_DATE';
						var selHour = $('#deployment_stop_time').val();
						var selAMPM = $('#deployment_stop_ampm').val();
						var selectedDate = $('#deployment_stop_date').datepicker( "getDate" );
						var rightNow = new Date();
						var selectedHours = (selAMPM == 'am') ? selHour : (parseInt(selHour) + 12);
						selectedDate.setUTCHours(selectedHours);
						selectedDate.setUTCMinutes(selectedDate.getUTCMinutes() + (rightNow.getTimezoneOffset()));					
						// Insure date is in the future.
						if(selectedDate && selectedDate.getTime() && selectedDate.getTime() >= rightNow.getTime()){
							selectedScheduleValue = selectedDate.getTime();
							hasSelectedSchedule = true;
						}else{
							hasSelectedSchedule = false;
						}
					}
				}else{
					hasSelectedSchedule = false;
				}	
				var returnable={
					passed : hasSelectedSchedule,
					msg : scheduleSelectionMessage
				}
				return returnable;
			}
			
			var validateTOC = function(){
				var hasAcceptedTAC = false;
				if($('#terms-and-cond-row').hasClass('terms-and-cond-selected')){
					hasAcceptedTAC = true;
				}else{
					hasAcceptedTAC = false;
				}
				var returnable={
					passed : hasAcceptedTAC,
					msg : 'You must agree to the Terms & Conditions.'
				}
				return returnable;
			}

			// Deployment per-page validation
			var val = {
				idate: [
					{
						page: function(){ //0
							var dt = validateDeploymentType();
							var errorMsg = '';
							if(!dt.passed){
								errorMsg += dt.msg + '\n';
							}
							if(errorMsg){
								alert(errorMsg);
							}
							return dt.passed;
						}
					},			
					{
						page: function(){ //1
							var passed = false;
							var dep = validateDeploymentName();
							var errorMsg = '';
							if(!dep.passed){
								errorMsg += dep.msg + '\n';
							}
							passed = dep.passed;
							if(selectedDeploymentType === 'AWS'){
								var aws = validateAWSCredentials();
								if(!aws.passed){
									errorMsg += aws.msg + '\n';
								}
								passed = passed && aws.passed;
								$('.display-product-list-container.aws-type').html(displayDeployProductList('AWS',$('#aws_deployment_region').val()));
							}
							if(errorMsg){
								alert(errorMsg);
							}
							return passed;
						}
					},
					{
						page: function(){ //2
							var prod = validateProductsSelected();
							if(!prod.passed){
								alert(prod.msg);
							}
							return prod.passed;
						}
					},
					{
						page: function(){ //3
							var passed = false;
							var errorMsg = '';	
							if(selectedDeploymentType === 'AWS'){	
								var so = validateStartOptions();						
								if(!so.passed){
									errorMsg += so.msg + '\n';
								}
							}										
							var toc = validateTOC();
							if(!toc.passed){
								errorMsg += toc.msg + '\n';
							}
							if(errorMsg){
								passed= false;
								alert(errorMsg);
							}else{
								passed = true;
							}						
							return passed;
						}
					}
				]
			}
			
			var pages = $('.deploy-page');
			pages.hide();
			pages.first().show();
			var headerPane = $('#deployment-text-header');
			var currentPage = 0;
			var numPages = 3;
			
			$('#deploy-next').on('click',function(){
				var nextButton = $(this);
				// Validate page before moving on
				if((val.idate[currentPage]).page()){
					currentPage++;
					$('#deploy-previous').show();
					if(currentPage > numPages){
						currentPage = numPages;
					}	
					if(currentPage == numPages){
						nextButton.hide();
						$('#deploy_to_amazon_confirmation').show();
					}
					pages.hide();
					if(selectedDeploymentType==='INFOR24'){
						$('.aws-type').hide();
						$('.infor24-type').show();	
					}else{
						$('.aws-type').show();
						$('.infor24-type').hide();			
					}
					headerPane.text($('#deploy-page-'+(currentPage)).attr('data-page-title'));
					$('#deploy-page-'+(currentPage)).show();
				}
			}).disableSelection();
			
			$('#deploy-previous').hide().on('click',function(){
				var prevButton = $(this);
				$('#deploy-next').show();
				$('#deploy_to_amazon_confirmation').hide();
				currentPage--;
				if(currentPage < 0){
					currentPage = 0;
				}	
				if(currentPage == 0){
					prevButton.hide();
				}
				pages.hide();
				headerPane.text($('#deploy-page-'+(currentPage)).attr('data-page-title'));
				$('#deploy-page-'+(currentPage)).show();			
			}).disableSelection();			
			
			// Launch action
			$('#deploy_to_amazon_confirmation').on('click', function(){

				if((val.idate[numPages]).page()){
					var button2 = $(this);
					button2.text($.i18n.prop('trial.dialog.9'));
				
					var post_data = {
						regionId : selectedAWSRegion,
						deploymentType : selectedDeploymentType,
						deploymentName : selectedDeploymentName,
						productIds : selectedProducts,
						amazonCredentialsId : selectedAWSCrednetials,
						scheduleType : selectedScheduleType,
						scheduleValue : selectedScheduleValue
					}
					
					if(forOtherUser){
						post_data.userId = button.attr('data-user-id');
					}
					
					$.ajax({ 
						url: "services/deploy", 
						type: "POST",
						cache: false,
						data : JSON.stringify(post_data),
						dataType: "json",
						processData : true,
						contentType: "application/json; charset=utf-8",
						success: function(deployment){		
							// Do not add to my list of deployments, if I'm deploying for another user
							if(!forOtherUser){
								app_router.navigate("deployments",true);	
								UserDeployments.add(deployment);
								var options = {
									pageNum : 0,
									perPage : UserDeployments.perPage
								}							
								refreshDeploymentsView(UserDeployments,options);						
							}else{ // Add to users list of deployments
								var newDeployments = user.get('deployments');
								newDeployments.push(deployment);
								user.set('deployments',newDeployments);
							}
							
							// Close old dialog box before opening confirmation window
							closeBigDialog();
							iam = '<span style="color: #990000;">'+deployment.deploymentName+'</span>';
							var html = 	'\
								<div id="cloud_deploy">\
									<div class="cloud_deploy_header_container_singlepage">\
										<span style="color: #fff;font-size:16px;font-weight: bold;">AWS Deployment has started</span></span>\
									</div>\
									<div class="cloud_deploy_singlepage_content" style="height:130px;">\
										<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:15px;width:auto;height:auto;line-height:11px;font-weight:bold;">\
										The credentials for your newly deployed suite, "'+iam+'", and other relevant data is available by clicking on the "Deployments" tab on the left.\
										</div>\
										<div class="cdc_input_desc_text" style="margin-bottom:0px;margin-top:20px;width:auto;height:auto;line-height:11px;">\
										*Remember, '+iam+' has been deployed to Amazon AWS using your AWS account credentials. You will be charged for storage and running the application by Amazon. Monitoring these costs are your responsibility.\
										</div>\
									</div>\
									<div class="cloud_deploy_button_container_singlepage">\
										<span class="cloud_deploy_button" id="cloud_deployed_close" style="width:120px;text-align:center;">'+$.i18n.prop('aws.dialog.18')+'</span>\
									</div>\
								</div>';
							
							openBigDialog(250,500,20,20,250,500,html,true);
							
							$('#cloud_deployed_close').on('click',function(){
								closeBigDialog();
								$('.view-button',$('#deployment-'+deployment.id)).trigger('click');	
							});	
						},
						error: function(jqXHR, textStatus, errorThrown){
							button2.text($.i18n.prop('aws.dialog.12'));
							alert($.i18n.prop('aws.dialog.19')+'\n\n'+jqXHR.responseText);
						}
					});				
				}
			}).disableSelection();			
		}
	});			
	
	function reLayout(){
		tileContainer.isotope( 'reLayout', function(){} );
	}

	function determineDeployButton(){
			if(availableDeployOptions()){
				$('#global-deploy').removeClass('disabled');
			}	
	}
	
	function initProducts(){
		if(UserProducts.length === 0){
			UserProducts.fetch({
				success:function(collection){
					initDashboard();	
					determineDeployButton();								
					displayProducts(collection);					
				},
				error:function(collection,response){
					alert('Error getting Products.');
				}	
			});
		}		
	}
	
	function initDeployments(){
		if(UserDeployments.length === 0){
			UserDeployments.fetch({
				success:function(collection){
					var options = {
						pageNum : 0,
						perPage : UserDeployments.perPage
					}
					refreshDeploymentsView(collection,options);
				},
				error:function(collection,response){
					alert('Error getting User Deployments.');
				}	
			});
		}	
	}
	
	function initDashboard(){	
		// Chart 1
		// Deployments by Month
		$.ajax({ 
			url: "services/dashboard/rollingDeployments?months=6", 
			type: "GET",
			cache: false,
			dataType: "json",
			processData : true,
			contentType: "application/json; charset=utf-8",
			success: function(data){
				var aws 	= [];
				var infor 	= [];
				var totalCount = [];
				// Can specify a custom tick Array.
				// Ticks should match up one for each y value (category) in the series.
				var ticks = [];			
				var rows = ['Deployments by Month']
				var months = data.rollingDeployments;
				months.each(function(mon,i) {
					//"name":"Dec","month":2,"timestamp":1322715600000,"aws":0,"infor24":0,"products":[]
					aws[i] 	= mon.aws;
					infor[i] 	= mon.infor24;
					ticks[i]	= mon.name;
					totalCount[i] = (mon.aws + mon.infor24);
					rows[7-i] 	= '<div class="chart-row-col" style="border-right:1px solid #ccc;width:45px;">'+mon.name + ' </div><div class="chart-row-col">AWS:<span class="aws-data">' +mon.aws+'</span></div><div class="chart-row-col">Infor24:<span class="infor24-data">'+ mon.infor24+'</span></div>';
				});
				
				var rowString = '';
				rows.each(function(row,i){
					rowString += '<div class="dashboard-widget-row">'+row+'</div>';
				});
				$('#chart-1-data').html(rowString);

				var plot1 = $.jqplot('chart-1-chart', [totalCount], {
					title: 'Deployments by Month', 
					seriesDefaults: { 
						showMarker:false,
						pointLabels: { show:true } 
					},
					legend: {
						show: true,
						location: 'nw',
						placement: 'insideGrid'
					},
					series:[
						{label:'Deployments',color:'#33B5E5'}
					],					
					axes: {
						// Use a category axis on the x axis and use our custom ticks.
						xaxis: {
							renderer: $.jqplot.CategoryAxisRenderer,
							ticks: ticks
						},
						yaxis: {
							numberTicks: 7,
							min: 0
						}
					}					  
				});	
				  
				var plot2 = $.jqplot( 'chart-3-chart' , [ aws , infor ] , {
					// The "seriesDefaults" option is an options object that will
					// be applied to all series in the chart.
					seriesDefaults:{
						renderer:$.jqplot.BarRenderer,
						rendererOptions: {fillToZero: true},
						pointLabels: { show:true } 
					},
					// Custom labels for the series are specified with the "label"
					// option on the series option.  Here a series option object
					// is specified for each series. #CC121B
					series:[
						{label:'AWS',color:'#33B5E5'},
						{label:'Infor24',color:'#99cc00'}
					],
						title: {
							text: 'Monthly Deployments by Cloud',   // title for the plot,
							show: true,
						},							
					// Show the legend and put it outside the grid, but inside the
					// plot container, shrinking the grid to accomodate the legend.
					// A value of "outside" would not shrink the grid and allow
					// the legend to overflow the container.
					legend: {
						show: true,
						location: 'nw',
						placement: 'insideGrid'
					},
					axes: {
						// Use a category axis on the x axis and use our custom ticks.
						xaxis: {
							renderer: $.jqplot.CategoryAxisRenderer,
							ticks: ticks
						},
						yaxis: {
							numberTicks: 7,
							min: 0
						}
					}
				});

				var rows = ['Total Deployments By Product']
				var months = data.rollingDeployments;
				var awsProductArr = [];
				var infor24ProductArr = [];
				var nameProductArr = [];
				var totalAWSCount = 0;
				var totalInfor24Count = 0;
				UserProducts.each(function(product,i){
					if(product.get('availability').trial || product.get('availability').deployment){
						var awsCount = 0;
						var infor24Count = 0;
						var totalCount = 0;
						var productName = product.get('displayName1');
						months.each(function(mon,i) {
							var products = mon.products;
							products.each(function(prod,j){
								if(prod.productId == product.get('id')){
									awsCount 	+= prod.aws;
									infor24Count  += prod.infor24;
								}
							});
						});	
						rows[i+1] 	= '<div class="chart-row-col" style="border-right:1px solid #ccc;width:230px;overflow:hidden;">'+productName+'</div><div class="chart-row-col">AWS:<span class="aws-data">' +awsCount+'</span></div><div class="chart-row-col">Infor24:<span class="infor24-data">'+ infor24Count+'</span></div>';
						var totalArr = [productName,(awsCount+infor24Count),];
						var awsArr =  [productName,awsCount];
						var infor24Arr =  [productName,infor24Count];

						awsProductArr[i] =awsCount;
						infor24ProductArr[i] = infor24Count;
						nameProductArr[i] = productName;

						totalAWSCount += awsCount;
						totalInfor24Count += infor24Count
					}
				});

				var totalData = [['AWS',totalAWSCount],['Infor24',totalInfor24Count]];
				var rowString = '';
				rows.each(function(row,i){
					rowString += '<div class="dashboard-widget-row" style="width:420px;">'+row+'</div>';
				});
				$('#chart-2-data').html(rowString);

				var plot3 = jQuery.jqplot ('chart-2-chart', [totalData],{ 
						seriesColors: [ "#33B5E5", "#99cc00"],
						seriesDefaults: {
							// Make this a pie chart.
							renderer: jQuery.jqplot.PieRenderer, 
							rendererOptions: {
								// Put data labels on the pie slices.
								// By default, labels show the percentage of the slice.
								showDataLabels: true
							}
						},					
						title: {
							text: 'Total Deployments by Cloud',   // title for the plot,
							show: true,
						},						
						legend: { show:true, location: 'nw' }
					}
				);	
			}
		});
	}
	
	function initRegions(){
		var regionList = '';
		Regions.each(function(region){
			if(region.get('regionType') == 'AWS'){
				var defaulted = region.get('cloudAlias') == 'USEAST1' ? 'selected' : '';
				regionList += '<option value="'+region.get('id')+'" '+defaulted+'>'+(region.get('name')).toUpperCase()+'</option>';
			}
		});
		$('#aws_instance_regions').replaceWith( '\
			<select id="aws_instance_regions" name="test" class="cdc_input_select" style="margin-top:0px;margin-left:0px;width:155px;margin-right:10px;float:left;padding: 5px 4px 6px 5px;">\
				'+regionList+'\
			</select>');		
	}
	
	function initInstances(){
		var awsList = '';
		UserAWSList.each(function(awscred){
			awsList += '<option value="'+awscred.get('id')+'">'+awscred.get('name')+'</option>';
		});		
		$('#aws_instance_credentials').replaceWith( '\
			<select id="aws_instance_credentials" name="test" class="cdc_input_select" style="margin-top:0px;margin-left:0px;width:225px;margin-right:10px;float:left;padding: 5px 4px 6px 5px;">\
				<option value="-1"></option>\
				'+awsList+'\
			</select>');
	}

	var R = Backbone.Router.extend({
		routes: {
			""	    				:	"start"	,
			"marketplace"	:	"marketplace" ,
			"app/:id"			:	"app"	,
			"deployments"	:	"deployments",
			"users"				:	"users",
			"trialrequests"	:	"trialrequests",
			"instances"		:	"instances",
			"dashboard"		:	"dashboard"
		},
		start: function(){
			if($('#dashboard-link').length > 0){
				app_router.navigate("dashboard",true);
			}else{
				app_router.navigate("deployments",true);
			}
		},
		marketplace: function(){
			slideTo(0,'Marketplace');
			initProducts();
			initDeployments();
		},
		app: function(id){
			async.series({
				one: function(callback){
					if(UserProducts.length === 0){
						UserProducts.fetch({
							success:function(collection){
								initDashboard();
								determineDeployButton();
								displayProducts(collection);
								callback(null,1);
								initDeployments();
							},
							error:function(collection,response){
								callback('Error getting collection.',1);
								alert('Error getting collection.');
							}	
						});	
					}else{
						initDeployments();
						callback(null,1);
					}					
				},
				two: function(callback){
					var up = UserProducts.get(id);
					showProductSlide(up);
					callback(null,2);
				},
			},
			function(err, results) {
				// results is now equal to: {one: 1, two: 2}
			});			
		},
		deployments: function(){
			slideTo(2,'Deployments');
			initProducts();
			initDeployments();
		},
		users: function(){
			slideTo(3,'Users');
			initProducts();
			initDeployments();
		},
		trialrequests: function(){
			slideTo(4,'Trial Requests');
			initProducts();
			initDeployments();
		},		
		instances: function(){
			slideTo(5,'Instances');
			initProducts();
			initDeployments();
		},
		dashboard: function(){
			slideTo(6,'Dashboard');
			initProducts();
			initDeployments();				
		}
	});
	
	// Init routing bindings created above
	var app_router = new R;
	Backbone.history.start();

	// MENU LINKS ///////////////////////////////////////////////////////////////////////////////////
	mMarketplace.on('click',function(){
		app_router.navigate("marketplace",false);
		slideTo(0,'Marketplace');
	});
	mDeployments.on('click',function(){
		app_router.navigate("deployments",false);
		slideTo(2,'Deployments');	
	});
	mUsers.on('click',function(){
		app_router.navigate("users",false);
		slideTo(3,'Users');	
	});
	mTrialRequests.on('click',function(){
		app_router.navigate("trialrequests",false);
		slideTo(4,'Trial Requests');	
	});
	mInstances.on('click',function(){
		app_router.navigate("instances",false);
		slideTo(5,'Instances');	
	});
	mDashboard.on('click',function(){
		app_router.navigate("dashboard",false);
		slideTo(6,'Dashboard');	
	});	
});